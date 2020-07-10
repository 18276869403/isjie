<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="省份">
              <a-select v-model="queryParam.oneAreaName"  placeholder="请选择省份"  class="input">
                <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
                <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                                 @click="initialGetTwoAreaList(oneArea.id)">{{oneArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="24">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市"  class="input">
                <a-select-option value="" @click="initialTwoAreaList">全部</a-select-option>
                <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"  @click="initialGetCustomersList(twoArea.id)" >{{twoArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-select  v-model="queryParam.name"  placeholder="请选择客户名称"  class="input">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(customer,customerIndex) in getCustomers" :key="customerIndex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :md="9" :sm="12"   :labelCol="{span: 5}" :wrapperCol="{span: 18,offset: 1}">
              <a-form-item label="报警时间" >
                <j-date v-model="queryParam.alarmTime_begin" :showTime="true" date-format="YYYY-MM-DD HH:mm:ss" style="width:45%" placeholder="起始时间"  class="input"></j-date>
                <span style="width: 10px;">~</span>
                <j-date v-model="queryParam.alarmTime_end" :showTime="true" date-format="YYYY-MM-DD HH:mm:ss" style="width:45%" placeholder="结束时间"></j-date>
              </a-form-item>
            </a-col>

            <a-col :md="5" :sm="8">
              <a-form-item label="设备类型" >
                <a-select v-model="queryParam.alarmDeviceType" placeholder="请选择设备类型"  class="input">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">探测器</a-select-option>
                  <a-select-option value="2">控制器</a-select-option>
                  <a-select-option value="3">输出模块</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="7" :sm="10" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" @click="deleteAll" style="margin-left: 8px">一键清空</a-button>
              <!--<a-button type="primary" icon="download" @click="handleExportXls('报警记录表')">导出</a-button>-->
               <a-dropdown v-if="selectedRowKeys.length > 0">
                 <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
              </a-dropdown>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">
        <!-- 备注字符串超长截取省略号显示 -->
        <span slot="name" slot-scope="text">
          <j-ellipsis :value="text" :length="4" />
        </span>
        <span slot="projectName" slot-scope="text">
          <j-ellipsis :value="text" :length="4" />
        </span>
        <span slot="alarmDeviceImei" slot-scope="text">
          <j-ellipsis :value="text" :length="5" />
        </span>
        <span slot="alarmICCID" slot-scope="text">
          <j-ellipsis :value="text" :length="5" />
        </span>
        <span slot="deviceNum" slot-scope="text">
          <j-ellipsis :value="text" :length="2" />
        </span>

        <span slot="alarmDeviceType" slot-scope="text,record">
            <span v-if="record.alarmDeviceType==1">探测器</span>
            <span v-if="record.alarmDeviceType==2">控制器</span>
           <span v-if="record.alarmDeviceType==3">输出模块</span>
        </span>

        <span slot="action" slot-scope="text, record">
           <a @click="handleDetail1(record)">详情</a>
          <a-divider type="vertical" />
           <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a >删除</a>
                </a-popconfirm>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- 表单区域 -->
    <alarmRecord-modal ref="modalForm" @ok="modalFormOk"></alarmRecord-modal>
  </a-card>
</template>

<script>
  import AlarmRecordModal from './modules/AlarmRecordModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll,getTwoAreaAll,getCustomersAll} from '@/api/api'
  import JDate from '@/components/jeecg/JDate'
  import {initDictOptions, filterDictText} from '@/components/dict/JDictSelectUtil'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import { getAction } from '../../api/manage'
  export default {
    name: "AlarmRecordList",
    mixins:[JeecgListMixin],
    components: {
      AlarmRecordModal,
      JDate,
      JEllipsis
    },
    data () {
      return {
        description: '报警记录表管理页面',
        // 表头
        getOneArea:[], //获取所有的一级区域
        getTwoArea:[],//获取所有的二级区域
        getCustomers:[],//获取所有的客户
        typeDictOptions:[],
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
			width: "200px",
            dataIndex: 'name'/* ,
            scopedSlots: {customRender: 'name'} */
          },
          {
            title: '设备类型',
            align:"center",
            dataIndex: 'alarmDeviceType',
            scopedSlots: { customRender: 'alarmDeviceType' }
          },
          {
            title: 'IMEI',
						width: "120px",
            align:"center",
            dataIndex: 'alarmDeviceImei'/* ,
            scopedSlots: { customRender: 'alarmDeviceImei' } */
          },
          {
            title: '设备ADD',
            align:"center",
            dataIndex: 'alarmAddress'
          },

          {
            title: '编号/位置',
            align:"center",
            dataIndex: 'deviceNum',
            scopedSlots: { customRender: 'deviceNum' }
          },
          {
            title: '报警类型',
            align:"center",
            dataIndex: 'tstatus',
          },
          {
            title: '报警时间',
            align:"center",
			width: "150px",
            dataIndex: 'alarmTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/alarmRecord/queryAlarmVo",
          delete: "/monitor/alarmRecord/delete",
          deleteBatch: "/monitor/alarmRecord/deleteBatch",
          exportXlsUrl: "monitor/alarmRecord/exportXls",
          importExcelUrl: "monitor/alarmRecord/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created(){
      this.initialGetOneAreaList();
      this.initDictConfig();
    },
    methods: {
      deleteAll() {
        getAction("/monitor/alarmRecord/deleteAll").then((res) =>{
          console.log("删除成功")
        })
        this.loadData();
      },
      initDictConfig() {
        //初始化字典 - 类型
        initDictOptions('Alarm_state').then((res) => {
          if (res.success) {
            this.typeDictOptions = res.result;
          }
        });
      },
      //查询报警时间
      onTimeChange: function(value, dateString) {
        this.queryParam.alarmTime_begin = dateString[0]
        this.queryParam.alarmTime_end = dateString[1]
      },
      //获取所有的一级区域
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            /*  console.log(res.message)*/
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
            /*  console.log(res.message)*/
          }
        })
      },
      //当未选择省份时，城市、客户下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.twoAreaId="";
        this.getCustomers = ['']
        this.customerId="";
      },
      //获取所有的客户
      initialGetCustomersList(id){
        this.getCustomers = ['']
        getCustomersAll({id:id}).then((res) => {
          if (res.success){
            this.getCustomers = res.result
          } else {
            console.log(res.message)
          }
        })
      },
      //当未选择省份、城市时，客户下拉框清空
      initialTwoAreaList(){
        this.getCustomers = ['']
        this.customerId="";
      },

    },

  }
</script>
<style scoped>
</style>