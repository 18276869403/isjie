<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="4" :sm="8">
            <a-form-item label="设备类型">
              <a-select v-model="queryParam.deviceType" placeholder="请选择设备类型">
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="探测器">探测器</a-select-option>
                <a-select-option value="控制器">控制器</a-select-option>
                <a-select-option value="输出模块">输出模块</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item label="设备IMEI">
              <a-input placeholder="请输入IMEI" v-model="queryParam.deviceImei"></a-input>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :md="4" :sm="8">
              <a-form-item label="设备IMEI">
                <a-input placeholder="请输入设备IMEI" v-model="queryParam.deviceImei"></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="4" :sm="8">
              <a-form-item label="设备位号">
                <a-input placeholder="请输入设备位号" v-model="queryParam.devicePositionnum"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="10" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <a-button @click="handleAdd(devid)" type="primary" icon="plus"  style="margin-left: 8px">添加设备</a-button>
                <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
               <a-button type="primary" icon="import" style="margin-left: 8px">Excle导入</a-button>
                  </a-upload>
              <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls('设备表')">下载模板</a-button>
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
    <div class="table-operator">

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

         <span slot="deviceType" slot-scope="text,record">
            <span v-if="record.deviceType==1">探测器</span>
            <span v-if="record.deviceType==2">控制器</span>
           <span v-if="record.deviceType==3">输出模块</span>
        </span>

        <!-- 客户字符串超长截取省略号显示 -->
        <span slot="name" slot-scope="text">
          <j-ellipsis :value="text" :length="10" />
        </span>

        <span slot="action" slot-scope="text, record">
           <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>

        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <device-modal ref="modalForm1" @ok="modalFormOk"></device-modal>
<!--    <device-modal-b ref="modalForm" @ok="modalFormOk1"></device-modal-b>-->
  </a-card>
</template>

<script>
  import DeviceModal from './modules/DeviceModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import { filterObj } from '@/utils/util';
  import { deleteAction, getAction } from '@/api/manage'
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import DeviceModalB from './modules/DeviceModalB'


  export default {
    name: "DeviceList",
    mixins:[JeecgListMixin],
    components: {
      DeviceModalB,
      JEllipsis,
      DeviceModal
    },
    data () {
      return {
        description: '设备表管理页面',
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
            dataIndex: 'name',
            scopedSlots: {customRender: 'name'}
          },
          {
            title: '设备类型',
            align:"center",
            dataIndex: 'deviceType',
            scopedSlots: { customRender: 'deviceType' }
          },
          {
            title: 'IMEI',
            align:"center",
            dataIndex: 'deviceImei',
            scopedSlots: {customRender: 'name'}
          },
          {
            title: '设备ADD/485地址',
            align:"center",
            dataIndex: 'addressNumber',
            scopedSlots: { customRender: 'name' }
          },
          {
            title: '出厂编号',
            align:"center",
            dataIndex: 'factoryNum',
            scopedSlots: { customRender: 'name' }
          },
          {
            title: '量程',
            align:"center",
            dataIndex: 'ranges1'
          },
          {
            title: '换算系数',
            align:"center",
            dataIndex: 'reductionCoefficient'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/device/Devicelist",
          delete: "/monitor/device/delete",
          deleteBatch: "/monitor/device/deleteBatch",
          exportXlsUrl: "monitor/device/exportXls",
          importExcelUrl: "monitor/device/importExcel",
        },
        //token header
        tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
        /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
        queryParam: {customerId:this.$route.query.devId},
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
      this.initialGetOneAreaList();
      this.loadData();
      this.devid = this.$route.query.devId;
    },
    methods: {
      //获取所有的一级区域
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
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
        this.queryParam = {customerId:this.$route.query.devId}
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
        this.$refs.modalForm1.edit(record);
        this.$refs.modalForm1.title = "编辑";
        this.$refs.modalForm1.disableSubmit = false;
      },
      handleDetail:function(record){
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title="详情";
        this.$refs.modalForm.disableSubmit = true;
      },
      handleAdd: function (oneareaid,twoareaid,custid) {
        this.$refs.modalForm1.add(oneareaid,twoareaid,custid);
        this.$refs.modalForm1.title = "新增";
        this.$refs.modalForm1.disableSubmit = false;
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
      /* 导入 */
      handleImportExcel(info){
        if (info.file.status !== 'uploading') {
          /* console.log(info.file, info.fileList);*/
        }
        if (info.file.status === 'done') {
          if (info.file.response.success) {
            // this.$message.success(`${info.file.name} 文件上传成功`);
            if (info.file.response.code === 201) {
              let { message, result: { msg, fileUrl, fileName } } = info.file.response
              let href = window._CONFIG['domianURL'] + fileUrl
              this.$warning({
                  title: message,
                  content: (
                    <div>
                    <span>{msg}</span><br/>
                    <span>具体详情请 <a href={href} target="_blank" download={fileName}>点击下载</a> </span>
                </div>
              )
            })
            } else {
              this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`)
            }
            this.loadData()
          } else {
            this.$message.error(`${info.file.name} ${info.file.response.message}.`);
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`文件上传失败: ${info.file.msg} `);
        }
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