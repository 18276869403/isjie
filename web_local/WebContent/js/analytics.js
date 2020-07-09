$(function () {
	$("#endTime").val(new Date().Format('yyyy-MM-dd hh:mm:ss'));
	$("#startTime").val(new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000).Format('yyyy-MM-dd hh:mm:ss'));
	$(".singleDevice").hide();
	$(".excelExport").hide();
	if (isAdmin == 1)
		$("#user").find("span[id=name]").text(user.userInfo.username)
	else {
		guster = JSON.parse(localStorage.getItem("guster"));
		$("#user").find("span[id=name]").text(user.webName)
		$("#logo").attr("src", requestConf.domain + "sys/common/view/" + user.customerLogo)
		$("#logoText").html(user.webName)
		$("#sfmr").text(user.oneAreaName)
		$("#mrcity").text(user.twoAreaName)
		$("#gsname").text(user.customerName)
	}
	queryAnalytics();
	setInterval((r) => {
		queryAnalytics();
	}, 5 * 1000);
});
function queryAnalytics() {
	var req = {
		dids : getQueryString("dids"),
		startTime : $("#startTime").val(),
		endTime   : $("#endTime").val()
	}
	$.ajax({

		url: requestConf.domain + "monitor/testRecord/queryAnalytics",
		type: "get",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);

		},
		success: function (data) {
			let x_7 = [];
			let data_value = [];
			let type = [];
			$.each(data.result, function (i, item) {
				x_7.push(item.acquisitionTime);
				data_value.push(item.testPv);
			})
			initoption2(x_7, data_value, type)

		},
		error: function () {
			//console.log("请求失败");
		}
	});
}
//当前设备7天曲线图   
function initoption2(xAxis, data_, type) {
	var seltype = ""
	let type1 = {
		name: '探测器',
		type: 'line',
		itemStyle: {
			normal: {
				color: '#3b78ff',

			},
			emphasis: {
				color: 'red',
				lineStyle: { // 系列级个性化折线样式
					width: 2,
					type: 'dotted',
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
						offset: 0,
						color: 'green'
					}, {
						offset: 1,
						color: '#3b78ff'
					}])
				}
			}
		}, //线条样式
		areaStyle: {
			normal: {
				//颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
				color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{

					offset: 0,
					color: 'rgba(49,95,199,1)'
				}, {
					offset: 0.34,
					color: 'rgba(49,95,199,0.55)'
				}, {
					offset: 1,
					color: 'rgba(49,95,199,0.0)'
				}])

			}
		}, //区域颜色渐变
		data: data_
	};
	let type2 = {
		name: '控制器',
		type: 'line',
		itemStyle: {
			normal: {
				color: '#fa5656',

			},
			emphasis: {
				color: '#008B45',
				lineStyle: { // 系列级个性化折线样式
					width: 2,
					type: 'dotted',
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
						offset: 0,
						color: '#00FFFF'
					}, {
						offset: 1,
						color: '#fa5656'
					}])
				}
			}
		}, //线条样式
		areaStyle: {
			normal: {
				//颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
				color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{

					offset: 0,
					color: 'rgba(228,81,80,1)'
				}, {
					offset: 0.34,
					color: 'rgba(228,81,80,0.55)'
				}, {
					offset: 1,
					color: 'rgba(228,81,80,0.00)'
				}])

			}
		}, //区域颜色渐变
		data: data_
	};
	let type3 = {
		name: '输出模块',
		type: 'line',
		itemStyle: {

			normal: {
				color: '#fbc82a',

			},
			emphasis: {
				color: '#6A5ACD',
				lineStyle: { // 系列级个性化折线样式
					width: 2,
					type: 'dotted',
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
						offset: 0,
						color: '#1E90FF'
					}, {
						offset: 1,
						color: '#fbc82a'
					}])
				}
			}
		}, //线条样式
		areaStyle: {
			normal: {
				//颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
				color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{

					offset: 0,
					color: 'rgba(224,180,41,1)'
				}, {
					offset: 0.34,
					color: 'rgba(224,180,41,0.35)'
				}, {
					offset: 1,
					color: 'rgba(224,180,41,0.00)'
				}])

			}
		}, //区域颜色渐变
		data: data_
	};
	if (type == "探测器") {
		seltype = type1
	} else if (type == "控制器") {
		seltype = type2
	} else {
		seltype = type3
	}
	var myChart = echarts.init(document.getElementById('callNumberLine'));
	var arrDate = xAxis;
	var option = {
		height:150,
		backgroundColor: '', //背景颜色透明
		tooltip: {
			trigger: 'axis', //鼠标经过提示
		},
		grid: {
			left: 0,
			top: '5%',
			left: 0,
			bottom: '5%',
			containLabel: true
		},
		legend:{
			textStyle: { //图例文字的样式
				color: '#fff',
				fontSize: 16
			},
			data:['探测器','控制器','输出模块'],
			icon: "circle",
			bottom:'-3%',
			itemWidth: 10,  // 设置宽度
			itemHeight: 10, // 设置高度
			itemGap: 40 // 设置间距
		},
		xAxis: {
			type: 'category',
			name:'',

			nameTextStyle:{
				color:"white",
				fontSize: 16
			},
			data: arrDate,
			color:['#fff'],
			show: true, //隐藏x轴
			axisLabel: {
				interval: 0,
				fontSize:25,
				textStyle: {
					color: 'rgba(162,165,176,0)'
				}
			}
		},
		yAxis: {
			type: 'value',
			name:'',
			splitLine: {
				show: false,

			}, //去除网格线
			show: true, //隐藏y轴,
			axisLabel: {
				textStyle: {
					color: '#a2a5b0'
				}
			}
		},
		// color: ['#1DB0B8', '#37C6C0', '#D0E9FF', '#c7353a', '#f5b91e'],
		series: [seltype]
	};
	myChart.setOption(option);
} 