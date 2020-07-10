<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.customerName"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="8">
            <a-form-item label="区域名称">
              <a-input placeholder="区域名称" v-model="queryParam.areaName"></a-input>
            </a-form-item>
          </a-col>



          <a-col :md="10" :sm="18">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button @click="addDeviceArea" type="primary" icon="plus" style="margin-left: 8px">添加</a-button>

              <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
           <a-button type="primary" icon="import" style="margin-left: 8px">Excle导入设备</a-button>
      </a-upload>
      <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls('设备表')">下载模板</a-button>
      <a-button type="primary" icon="download" style="margin-left: 8px" @click="dataExportXls('设备列表')">导出</a-button>-->

              <a-dropdown v-if="selectedRowKeys.length > 0">
                <a-button @click="batchDel" type="primary" icon="delete" style="margin-left: 8px">批量删除</a-button>
              </a-dropdown>
              <a @click="handleToggleSearch" style="margin-left: 8px;display: none;">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
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
          <a @click="toEdit(record)">编辑</a>
          <a-divider type="vertical" />
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- 表单区域 -->
    <device-area-modal ref="modalForm" @ok="modalFormOk"></device-area-modal>
  </a-card>
</template>

<script>
  import DeviceAreaModal from './modules/DeviceAreaModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  import { deleteAction, getAction,downFile } from '@/api/manage'

  export default {
    name: "DeviceAreaList",
    mixins:[JeecgListMixin],
    components: {
      DeviceAreaModal,
      JEllipsis,
    },
    data () {
      return {
        description: '设备区域管理页面',
        // 表头
        getOneArea:[], //获取所有的一级区域
        getTwoArea:[],//获取所有的二级区域
        columns: [
          {
            title: '客户名称',
            align:"center",
            dataIndex: 'customerName',
			      width:"200px"/* ,
            scopedSlots: {customRender: 'name'} */
          },
          {
            title: '区域名称',
            align: "center",
            dataIndex: 'areaName',
            width: "200px"/* ,
            scopedSlots: {customRender: 'name'} */
          },
          {
            title: '创建时间',
            align: "center",
            dataIndex: 'createTime',
            width: "200px"/* ,
            scopedSlots: {customRender: 'name'} */
          },
          {
            title: '更新时间',
            align: "center",
            dataIndex: 'updateTime',
            width: "200px"/* ,
            scopedSlots: {customRender: 'name'} */
          },

          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/deviceArea/list",
          delete: "/monitor/deviceArea/delete",
          deleteBatch: "/monitor/deviceArea/deleteBatch",
          exportXlsUrl: "monitor/deviceArea/exportXls",
          importExcelUrl: "monitor/deviceArea/importExcel",
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
      addDeviceArea() {
        this.$refs.modalForm.customerAdd(this.$route.query);
        this.$refs.modalForm.title = "新增";
        this.$refs.modalForm.disableSubmit = false;
      },
      toEdit(r) {
        this.$refs.modalForm.edit(r);
        this.$refs.modalForm.title = "编辑";
        this.$refs.modalForm.disableSubmit = false;
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>