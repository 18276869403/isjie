<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="4" :sm="8">
            <a-form-item label="省份">
              <a-select v-model="queryParam.oneAreaName" placeholder="请选择省份">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                >{{oneArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item label="城市">
              <a-input placeholder="请输入城市" v-model="queryParam.twoAreaName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
               <a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px">新增</a-button>
              <a-dropdown v-if="selectedRowKeys.length > 0">
              <a-button @click="batchDel" type="primary" icon="delete" style="margin-left: 8px">批量删除</a-button>
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

        <span slot="action" slot-scope="text, record">
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
    <area-modal1 ref="modalForm" @ok="modalFormOk"></area-modal1>
  </a-card>
</template>

<script>
  import AreaModal1 from './modules/AreaModal1'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getOneAreaAll} from '@/api/api'

  export default {
    name: "AreaList1",
    mixins:[JeecgListMixin],
    components: {
      AreaModal1
    },
    data () {
      return {
        description: '省市表管理页面',
        getOneArea:[], //获取所有的一级区域
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
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/monitor/area/queryAreaVoAll",
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
    created () {
      this.initialGetOneAreaList();
    },
    methods: {
      //获取所有的省份
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            console.log(res.message)
          }
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>