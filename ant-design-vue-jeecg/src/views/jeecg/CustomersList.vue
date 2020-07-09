<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="4" :sm="8">
            <a-form-item label="省份">
              <a-select v-model="queryParam.oneAreaName" placeholder="请选择省份">
                <a-select-option value @click="initialOneAreaList">全部</a-select-option>
                <a-select-option
                  v-for="(oneArea,oneAreaindex) in getOneArea"
                  :key="oneAreaindex.toString()"
                  :value="oneArea.id"
                  @click="initialGetTwoAreaList(oneArea.id)"
                >{{oneArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item label="城市">
              <a-select v-model="queryParam.twoAreaName" placeholder="请选择城市">
                <a-select-option value>全部</a-select-option>
                <a-select-option
                  v-for="(twoArea,twoAreaindex) in getTwoArea"
                  :key="twoAreaindex.toString()"
                  :value="twoArea.id"
                >{{twoArea.areaName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="8">
            <a-form-item label="客户名称">
              <a-input placeholder="请输入客户名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button
                type="primary"
                @click="searchReset"
                icon="reload"
                style="margin-left: 8px"
              >重置</a-button>
              <a-button @click="handleAdds" type="primary" icon="plus" style="margin-left: 8px">新增客户</a-button>
              <a-dropdown v-if="selectedRowKeys.length > 0">
                <a-button
                  @click="batchDel"
                  type="primary"
                  icon="delete"
                  style="margin-left: 8px"
                >批量删除</a-button>
              </a-dropdown>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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
        @change="handleTableChange"
      >
        <!-- 客户名字超长截取省略号显示 -->
        <span slot="customerName" slot-scope="text">
          <j-ellipsis :value="text" :length="6" />
        </span>

        <!-- 项目名称字符串超长截取省略号显示 -->
        <span slot="projectName" slot-scope="text">
          <j-ellipsis :value="text" :length="6" />
        </span>

        <!-- 备注字符串超长截取省略号显示 -->
        <span slot="detailedAddress1" slot-scope="text">
          <j-ellipsis :value="text" :length="6" />
        </span>
        <!--客户logo-->
        <span slot="logo" slot-scope="text, record">
          <a v-if="record.custLogo==null"></a>
          <a v-else @click="() => getUrlNewView(record.custLogo)">{{text.slice(27)}}</a>
        </span>

        <!--标注logo-->
        <span slot="logo2" slot-scope="text, record">
          <a v-if="record.biaoZhuLogo==null"></a>
          <a v-else @click="() => getUrlNewView(record.biaoZhuLogo)">{{text.slice(27)}}</a>
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail1(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="handleEdits(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
          <!-- <a-divider type="vertical" />
          <router-link :to="{name: 'jeecg-CustomerAccountsList1', query: {custId:record.id} }">账号</router-link>-->
          <a-divider type="vertical" />
          <router-link :to="{name: 'jeecg-DeviceList1', query: {devId:record.id} }">设备</router-link>
          <a-divider type="vertical" />
          <router-link :to="{name: 'jeecg-FloorPlanList1', query: {floorId:record.id} }">图纸</router-link>
          <a-divider type="vertical" />
          <!--<a @click="deviceArea(record)">分区</a>-->
          <router-link
            :to="{name: 'jeecg-DeviceAreaList', query: {customerId:record.id, oneAreaId: record.oneAreaId, twoAreaId: record.twoAreaId} }"
          >分区</router-link>
          <router-link
            :to="{name: 'jeecg-DeviceAreaList', query: {customerId:record.id, oneAreaId: record.oneAreaId, twoAreaId: record.twoAreaId} }"
          >标注</router-link>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- 表单区域 -->
    <customers-modal ref="modalForm1" @ok="modalFormOk"></customers-modal>
    <customers-modal-b ref="modalForm" @ok="modalFormOk"></customers-modal-b>
    <device-area-modal ref="deviceAreaModal" @ok="modalFormOk"></device-area-modal>
  </a-card>
</template>

<script>
import CustomersModal from './modules/CustomersModal'
import CustomersModalB from './modules/CustomersModalB'
import DeviceAreaModal from './modules/DeviceAreaModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getOneAreaAll, getTwoAreaAll } from '@/api/api'
import JEllipsis from '../../components/jeecg/JEllipsis'
export default {
  name: 'CustomersList',
  mixins: [JeecgListMixin],
  components: {
    CustomersModal,
    JEllipsis,
    CustomersModalB,
    DeviceAreaModal
  },
  data() {
    return {
      description: '客户表管理页面',
      getOneArea: [], //获取所有的一级区域
      getTwoArea: [], //获取所有的二级区域
      // 表头
      columns: [
        {
          title: '省份',
          align: 'center',
          dataIndex: 'oneAreaName'
        },
        {
          title: '城市',
          align: 'center',
          dataIndex: 'twoAreaName'
        },
        {
          title: '客户名称',
          align: 'center',
          dataIndex: 'name',
          width: '200px' /* ,
            scopedSlots: {customRender: 'customerName'} */
        },
        {
          title: '项目名称',
          align: 'center',
          width: '120px',
          dataIndex: 'projectName' /* ,
            scopedSlots: {customRender: 'projectName'} */
        },
        {
          title: '经度',
          align: 'center',
          dataIndex: 'longitude'
        },
        {
          title: '纬度',
          align: 'center',
          dataIndex: 'latitude'
        },
        {
          title: '客户web端显示名称',
          align: 'center',
          dataIndex: 'webName'
        },
        {
          title: '添加时间',
          align: 'center',
          dataIndex: 'createTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/monitor/customers/queryCustomersVoAll',
        delete: '/monitor/customers/delete',
        deleteBatch: '/monitor/customers/deleteBatch',
        exportXlsUrl: 'monitor/customers/exportXls',
        importExcelUrl: 'monitor/customers/importExcel',
        imgerver: window._CONFIG['domianURL'] + '/sys/common/view'
      }
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  created() {
    this.initialGetOneAreaList()
  },
  methods: {
    //获取所有的省份
    initialGetOneAreaList() {
      this.getOneArea = ['']
      getOneAreaAll().then(res => {
        if (res.success) {
          this.getOneArea = res.result
        } else {
        }
      })
    },
    //获取所有的城市
    initialGetTwoAreaList(id) {
      this.getTwoArea = ['']
      getTwoAreaAll({ id: id }).then(res => {
        if (res.success) {
          this.getTwoArea = res.result
        } else {
        }
      })
    },
    //当未选择省份时，城市下拉框清空
    initialOneAreaList() {
      this.getTwoArea = ['']
      this.queryParam.twoAreaName = ''
    },
    //获取客户logo的url打开新页面
    getUrlNewView(textUrl) {
      let config = window._CONFIG['domianURL'] + '/sys/common/view'
      window.open(config + '/' + textUrl, '_blank')
    },
    deviceArea(record) {
      this.$refs.deviceAreaModal.customerAdd({
        oneAreaId: record.oneAreaId,
        twoAreaId: record.twoAreaId,
        customerId: record.id
      })
      this.$refs.deviceAreaModal.title = '新增区域'
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>