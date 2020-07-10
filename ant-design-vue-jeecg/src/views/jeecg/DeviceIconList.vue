<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="设备类型">
              <a-select v-model="queryParam.deviceType" placeholder="请选择设备类型">
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="探测器">探测器</a-select-option>
                <a-select-option value="控制器">控制器</a-select-option>
                <a-select-option value="输出模块">输出模块</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="图标类型">
              <a-select v-model="queryParam.iconType" placeholder="请选择图标类型">
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="正常">正常</a-select-option>
                <a-select-option value="维护">维护</a-select-option>
                <a-select-option value="报警">报警</a-select-option>
                <a-select-option value="故障">故障</a-select-option>
                <a-select-option value="通讯故障">通讯故障</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                 <a-button @click="handleAdd" type="primary" icon="plus"  style="margin-left: 8px">添加图标</a-button>
             <a-dropdown v-if="selectedRowKeys.length > 0">
                 <a-button @click="batchDel" type="primary" icon="delete"  style="margin-left: 8px">批量删除</a-button>
              </a-dropdown>
                </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->
<!--    </div>-->

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
        <span slot="remark1" slot-scope="text">
          <j-ellipsis :value="text" :length="10" />
        </span>

        <!--        &lt;!&ndash;报警短信接收1-接受，2-拒绝&ndash;&gt;-->
        <!--        <span slot="iconType" slot-scope="text,record">-->
        <!--            <span v-if="record.iconType=='高警'">报警</span>-->
        <!--            <span v-if="record.iconType=='低警'">报警</span>-->
        <!--            <span v-if="record.iconType=='正常'">正常</span>-->
        <!--            <span v-if="record.iconType=='维护'">维护</span>-->
        <!--            <span v-if="record.iconType=='通讯故障'">通讯故障</span>-->
        <!--            <span v-if="record.iconType=='故障'">故障</span>-->
        <!--            <span v-if="record.iconType=='主电欠压'">故障</span>-->
        <!--            <span v-if="record.iconType=='备电欠压'">故障</span>-->
        <!--        </span>-->

        <template slot="iconlot" slot-scope="text,record,index">
          <div class="anty-img-wrap">

            <a @click="() => getUrlNewView(record.iconPic)">
              <a-avatar shape="square" :src="getAvatarView(record.iconPic)"/>
            </a>
          </div>
        </template>

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
    <deviceIcon-modal ref="modalForm" @ok="modalFormOk"></deviceIcon-modal>
  </a-card>
</template>

<script>
  import DeviceIconModal from './modules/DeviceIconModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '../../components/jeecg/JDate'
  import JEllipsis from '../../components/jeecg/JEllipsis'
  export default {
    name: "DeviceIconList",
    mixins:[JeecgListMixin],
    components: {
      JEllipsis,
      DeviceIconModal,
      JDate
    },
    data () {
      return {
        description: '设备图标表管理页面',
        // 表头
        columns: [

          {
            title: '设备类型',
            align:"center",
            dataIndex: 'deviceType',
          },

          {
            title: '图标类型',
            align:"center",
            dataIndex: 'iconType',
            // scopedSlots: { customRender: 'iconType' }
          },
          {
            title: '图标',
            align:"center",
            dataIndex: 'iconPic',
            scopedSlots: { customRender: 'iconlot' }
          },
          {
            title: '备注',
            align:"center",
            dataIndex: 'remark',
            width:280,
            scopedSlots: {customRender: 'remark1'}
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
          imgerver: window._CONFIG['domianURL'] + '/sys/common/view'
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getAvatarView: function(iconPic) {
        return this.url.imgerver + '/' + iconPic
      },
      //获取url打开新页面
      getUrlNewView(record){
        let config = window._CONFIG['domianURL'] + "/sys/common/view";
        window.open(config+"/"+record, '_blank')
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>