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
                                 @click="initialGetTwoAreaList(oneArea.id)">{{oneArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id">{{twoArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>


          <template>
            <a-col :md="4" :sm="8">
              <a-form-item label="设备IMEI">
                <a-input placeholder="请输入IMEI" v-model="queryParam.deviceImei"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="8">
              <a-form-item label="设备类型">
                <a-select v-model="queryParam.deviceType" placeholder="请选择设备类型" class="input">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">探测器</a-select-option>
                  <a-select-option value="2">控制器</a-select-option>
                  <a-select-option value="3">输出模块</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="8">
              <a-form-item label="设备编号">
                <a-input placeholder="设备编号" v-model="queryParam.deviceNum"></a-input>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="10" :sm="18" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <a-button @click="handleAdds" type="primary" icon="plus"  style="margin-left: 8px">添加设备</a-button>
             <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
                   <a-button type="primary" icon="import" style="margin-left: 8px">Excle导入设备</a-button>
              </a-upload>
              <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls('设备表')">下载模板</a-button>
			  <a-button type="primary" icon="download" style="margin-left: 8px" @click="dataExportXls('设备列表')">导出</a-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
          <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
         </a-dropdown>
          <a @click="handleToggleSearch" style="margin-left: 8px;display: none;">
             {{ toggleSearchStatus ? '收起' : '展开' }}
              <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
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
      <span slot="deviceType" slot-scope="text,record">
            <span v-if="record.deviceType==1">探测器</span>
            <span v-if="record.deviceType==2">控制器</span>
           <span v-if="record.deviceType==3">输出模块</span>
        </span>

        <!-- 客户字符串超长截取省略号显示 -->
        <span slot="name" slot-scope="text">
          <j-ellipsis :value="text" :length="15" />
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail1(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="handleEdits(record)">编辑</a>
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
    <device-modal-b ref="modalForm" @ok="modalFormOk1"></device-modal-b>
  </a-card>
</template>

<script>
  import DeviceModal from './modules/DeviceModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import DeviceModalB from './modules/DeviceModalB'
  import { deleteAction, getAction,downFile } from '@/api/manage'

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
			width:"80px",
            dataIndex: 'oneAreaName'
          },
          {
            title: '城市',
            align:"center",
			width:"80px",
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
            title: '区域',
            align: "center",
            dataIndex: 'deviceAreaName'
          },
          {
            title: '设备类型',
            align:"center",
            width: "80px",
            dataIndex: 'deviceType',
            scopedSlots: { customRender: 'deviceType' }
          },
          {
            title: 'IMEI',
            align:"center",
            width: "120px",
            dataIndex: 'deviceImei',
            scopedSlots: {customRender: 'name'}
          },

          {
            title: '设备编号',
            align: "center",
            dataIndex: 'deviceNum',
            scopedSlots: { customRender: 'name' }
          },

          {
            title: '设备ADD',
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
          // {
          //   title: '单位',
          //   align:"center",
          //   dataIndex: 'unit'
          // },
          {
            title: '换算系数',
            width: "80px",
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
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created() {
      this.initialGetOneAreaList()
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
      dataExportXls(){
        let param = this.queryParam;
		let table = this.$refs.table;
        if(table.selectedRowKeys && table.selectedRowKeys.length>0){
          param['selections'] = table.selectedRowKeys.join(",")
        } 
        //console.log("导出参数",param)
        //let paramsStr = JSON.stringify(filterObj(param));
        let paramsStr = JSON.stringify(param);
        downFile(this.url.exportXlsUrl, {
          paramsStr: paramsStr,
          exportData: true
        }).then((data) => {
          if (!data) {
            this.$message.warning("文件下载失败")
            return
          }
          if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(new Blob([data]), this.description+'.xls')
          }else{
            let url = window.URL.createObjectURL(new Blob([data]))
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', this.description+'.xls')
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link); //下载完成移除元素
            window.URL.revokeObjectURL(url); //释放掉blob对象
          }
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>