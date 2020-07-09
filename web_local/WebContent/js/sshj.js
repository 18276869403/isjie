var user = JSON.parse(localStorage.getItem("user"));
var timestamps, timestamps_nother = null;
var isAdmin = localStorage.getItem("isAdmin");
var guster = null;
var suliang = 0;
var name = localStorage.getItem("selcommay");
var deviceId = getQueryString("deviceId");
var activeDevice;

if (isAdmin == 1) {
	if (localStorage.getItem("addressobj") != null) {
		var address = localStorage.getItem("addressobj")
		var selfaddress = JSON.parse(address)
		// console.log(selfaddress)
	}
}

$(function () {
	if(deviceId != null && deviceId != ""){
		$("#endTime").val(new Date().Format('yyyy-MM-dd hh:mm:ss'));
		$("#startTime").val(new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000).Format('yyyy-MM-dd hh:mm:ss'));
		$(".singleDevice").hide();
		$(".excelExport").hide();
	}else{
		$("#testtime").hide();
		$("#testtime2").hide();
		$('.singleDeviceText').hide();
	}
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
	if (deviceId != null && deviceId != "") {
		singlesb()
		currentsb_7day();
		currentsb_1day();
		setInterval((r) => {
			pagelimit(parseInt($("#currentpage").text()));
			currentsb_7day();
			currentsb_1day();
		}, 5 * 1000);
	} else {
		pagelimit(1)
		var khValue = $("#khlist").val();
		if ($("#khlist").val() == null)
			khValue = '';
		baojing_7day(khValue)
		baojing_1day(khValue)
		setInterval((r) => {
			pagelimit(parseInt($("#currentpage").text()));
			var khValue = $("#khlist").val();
			if ($("#khlist").val() == null)
				khValue = '';
			baojing_7day(khValue)
			baojing_1day(khValue)
		}, 5 * 1000);
	}
	province_popup();
	
	$(".table1").on("click", ".analytics-img", function(){
// var data = [];
// $(".analytics-cb:checked").each(function(){
// data.push($(this).attr("data"));
// });
// data.push($(this).attr("data"));
// analyticsDeviceIds = data;
		activeDevice = $(this);
		
		// window.location.href = "analytics.html?dids=" + data.join(",");
		layui.use('layer', function() {
	        var layer = layui.layer;
	        layer.open({
	            id : "analyticsLayer",
	            type : 1,
	            title : false,
	            shade : 0,
	            btn : false,
	            offset : 'tc',
	            area:["1200px", "700px"],
	            closeBtn : "关闭",
	            anim : 2,
	            content : getChartHtml(),
	            success : function(layero, index) {
	            	setLayerStyle(index);
	            }
	        });
	    });
	});
})
function getChartHtml(){
	return `<div id="analyticsChart" style="padding: 10px;">
	<div class="chaxun" style="padding:5px 50px"> 
		楼层名称：<span id="floorName"></span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备类型：<span id="deviceTypeName"></span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备编号：<span id="deviceNumber"></span>
	</div>
 	<div id="testTimeChart" class="date" style="padding:5px 10px 5px 40px;float:left">
		<span class="fl">采样时间&nbsp;</span>
		<input autocomplete="off" style="color: #fff;border:1px solid #fff;width: 140px;font-size: 14px;padding:0px 5px;background: gray;" disabled type="text" class="fl" id="analyticsStartTime">
		<div class="fl" style="margin: 0 5px;">—</div>
		<input autocomplete="off" style="color: #fff;border:1px solid #fff;width: 140px; font-size: 14px;padding:0px 5px;background: gray;" disabled type="text" class="fl" id="analyticsEndTime">
		<input id="currentTime" checked type="checkbox" style="color:white; font-size: 14px;    width: 40px; height: 20px; margin-top: 5px" onclick="switchTimeModel(this)"/>24小时实时刷新
		<!--
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		刷新间隔&nbsp;&nbsp;
		<select id="timeUnit" style="background:#263d66">
			<option value='30'>30秒</option>
			<option value='45'>45秒</option>
			<option value='60'>1分钟</option>
			<option value='300'>5分钟</option>
		</select>
		-->
	</div>
	<div class="chaxun fl" style="margin-top:8px">
		<button onclick="queryAnalytics()" style="color:white; font-size: 14px;">查询</button>
	</div>
	<div id="analyticsChartTip" style="margin:10px 0px 0px 30px;float:left;color:#00c783">
		查询中...
	</div>
 </div>
 <div id='deviceAnalyticsChart' style="height:600px;margin-top:20px">
 </div>
 `;
}
function switchTimeModel(ck){
if(ck.checked){
	$("#analyticsStartTime,#analyticsEndTime")
		.attr("disabled", true)
		.css({
			"background" : "gray"
		});
	setDefaultTime();
	queryAnalytics();
} else {
	$("#analyticsStartTime,#analyticsEndTime")
		.removeAttr("disabled")
		.css({
			"background" : "transparent"
		});
	lastDataId = -1;
}
}
function setDefaultTime(){
$("#analyticsEndTime").val(new Date().Format('yyyy-MM-dd hh:mm:ss'));
$("#analyticsStartTime").val(new Date(new Date().getTime() - 1 * 24 * 60 * 60 * 1000).Format('yyyy-MM-dd hh:mm:ss'));
}
function setLayerStyle(index){
layer.style(index, {
	"background": "#263d67"
    ,"color": "white"
    ,"opacity": "0.99"
});
$("#analyticsEndTime").fdatepicker({
	format: 'yyyy-mm-dd hh:ii:ss',
	pickTime: true
}).on("changeDate", function(){
	setTimeout(() => {
		$("#analyticsEndTime").val($("#analyticsEndTime").val().substring(0, 17) + "00");
	}, 200);
});
$("#analyticsStartTime").fdatepicker({
	format: 'yyyy-mm-dd hh:ii:ss',
	pickTime: true 
}).on("changeDate", function(){
	setTimeout(() => {
		$("#analyticsStartTime").val($("#analyticsStartTime").val().substring(0, 17) + "00");
	}, 200);
});
$("#deviceNumber").html(activeDevice.attr("deviceNum"));
$("#deviceTypeName").html(activeDevice.attr("deviceType"));
$("#floorName").html(activeDevice.attr("floorName"));


analyticsChart = echarts.init(document.getElementById('deviceAnalyticsChart'));
// setTimeout(() => {
	setDefaultTime();
	queryAnalytics();
	startAutoTimer();
// }, 500);
}
var analyticsLoading = false;
var timeInt;
var autoTimerInv;
var lastDataId;
var analyticsChart;
function startAutoTimer(){
autoTimerInv = setInterval(() => {
	if($("#currentTime").prop("checked")){
		setDefaultTime();
		queryAnalytics();
	}
}, 10 * 1000);
}
function queryAnalytics() {
if(analyticsLoading){
	return;
}

var bd = $("#analyticsStartTime").val();    
var ed = $("#analyticsEndTime").val();    
bd     = bd.replace(/-/g,'/'); 
ed     = ed.replace(/-/g,'/'); 
if(new Date(ed).getTime() - new Date(bd).getTime() > 3 * 24 * 60 * 60 * 1000){
	return alert("暂不支持大于3天的时间段");
}

analyticsLoading = true;
// 加载层-风格4
// var loadLIndex = layer.msg('加载中', {
// icon: 16
// ,shade: 0.01
// ,time : -1
// });

// 实时更新结束时间
// if($("#currentTime").prop("checked")){
// $("#analyticsEndTime").val(new Date().Format('yyyy-MM-dd hh:mm:ss'));
// }


$("#analyticsChartTip").html("查询中...");
var deviceId = activeDevice.attr("data");
var req = {
	dids : deviceId,
	startTime : $("#analyticsStartTime").val(),
	endTime   : $("#analyticsEndTime").val()
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
// layer.close(loadLIndex);
		showChart(data, analyticsChart);
		analyticsLoading = false;
		
// if(!timeInt){
// timeInt = setInterval(() => {
// queryAnalytics();
// }, $("#timeUnit").val() * 1000);
// }
	},
	error: function () {
		layer.close(loadLIndex);
		analyticsLoading = false;
		// console.log("请求失败");
	} 
});
}
function showChart(data, myChart){
// myChart.clear();
if(!data.result || data.result.length == 0 ) {
	myChart.clear();
	$("#analyticsChartTip").html("该时间段内无采样数据");
	return;
} else {
	$("#analyticsChartTip").html("");
}
if($("#currentTime").prop("checked") 
		&& lastDataId == data.result[data.result.length - 1].id){
	return;// 实时刷新，且数据未变动
}
lastDataId = data.result[data.result.length - 1].id;
var times = [];
var datas = [];
$(data.result).each(function(i, d){
	times.push(d.acquisitionTime);
	datas.push(d.testPv);
});
var option = {
    xAxis: {
        type: 'category',
        data: times,
        axisLabel: {
			textStyle: {
				color: '#a2a5b0'
			}
		}
    },
    yAxis: {
        type: 'value',
        axisLabel: {
			textStyle: {
				color: '#a2a5b0'
			}
		},
		max : activeDevice.attr("maxVal") * 1
    },
    series: [{
		name: '数值',
        data: datas,
        type: 'line'
    }],
    tooltip: {
		trigger: 'axis', // 鼠标经过提示
	},
	itemWidth: 10,  // 设置宽度
	itemHeight: 10, // 设置高度
	itemGap: 40 // 设置间距
	,color : ["#00c884"]
};
myChart.setOption(option);
}
function sure() {
	clearInterval(timestamps)
	timestamps = null
	let pagemax = parseInt($("#pageall").text())
	let topage = parseInt($("#topage").val())
	if (topage <= pagemax) {
		pagelimit(topage)
// timestamps = setInterval(function () {
// pagelimit(topage)
// }, 10000)
	}

}

function sure() {
	clearInterval(timestamps)
	timestamps = null
	let pagemax = parseInt($("#pageall").text())
	let topage = parseInt($("#topage").val())
	if (topage <= pagemax) {
		pagelimit(topage)
// timestamps = setInterval(function () {
// pagelimit(topage)
// }, 10000)
	}

}

function pagedown() {
	// console.log("清楚定时")
	clearInterval(timestamps)
	timestamps = null
	// console.log("清楚成功")
	let current_ = parseInt($("#currentpage").text())
	let pagemax = parseInt($("#pageall").text())
	// console.log(current_,pagemax);
	if (current_ < pagemax) {
		pagelimit(current_ + 1)
// timestamps = setInterval(function () {
// var khValue = $("#khlist").val();
// if ($("#khlist").val() == null)
// khValue = '';
// baojing_7day(khValue)
// baojing_1day(khValue)
// pagelimit(current_ + 1)
// }, 10000)
	}

}

function pageup() {
	// console.log("清楚定时")
	clearInterval(timestamps)
	timestamps = null
	// console.log("清楚成功")
	let current_ = parseInt($("#currentpage").text())
	if (current_ > 1) {
		pagelimit(current_ - 1)
// timestamps = setInterval(function () {
// var khValue = $("#khlist").val();
// if ($("#khlist").val() == null)
// khValue = '';
// baojing_7day(khValue)
// baojing_1day(khValue)
// pagelimit(current_ - 1)
// }, 10000)
	}

}

// daemon定义的
var actions = {
	getInputVal: function (id) {
		var val = "";
		if (isAdmin == 0)
			val = $("#search1").find("[id='" + id + "']").val();
		else
			val = $("#search2").find("[id='" + id + "']").val();
		if (val == null || val == undefined)
			return ""
		else
			return val;
	},
	getAttr: function (id) {
		var val = "";
		if (isAdmin == 0)
			val = $("#search1").find("[id='" + id + "']").find("option:selected").attr("attr");
		else
			val = $("#search2").find("[id='" + id + "']").find("option:selected").attr("attr");
		if (val == null || val == undefined)
			return ""
		else
			return val;
	}
}

function islou(id){
	let customerId = id.split(",")[0];
	let floorId = id.split(",")[1];
	let deviceId = id.split(",")[2];
	// console.log(customerId,floorId);
	// //console.log("数量",suliang);
	var req = {
		did: deviceId
	}
	$.ajax({
		url: requestConf.domain + "devicePlace/findFloorByDeviceId",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
			panduan(data);
			if (data.success && data.result.length>0) {
				// 有楼层
				localStorage.setItem("khid", customerId);
				// window.alert("11111")
				window.location.href = "louceng.html"
			} else {
				// 无楼层
				// window.alert("当前设备未布置");
				$("#popBox").css("display", "block");
				$("#popLayer").css("display", "block");
			}
		},
		error: function () {
			// console.log("请求失败");
		}
	});

	// window.alert(id);
}
function builderLouchenTd(item){
	if(!item.floorName || item.floorName == '-' || item.floorName == 'null'){
		return '<td></td>';
	} else {
		return '<td title="'+item.floorName+'"><a href="#" onclick="openLouchen(\'' 
			+ item.customerId + '\',\'' + item.floorId + '\',\'' + item.deviceId + '\')">' 
			+ item.floorName + '</td>';
	}
}
function openLouchen(customerId, floorId, deviceId){
	localStorage.setItem("khid", customerId);
	if(floorId && floorId != 'undefined'){
		localStorage.setItem("floorId", floorId);
		window.location.href = "louceng.html?id=" + floorId;
	} else {
		var req = {
            did: deviceId
        }
        $.ajax({
            url: requestConf.domain + "devicePlace/findFloorByDeviceId",
            type: "get",
            dataType: "json",
            data: req,
            beforeSend: function (request) {
                request.setRequestHeader("X-Access-Token",
                    user.token
                );
            },
            success: function (data) {
                panduan(data);
                if (data.success && data.result.length > 0) {
            		localStorage.setItem("floorId", floorId);
                    window.location.href = "louceng.html?id=" + data.result[0];
                }
            }
        });
	}
}

function pagelimit(no) {
	var req = {
		customerId: khlist,
		pageNo: no,
		pageSize: 15,
		oneAreaId: "",
		twoAreaId: "",
		userId: "",
		deviceType: deviceType,
		deviceNum: deviceNum,
// acquisitionTime_begin: startTime,
// acquisitionTime_end: endTime,
		acquisitionTime_begin : $('#startTime').val(),
		acquisitionTime_end : $('#endTime').val(),
		testAddress: $('#testAddress').val(),
		detectionTarget: $('#detectionTarget').val(),
		tstatus: $('#tstatus').val()
	}
	// alert(req.customerId)
	if (isAdmin == 0)
		req.customerId = user.customerId;
	else if (isAdmin == 1) {
		// alert(citylist)
		req.userId = user.userId;
		req.oneAreaId = prolist;
		req.twoAreaId = citylist;
		if(deviceId != null && deviceId != ""){
			req.deviceId = deviceId;
		} 
		// localStorage.getItem("addressobj").id;
		if(req.customerId==""){
			if(req.oneAreaId==""){
				if(req.twoAreaId==""){
					var address = localStorage.getItem("addressobj")
					var selfaddress = JSON.parse(address)
					// //console.log(1,selfaddress);
					if(selfaddress!=null&&selfaddress.custLogo!='000'){
						// console.log("1234");
						// console.log(selfaddress)
						req.customerId = selfaddress.id;
						// console.log(selfaddress.id)
						selfaddress.custLogo = "000"
						localStorage.setItem("addressobj", JSON.stringify(selfaddress));
					}
				}
			}
		}
	}
	$.ajax({
		url: requestConf.domain + "monitor/testRecord/queryTest",
		type: "get",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
			$("#tbodydata").empty();
			var a = parseInt(data.result.total / 15)
			$("#currentpage").text(data.result.current)
			var isAdmin = localStorage.getItem("isAdmin");
			if ((data.result.total) % 15 == 0) {
				$("#pageall").text(a)
			} else {
				// console.log(a + 1)
				$("#pageall").text(a + 1)
			}
			bindPageHtml(data.result.total, data.result.current);
			let tabledata = ""
			suliang = data.result.records.length;
			$.each(data.result.records, function (i, item) {
				if(item.acquisitionTime ==null ){
					item.acquisitionTime = "-"
				}
				let device = "", deviceType = "";
				if (item.deviceType == "1") {
					device = "探测器";
					deviceType = device;
				} else if (item.deviceType == "2") {
					device = "控制器";
					deviceType = device;
				} else {
					device = "输出模块";
					deviceType = device;
				}
				if(item.deviceModel != null && item.deviceModel != 'null'){
					device += "/" + item.deviceModel;
				}
				var ranges = formatVal(item.ranges1);
				if(ranges == ""){
					ranges = formatVal(item.ranges);
				}
				ranges = ranges.replace(".000", "");
				var testPv = formatVal(item.testPv).replace(".000", "");
				if(!isNaN(testPv)){
					testPv = (testPv * 1).toFixed(2);
				}
				tabledata += '<tr>' +
					'<td style="" title="'+item.name+'">' + item.name + '</td>' +
					builderLouchenTd(item) +
					'<td>' + device + '</td>' +
					'<td style="" title="'+item.deviceNum+'">' + item.deviceNum +'</td>' +
	                '<td style="" title="'+item.testAddress+'">' + item.testAddress + '</td>' +
					' <td style="" title="'+item.detectionTarget+'">' + formatVal(item.detectionTarget ) + '</td>' +
					'<td title="' + ranges + '">' + ranges + '</td>' +
					'<td style="" title="'+item.tstatus+'">' + item.tstatus + '</td>' +
					'<td title="' + testPv + '" onclick="toDeviceSingle(' + item.deviceId + ',\'' + deviceType + '\')"><a>' + formatVal(testPv) + '</a></td>' +
					'<td  style="" title="'+item.acquisitionTime+'">' + item.acquisitionTime + '</td>' +
					'<td>' + 
// '<input data="' + item.deviceId + '" class="analytics-cb" type="checkbox"/>'
// +
					'<img data="' + item.deviceId + 
						'" deviceNum="' + item.deviceNum + 
						'" deviceType="' + device + 
						'" floorName="' + item.floorName + 
						'" maxVal="' + item.ranges.replace(item.unit, "") + 
						'" class="analytics-img" src="img/analytics.png"/></td>' +
					'</tr>';
			})
			if (data.result.records.length < 15) {
				for (let a = 0; a < 15 - data.result.records.length; a++) {
					tabledata += '<tr>' +
						'<td style="width:300px"></td>' +
						'<td></td>' +
						'<td></td>' +
                        '<td></td>' +
						'<td></td>' +
						' <td></td>' +
						'<td></td>' +
						' <td></td>' +
						'<td></td>' +
						'<td style="width:250px"></td>' +
						'<td></td>' +
						'</tr>';
				}
			}
			$("#tbodydata").append(tabledata)
		},
		error: function () {
			// console.log("请求失败");
		}
	});
}

// 当前客户一天报警统计
function baojing_1day(id) {
	$("#chartTitle").html("当天设备报警top10");
	var req = {
		customerId: id,
		alarmDeviceType: deviceType,
		deviceNum: deviceNum,
		floorName: "",
		oneAreaId: prolist,
		twoAreaId: citylist,
		userId: ""
	}
	if (isAdmin == 0)
		req.customerId = user.customerId;
	else
		req.userId = user.userId;
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectOneDayAll",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);

		},
		success: function (data) {
			// console.log("======================")
			// console.log(data)
			// console.log("======================")

			let day1 = [];
			let x = [];
			let xi = [];
			if (data.success) {
				// console.log(111,data.result)

				$.each(data.result, function (i, item) {
					var current_ = [];
					xi.push(i);
					$.each(item, function (index, child_item) {
						if (x.length < item.length)
							x.push(child_item.days.substring(10, child_item.days.length))
							current_.push(child_item.count);
					})
					var ss = 	{
						textStyle: { // 图例文字的样式
							color: '#fff',
								fontSize: 16
						},
						data:['探测器','控制器','输出模块'],
							bottom: '-3%' , // 垂直方向位置
							icon: "circle",
							itemWidth: 10,  // 设置宽度
							itemHeight: 10, // 设置高度
							itemGap: 40 // 设置间距
					}
					day1.push({ name: i, type: "line", data: current_});
				})
				// console.log(x)
			} else {
				x = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","时"];
				day1 = [{
					name: '暂无数据',
					type: 'line',
					data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
				}];

			}

			dat1(day1, x,xi)

		},
		error: function () {
			// console.log("请求失败");
		}
	});
}

function exporttoexel_single() {
// //console.log("单设备导出")
	let current = parseInt($("#currentpage").text())
	$.ajax({
		type: "get",
// url: requestConf.domain + "monitor/testRecord/exportXlsTestAll",
		url: requestConf.domain + "monitor/testRecord/exportDeviceXls",
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);

		},
		data: {
			deviceId : getQueryString('deviceId'),
			tstatus : $('#tstatus').val(),
			acquisitionTime_begin : $('#startTime').val(),
			acquisitionTime_end : $('#endTime').val(),
			pageNo: current,
			pageSize: 15,
		},
		dataType: "json",
		error: function (data) {
			// console.log("");
		},
		success: downLoadExcel
	});

}

function exporttoexel() {
	var deviceId = getQueryString('deviceId');
	if (deviceId != null && deviceId != '') {
		exporttoexel_single()
		return;
	}
	var userId = "";
	var customerId = $("#khlist").val();
	customerId = $("#khlist").val() == null ? "" : $("#khlist").val();
	if (isAdmin == 0)
		customerId = user.customerId;
	else
		userId = user.userId;

	let current = parseInt($("#currentpage").text())
// //console.log("本页",current)
	$.ajax({
		type: "get",
		url: requestConf.domain + "monitor/testRecord/exportXlsTestAll",
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},

		data: {
			pageNo: current,
			pageSize: 15,
			userId: userId,
			acquisitionTime_begin: actions.getInputVal("startTime"),
			acquisitionTime_end: actions.getInputVal("endTime"),
			oneAreaId: $("#prolist").val(),
			twoAreaId: $("#citylist").val() == null ? "" : $("#citylist").val(),
			// customerId: $("#khlist").val() == null ? "" : $("#khlist").val(),
			customerId: customerId,
			deviceType: actions.getInputVal('deviceType'),
			deviceNum: actions.getInputVal("deviceNum"),
			floorName: "",
			testAddress: $('#testAddress').val(),
			detectionTarget: $('#detectionTarget').val(),
			tstatus: $('#tstatus').val()
		},
		dataType: "json",
		error: function (data) {
			// console.log("");
		},
		success: downLoadExcel
	});

}
function downLoadExcel(data){
	let dataResult = data.result;
	let str = "客户名称,楼层名称,设备类型/型号,设备编号/位置,设备ADD,检测目标,量程,状态,数值,采集时间\n";
	for (let i = 0; i < dataResult.length; i++) {
		if(dataResult[i].acquisitionTime ==null ){
			dataResult[i].acquisitionTime = "-"
		}
		var floor = formatVal(dataResult[i].floorName);
		var status = formatVal(dataResult[i].tstatus);
		if (status){
			status = status.replace(/,/g, "，");
		} 
		var ranges = formatVal(dataResult[i].ranges1);
		if(ranges == ""){
			ranges = formatVal(dataResult[i].ranges);
		}
		str += dataResult[i].name + '\t,' +
			floor + '\t,' +
			dataResult[i].deviceType + (dataResult[i].deviceModel && dataResult[i].deviceModel != 'null' ? ("/" + dataResult[i].deviceModel) : "") + '\t,' +
			dataResult[i].deviceNum + '\t,' +
            dataResult[i].testAddress + '\t,' +
            formatVal(dataResult[i].detectionTarget) + '\t,' +
			formatVal(ranges)  + '\t,' +
			status + '\t,' +
			formatVal(dataResult[i].testPv) + '\t,' +
			dataResult[i].acquisitionTime + '\t,'
		str += '\n';
	}
// //console.log(str)
	let uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);
	var link = document.createElement("a");
	link.href = uri;
	link.download = "实时列表 " + new Date().Format('yyyyMMdd hhmmss') + ".csv";
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}

// 当前设备1天曲线图 来自单个设备的跳转
function currentsb_1day() {
	$("#chartTitle").html("当前设备报警");
	var req = {
		deviceId : deviceId
	}
	$.ajax({
		url: requestConf.domain + "monitor/alarmRecord/selectOneDay",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
			var day1_single = [];
			var x_single = [];
			if (data.success) {
				$.each(data.result, function (i, item) {

					day1_single.push(item.count)
					x_single.push(item.days.substring(item.days.length - 3, item.days.length))

				})
			} else {
				x = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "222", "23", "24","时"];
				day1 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];

			}
			let deviceType = getQueryString("deviceType");
			dat2(day1_single, x_single, deviceType)

		},
		error: function (e) {
			console.log("请求失败", e);
		}
	});
}

// 其他报警次数统计
function baojing_7day(id) {
	// alert(deviceTypeAttr)
	var req = {
		customerId: id,
		alarmDeviceType: deviceTypeAttr,
		deviceNum: deviceNum,
		floorName: "",
		oneAreaId: prolist,
		twoAreaId: citylist,
		userId: ""
	}
	if (isAdmin == 0)
		req.customerId = user.customerId;
	else
		req.userId = user.userId
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectSevenDayAll",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);

		},
		success: function (data) {

			// console.log(data)

			let xAxis_ = []
			let sb1 = [];
			let sb2 = [];
			let sb3 = [];
			// console.log("dgdqqqq")
			// console.log(data.result)
			var this_type = "";
			$.each(data.result, function (i, item) {
				this_type = i;
				$.each(item, function (y, item_child) {
					if (xAxis_.length < item.length)
						xAxis_.push(item_child.days.substring(5, item_child.days.length))
					if (i == "type3") {
						// 输出模块
						sb3.push(item_child.count)

					} else if (i == "type2") {
						// 控制器
						sb2.push(item_child.count)

					} else if (i == "type1") {
						// 探测器
						sb1.push(item_child.count)

					}
				})
			})
			// console.log(xAxis_)
			initoption(xAxis_, sb1, sb2, sb3)

		},
		error: function () {
			// console.log("请求失败");
		}
	});
}


// 当前设备7天曲线图 来自单个设备的跳转
var deviceTypeAttr = actions.getAttr('deviceType'), deviceType = actions.getInputVal('deviceType'), deviceNum = actions.getInputVal('deviceNum')
	, startTime = actions.getInputVal('startTime'), endTime = actions.getInputVal('endTime'),
	khlist = actions.getInputVal("khlist"), prolist = actions.getInputVal("prolist"), citylist = actions.getInputVal("citylist");
function currentsb_7day() {
	var req = {
		deviceId : deviceId
	}
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectSevenDayOne",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);

		},
		success: function (data) {
			// console.log(data)
			// console.log("---------------------")
			let x_7 = [];
			let data_value = [];
			let sbtype = getQueryString("deviceType")
			// console.log("dgqqqeqe")
			// console.log(data.result)
			$.each(data.result, function (i, item) {
				// console.log(item)
				x_7.push(item.days.substring(5, item.days.length))
				// xAxis_.push(item_child.days.substring(5,
				// item_child.days.length))

				data_value.push(item.count)
			})
			initoption2(x_7, data_value, sbtype)

		},
		error: function () {
			// console.log("请求失败");
		}
	});
}

// 当前设备实时数据 来自单个设备的跳转
function singlesb() {
	var req = {
		deviceId: deviceId,
		pageNo: 1,
		pageSize: 15,
	}
	$.ajax({

		url: requestConf.domain + "monitor/testRecord/queryTest",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
// //console.log(data)
			$("#tbodydata").empty();
			var a = parseInt(data.result.total / 15)
			$("#currentpage").text(data.result.current)
			if ((data.result.total) % 15 == 0) {
				$("#pageall").text(a)
			} else {
				// console.log(a + 1)
				$("#pageall").text(a + 1)
			}
			bindPageHtml(data.result.total, data.result.current);
			if (data.success) {
				let tabledata = ""
				$.each(data.result.records, function (i, item) {
					let device = "", deviceType = "";
					if (item.deviceType == "1") {
						device = "探测器";
						deviceType = device;
					} else if (item.deviceType == "2") {
						device = "控制器";
						deviceType = device;
					} else {
						device = "输出模块"
						deviceType = device;
					}
					var ranges = formatVal(item.ranges1);
					if(ranges == ""){
						ranges = formatVal(item.ranges);
					}
					ranges = ranges.replace(".000", "");
					var testPv = formatVal(item.testPv).replace(".000", "");
					if(!isNaN(testPv)){
						testPv = (testPv * 1).toFixed(2);
					}
					if (isAdmin == 1){
						tabledata += '<tr>' +
							'<td style="" title="'+item.name+'">' + item.name + '</td>' +
							builderLouchenTd(item) +
							'<td>' + device + formatVal(item.deviceModel, "/") + '</td>' +
// '<td style="" title="'+item.deviceNum+'">' + '<a href="' + '#' + '"
// onclick=islou("' + item.customerId +','+item.floorId+ ','+item.deviceId+'")>'
// + item.deviceNum + '<a>'+ '</td>' +
							'<td style="" title="'+item.deviceNum+'">' + item.deviceNum +  '</td>' +
                            '<td style="" title="'+item.testAddress+'">' + item.testAddress + '</td>' +
							' <td style="" title="'+item.detectionTarget+'">' + formatVal(item.detectionTarget) + '</td>' +
							'<td title="' + ranges + '">' + formatVal(ranges) + '</td>' +
							'<td title="' + formatVal(item.tstatus) + '">' + formatVal(item.tstatus) + '</td>' +
							'<td title="' + testPv + '" onclick="toDeviceSingle(' + item.deviceId + ',\'' + deviceType + '\')"><a>' + testPv +  '</a></td>' +
							'<td  style="" title="'+item.acquisitionTime+'">' + item.acquisitionTime + '</td>' +
							'<td>' + 
// '<input data="' + item.deviceId + '" class="analytics-cb" type="checkbox"/>'
// +
							'<img data="' + item.deviceId + 
								'" deviceNum="' + item.deviceNum + 
								'" deviceType="' + device + 
								'" floorName="' + item.floorName + 
								'" class="analytics-img" src="img/analytics.png"/></td>' +
							'</tr>';
					}else{
						tabledata += '<tr>' +
						builderLouchenTd(item) +
							'<td>' + device + '</td>' +
// '<td style="" title="'+item.deviceNum+'">' + '<a href="' + '#' + '"
// onclick=islou("' + item.customerId +','+item.floorId+ ','+item.deviceId+'")>'
// + item.deviceNum + '<a>'+ '</td>' +
							'<td style="" title="'+item.deviceNum+'">' +  item.deviceNum +  '</td>' +
                            '<td style="" title="'+item.testAddress+'">' + item.testAddress + '</td>' +
							' <td style="" title="'+item.detectionTarget+'">' + item.detectionTarget + '</td>' +
							'<td>' + item.ranges + '</td>' +
							'<td>' + item.tstatus + '</td>' +
							'<td>' + item.testPv +  '</td>' +
							'<td style="" title="'+item.acquisitionTime+'">' + item.acquisitionTime + '</td>' +
							'<td></td>' +
							'</tr>';
					}

				})
				if (data.result.records.length < 15) {
					for (let a = 0; a < 15 - data.result.records.length; a++) {
						tabledata += '<tr>' +
							'<td style="width:300px"></td>' +
							'<td></td>' +
							'<td></td>' +
							'<td></td>' +
                            '<td></td>' +
							' <td></td>' +
							'<td></td>' +
							' <td></td>' +
							'<td></td>' +
							'<td style="width:250px"></td>' +
							'<td></td>' +
							'</tr>';
					}
				}
				$("#tbodydata").append(tabledata)
			}

		},
		error: function () {
			// console.log("请求失败");
		}
	});
}
function bindPageHtml(total, page){
        // 示例1
        $("#myPage").sPage({
            page: page,// 当前页码，必填
            total:total,// 数据总条数，必填
            pageSize:15,// 每页显示多少条数据，默认10条
            showTotal:true,// 是否显示总条数，默认关闭：false
            totalTxt:"共{total}条",// 数据总条数文字描述，{total}为占位符，默认"共{total}条"
            noData: false,// 没有数据时是否显示分页，默认false不显示，true显示第一页
            // showSkip:true,//是否显示跳页，默认关闭：false
            showPN:true,// 是否显示上下翻页，默认开启：true
            prevPage:"上一页",// 上翻页文字描述，默认“上一页”
            nextPage:"下一页",// 下翻页文字描述，默认“下一页”
            fastForward: 5,// 快进快退页数，默认0表示不开启快进快退
            backFun:function(current_){
            	// 点击分页按钮回调函数，返回当前页码
            	clearInterval(timestamps)
            	timestamps = null
        		pagelimit(current_);
// timestamps = setInterval(function () {
// var khValue = $("#khlist").val();
// if ($("#khlist").val() == null)
// khValue = '';
// baojing_7day(khValue)
// baojing_1day(khValue)
// pagelimit(current_)
// }, 10000)
            }
    });
}
function toDeviceSingle(deviceId, deviceType){
	window.location.href = "data-floor.html?deviceId=" + deviceId + "&deviceType=" + deviceType;
}