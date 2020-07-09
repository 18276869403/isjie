<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="4" :sm="8">
            <a-form-item label="省份">
              <j-input placeholder="请输入省份" v-model="queryParam.areaName"></j-input>
            </a-form-item>
          </a-col>

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

          <a-divider type="vertical" />

         <a-popconfirm title="确认删除吗，删除省份时下属城市会一起删除?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
         </a-popconfirm>

          <a-divider type="vertical"/>

          <router-link :to="{ name: 'jeecg-AreaTwoList',query:{oneid:record.id} } ">
            查看下属城市
          </router-link>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <area-modal ref="modalForm" @ok="modalFormOk"></area-modal>
  </a-card>
</template>

<script>
  import AreaModal from './modules/AreaModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JInput from '../../components/jeecg/JInput'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  export default {
    name: "AreaList",
    mixins:[JeecgListMixin],
    components: {
      JInput,
      JEllipsis,
      AreaModal
    },
    data () {
      return {
        description: '省市表管理页面',
        // 表头
        columns: [
          {
            title: '省份',
            align:"center",
            dataIndex: 'areaName',
            width:'380px',
          },
          {
            title: '备注',
            align:"center",
            width:'380px',
            dataIndex: 'backup1',
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
          list: "/monitor/area/list",
          delete: "/monitor/area/delete",
          deleteBatch: "/monitor/area/deleteBatch",
          exportXlsUrl: "monitor/area/exportXls",
          importExcelUrl: "monitor/area/importExcel",
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