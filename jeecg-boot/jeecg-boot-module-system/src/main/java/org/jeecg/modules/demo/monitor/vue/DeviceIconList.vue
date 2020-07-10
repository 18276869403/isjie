<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="设备类型名称(探测器，控制器，输出模块)">
              <a-input placeholder="请输入设备类型名称(探测器，控制器，输出模块)" v-model="queryParam.deviceType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="图标图片">
              <a-input placeholder="请输入图标图片" v-model="queryParam.picUrl"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="图标类型(正常，维护，低/高警，故障，通讯故障)">
              <a-input placeholder="请输入图标类型(正常，维护，低/高警，故障，通讯故障)" v-model="queryParam.iconType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="备注">
              <a-input placeholder="请输入备注" v-model="queryParam.remark"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="删除状态(0-正常,1-已删除)">
              <a-input placeholder="请输入删除状态(0-正常,1-已删除)" v-model="queryParam.delFlag"></a-input>
            </a-form-item>
          </a-col>
          </template>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
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
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('设备图标表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <deviceIcon-modal ref="modalForm" @ok="modalFormOk"></deviceIcon-modal>
  </a-card>
</template>

<script>
  import DeviceIconModal from './modules/DeviceIconModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "DeviceIconList",
    mixins:[JeecgListMixin],
    components: {
      DeviceIconModal
    },
    data () {
      return {
        description: '设备图标表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
           },
		   {
            title: '设备类型名称(探测器，控制器，输出模块)',
            align:"center",
            dataIndex: 'deviceType'
           },
		   {
            title: '图标图片',
            align:"center",
            dataIndex: 'picUrl'
           },
		   {
            title: '图标类型(正常，维护，低/高警，故障，通讯故障)',
            align:"center",
            dataIndex: 'iconType'
           },
		   {
            title: '备注',
            align:"center",
            dataIndex: 'remark'
           },
		   {
            title: '删除状态(0-正常,1-已删除)',
            align:"center",
            dataIndex: 'delFlag'
           },
		   {
            title: '备用字段1',
            align:"center",
            dataIndex: 'backup1'
           },
		   {
            title: '备用字段2',
            align:"center",
            dataIndex: 'backup2'
           },
		   {
            title: '备用字段3',
            align:"center",
            dataIndex: 'backup3'
           },
		   {
            title: 'BackUp4',
            align:"center",
            dataIndex: 'backup4'
           },
		   {
            title: '备用字段5',
            align:"center",
            dataIndex: 'backup5'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/monitor/deviceIcon/list",
          delete: "/monitor/deviceIcon/delete",
          deleteBatch: "/monitor/deviceIcon/deleteBatch",
          exportXlsUrl: "monitor/deviceIcon/exportXls",
          importExcelUrl: "monitor/deviceIcon/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
     
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>