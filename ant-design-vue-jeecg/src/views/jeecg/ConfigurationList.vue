<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
               <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">配置</a-button>
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

        <!-- 字符串超长截取省略号显示 -->
        <span slot="backup1" slot-scope="text">
          <j-ellipsis :value="text" :length="10"/>
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>



          <!--
            <a-divider type="vertical" />
            a-popconfirm title="确认删除吗 ?" @confirm="() => handleDelete(record.id)">
           <a>删除</a>
          </a-popconfirm>-->
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <configuration-modal ref="modalForm" @ok="modalFormOk"></configuration-modal>
  </a-card>
</template>

<script>
  import ConfigurationModal from './modules/ConfigurationModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JInput from '../../components/jeecg/JInput'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  export default {
    name: "ConfigurationList",
    mixins:[JeecgListMixin],
    components: {
      JInput,
      JEllipsis,
      ConfigurationModal
    },
    data () {
      return {
        description: '系统配置',
        // 表头
        columns: [
          {
            title: '数据保留x月',
            align:"center",
            dataIndex: 'monitorDataMonth',
            width:'380px',
          },
          {
            title: '报警保留x月',
            align:"center",
            width:'380px',
            dataIndex: 'alarmDataMonth',
            scopedSlots: { customRender: 'backup1' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/conf/list",
          delete: "/monitor/conf/delete",
          deleteBatch: "/monitor/conf/deleteBatch",
          exportXlsUrl: "monitor/conf/exportXls",
          importExcelUrl: "monitor/conf/importExcel",
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