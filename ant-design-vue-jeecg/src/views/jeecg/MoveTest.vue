<template>
	<div>
		<a-row>
			<a-col id="col" class="right-con" :span="18">
				<div id="bjt" ref="testDivDom" style="position: relative;width: 99%;height: 840px;overflow: hidden;">
					<img
						id="timg"
						:src="picUrl"
						@mousedown="dragImg"
						ref="dragImgDom"
						@load="getWidth($event)"
						@mousewheel="fnWheel"
						style="width: 100%;position: absolute;"
					/>
					<div>
						<span v-for="item in XYs" v-bind="item.id">
							<a-modal title="Title" :visible="visible" @ok="handleOk1(deleteid)" @cancel="handleCancel1">
								<p>是否删除此图标</p>
							</a-modal>
							<a-row v-if="item.xposition != null && item.xposition != ''" style="position:absolute;">
								<img
									:title="item.deviceNum"
									class="deviceImg"
									id="app"
									ref="tubiao"
									:src="src + item.picUrl"
									@click="getImg()"
									:name="item.xyAxisDTO.id"
									@mousedown="PointMouseDown"
									@contextmenu.prevent="showModal1($event, item.xyAxisDTO.id)"
									:initY="item.xyAxisDTO.yaxis"
									:initBl="picbeilv"
									:style="{
										top: item.xyAxisDTO.yaxis * picbeilv + curY - 40 + 'px',
										left: item.xyAxisDTO.xaxis * picbeilv + curX - 20 + 'px',
										position: 'absolute',
										width: '40px',
										height: '40px',
										'z-index': '100'
									}"
								/>
								<!--
                    <div :id="item.xyAxisDTO.id" :name="item.deviceNum"
                      :title="item.deviceNum"
                      :style="{
                        top:(item.xyAxisDTO.yaxis*picbeilv+curY)+'px',
                        left:(item.xyAxisDTO.xaxis*picbeilv+curX-10)+'px',
                        position:'absolute',
                        color:'#000',
                        background:'#FFEC8B',
                        opacity: 0.7,
                        width: '25px',
                        height:'18px',
                        'text-align': 'center',
                        'z-index' : 101
                      }"
                      class="deviceNum">
                      <span :id = "item.deviceNum">{{item.deviceNum}}</span>
                </div>-->
							</a-row>
							<a-row v-if="item.xposition == null">
								<img
									:src="item.picUrdeviceNuml"
									@contextmenu.prevent="showModal1"
									:id="item.xyAxisDTO.id"
									v-show="false"
									:name="item.xyAxisDTO.id"
									@mousedown="PointMouseDown"
									:style="{
										top: item.xyAxisDTO.yaxis * beilv + 'px',
										left: item.xyAxisDTO.xaxis * beilv + 'px',
										position: 'absolute',
										width: '40px',
										height: '40px'
									}"
								/>
								<div
									v-show="false"
									:id="item.xyAxisDTO.id"
									:title="item.deviceNum"
									:style="{
										top: item.xyAxisDTO.yaxis * beilv + 40 + 'px',
										left: item.xyAxisDTO.xaxis * beilv - 14 + 'px',
										position: 'absolute',
										color: '#000',
										background: '#FFEC8B',
										width: '20px'
									}"
									class="deviceNum"
								>
									{{ item.deviceNum }}
								</div>
							</a-row>
						</span>
					</div>
				</div>
			</a-col>
			<a-col class="right-con" :span="6">
				<div style="background:#001529; color:#FFF;height: 840px">
					<br />
					<div style="margin-left: 20px">
						<p style="font-size: 15px">客户名：{{ cusName }}</p>

						<!--            <a-form-item label="客户名称">-->

						<!--            </a-form-item>-->

						<!--            <button onclick="" style="font-size: 15px">查找：{{123}}</button>-->

						<p style="font-size: 15px; margin-top: 10px ">
							楼层名：
							<select style=" width: 133px;background-color: #4a4a48 " @change="handleChange($event)">
								<option
									style="background-color: #4a4a48"
									v-for="(item, index) in options"
									:key="index.toString()"
									v-if="item.id == paramF"
									selected
									:value="item.id"
									>{{ item.floorName }}</option
								>
								<option
									style="background-color: #4a4a48"
									v-for="(item, index) in options"
									:key="index.toString()"
									v-if="item.id != paramF"
									:value="item.id"
									>{{ item.floorName }}</option
								>
							</select>
							<a-button type="primary" @click="saveButton" style="margin-left: 20px;height: 35px;"
								>保存</a-button
							>
						</p>

						<a-row>
							<a-col span="13">
								<a-input id="searchId" style="width: 90%" placeholder="请输入设备编号"></a-input>
							</a-col>
							<a-col span="4">
								<a-button
									type="primary"
									@click="searchBydeviceNum()"
									style="margin-left: 10px height: 35px;"
									>查找</a-button
								>
							</a-col>
							<a-col span="7"></a-col>
						</a-row>
					</div>
					<br />
					<div style="height:600px;margin-left: 20px;margin-right: 20px">
						<table
							style="background-color: #02172b;text-align: center;width:100%;border-color: white;table-layout: fixed"
							border="1"
						>
							<tr style="height: 40px;">
								<th style="width: 100px;color:#FFF;">设备类型</th>
								<th style="width: 100px;color:#FFF">设备编号</th>
								<th style="width: 100px;color:#FFF">操作</th>
							</tr>
							<tr v-for="item in currentPageData" style="height: 40px">
								<td :style="{ color: trColor(item) == null ? '#FFF' : trColor(item) }">
									{{ item.deviceType }}
								</td>
								<td
									:style="{ color: trColor(item) == null ? '#FFF' : trColor(item) }"
									style="white-space: nowrap;
                           overflow: hidden;
                            text-overflow: ellipsis;"
									:title="item.deviceNum"
								>
									{{ item.deviceNum }}
								</td>
								<td style="color:#000000">
									<button
										type="primary"
										@click="addOneDevice(item.id)"
										:disabled="toStatus(item) == null ? null : toStatus(item)"
									>
										添加
									</button>
								</td>
							</tr>
						</table>
					</div>
					<footer style="margin-left: 25%;margin-top: 20px;">
						<button @click="prevPage()" style="color: #1890ff">上一页</button>
						<span style="margin-left: 10px;">第{{ currentPage }}页/共{{ totalPage }}页</span>
						<button @click="nextPage()" style="color: #1890ff;margin-left: 10px;">下一页</button>
					</footer>
				</div>
			</a-col>
		</a-row>
	</div>
</template>

<script>
import { getAction } from '@api/manage'
import { postAction } from '../../api/manage'
import ACol from 'ant-design-vue/es/grid/Col'

export default {
	name: 'movetest',
	components: { ACol },
	data() {
		return {
			toStatus(item) {
				let XYlist = this.XYs
				for (let i = 0; i < XYlist.length; i++) {
					if (XYlist[i].id == item.id) {
						if (XYlist[i].xposition != null && XYlist[i].xposition != '') {
							return 'diasabled'
						} else {
							return null
						}
					}
				}
			},
			trColor: function(item) {
				let XYlist = this.XYs
				for (let i = 0; i < XYlist.length; i++) {
					if (XYlist[i].id == item.id) {
						if (XYlist[i].xposition != null && XYlist[i].xposition != '') {
							return 'red'
						} else {
							return null
						}
					}
				}
			},
			imgback: new Image(),
			deleteid: '',
			cusName: '',
			picUrl: '',
			src: window._CONFIG['domianURL'] + '/sys/common/view/',
			picbeilv: 1,
			picbeilv1: 1,
			picWidth: 0,
			picHeight: 0,
			ModalText: '是否删除此图标',
			visible: false,
			confirmLoading: false,
			options: [],
			imgSmall: [],

			url: {
				device: '/devicePlace/findDevicePlaceByFloorPlanId',
				devicefloor: '/devicePlace/findDeviceAndFloorPlanByFId',
				saveDevicePosition: '/devicePlace/saveDeviceXY',
				findDevice: '/devicePlace/findDeviceByCid',
				savedeviceFloor: '/devicePlace/saveDeviceFloor'
			},
			columns: [
				{ title: '设备类型', dataIndex: 'deviceType' },
				{ title: '设备编号', dataIndex: 'deviceNum' },
				{
					title: '操作',
					scopedSlots: { customRender: 'action' }
				}
			],
			map: new Map(),
			param: 0,
			paramF: 0,
			XYs: [],
			XYs1: [],
			NewXYs: [],
			xyAxisDTO: {
				id: null,
				xaxis: null,
				yaxis: null
			},
			datas: [],
			// productList, //所有数据
			totalPage: 1, // 统共页数，默认为1
			currentPage: 1, //当前页数 ，默认为1
			pageSize: 14, // 每页显示数量
			currentPageData: [], //当前页显示内容
			beilv: 1,
			mouseLeft: 0,
			mouseTop: 0,
			curX: 0,
			curY: 0,
			dragFlag: false,
			wheelFlag: null,
			oldWidth: 0,
			oldHeight: 0,
			scaleX: 1,
			scaleY: 1,
			newWidth: 0,
			newHeight: 0,
			x: 0,
			y: 0,
			bgX: 0,
			bgY: 0,
			ww: null,
			wh: null,
			imgw: null,
			imgh: null,
			scaleSize: null,
			testDivDom: null,
			dragImgDom: null,
			w: null,
			h: null,
			i: null
		}
	},
	mounted() {
		// console.log(this.datas.length+'1231456');

		document.oncontextmenu = function() {
			window.event.returnValue = false
			return false
		}
		document.getElementById('timg').style.backgroundRepeat = 'no-repeat'
		document.getElementById('timg').style.backgroundSize = 'cover'
		this.paramF = this.$route.query.floorId
		this.cusName = this.$route.query.custName
		this.customerId = this.$route.query.customerId
		// this.flag1 = false;
		this.testDivDom = this.$refs.testDivDom //背景上层div
		this.dragImgDom = this.$refs.dragImgDom //背景div
		//获得对应宽高
		this.ww = parseInt(this.testDivDom.style.width)
		this.wh = parseInt(this.testDivDom.style.height)
		this.imgw = parseInt(this.dragImgDom.style.width)
		this.imgh = parseInt(this.dragImgDom.style.height)
		this.dragImgDom.style.width = this.w + 'px'
		this.dragImgDom.style.height = this.h + 'px'
		this.dragImgDom.style.left = '0px'
		this.dragImgDom.style.top = '0px'
		this.deviceFloor()
	},

	methods: {
		searchBydeviceNum: function() {
			let ss = []
			let yy = []
			let deviceNum = document.getElementById('searchId').value
			// let ss = document.getElementById(deviceNum);
			ss = document.getElementsByName(deviceNum)
			yy = document.getElementsByClassName('deviceImg')
			// ss.style.color="#FF2115";
			// console.log("下方图标",ss.length)
			for (var i = 0; i < yy.length; i++) {
				let num = yy[i].getAttribute('title')
				if (deviceNum != '' && num.indexOf(deviceNum) > -1) {
					yy[i].className += ' img-animation'
				} else {
					yy[i].className = yy[i].className.replace('img-animation', '')
				}
			}
		},
		// 设置当前页面数据，对数组操作的截取规则为[0~9],[10~20]...,
		// 当currentPage为1时，我们显示(0*pageSize+1)-1*pageSize，当currentPage为2时，我们显示(1*pageSize+1)-2*pageSize...
		getCurrentPageData() {
			let begin = (this.currentPage - 1) * this.pageSize
			let end = this.currentPage * this.pageSize
			this.currentPageData = this.datas.slice(begin, end)
			console.log(this.currentPageData + '3214121')
		},
		//上一页
		prevPage() {
			console.log(this.currentPage)
			if (this.currentPage == 1) {
				return false
			} else {
				this.currentPage--
				this.getCurrentPageData()
			}
		},
		// 下一页
		nextPage() {
			console.log(this.currentPage + '333')
			if (this.currentPage == this.totalPage) {
				return false
			} else {
				this.currentPage++
				this.getCurrentPageData()
			}
		},
		getImg(deviceType, status) {
			yy = document.getElementsByClassName('deviceImg')
            yy.className.replace('img-animation', '')

		},
		//删除坐标
		showModal1(event, id) {
			this.visible = true
			this.deleteid = id
			event.stopPropagation()
		},
		handleOk1(id) {
			console.log('删除id', id)
			this.confirmLoading = true
			let axy = {
				id: id,
				xaxis: null,
				xaxis: null
			}
			this.$message.success('删除成功', 1)
			postAction('/devicePlace/saveDeviceXY', axy).then(res => {})
			let ii = id
			if (this.NewXYs.length <= 0) {
			} else {
				this.NewXYs.forEach(item => {
					if (item.id == ii) {
						item.xaxis = null
						item.yaxis = null
					}
				})
			}

			this.visible = false
			if (this.NewXYs.length > 0) {
				this.saveDevicePosition()
			}
			this.deviceFloor()
		},
		handleCancel1(e) {
			this.visible = false
			if (this.NewXYs.length > 0) {
				this.saveDevicePosition()
			}
		},
		//添加坐标
		addOneDevice(id) {
			let flag = false
			let left = this.picWidth * 0.01
			console.log(this.curX + 40 * this.beilv, this.curY + 40 * this.beilv)

			this.XYs.forEach(item => {
				if (item.id == id) {
					console.log('123222222')
					let newxy = {
						id: item.xyAxisDTO.id,
						//xaxis: 45/this.beilv,
						//yaxis: 80/this.beilv
						xaxis: (this.picWidth * this.picbeilv) / 2,
						yaxis: (this.picWidth * this.picbeilv) / 3.5,
						cid: this.customerId
					}
					postAction(this.url.saveDevicePosition, newxy).then(res => {})
					if (this.NewXYs.length > 0) {
						this.saveDevicePosition()
					}
					this.deviceFloor()
					flag = true
				}
			})
			if (flag) {
				return
			} else {
				console.log('添加数据')
				postAction(this.url.savedeviceFloor, {
					did: id,
					fid: this.paramF,
					//xaxis: 45 / this.beilv,
					//yaxis: 80 / this.beilv,
					xaxis: (this.picWidth * this.picbeilv) / 2,
					yaxis: (this.picWidth * this.picbeilv) / 3.5,
					cid: this.customerId
				})
				this.deviceFloor()
			}
		},
		getDeviceNum(e) {
			var img = e.target
		},
		handleChange(event) {
			let fid = event.target.value
			this.paramF = fid
			getAction(this.url.findDevice, { Fid: fid }).then(res => {
				this.datas = res.result
			})
			getAction(this.url.device, { id: this.param, Fid: fid }).then(reslt => {
				//查出对应x轴y轴坐标
				this.XYs = reslt.result.deviceDTOS
				if (this.XYs.length > 0) {
					this.XYs.forEach(device => {
						getAction('/devicePlace/getPicByDeviceType', { type: device.deviceType }).then(res => {
							res.result.forEach(item => {
								let ss = ''
								ss = device.statusType
								if (ss == '低警' || ss == '高警') {
									if (item.iconType == '报警' && device.picUrl == null) {
										device.picUrl = item.iconPic
										// device.picUrl =  window._CONFIG['domianURL'] + '/sys/common/view/'+item.iconPic;
										// this.map.set(device.deviceType+device.statusType,device.picUrl)
									}
								}

								if (item.iconType == device.statusType) {
									if (device.picUrl == null) {
										device.picUrl = window._CONFIG['domianURL'] + '/sys/common/view/' + item.iconPic
										// this.map.set(device.deviceType+device.statusType,device.picUrl)
									}
								}
							})
						})
					})
				}

				this.options = reslt.result.floorPlanDTOS
				//得到背景图片
				this.options.forEach(item => {
					if (item.id == this.paramF) {
						this.picUrl = window._CONFIG['domianURL'] + '/sys/common/view/' + item.planPic
						document.getElementById('timg').style.backgroundImage = 'url(' + this.picUrl + ')'
					}
				})
			})
		},
		deviceFloor() {
			if (this.paramF == 0) {
				this.paramF = this.$route.query.floorId
			}
			var ss = '123456'
			getAction(this.url.findDevice, { Fid: this.paramF }).then(res => {
				this.datas = res.result
				// 计算一共有几页
				this.totalPage = Math.ceil(this.datas.length / this.pageSize)
				// 计算得0时设置为1
				this.totalPage = this.totalPage == 0 ? 1 : this.totalPage
				this.getCurrentPageData()
			})
			getAction(this.url.device, { id: this.param, Fid: this.paramF }).then(reslt => {
				//查出对应x轴y轴坐标
				this.XYs = reslt.result.deviceDTOS
				this.XYs.forEach(device => {
					getAction('/devicePlace/getPicByDeviceType', { type: device.deviceType }).then(res => {
						res.result.forEach(item => {
							let ss = ''
							ss = device.statusType
							if (ss == '低警' || ss == '高警') {
								if (item.iconType == '报警') {
									device.picUrl = item.iconPic
									//device.picUrl =  window._CONFIG['domianURL'] + '/sys/common/view/'+item.iconPic;
									// this.map.set(device.deviceType+device.statusType,device.picUrl)
								}
							}
							if (item.iconType == device.statusType) {
								if (device.picUrl == null) {
									device.picUrl = window._CONFIG['domianURL'] + '/sys/common/view/' + item.iconPic
									// this.map.set(device.deviceType+device.statusType,device.picUrl)
								}
							}
						})
					})
				})
				console.log(this.XYs)
				var ss = new Map()
				this.options = reslt.result.floorPlanDTOS
				this.options.forEach(item => {
					if (item.id == this.paramF) {
						this.picUrl = window._CONFIG['domianURL'] + '/sys/common/view/' + item.planPic
					}
				})
			})
		},
		saveButton() {
			this.$message.success('保存成功', 1)
			this.saveDevicePosition()
		},
		PointMouseDown(e) {
			//图标移动
			this.move(e)
			//阻止事件冒泡
			e.stopPropagation()
		},
		move(e) {
			let odiv = e.target //获取目标元素
			odiv.className = odiv.className.replace('img-animation', '')
			//算出鼠标相对元素的位置
			let disX = e.clientX - odiv.offsetLeft
			let disY = e.clientY - odiv.offsetTop
			document.onmousemove = e => {
				//鼠标按下并移动的事件
				//用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
				let left = e.clientX - disX
				let top = e.clientY - disY
				if (top < 0) {
					top = 0
				}
				if (left < 0) {
					left = 0
				}
				//绑定元素位置到positionX和positionY上面
				this.xyAxisDTO.id = odiv.name
				var ss = parseInt(this.picbeilv)
				this.xyAxisDTO.xaxis = Math.round((left + 20 - this.curX) * this.picbeilv1)
				this.xyAxisDTO.yaxis = Math.round((top + 40 - this.curY) * this.picbeilv1)
				// //移动当前元素
				odiv.style.left = left + 'px'
				odiv.style.top = top + 'px'
				//移动文字
				let ww = document.getElementById(odiv.name)
				if (ww) {
					ww.style.left = left + 10 + 'px'
					ww.style.top = top + 40 + 'px'
				}
			}
			document.onmouseup = e => {
				console.log('移动完成')

				//往修改数组添加元素
				let flag = -1
				let array = this.NewXYs
				let id1 = this.xyAxisDTO.id
				//判断是否重复
				array.forEach(item => {
					let ss = 0
					if (item.id == id1) {
						flag = ss
					}
					ss = ss + 1
				})
				let xya = {
					id: this.xyAxisDTO.id,
					xaxis: this.xyAxisDTO.xaxis,
					yaxis: this.xyAxisDTO.yaxis
				}
				this.xyAxisDTO = {
					id: 0,
					xaxis: null,
					yaxis: null
				}

				this.XYs.forEach(item => {
					if (item.xyAxisDTO.id == xya.id) {
						item.xyAxisDTO.xaxis = xya.xaxis
						item.xyAxisDTO.yaxis = xya.yaxis
					}
				})
				if (flag < 0) {
					this.NewXYs.push(xya)
				} else {
					this.NewXYs[flag] = xya
				}
				flag = -1
				this.saveDevicePosition()
				document.onmousemove = null
				document.onmouseup = null
			}
		},
		getOffset(o) {
			var left = 0
			var top = 0
			while (o != null && o !== document.body) {
				top += o.offsetTop
				left += o.offsetLeft
				o = o.offsetParent
			}
			return {
				left: left,
				top: top
			}
		},
		dragImg: function(e) {
			//拖动图片
			this.dragFlag = true //更改状态
			this.$refs.dragImgDom.offsetWidth
			this.$refs.dragImgDom.offsetHeight
			this.mouseLeft = e.clientX - parseInt(this.$refs.dragImgDom.offsetLeft)
			this.mouseTop = e.clientY - parseInt(this.$refs.dragImgDom.offsetTop)
			document.onmousemove = e => {
				//背景图片按下并移动的事件
				if (this.dragFlag) {
					this.curX = e.clientX - this.mouseLeft
					this.curY = e.clientY - this.mouseTop
					if (this.curX > 0) {
						this.curX = 0
					}
					if (this.curY > 0) {
						this.curY = 0
					}
					this.$refs.dragImgDom.style.left = this.curX + 'px'
					this.$refs.dragImgDom.style.top = this.curY + 'px'
				}
			}
			document.onmouseup = () => {
				this.dragFlag = false
			}
		},

		getWidth(e) {
			var ss = e.target
			this.picWidth = ss.naturalWidth
			this.picHeight = ss.naturalHeight
			this.picbeilv = ss.width / ss.naturalWidth
			this.picbeilv1 = ss.naturalWidth / ss.width
		},
		fnWheel: function(e) {
			// 思路：以鼠标为中心实现滚动的话，那么将会鼠标在背景图片（注意我这里是用背景图片的，不是img的）中滚动的时候的位置比率是不会变的
			if (e.wheelDelta > 0) {
				this.wheelFlag = true
			} else {
				this.wheelFlag = false
			}
			this.oldWidth = this.$refs.dragImgDom.offsetWidth
			this.oldHeight = this.$refs.dragImgDom.offsetHeight
			this.mouseLeft = e.clientX - this.$refs.dragImgDom.offsetLeft - parseInt(this.$refs.testDivDom.offsetLeft)
			this.mouseTop = e.clientY - this.$refs.dragImgDom.offsetTop - parseInt(this.$refs.testDivDom.offsetTop)
			// 分别计算出scaleX,scaleY的倍数
			this.scaleX = this.mouseLeft / this.oldWidth
			this.scaleY = this.mouseTop / this.oldHeight
			let widthsign = this.oldWidth - this.mouseLeft
			let heightsign = this.oldHeight - this.mouseTop
			let imgs = document.getElementsByTagName('img')
			// let ww = document.getElementById()
			//放大图片
			if (this.wheelFlag) {
				//图片边界限定
				if (this.beilv < 6) {
					//this.beilv += 0.05
					//this.$refs.dragImgDom.style.width  = this.$refs.dragImgDom.offsetWidth * 1.05 + 'px'
					//this.$refs.dragImgDom.style.height = this.$refs.dragImgDom.offsetHeight * 1.05 + 'px'
					//移动图标
					this.positionX = this.positionX * 1.05
					this.positionY = this.positionY * 1.05

					this.picbeilv += 0.05
					this.$refs.dragImgDom.style.width = this.picWidth * this.picbeilv + 'px' //this.$refs.dragImgDom.offsetWidth * 1.05 + 'px'
					this.$refs.dragImgDom.style.height = this.picHeight * this.picbeilv + 'px' //this.$refs.dragImgDom.offsetHeight * 1.05 + 'px'
				}
			} else {
				//图片缩小
				if (this.beilv > 0.75) {
					if (widthsign > 0) {
						if (heightsign > 0) {
							//this.beilv -= 0.05
							//this.$refs.dragImgDom.style.width = this.$refs.dragImgDom.offsetWidth * 0.95 + 'px'
							//this.$refs.dragImgDom.style.height = this.$refs.dragImgDom.offsetHeight * 0.95 + 'px'

							this.picbeilv -= 0.05
							this.$refs.dragImgDom.style.width = this.picWidth * this.picbeilv + 'px' //this.$refs.dragImgDom.offsetWidth * 1.05 + 'px'
							this.$refs.dragImgDom.style.height = this.picHeight * this.picbeilv + 'px' //this.$refs.dragImgDom.offsetHeight * 1.05 + 'px'
						}
					}
				}
			}

			this.picbeilv = this.$refs.dragImgDom.offsetWidth / this.picWidth
			console.log('缩放比例', this.picbeilv)
			console.log('curY', this.curY)
			this.picbeilv1 = this.picWidth / this.$refs.dragImgDom.offsetWidth
		},
		//保存图标位置
		saveDevicePosition() {
			let that = this
			for (let i = 0; i < this.NewXYs.length; i++) {
				postAction(this.url.saveDevicePosition, this.NewXYs[i]).then(res => {})
			}
			this.NewXYs = []
			this.deviceFloor()
		}
	},
	created() {}
}
</script>
<style scoped>
.div_img {
	background: url('./img/pingmiantu.jpg') no-repeat;
	/*object-fit: contain;*/
	background-size: contain;
	/*background-origin: padding-box;*/
}
</style>
<style lang="less" scoped>
@import './row.less';
.deviceNum {
	width: 100px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	text-align: center;
}
.img-animation {
	animation: rotate 0.5s linear infinite;
}
@keyframes rotate {
	0% {
		transform: translateY(0);
	}

	25% {
		transform: translateY(10px);
	}

	50% {
		transform: translateY(20px) scale(1.1, 0.9);
	}

	75% {
		ransform: translateY(10px);
	}

	100% {
		transform: translateY(0);
	}
}
</style>

