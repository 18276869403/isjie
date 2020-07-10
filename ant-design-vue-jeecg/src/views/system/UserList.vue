<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="5" :sm="9">
            <a-form-item label="用户名">
              <a-input placeholder="请输入用户名" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="9">
            <a-form-item label="真实姓名">
              <a-input placeholder="请输入真实姓名" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
               <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">添加用户</a-button>

              <a-dropdown v-if="selectedRowKeys.length > 0">
                 <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
              </a-dropdown>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        bordered
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>

          <a-dropdown>
            <a class="ant-dropdown-link">
              更多 <a-icon type="down"/>
            </a>
            <a-menu slot="overlay">


        <a-menu-item>
          <a href="javascript:;" @click="handleChangePassword(record.username)">密码</a>
        </a-menu-item>

        <a-menu-item>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </a-menu-item>

        <a-menu-item v-if="record.status==1">
          <a-popconfirm title="确定冻结吗?" @confirm="() => handleFrozen(record.id,2,record.username)">
            <a>冻结</a>
          </a-popconfirm>
        </a-menu-item>

        <a-menu-item v-if="record.status==2">
          <a-popconfirm title="确定解冻吗?" @confirm="() => handleFrozen(record.id,1,record.username)">
            <a>解冻</a>
          </a-popconfirm>
        </a-menu-item>

        <a-menu-item>
            <a href="javascript:;" @click="handleAssignCustomers(record.id)">管理分配</a>
         </a-menu-item>
            <a-menu-item v-if="record.orgCode==1">
          <a-popconfirm title="确定拒收吗?" @confirm="() => juShou(record.id,2,record.username)">
            <a>拒收报警短信</a>
          </a-popconfirm>
        </a-menu-item>
         <a-menu-item v-if="record.orgCode!=1">
          <a-popconfirm title="确定接收吗?" @confirm="() => juShou(record.id,1,record.username)">
            <a>接收报警短信</a>
          </a-popconfirm>
        </a-menu-item>

        </a-menu>
        </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>

    <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>

    <sys-user-agent-modal ref="sysUserAgentModal"></sys-user-agent-modal>

    <distribution-modal ref="distributionModal"></distribution-modal>
  </a-card>
</template>

<script>
  import UserModal from './modules/UserModal'
  import PasswordModal from './modules/PasswordModal'
  import {frozenBatch,queryall} from '@/api/api'
  //import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import SysUserAgentModal from "./modules/SysUserAgentModal";
  import JInput from '@/components/jeecg/JInput'
  import JDate from '../../components/jeecg/JDate'
  import { filterObj } from '@/utils/util';
  import { deleteAction, getAction,downFile,putAction } from '@/api/manage'
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import DistributionModal from './modules/DistributionModal'

  export default {
    name: "UserList",
    //mixins: [JeecgListMixin],
    components: {
      SysUserAgentModal,
      DistributionModal,
      UserModal,
      PasswordModal,
      JInput,
      JDate
    },
    data() {
      return {
        description: '这是用户管理页面',
        roleList:[],
        columns: [
          {
            title: '用户名',
            align: "center",
            dataIndex: 'username',
          },
          //角色
          {
            title: '用户角色',
            align: "center",
            width: "150px",
            dataIndex: 'roleName',
          },

          {
            title: '真实姓名',
            align: "center",
            dataIndex: 'realname',
          },
          {
            title: '性别',
            align: "center",
            width: "50px",
            dataIndex: 'sex_dictText',
            // sorter: true
          },
          {
            title: '手机号',
            align: "center",
            width: "120px",
            dataIndex: 'phone'
          },
          {
            title: '状态',
            align: "center",
            width: "70px",
            dataIndex: 'status_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            scopedSlots: {customRender: 'action'}
          }

        ],
        url: {
          imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
          syncUser: "/process/extActProcess/doSyncUser",
          list: "/sys/user/list",
          delete: "/sys/user/delete",
          deleteBatch: "/sys/user/deleteBatch",
          exportXlsUrl: "/sys/user/exportXls",
          importExcelUrl: "sys/user/importExcel",
        },
        //token header
        tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
        /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
        queryParam: {},
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
    methods: {
      juShou : function(id,status,username){
        console.log("1234")
        getAction("/sys/user/shouJuMSG",{id:id,status:status}).then((res) =>{
         console.log("修改完成");
        })
        this.loadData();
      },
      getAvatarView: function (avatar) {
        return this.url.imgerver + "/" + avatar;
      },
      /*复制*/
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        var params = this.getQueryParams()//查询条件
        this.loading = true
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      },
      handleSuperQuery(arg) {
        //高级查询方法
        if (!arg) {
          this.superQueryParams = ''
          this.superQueryFlag = false
        } else {
          this.superQueryFlag = true
          this.superQueryParams = JSON.stringify(arg)
        }
        this.loadData()
      },
      getQueryParams() {
        //获取查询条件
        let sqp = {}
        if (this.superQueryParams) {
          sqp['superQueryParams'] = encodeURI(this.superQueryParams)
        }
        var param = Object.assign(sqp, this.queryParam, this.isorter, this.filters)
        param.field = this.getQueryField()
        param.pageNo = this.ipagination.current
        param.pageSize = this.ipagination.pageSize
        return filterObj(param)
      },
      getQueryField() {
        //TODO 字段权限控制
        var str = 'id,'
        this.columns.forEach(function(value) {
          str += ',' + value.dataIndex
        })
        return str
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
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      onClearSelected() {
        this.selectedRowKeys = []
        this.selectionRows = []
      },
      searchQuery() {
        this.loadData(1)
      },
      appointlist1(needid) {
        this.loadData1(1, needid)
      },
      superQuery() {
        this.$refs.superQueryModal.show()
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1)
      },
      handleEdit: function(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
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
      handleAdd: function() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.disableSubmit = false
      },

      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field
          this.isorter.order = 'ascend' == sorter.order ? 'asc' : 'desc'
        }
        this.ipagination = pagination
        this.loadData()
      },
      handleToggleSearch() {
        this.toggleSearchStatus = !this.toggleSearchStatus
      },
      modalFormOk() {
        // 新增/修改 成功时，重载列表
        this.loadData()
      },
      handleDetail: function(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '详情'
        this.$refs.modalForm.disableSubmit = true
      },
      batchFrozen: function (status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          let isAdmin = false;
          that.selectionRows.forEach(function (row) {
            if (row.username == 'admin') {
              isAdmin = true;
            }
          });
          if (isAdmin) {
            that.$message.warning('管理员账号不允许此操作,请重新选择！');
            return;
          }
          that.selectedRowKeys.forEach(function (val) {
            ids += val + ",";
          });
          that.$confirm({
            title: "确认操作",
            content: "是否" + (status == 1 ? "解冻" : "冻结") + "选中账号?",
            onOk: function () {
              frozenBatch({ids: ids, status: status}).then((res) => {
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
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel();
        } else if (e.key == 2) {
          this.batchFrozen(2);
        } else if (e.key == 3) {
          this.batchFrozen(1);
        }
      },
      handleFrozen: function (id, status, username) {
        let that = this;
        //TODO 后台校验管理员角色
        if ('admin' == username) {
          that.$message.warning('管理员账号不允许此操作！');
          return;
        }
        frozenBatch({ids: id, status: status}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      handleChangePassword(username) {
        this.$refs.passwordmodal.show(username);
      },
      handleAgentSettings(username){
        this.$refs.sysUserAgentModal.agentSettings(username);
        this.$refs.sysUserAgentModal.title = "用户代理人设置";
      },
      handleAssignCustomers(id){
        this.$refs.distributionModal.settings(id);
        this.$refs.distributionModal.title="分配省市客户";
      },
      passwordModalOk() {
        //TODO 密码修改完成 不需要刷新页面，可以把datasource中的数据更新一下
      }
    },
    created() {
      this.loadData()
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>