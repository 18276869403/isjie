<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="5" :sm="8">
            <a-form-item label="省份">
              <j-input placeholder="请输入省份" v-model="queryParam.oneAreaName" class="input"></j-input>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="状态">
                <a-input placeholder="请输入状态" v-model="queryParam.delFlag"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
               <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">新增省份</a-button>


            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->

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
        :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">
        <!-- 字符串超长截取省略号显示 -->
        <span slot="remark" slot-scope="text">
          <j-ellipsis :value="text" :length="10"/>
        </span>


        <span slot="delFlag" slot-scope="text,record">
            <a v-if="record.delFlag==0"><p style="color: green;">正常</p></a>
            <a v-if="record.delFlag==1"><p style="color: red;">禁用</p></a>
        </span>
        <span slot="action" slot-scope="text,record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <!--删除-->
          <a-popconfirm title="确认删除吗，删除省份时下属城市会一起删除？" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
          </a-popconfirm>
         <!-- <a-divider type="vertical"/>-->
          <!--查看二级区域-->
               <!--  <a-popconfirm v-if="record.delFlag==0"  title="确定禁用吗，禁用省份时下属城市会一起禁用" @confirm="() => handleFrozen(record.id,1,record.oneAreaId)">
                  <a>禁用</a>
                </a-popconfirm>
                <a-popconfirm v-if="record.delFlag==1" title="确定恢复吗?" @confirm="() => handleFrozen(record.id,0,record.oneAreaId)">
                  <a>恢复</a>
                </a-popconfirm>-->
          <a-divider type="vertical"/>
          <router-link :to="{ path: 'TwoAreaList1',query:{oneid:record.id} } ">
            查看下属城市
          </router-link>
        </span>


      </a-table>
    </div>
    <!--表单区域-->
    <oneArea-modal ref="modalForm" @ok="modalFormOk"></oneArea-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import OneAreaModal from './modules/OneAreaModal'
  import JInput from '../../components/jeecg/JInput'
  import { oneAreaStatus } from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  export default {
    name: "OneAreaList",
    mixins:[JeecgListMixin],
    components: {
      JEllipsis,
      JInput,
      OneAreaModal
    },
    data () {
      return {
        description: '省份管理页面',
        // 表头
        columns: [
          {
            title:'省份',
            align:"center",
            width:'380px',
            dataIndex: 'oneAreaName'
          },
         /* {
            title: '状态',
            align: 'center',
            dataIndex: 'delFlag',
            scopedSlots: { customRender: 'delFlag' }
          },*/
          {
            title:'备注',
            align:"center",
            width:'380px',
            dataIndex: 'remark',
            scopedSlots: { customRender: 'remark' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/monitor/oneArea/list",
          delete: "/monitor/oneArea/delete",
          deleteBatch: "/monitor/oneArea/deleteBatch",
          exportXlsUrl: "/monitor/oneArea/exportXls",
          importExcelUrl: "monitor/oneArea/importExcel",
        },
        /*dictOptions:{
        },*/
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      batchFrozen: function(delFlag) {
        if (this.selectedRowKeys.length <= 0) {
          // this.$message.warning('请选择一条记录！')
          // return false
        } else {
          let ids = ''
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
            ids += val + ','
          })
          that.$confirm({
            title: '确认操作',
            content: '是否' + (delFlag == 1 ? '恢复' : '禁用') + '选中账号?',
            onOk: function() {
              var url = ""
              oneAreaStatus({ ids: ids, delFlag: delFlag }).then((res) => {
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
      initDictConfig(){
      },
      handleFrozen: function(id, delFlag, username) {
        let that = this
        // TODO 后台校验管理员角色
        if ('admin' == username) {
          that.$message.warning('管理员账号不允许此操作！')
          return
        }
        oneAreaStatus({ ids: id, delFlag: delFlag }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>