var voiceList = {
		ala : {},// 报警
		bd  : {} // 故障
	},
	voice = {};
function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURI(r[2]);// unescape(r[2]);
    }
    return "";
}
// 暂存当前设备是否已经报警
var devicesAlarMap = {};
// 最后一次报警的id
var lastAlarId = 0;
// 报警弹窗状态
var layerStatus = ""; // open close
window.onload = function(){
	voiceList.ala = $("#voice")[0];
	voiceList.bd  = $("#breakdownVoice")[0];
	
	voice = voiceList.ala;
	var preDevicesAlarMap = window.localStorage.getItem('devicesAlarMap');
	if(preDevicesAlarMap){
		devicesAlarMap = JSON.parse(preDevicesAlarMap);
	} 
};
var isAdmin = localStorage.getItem("isAdmin");
// //console.log("用户登录或客户的登陆",isAdmin)
if(isAdmin == null){
    {window.location.href = "index.html"}
}
var devicesl = [];
// //console.log("000000")
// if (isAdmin == 1)
// {
// //console.log("111111111")
// window.location.href = "../index.html"}
// else
// {window.location.href = "../index_kh.html"}
// //console.log("ivshiufdhvgagsfdkjvaklsjhgfvklishgavkldbvldsf")
function panduan(data) {
    // console.log("判断",data)
    if ((data.message == "Token失效，请重新登录" || data.message =="token为空!" || data.message == "token非法无效!")) {
        alert('登陆超时，或账号在其他地方登录，请重新登陆');
    	localStorage.clear()
        window.location.href = "index.html";
    }
}
function formatTime(date) {
	if(!date){
		return "";
	}
    // //console.log("时间",date)
    var ss = date.substring(4).replace("-","").replace("-","/");
    return ss;
}

// var leibiaodevice = []
// function test01() {
// tt.play();
// voice.pause();
// }
// function loadRec() {
// //console.log("进入判断")
// // var _this = szqbl_qvocs;
// var req = {
// userId:'',
// customerId:''
// }
// if (isAdmin == 0)
// req.customerId = user.customerId;
// else if (isAdmin == 1) {
// req.userId = user.userId;
// }
// // //console.log(123,req);
// var url = requestConf.domain + "monitor/device/queryAlarmdevice";
// $.ajax({
// url: url,
// type: "get",
// dataType: "json",
// data: req,
// beforeSend: function (request) {
// request.setRequestHeader("X-Access-Token",
// user.token
// );
// },
//
// success: function (data) {
// let devices = data.result;
// if(devices == null){
// voice.pause();
// return;
// }
// var isNewDevice = false;
// var ss= 0;
// if(devices != null){
// var temp;
// devices.forEach((device)=>{
// if(device.statusType.indexOf("警") > 0){
// ss ++;
// }
// var key = device.id;
// if(!devicesAlarMap[key]){
// devicesAlarMap[key] = {};
// }
// if(devicesAlarMap[key].statusType != device.statusType){
// //console.log("新数据")
// isNewDevice = true;
// devicesAlarMap[key].statusType = device.statusType;
// }
// })
// window.localStorage.setItem('devicesAlarMap',
// JSON.stringify(devicesAlarMap));
// if(ss > 0){
// //console.log("设备报警",ss);
// temp = voiceList.ala;
// ss=0;
// } else {
// //console.log("设备故障");
// temp = voiceList.bd;
//
// }
// if(voice.pause && voice.played){
// voice.pause();
// voice = temp;
// if(alaLayerIndex){
// voice.play();
// }
//
// }
// }
// contentDataList = data.result;
// //音频即时播放
// if(isNewDevice){
// if(voiceList.ala.pause && voiceList.ala.played){
// voiceList.ala.pause();
// }
// if(voiceList.bd.pause && voiceList.bd.played){
// voiceList.bd.pause();
// }
// if(voice.play){
// voice.play();
// }
// showAlaLayer();
// }
// if (data.result.length > 0) {
// if (data.result.length > 4) {
// $("#xx1").height(26 * 4);
// $("#xx2").height(26 * 4 + 20);
// } else {
// $("#xx1").height(data.result.length * 26);
// $("#xx2").height(data.result.length * 26 + 20);
// }
// // 有报警数据 & 无弹窗，显示隐藏按钮
// if(!isNewDevice && !alaLayerIndex){
// showMinLayer();
// }
// }
// },
// error: function (data) {
// if(data.status == 500){
// alert("账号已在其他地方登陆，请重新登录");
// window.location.href = "index.html";
// return;
// }
// ////console.log("请求失败");
// }
// });
// }
function loadRec()  {
	lastAlarId  = localStorage.getItem('lastAlarId')  || 0;
	layerStatus = localStorage.getItem('layerStatus') || 'open';
	// console.log("进入判断")
	// var _this = szqbl_qvocs;
	var req = {
			userId:'',
			customerId:''
	}
	if (isAdmin == 0)
		req.customerId = user.customerId;
	else if (isAdmin == 1) {
		req.userId = user.userId;
	}
// //console.log(123,req);
	var url = requestConf.domain + "monitor/alarmRecord/toDayAlarm";
	$.ajax({
		url: url,
		type: "get",
		dataType: "json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
					user.token
			);
		},
		
		success: function (data) {
			let devices = data.records;
			contentDataList = data.records;
			if(devices == null || devices.length == 0){
				contentDataList = [];
				voice.pause();
				if(alaLayerIndex){
					layer.close(alaLayerIndex);
					alaLayerIndex = undefined;
				}
				return;
			}
			// 临时声音切换
			var voice = voiceList.bd, maxId = 0;// 故障声
			devices.forEach((device)=>{
				if(!device.id || device.id == null || device.id == 'null'){ // 兼容设备状态异常，报警表无数据的bug
					device.id = "-1";
				}
				if(device.id != lastAlarId && 
						device.alarmState && 
						device.alarmState.indexOf("警") > -1){
					voice = voiceList.ala;// 报警声
				}
				maxId = Math.max(maxId, device.id);
			});
//			var isNewDevice = lastAlarId != maxId;
			var isNewDevice = maxId > lastAlarId;
			if(!isNewDevice && layerStatus == 'close'){
				// 无新报警数据 & 无弹窗：显示隐藏按钮 关闭声音
				showMinLayer();
				voice.pause();
				return;
			}
			if(layerStatus == 'open'){ // 打开状态存储最后报警id
				lastAlarId = maxId;
				localStorage.setItem('lastAlarId', maxId);
			}
			// 弹出报警
			showAlaLayer(); 
			
			// 播放音频
			if(voiceList.ala.pause && voiceList.ala.played){
				voiceList.ala.pause();
			}
			if(voiceList.bd.pause && voiceList.bd.played){
				voiceList.bd.pause();
			}
			if(voice.play){
				voice.play();
			}
			
			
			if (data.records.length > 4) {
				$("#xx1").height(26 * 4);
				$("#xx2").height(26 * 4 + 20);
			} else {
				$("#xx1").height(data.records.length * 26);
				$("#xx2").height(data.records.length * 26 + 20);
			}
		},
		error: function (data) { 
			panduan(JSON.parse(data.responseText));
// if(data.status == 500){
// alert("账号已在其他地方登陆，请重新登录");
// window.location.href = "index.html";
// return;
// }
			// //console.log("请求失败");
		}
	});
}
var contentDataList;
var pageIndex = 1;
var alaPageSize  = 10;
var fistLoad  = true;
var alaLayerIndex;
var maxDericeCount = 0;
// function getAlaPageHtml(){
// maxDericeCount = 0;
// var alaHtml = ["<table class='alaTable'><tr
// class='a-head'><td>序号</td><td>报警时间</td><td>设备类型</td><td>编号/安装位置</td><td>报警状态</td></tr>"];
// var index = (pageIndex - 1) * alaPageSize;
// var end = index + alaPageSize;
// for(;index < end //&& index < contentDataList.length
// ; index++){
// var item = contentDataList[index] || {};
// if(item.deviceNum==undefined){
// item.deviceNum = '';
// }
// alaHtml.push('<tr>');
// alaHtml.push('<td>');
// alaHtml.push(index + 1);
// alaHtml.push('</td>');
// alaHtml.push('<td>');
// alaHtml.push(item.createTime);
// alaHtml.push('</td>');
// alaHtml.push('<td>');
// alaHtml.push(item.deviceType);
// alaHtml.push('</td>');
// alaHtml.push('<td>');
//			
// maxDericeCount = Math.max(maxDericeCount, item.deviceNum.length);
//			
// alaHtml.push('<a title="' + item.deviceNum + '" href="' + '#' + '"
// onclick=islou2("'
// + item.customerId + ','
// +item.id+','
// +item.deviceType+','
// +item.addressNumber+','
// +item.deviceImei+','
// +item.iccid+'")>' + item.deviceNum);
// alaHtml.push('</a>');
// // alaHtml.push(item.deviceNum);
// alaHtml.push('</td>');
// alaHtml.push('<td>');
// alaHtml.push(item.alarmState);
// alaHtml.push('</td>');
// alaHtml.push('</tr>');
// }
// alaHtml.push("</table>");
// return "<div id='alaDiv'>" + alaHtml.join('') + "</div>";
// }
function getAlaPageHtml(){
	maxDericeCount = 0;
	var alaHtml = ["<table class='alaTable'><tr class='a-head'><td>序号</td><td>报警时间</td><td>设备类型</td><td>编号/安装位置</td><td>报警状态</td></tr>"];
	var index   = (pageIndex - 1) * alaPageSize;
	var end     = index + alaPageSize;
	for(;index < end // && index < contentDataList.length
	; index++){
		var item = contentDataList[index] || {};
		if(item.deviceNum==undefined){
			item.deviceNum = '';
		}
		alaHtml.push('<tr>');
		alaHtml.push('<td>');
		alaHtml.push(index + 1);
		alaHtml.push('</td>');
		alaHtml.push('<td>');
		alaHtml.push(item.alarmTime);
		alaHtml.push('</td>');
		alaHtml.push('<td>');
// alaHtml.push(item.detectionTarget);
		alaHtml.push(item.tstatus);
		alaHtml.push('</td>');
		alaHtml.push('<td>');
		if(!item.alarmState){
			item.alarmState = "";
		}
		maxDericeCount = Math.max(maxDericeCount, item.deviceNum.length + item.alarmState.length);
		
		alaHtml.push('<a title="' + item.deviceNum + '" href="' + '#' + '" onclick=islou2("'
				+ item.customerId + ','
				+item.deviceId+','
				+item.detectionTarget+','
				+item.alarmAddress+','
				+item.alarmDeviceImei+','
				+item.iccid+'")>' + item.deviceNum);
		alaHtml.push('</a>');
		// alaHtml.push(item.deviceNum);
		alaHtml.push('</td>');
		alaHtml.push('<td>');
		alaHtml.push(item.alarmState);
		alaHtml.push('</td>');
		alaHtml.push('</tr>');
	}
	alaHtml.push("</table>");
	return "<div id='alaDiv'>" + alaHtml.join('') + "</div>";
}
function showMinLayer(){
	layerStatus = 'close';
	localStorage.setItem('layerStatus', 'close');
	if($('.minLayer').length > 0){
		$('.minLayer').show();
	} else {
		$('body').append('<div class="minLayer"><</div>');
		$('.minLayer').on('click', function(){
			pageIndex = 1;
			$(this).hide();
			alaLayerIndex = false;
			localStorage.removeItem('devicesAlarMap');
			localStorage.removeItem('lastAlarId');
			layerStatus = 'open';
			localStorage.setItem('layerStatus', 'open');
			showAlaLayer(true);
		});
	}
}
function showAlaLayer(){
	layerStatus = 'open';
	localStorage.setItem('layerStatus', 'open');
	if(alaLayerIndex){
		// 更新数据
    	$('#alaDiv').html(getAlaPageHtml());
    	resetLayerWidth(alaLayerIndex);
        return;// layer.close(alaLayerIndex);
	}
	layui.use('layer', function() {
        var layer = layui.layer;
        alaLayerIndex = layer.open({
            id : "alarmTip",
            type : 0,
            title : false,
            shade : 0,
            btn : ['关闭声音', '上一页', '下一页'],
            yes : function(index, layero) {
                voice.pause();
                showMinLayer();
                layer.close(alaLayerIndex);
                alaLayerIndex = undefined;
            },
            btn2 : function(index, layero) {
            	if(pageIndex == 1){
            		return false;
            	}
            	pageIndex--;
            	$('#alaDiv').html(getAlaPageHtml());
            	resetLayerWidth(alaLayerIndex);
                return false;
            },
            btn3 : function(index, layero) {
            	if(pageIndex * alaPageSize > contentDataList.length){
            		return false;
            	}
            	pageIndex++;
            	$('#alaDiv').html(getAlaPageHtml());
            	resetLayerWidth(alaLayerIndex);
                return false;
            },
            btnAlign : 'c',
            offset : 'rb',
            closeBtn : 0,
            anim : 2,
            content : getAlaPageHtml(),
            success : function(layero, index) {
            	resetLayerWidth(index);
            }
        });
    });
}
function resetLayerWidth(index){
	var autoWidth = Math.max(650, 
			(maxDericeCount * 16) + 380);
    layer.style(index, {
        marginLeft : -10
        ,"left" : "auto"
        ,"width" : autoWidth + "px"
        ,"right" :  "10px"
        ,"top" : "auto"
        ,"bottom" :  "10px"
    	,"background": "#263d67"
	    ,"color": "white"
	    ,"opacity": "0.9"
    });
    if($('#alarmTip').height() > 400){
    	$('#alarmTip').css({
    		"height" : ($('#alaDiv').height()) + "px"
    	})
// layer.style(index, {
// "height" : ($('#alaDiv').height() + 60) + "px"
// ,"width" : autoWidth + "px"
// });
    }
}

function islou2(id){
    let customerId = id.split(",")[0];
    let deviceId   = id.split(",")[1];
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
                // 有楼层
                localStorage.setItem("khid", customerId);
                // window.alert("11111")
                window.location.href = "louceng.html?id=" + data.result[0];
            } else {

                if(id.split(",").length>2){
                    // console.log("当前设备无楼层")
                    // console.log(id);
                    // tosingle1(id.split(",")[2])
// localStorage.setItem("deviceType",id.split(",")[2])
// localStorage.setItem("sbdz", id.split(",")[3])
// localStorage.setItem("testDeviceImei", id.split(",")[4])
// localStorage.setItem("iccid", id.split(",")[5])
                    window.location.href = "data-floor.html?deviceType=" + id.split(",")[2]
                    	+ "&sbdz=" + id.split(",")[3]
	                    + "&testDeviceImei=" + id.split(",")[4]
	                    + "&iccid=" + id.split(",")[5]
                    	;
                }
                // 无楼层
                // console.log("当前设备无楼层")
                // window.alert("当前设备未布置");
                // $("#popBox").css("display", "block");
                // $("#popLayer").css("display", "block");
            }
        },
        error: function () {
            console.log("请求失败");
        }
    });
}
Date.prototype.Format = function(fmt)   
{ // author: meizz
  var o = {   
    "M+" : this.getMonth()+1,                 // 月份
    "d+" : this.getDate(),                    // 日
    "h+" : this.getHours(),                   // 小时
    "m+" : this.getMinutes(),                 // 分
    "s+" : this.getSeconds(),                 // 秒
    "q+" : Math.floor((this.getMonth()+3)/3), // 季度
    "S"  : this.getMilliseconds()             // 毫秒
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}
function formatVal(val, join){
	if(!val || val == 'null' || val == 'nullnull'){
		return "";
	}
	return (join || '') + val;
}