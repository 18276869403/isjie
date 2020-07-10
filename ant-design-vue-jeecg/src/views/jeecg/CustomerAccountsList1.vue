<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="5" :sm="8">
            <a-form-item label="账户名称">
              <a-input placeholder="请输入账户名称" v-model="queryParam.account"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button @click="handleAdd(custid)" type="primary" icon="plus" style="margin-left: 8px">新增客户账号</a-button>
               <a-button type="primary" @click="backOne" icon="left-circle" style="margin-left: 8px;border: none;background-color: #FA541C">返回</a-button>
              <a-dropdown v-if="selectedRowKeys.length > 0">
                <a-button @click="batchDel" type="primary" icon="delete" style="margin-left: 8px">批量删除</a-button>
              </a-dropdown>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

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
          <j-ellipsis :value="text" :length="20" />
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
          <!--<a-dropdown>-->
          <!--<a class="ant-dropdown-link">更多 <a-icon type="down" /></a>-->
          <!--<a-menu slot="overlay">-->
          <!--<a-menu-item>-->
          <!--<a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
          <!--<a>删除</a>-->
          <!--</a-popconfirm>-->
          <!--</a-menu-item>-->
          <!--</a-menu>-->
          <!--</a-dropdown>-->
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
  import {getOneAreaAll,getTwoAreaAll,custStates} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import { filterObj } from '@/utils/util';
  import { deleteAction, getAction } from '@/api/manage'
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"

  export default {
    name: "CustomerAccountsList",
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
        //token header
        tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
        /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
        queryParam: {customerId:this.$route.query.custId},
        /* 数据源 */
        dataSource:[],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        /* 排序参数 */
        isorter:{
          column: 'createTime',
          order: 'desc',
        },
        /* 筛选参数 */
        filters: {},
        /* table加载状态 */
        loading:false,
        /* table选中keys*/
        selectedRowKeys: [],
        /* table选中records*/
        selectionRows: [],
        /* 查询折叠 */
        toggleSearchStatus:false,
        /* 高级查询条件生效状态 */
        superQueryFlag:false,
        /* 高级查询条件 */
        superQueryParams:""
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created(){
      this.initialGetOneAreaList();
      this.loadData();
      this.custid = this.$route.query.custId;
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
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      handleSuperQuery(arg) {
        //高级查询方法
        if(!arg){
          this.superQueryParams=''
          this.superQueryFlag = false
        }else{
          this.superQueryFlag = true
          this.superQueryParams=JSON.stringify(arg)
        }
        this.loadData()
      },
      getQueryParams() {
        //获取查询条件
        let sqp = {}
        if(this.superQueryParams){
          sqp['superQueryParams']=encodeURI(this.superQueryParams)
        }
        var param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      getQueryField() {
        //TODO 字段权限控制
        var str = "id,";
        this.columns.forEach(function (value) {
          str += "," + value.dataIndex;
        });
        return str;
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
      },
      searchQuery() {
        this.loadData(1);
      },
      superQuery() {
        this.$refs.superQueryModal.show();
      },
      searchReset() {
        this.queryParam = {customerId:this.$route.query.custId}
        this.loadData(1);
      },
      batchDel: function () {
        if(!this.url.deleteBatch){
          this.$message.error("请设置url.deleteBatch属性!")
          return
        }
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return;
        } else {
          var ids = "";
          for (var a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ",";
          }
          var that = this;
          this.$confirm({
            title: "确认删除",
            content: "是否删除选中数据?",
            onOk: function () {
              deleteAction(that.url.deleteBatch, {ids: ids}).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData();
                  that.onClearSelected();
                } else {
                  that.$message.warning(res.message);
                }
              });
            }
          });
        }
      },
      handleDelete: function (id) {
        if(!this.url.delete){
          this.$message.error("请设置url.delete属性!")
          return
        }
        var that = this;
        deleteAction(that.url.delete, {id: id}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = "编辑";
        this.$refs.modalForm.disableSubmit = false;
      },
      handleAdd: function (custid) {
        this.$refs.modalForm.add(custid);
        this.$refs.modalForm.title = "新增";
        this.$refs.modalForm.disableSubmit = false;
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleToggleSearch(){
        this.toggleSearchStatus = !this.toggleSearchStatus;
      },
      modalFormOk() {
        // 新增/修改 成功时，重载列表
        this.loadData();
      },
      handleDetail:function(record){
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title="详情";
        this.$refs.modalForm.disableSubmit = true;
      },
      //返回客户管理
      backOne(){
        this.$router.push('CustomersList')
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>