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
								<a-select-option
									v-for="(oneArea, oneAreaindex) in getOneArea"
									:key="oneAreaindex.toString()"
									:value="oneArea.id"
									@click="initialGetTwoAreaList(oneArea.id)"
									>{{ oneArea.areaName }}</a-select-option
								>
							</a-select>
						</a-form-item>
					</a-col>

					<a-col :md="4" :sm="8">
						<a-form-item label="城市">
							<a-select v-model="queryParam.twoAreaName" placeholder="请选择城市">
								<a-select-option value="">全部</a-select-option>
								<a-select-option
									v-for="(twoArea, twoAreaindex) in getTwoArea"
									:key="twoAreaindex.toString()"
									:value="twoArea.id"
									>{{ twoArea.areaName }}</a-select-option
								>
							</a-select>
						</a-form-item>
					</a-col>

					<a-col :md="4" :sm="8">
						<a-form-item label="客户名称">
							<a-input placeholder="请输入客户名称" v-model="queryParam.name"></a-input>
						</a-form-item>
					</a-col>

					<a-col :md="4" :sm="8">
						<a-form-item label="楼层名">
							<a-input placeholder="请输入楼层名" v-model="queryParam.floorName"></a-input>
						</a-form-item>
					</a-col>
					<a-col :md="6" :sm="8">
						<span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
							<a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
							<a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
								>重置</a-button
							>
							<a-button @click="handleAdd" type="primary" icon="plus" style="margin-left: 8px"
								>新增</a-button
							>
							<a-dropdown v-if="selectedRowKeys.length > 0">
								<a-button @click="batchDel" type="primary" icon="delete" style="margin-left: 8px"
									>批量删除</a-button
								>
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
				<i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
				<a style="font-weight: 600">{{ selectedRowKeys.length }}</a
				>项
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
				:rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
				@change="handleTableChange"
			>
				<!-- 备注字符串超长截取省略号显示 -->
				<span slot="remark1" slot-scope="text">
					<j-ellipsis :value="text" :length="20" />
				</span>

				<template slot="iconlot" slot-scope="text, record, index">
					<div class="anty-img-wrap">
						<a @click="() => getUrlNewView(record.planPic)">
							<a-avatar shape="square" :src="getAvatarView(record.planPic)" />
						</a>
					</div>
				</template>

				<span slot="action" slot-scope="text, record">
					<a @click="handleEdit(record)">编辑</a>

					<a-divider type="vertical" />
					<a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
						<a>删除</a>
					</a-popconfirm>
					<a-divider type="vertical" />
					<!--          <router-link :to="{name: 'jeecg-CustomerAccountsList1', query: {custId:record.id} }">设备布点</router-link>-->
					<router-link
						:to="{
							path: 'MoveTest',
							query: { customerId: record.customerId, floorId: record.id, custName: record.name }
						}"
						>设备布点</router-link
					>
				</span>
			</a-table>
		</div>
		<!-- table区域-end -->

		<!-- 表单区域 -->
		<floorPlan-modal ref="modalForm" @ok="modalFormOk"></floorPlan-modal>
	</a-card>
</template>

<script>
import FloorPlanModal from './modules/FloorPlanModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getOneAreaAll, getTwoAreaAll } from '@/api/api'
import JEllipsis from '../../components/jeecg/JEllipsis'
export default {
	name: 'FloorPlanList',
	mixins: [JeecgListMixin],
	components: {
		JEllipsis,
		FloorPlanModal
	},
	data() {
		return {
			description: '楼层图表管理页面',
			// 表头
			getOneArea: [], //获取所有的一级区域
			getTwoArea: [], //获取所有的二级区域
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
					dataIndex: 'name'
				},
				{
					title: '序号',
					align: 'center',
					dataIndex: 'backup1'
				},
				{
					title: '楼层名',
					align: 'center',
					dataIndex: 'floorName'
				},
				{
					title: '平面图',
					align: 'center',
					dataIndex: 'planPic',
					scopedSlots: { customRender: 'iconlot' }
				},
				{
					title: '备注',
					align: 'center',
					dataIndex: 'remark',
					scopedSlots: { customRender: 'remark1' }
				},

				{
					title: '操作',
					dataIndex: 'action',
					align: 'center',
					scopedSlots: { customRender: 'action' }
				}
			],
			url: {
				list: '/monitor/floorPlan/Florlist',
				delete: '/monitor/floorPlan/delete',
				deleteBatch: '/monitor/floorPlan/deleteBatch',
				exportXlsUrl: 'monitor/floorPlan/exportXls',
				importExcelUrl: 'monitor/floorPlan/importExcel',
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
		getAvatarView: function(planPic) {
			return this.url.imgerver + '/' + planPic
		},
		//获取url打开新页面
		getUrlNewView(record) {
			let config = window._CONFIG['domianURL'] + '/sys/common/view'
			window.open(config + '/' + record, '_blank')
		},
		//获取所有的一级区域
		initialGetOneAreaList() {
			this.getOneArea = ['']
			getOneAreaAll().then(res => {
				if (res.success) {
					this.getOneArea = res.result
				} else {
				}
			})
		},
		//获取所有的二级区域
		initialGetTwoAreaList(id) {
			this.getTwoArea = ['']
			getTwoAreaAll({ id: id }).then(res => {
				if (res.success) {
					this.getTwoArea = res.result
				} else {
				}
			})
		},
		//当未选择一级区域时，二级区域下拉框清空
		initialOneAreaList() {
			this.getTwoArea = ['']
			this.queryParam.twoAreaName = ''
		}
	}
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>