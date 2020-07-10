<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="5" :sm="24">
            <a-form-item label="省份">
              <a-select v-model="queryParam.oneAreaName" placeholder="请选择省份"  class="input">
                <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
                <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                                 @click="initialGetTwoAreaList(oneArea.id)">{{oneArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市"  class="input">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id">{{twoArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.name"  class="input"></a-input>
            </a-form-item>
          </a-col>
           
            <a-col :md="9" :sm="10"   :labelCol="{span: 5}" :wrapperCol="{span: 18,offset: 1}">
              <a-form-item label="监测时间">
                <j-date v-model="queryParam.acquisitionTime_begin" :showTime="true" date-format="YYYY-MM-DD HH:mm:ss" style="width:45%" placeholder="起始时间"  class="input"></j-date>
                <span style="width: 10px;">~</span>
                <j-date v-model="queryParam.acquisitionTime_end" :showTime="true" date-format="YYYY-MM-DD HH:mm:ss" style="width:45%" placeholder="结束时间"></j-date>
              </a-form-item>
            </a-col>

            <a-col :md="5" :sm="8">
              <a-form-item label="设备类型" >
                <a-select v-model="queryParam.deviceType" placeholder="请选择设备类型"  class="input">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">探测器</a-select-option>
                  <a-select-option value="2">控制器</a-select-option>
                  <a-select-option value="3">输出模块</a-select-option>
                </a-select>
              </a-form-item>
            </a-col> 

          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
               <a-dropdown v-if="selectedRowKeys.length > 0">
                 <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
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

        <!-- 备注字符串超长截取省略号显示 -->
        <span slot="name" slot-scope="text">
          <j-ellipsis :value="text" :length="4" />
        </span>
        <span slot="projectName" slot-scope="text">
          <j-ellipsis :value="text" :length="4" />
        </span>
        <span slot="testDeviceImei" slot-scope="text">
          <j-ellipsis :value="text" :length="5" />
        </span>
        <span slot="iccid" slot-scope="text">
          <j-ellipsis :value="text" :length="5" />
        </span>
        <span slot="tstatus" slot-scope="text">
          <j-ellipsis :value="text" :length="2" />
        </span>

        <span slot="deviceType" slot-scope="text,record">
            <span v-if="record.deviceType==1">探测器</span>
            <span v-if="record.deviceType==2">控制器</span>
           <span v-if="record.deviceType==3">输出模块</span>
        </span>

        <span slot="action" slot-scope="text, record">
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a >删除</a>
                </a-popconfirm>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <testRecord-modal ref="modalForm" @ok="modalFormOk"></testRecord-modal>
  </a-card>
</template>

<script>
  import TestRecordModal from './modules/TestRecordModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  export default {
    name: "TestRecordList",
    mixins:[JeecgListMixin],
    components: {
      JEllipsis,
      TestRecordModal,
      JDate
    },
    data () {
      return { 
        description: '监测记录表管理页面',
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
						width:"200px"/* ,
            scopedSlots: {customRender: 'name'} */
          },
          {
            title: '项目名称',
            align:"center",
            dataIndex: 'projectName',
						width:"120px"/* ,
            scopedSlots: {customRender: 'projectName'} */
          },
          {
            title: '设备类型',
            align:"center",
			width:"80px",
            dataIndex: 'deviceType',
            scopedSlots: { customRender: 'deviceType' }
          },
          {
            title: 'IMEI',
            align:"center",
			width:"100px",
            dataIndex: 'testDeviceimei',
            scopedSlots: { customRender: 'testDeviceimei' }
          },
          {
            title: '设备ADD',
            align:"center",
			width:"80px",
            dataIndex: 'testAddress'
          },

          /* {
             title: '设备位号',
             align:"center",
             dataIndex: 'devicePositionnum'
           },*/
          {
            title: '探测目标',
            align:"center",
			width:"80px",
            dataIndex: 'detectionTarget'
          },
          {
            title: '量程',
            align:"center",
			width:"70px",
            dataIndex: 'ranges'
          },
          {
            title: '状态',
            align:"center",
						width:"120px",
            dataIndex: 'tstatus'/* ,
            scopedSlots: { customRender: 'tstatus' } */
          },
          {
            title: '浓度',
            align:"center",
            dataIndex: 'testPv',
          },
          {
            title: '监测时间',
            align:"center",
						width:"150px",
            dataIndex: 'acquisitionTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/testRecord/queryTestAll",
          delete: "/monitor/testRecord/delete",
          deleteBatch: "/monitor/testRecord/deleteBatch",
          exportXlsUrl: "monitor/testRecord/exportXls",
          importExcelUrl: "monitor/testRecord/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      onTimeChange: function(value, dateString) {
        this.queryParam.acquisitionTime_begin = dateString[0]
        this.queryParam.acquisitionTime_end = dateString[1]
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
            /*   console.log(res.message)*/
          }
        })
      },
      //当未选择一级区域时，二级区域下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.queryParam.twoAreaName="";
      },
      dateFormat(fmt, date) {
        let ret;
        const opt = {
          "Y+": date.getFullYear().toString(),        // 年
          "m+": (date.getMonth() + 1).toString(),     // 月
          "d+": date.getDate().toString(),            // 日
          "H+": date.getHours().toString(),           // 时
          "M+": date.getMinutes().toString(),         // 分
          "S+": date.getSeconds().toString()          // 秒
          // 有其他格式化字符需求可以继续添加，必须转化成字符串
        };
        for (let k in opt) {
          ret = new RegExp("(" + k + ")").exec(fmt);
          if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
          };
        };
        return fmt;
      }
    },
    created() {
      this.initialGetOneAreaList();
      this.queryParam.acquisitionTime_begin = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000)),
        this.queryParam.acquisitionTime_end = this.dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
    },
  }
</script>
<style scoped>
  .input{
    margin-left: -8px;
  }
  @import '~@assets/less/common.less'
</style>