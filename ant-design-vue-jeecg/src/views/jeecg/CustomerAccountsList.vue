<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="4" :sm="8">
            <a-form-item label="省份">
              <a-select v-model="queryParam.oneAreaName" placeholder="请选择省份">
                <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
                <a-select-option v-for="(oneArea1,oneAreaindex1) in getOneArea1" :key="oneAreaindex1.toString()" :value="oneArea1.id"
                                 @click="initialGetTwoAreaList(oneArea1.id)">{{oneArea1.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(twoArea1,twoAreaindex1) in getTwoArea1" :key="twoAreaindex1.toString()" :value="twoArea1.id">{{twoArea1.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">新增客户账号</a-button>
              <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-button @click="batchDel" type="primary" icon="delete" style="margin-left: 8px">批量删除</a-button>
      </a-dropdown>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->

<!--      -->
<!--    </div>-->

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <!--报警短信接收1-接受，2-拒绝-->
        <span slot="shortLetter" slot-scope="text,record">
            <a v-if="record.shortLetter==2" style="color: red;">拒绝</a>
            <a v-if="record.shortLetter==1" style="color: green;">接收</a>
        </span>
        <!--状态(1-启用，2-禁用)-->
        <span slot="custState" slot-scope="text,record">
            <a v-if="record.custState==2" style="color: red;">禁用</a>
            <a v-if="record.custState==1" style="color: green;">启用</a>
        </span>
        <!-- 备注字符串超长截取省略号显示 -->
        <span slot="remark1" slot-scope="text">
          <j-ellipsis :value="text" :length="10" />
        </span>

        <span slot="action" slot-scope="text, record">

          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />

          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
          </a-popconfirm>

          <a-divider type="vertical" />

          <a v-if="record.custState==1">
          <a-popconfirm title="确定禁用吗?" @confirm="() => handleFrozen(record.id,2)">
          <a>禁用</a>
          </a-popconfirm>
          </a>
          <a v-if="record.custState==2">
          <a-popconfirm title="确定启用吗?" @confirm="() => handleFrozen(record.id,1)">
          <a>启用</a>
          </a-popconfirm>
          </a>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <customerAccounts-modal ref="modalForm" @ok="modalFormOk"></customerAccounts-modal>
  </a-card>
</template>

<script>
  import CustomerAccountsModal from './modules/CustomerAccountsModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll,getTwoAreaAll,custStates} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'

  export default {
    name: "CustomerAccountsList",
    mixins:[JeecgListMixin],
    components: {
      JEllipsis,
      CustomerAccountsModal
    },
    data () {
      return {
        description: '客户账号表管理页面',
        getOneArea1:[], //获取所有的一级区域
        getTwoArea1:[],//获取所有的二级区域
        // 表头
        columns: [
          {
            title: '省份',
            align:"center",
            dataIndex: 'oneAreaName'
          },
          {
            title: '城市',
            align:"center",
            dataIndex: 'twoAreaName'
          },
          {
            title: '客户名称',
            align:"center",
            dataIndex: 'name'
          },
          {
            title: '账户名称',
            align:"center",
            dataIndex: 'account'
          },
          {
            title: '报警短信接收',
            align:"center",
            dataIndex: 'shortLetter',
            scopedSlots: { customRender: 'shortLetter' }
          },
          {
            title: '接收手机号码',
            align:"center",
            dataIndex: 'phone',
            customRender: (value,row) => {
              if (row.shortLetter == 1){
                return value
              }else {
                return ""
              }
            }
          },
          {
            title: '备注',
            align:"center",
            dataIndex: 'remark',
            scopedSlots: {customRender: 'remark1'}
          },
          {
            title: '添加时间',
            align:"center",
            dataIndex: 'createTime'
          },
          {
            title: '状态',
            align:"center",
            dataIndex: 'custState',
            scopedSlots: { customRender: 'custState' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/customerAccounts/queryCustAccountsVoAll",
          delete: "/monitor/customerAccounts/delete",
          deleteBatch: "/monitor/customerAccounts/deleteBatch",
          exportXlsUrl: "monitor/customerAccounts/exportXls",
          importExcelUrl: "monitor/customerAccounts/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created(){
      this.initialGetOneAreaList()
    },
    methods: {
      //获取所有的省份
      initialGetOneAreaList(){
        this.getOneArea1 = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea1 = res.result
          } else {
          }
        })
      },
      //获取所有的城市
      initialGetTwoAreaList(id){
        this.getTwoArea1 = ['']
        getTwoAreaAll({id:id}).then((res) => {
          if (res.success){
            this.getTwoArea1 = res.result
          } else {
          }
        })
      },
      //当未选择省份时，城市下拉框清空
      initialOneAreaList(){
        this.getTwoArea1 = ['']
        this.queryParam.twoAreaName="";
      },
      //状态切换
      batchFrozen: function(custState) {
        if (this.selectedRowKeys.length <= -1) {
          this.$message.warning('请选择一条记录！')
          return false
        } else {
          let id = ''
          let that = this
          let isAdmin = false
          that.selectionRows.forEach(function(row) {
            if (row.username == 'admin') {
              isAdmin = true
            }
          })
          if (isAdmin) {
            that.$message.warning('管理员账号不允许此操作,请重新选择！')
            return
          }
          that.selectedRowKeys.forEach(function(val) {
            id += val + ','
          })
          that.$confirm({
            title: '确认操作',
            content: '是否' + (custState == 1 ? '启用' : '禁用') + '选中账号?',
            onOk: function() {
              custStates({ id: id, custState: custState }).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadData()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel()
        } else if (e.key == 2) {
          this.batchFrozen(2)
        } else if (e.key == 3) {
          this.batchFrozen(1)
        }
      },
      handleFrozen: function(id, custState, username) {
        let that = this
        //TODO 后台校验管理员角色
        if ('admin' == username) {
          that.$message.warning('管理员账号不允许此操作！')
          return
        }
        custStates({ ids: id, custState: custState }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>