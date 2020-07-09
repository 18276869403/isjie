<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="用户">
              <a-input placeholder="请输入用户Id" v-model="queryParam.userId"></a-input>
            </a-form-item>
          </a-col>
         <!-- <a-col :md="6" :sm="8">
            <a-form-item label="oneId">
              <a-input placeholder="请输入oneId" v-model="queryParam.oneId"></a-input>
            </a-form-item>
          </a-col>-->
        <!--  <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="twoId">
                <a-input placeholder="请输入twoId" v-model="queryParam.twoId"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="客户Id">
                <a-input placeholder="请输入客户Id" v-model="queryParam.customerId"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="删除状态(0-正常,1-已删除)">
                <a-input placeholder="请输入删除状态(0-正常,1-已删除)" v-model="queryParam.delFlag"></a-input>
              </a-form-item>
            </a-col>
          </template>-->
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                 <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">新增</a-button>

            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">

    <!--  <a-button type="primary" icon="download" @click="handleExportXls('用户分配表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
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
    <distribution-modal ref="modalForm" @ok="modalFormOk"></distribution-modal>
  </a-card>
</template>

<script>
  import DistributionModal from './modules/DistributionModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "DistributionList",
    mixins:[JeecgListMixin],
    components: {
      DistributionModal
    },
    data () {
      return {
        description: '用户分配表管理页面',
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
            title: '项目名称',
            align:"center",
            dataIndex: 'projectName'
          },

          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/distribution/Distributionlist",
          delete: "/monitor/distribution/delete",
          deleteBatch: "/monitor/distribution/deleteBatch",
          exportXlsUrl: "monitor/distribution/exportXls",
          importExcelUrl: "monitor/distribution/importExcel",
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