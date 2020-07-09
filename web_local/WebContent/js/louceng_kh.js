var guster = JSON.parse(localStorage.getItem("guster"));
 $("#user_l").find("span[id=name_l]").text(guster.webName)
var state = false;
var timestamp,timestamp1=null;
var imgurl = requestConf.domain + 'sys/common/view/';
$(function() {
	selectDeviceCount()
	getloucengname()
	timestamp=setInterval(function(){
		selectDeviceCount()
	},10000)
})

function selectDeviceCount() {

	var req = {
		customerId: guster.customerId,

	}
	$.ajax({

		url: requestConf.domain + "monitor/device/selectDeviceCount",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		success: function(data) {

			if (data.success) {

				$("#allcount").text(data.result.deviceCount)
				$("#normalcount").text(data.result.deviceNormalCount)
				$("#whsb").text(data.result.deviceDefendCount)
				$("#gzsb").text(data.result.deviceFaultCount)
				$("#txgz").text(data.result.deviceCommFailCount)
				$("#bjsb").text(data.result.deviceAlertCount)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
//楼层的名称
function getloucengname() {
	var req = {
		cid: guster.customerId,
	}
	$.ajax({

		url: requestConf.domain + "monitor/floorPlan/queryFloorNameAndIdByCid",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		success: function(data) {
			console.log(data)
			if (data.success) {
				let louceng = "";
                
				$.each(data.result, function(y, item) {



					if (y == 0) {
						 devinlouceng(item.id);
						console.log("into")
						$("#planPic").css("background-image", "url('" + imgurl + item.planPic + "')")
					}
					louceng += '<tr>' +
						'<td id="'+item.id+'">' + item.floorName + '</td>' +
						'<td style="color:#6de7ff" onclick=query("' + item.id + '")>查看</td>' +
						'</tr>';
				})
				$("#loucengall").find("td").css("color","")
				$("#loucengall").find("td[id="+data.result[0].id+"]").css("color","#e31c14")
				timestamp1=setInterval(function(){
					query(data.result[0].id)
				},10000)
				$("#loucengall").append(louceng)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}

function query(id) {
	$("#loucengall").find("td").css("color","")
	$("#loucengall").find("td[id="+id+"]").css("color","#e31c14")
	//更新右边的视图
	devinlouceng(id);
	console.log(id)
	var req = {
		customerId: guster.customerId,
		floorId: id
	}
	$.ajax({

		url: requestConf.domain + "monitor/device/selectCountDeviceByFlor",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		success: function(data) {
			console.log(data.result)
			if (data.success) {

				$("#self_all").text(data.result.deviceCount)
				$("#self_normal").text(data.result.deviceNormalCount)
				$("#self_whsb").text(data.result.deviceDefendCount)
				$("#self_gzsb").text(data.result.deviceFaultCount)
				$("#self_txgz").text(data.result.deviceCommFailCount)
				$("#self_bjsb").text(data.result.deviceAlertCount)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
//中间显示的楼层设备
function devinlouceng(id) {
	var req = {
		fid: id,

	}
	$.ajax({

		url: requestConf.domain + "monitor/floorPlan/queryFloorDeviceByFid",
		type: "get",
		// contentType: "application/json",
		dataType: "json",
		data: req,
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		success: function(data) {
			$("#louceng_dev").empty()
			console.log(data.result)
			//更换楼层图纸
			$("#planPic").css("background-image", "url('" + imgurl + data.result.picurl + "')")
			console.log(data.result.deviceInfoDTOS)
			if (data.success) {
				let sbinlouceng = ""
				let px = "px"
				// position: absolute;top: 50px;left: 30px;
				$.each(data.result.deviceInfoDTOS, function(y, item) {
					let aaa = JSON.stringify(item)
					var jcval=item.testPv==0? "-":item.testPv
					// sbinlouceng += '<img id="'+item.id+'" onclick=tosingle(' + aaa + ') onmouseout=out(out)  onmouseover=sbck(' + aaa + ') style="top:' + item.yposition + px + ';left:' + item.xposition +
					// 	px + '" class="sbinlouceng" src="' + imgurl + item.picUrl + '" >'
					sbinlouceng+='<div style="top:' + (item.yposition-45) + px + ';left:' + item.xposition +
					 	px + '" class="sbinlouceng">'+jcval+'</div>'+
						'<img  style="top:' + (item.yposition-30) + px + ';left:' + item.xposition +
					 	px + '" src="./img/tips_sanjiao.png" class="sbinloucengimg tipssanjiao">'+
						'<img id="'+item.id+'" onclick=tosingle(' + aaa + ') onmouseout=out(out)  onmouseover=sbck(' + aaa + ') style="top:' + item.yposition + px + ';left:' + item.xposition +
					 	px + '" class="sbinloucengimg" src="' + imgurl + item.picUrl + '" >'
				})
				$("#louceng_dev").append(sbinlouceng)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}

//鼠标移除,设备窗口消失
function out(){
	console.log("out")
$("#dev_popuo").css("display","none")
}

//设备上弹出的窗口  鼠标悬浮在上面
function sbck(obj) {
	console.log("in")
	console.log("in")
	$("#dev_popuo").css("display","block")
	$("#dev_popuo").css("top",parseInt(obj.yposition)-140+"px")
	$("#dev_popuo").css("left",parseInt(obj.xposition)-60+"px")
	$("#dev_popuo").find("span[id='deviceImei']").text(obj.deviceImei)
	$("#dev_popuo").find("span[id='ranges']").text(obj.ranges)
	$("#dev_popuo").find("span[id='unit']").text(obj.unit)
	$("#dev_popuo").find("span[id='unit1']").text(obj.unit)
	$("#dev_popuo").find("span[id='deviceType']").text(obj.deviceType)
	
	
	
	
	var parm = {

		addressNumber: obj.addressNumber,
		deviceImei: obj.deviceImei,
		// iccid: obj.iccid,
		id: obj.id
	}
	$.ajax({

		url: requestConf.domain + "monitor/testRecord/queryFloorDetector",
		type: "post",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify(parm),
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		success: function(data) {
			console.log(data)

			if (data.success) {
				state = true;
				$("#dev_popuo").find("span[id='cjsj']").text(data.result.acquisitionTime)
				$("#dev_popuo").find("span[id='testPv']").text(data.result.testPv)
				$("#dev_popuo").find("span[id='statusType']").text(data.result.statusType)
				$("#louceng_dev").find("img[id="+obj.id+"]").attr("src",imgurl+data.result.picUrl)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
