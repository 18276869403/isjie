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
                <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                                 @click="initialGetTwoAreaList(oneArea.id)">{{oneArea.oneAreaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="8">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id">{{twoArea.twoAreaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="8">
            <a-form-item label="楼层名">
              <a-input placeholder="请输入楼层名" v-model="queryParam.floorName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <a-button @click="handleAdd(floorid)" type="primary" icon="plus" style="margin-left: 8px">新增</a-button>
                <a-button type="primary" @click="backOne" icon="left-circle" style="margin-left: 8px;border: none;background-color: #FA541C">返回</a-button>
                <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
      </a-dropdown>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->
<!--     &lt;!&ndash; <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
<!--      </a-dropdown>&ndash;&gt;-->
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
        <!-- 备注字符串超长截取省略号显示 -->
        <span slot="remark1" slot-scope="text">
          <j-ellipsis :value="text" :length="20" />
        </span>

        <template slot="iconlot" slot-scope="text,record,index">
          <div class="anty-img-wrap">

            <a @click="() => getUrlNewView(record.planPic)">
              <a-avatar shape="square" :src="getAvatarView(record.planPic)"/>
            </a>
          </div>
        </template>
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
          <router-link :to="{path: 'MoveTest', query: {customerId:record.customerId,floorId:record.id,custName:record.name} }">设备布点</router-link>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <floorPlan-modal ref="modalForm" @ok="modalFormOk"></floorPlan-modal>
  </a-card>
</template>

<script>
  import FloorPlanModal from './modules/FloorPlanModal'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import { filterObj } from '@/utils/util';
  import { deleteAction, getAction } from '@/api/manage'
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"

  export default {
    name: "FloorPlanList",
    components: {
      JEllipsis,
      FloorPlanModal
    },
    data () {
      return {
        description: '楼层图表管理页面',
        // 表头
        getOneArea:[], //获取所有的一级区域
        getTwoArea:[],//获取所有的二级区域
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
            title: '楼层名',
            align:"center",
            dataIndex: 'floorName'
           },
		   {
            title: '平面图',
            align:"center",
            dataIndex: 'planPic',
         scopedSlots: { customRender: 'iconlot' }
           },
		   {
            title: '备注',
            align:"center",
            dataIndex: 'remark',
          scopedSlots: {customRender: 'remark1'}
           },

          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/monitor/floorPlan/Florlist",
          delete: "/monitor/floorPlan/delete",
          deleteBatch: "/monitor/floorPlan/deleteBatch",
          exportXlsUrl: "monitor/floorPlan/exportXls",
          importExcelUrl: "monitor/floorPlan/importExcel",
      imgerver: window._CONFIG['domianURL'] + '/sys/common/view'
       },
        //token header
        tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
        /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
        queryParam: {customerId:this.$route.query.floorId},
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
    created() {
      this.initialGetOneAreaList()
      this.loadData();
      this.floorid = this.$route.query.floorId;
    },
    methods: {
      getAvatarView: function(planPic) {
        return this.url.imgerver + '/' + planPic
      },
      //获取url打开新页面
      getUrlNewView(record){
        let config = window._CONFIG['domianURL'] + "/sys/common/view";
        window.open(config+"/"+record, '_blank')
      },
      //获取所有的一级区域
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
         /*   console.log(res.message)*/
          }
        })
      },
      //获取所有的二级区域
      initialGetTwoAreaList(id){
        this.getTwoArea = ['']
        getTwoAreaAll({id:id}).then((res) => {
          if (res.success){
            this.getTwoArea = res.result
          } else {
         /*   console.log(res.message)*/
          }
        })
      },
      //当未选择一级区域时，二级区域下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.queryParam.twoAreaName="";
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
        this.queryParam = {customerId:this.$route.query.floorId}
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
      handleAdd: function (oneareaid,twoareaid,custid) {
        this.$refs.modalForm.add(oneareaid,twoareaid,custid);
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