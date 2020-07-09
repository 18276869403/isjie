var timestamp = null;
var isAdmin = localStorage.getItem("isAdmin");
var state = false;
var pageIndex = 1;
var pageSize  = 15
$(function () {
	pagelimit(1)
//	timestamp = 
	setInterval(function () {
		pagelimit(pageIndex)
	}, 5 * 1000);
})

function sure() {
	clearInterval(timestamp)
	timestamp = null
	let pagemax = parseInt($("#pageall").text())
	let topage = parseInt($("#topage").val())
	if (topage <= pagemax) {

		pagelimit(topage)
//		timestamp = setInterval(function () {
//			pagelimit(topage)
//		}, 10000)
	}
}

function pagedown() {
	debugger
	clearInterval(timestamp)
	timestamp = null
	let current_ = parseInt($("#currentpage").text())
	let pagemax = parseInt($("#pageall").text())
	//console.log(pagemax)
	//console.log('下一页')
	//console.log(current_ < pagemax)
	if (current_ < pagemax) {
		pagelimit(current_ + 1)
//		timestamp = setInterval(function () {
//			pagelimit(current_ + 1)
//		}, 10000)
	}

}

function pageup() {
	clearInterval(timestamp)
	timestamp = null
	//console.log('上一页')
	let current_ = parseInt($("#currentpage").text())
	//console.log(current_)
	if (current_ > 1) {
		pagelimit(current_ - 1)
//		timestamp = setInterval(function () {
//			pagelimit(current_ - 1)
//		}, 10000)
	}
}

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
	}
}

var deviceType = actions.getInputVal('equipmentid'), deviceNum = actions.getInputVal('deviceNum')
	, startTime = actions.getInputVal('startTime'), endTime = actions.getInputVal('endTime'),
	khlist = actions.getInputVal("khlist"), prolist = actions.getInputVal("prolist"), citylist = actions.getInputVal("citylist");
function islou(customerId,floorId,deviceId){
	// suliang+=1;
	// let customerId = id.split(",")[0];
	// let floorId = id.split(",")[1];
	// let deviceId = id.split(",")[2];
	//console.log(customerId,floorId);
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
				//有楼层
				localStorage.setItem("khid", customerId);
				// window.alert("11111")
				window.location.href = "louceng.html?id=" + data.result[0];
			} else {
				//无楼层
				/*弹出提示框*/
				// window.alert("当前设备未布置");
				$("#popBox").css("display", "block");
				$("#popLayer").css("display", "block");
			}
		},
		error: function () {
			//console.log("请求失败");
		}
	});

	// window.alert(id);
}
function pagelimit(num) {

	// 获取alarmTime_begin	报警开始时间
	var alarmTime_begin = $('#startTime').val()
	if (alarmTime_begin == "") {
		alarmTime_begin = ""
	}
	//console.log(alarmTime_begin)
	// 获取alarmTime_end	报警结束时间
	var alarmTime_end = $('#endTime').val()
	//console.log(alarmTime_end)
	if (alarmTime_end == "") {
		alarmTime_end = ""
	}
//	//console.log(alarmTime_begin, alarmTime_end)
	//获取oneAreaId省份id
//	//console.log(localStorage.getItem("provinceid"))
	var provinceid = localStorage.getItem("provinceid")
	//获取twoAreaId城市id
//	//console.log(localStorage.getItem("cityid"))
	var cityid = localStorage.getItem("cityid")
	// 客户IDcustomerId 1008
	//console.log(localStorage.getItem("khid"))
	var khid = localStorage.getItem("khid")
	// 获取设备类型alarmDeviceType
	var alarmDeviceType = document.getElementById("equipmentid").value
	if (alarmDeviceType == 0) {
		alarmDeviceType = ""
	}
	//console.log(alarmDeviceType)
	// 获取楼层名floorName
	var selectFloorValue = $('#selectFloor').val()
	//console.log(selectFloorValue)
	if (selectFloorValue == null) {
		selectFloorValue = ""
	}
//	//console.log(window.localStorage)
	var req = {
		userId: "",
		alarmTime_begin: startTime,
		alarmTime_end: endTime,
		oneAreaId: prolist,
		twoAreaId: citylist,
		customerId: khlist,
		alarmDeviceType: deviceType,
		floorName: "",
		pageNo: num,
		pageSize: pageSize,
		deviceNum : $('#deviceNum').val()
	}
	if (isAdmin == 1)
		req.userId = user.userId;
	else {
		//req.userId = user.customerId;
		req.customerId = user.customerId;
	}
	$.ajax({
		url: requestConf.domain + "monitor/alarmRecord/queryAlarm",
		type: "GET",
		dataType: "json", //返回格式为json
		// contentType: "application/json",
		data: req,
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
			//console.log(data.result.total)
			var a = parseInt(data.result.total / 15)
			//console.log(a)
			$("#currentpage").text(data.result.current)
			if ((data.result.total) % 15 == 0) {
				if (data.result.total == 0) {
					$("#pageall").text(1)
				} else {
					$("#pageall").text(a)
				}
			} else {
				$("#pageall").text(a + 1)
			}
			bindPageHtml(data.result.total, data.result.current);
			var callRecordtbdValue = data.result.records;
			var tr = "";
			//console.log(state)
			if (callRecordtbdValue.length == 0 && state) {
				return;
			} else {
				$('#callRecordtbd').empty()
				state = true;
				if (isAdmin == 1){
					callRecordtbdValue.forEach((value) => {
						tr +=
							` <tr>
	                        <td class="EalarmTime" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.alarmTime}">${value.alarmTime}</td>     //报警时间
	                        <td class="Estatus" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.tstatus}` + formatVal(value.deviceModel, "/") + `">${value.tstatus}`+ formatVal(value.deviceModel, "/") + `</td>       //设备类型
	                        <td class="Ename"  style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.name}">${value.name}</td>          //客户名称
	                        <td class="EporjectName" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.deviceNum}">
							<a href="#" onclick="islou(${value.customerId},${value.floorId},${value.deviceId})">${value.deviceNum}</a>
							</td>
							<td class="EalarmPv">${value.alarmAddress}</td>           //设备ADD/485地址
	                        <td class="EdetectionTarget" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.detectionTarget}">${value.detectionTarget || ''}</td>   //探测目标
	                        <td class="Eranges">${value.ranges || ''}</td>            //量程${value.unit}
	                        <td class="EalarmState">${value.alarmState}</td>        //状态
	                        <!--<td class="EalarmPv">${value.alarmPv}</td>           //检测值-->
	                    </tr>`
					})
				}else{
					callRecordtbdValue.forEach((value) => {
						tr +=
							` <tr>
	                        <td class="EalarmTime" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.alarmTime}">${value.alarmTime}</td>     //报警时间
	                        <td class="Estatus">${value.tstatus}</td>       //设备类型
	                          <td class="EporjectName" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.deviceNum}">
							<a href="#" onclick="islou(${value.customerId},${value.floorId},${value.deviceId})">${value.deviceNum}</a>
							</td>
							<td class="EalarmPv">${value.alarmAddress}</td>           //设备ADD/485地址
	                        <td class="EdetectionTarget" style="overflow:hidden;white-space:nowrap;text-overflow: ellipsis;" title="${value.detectionTarget}">${value.detectionTarget}</td>   //探测目标
	                        <td class="Eranges">${value.ranges}</td>            //量程
	                        <td class="EalarmState">${value.alarmState}</td>        //状态
	                        <!--<td class="EalarmPv">${value.alarmPv}</td>           //检测值-->
	                    </tr>`
					})
				}
				if (isAdmin == 1){
					if (data.result.records.length < 15) {
						//console.log("=================================")
						for (let a = 0; a < 15 - data.result.records.length; a++) {
							tr +=
								`<tr>
					           <td class="EalarmTime"></td>     //报警时间
					           <td class="Estatus"></td>       //设备类型
					           <td class="Ename"></td>          //客户名称
					            <td class="EprojectName"></td>	//编号/位号
					            <td class="EalarmPv"></td>           //设备ADD/485地址
					           <td class="EdetectionTarget"></td>   //探测目标
					           <td class="Eranges"></td>            //量程
					           <td class="EalarmState"></td>        //状态
					           <!--<td class="EalarmPv"></td>           //检测值-->
					      </tr>`
						}
//						//console.log(tr)
					}
				}else{
					if (data.result.records.length < 15) {
						//console.log("=================================")
						for (let a = 0; a < 15 - data.result.records.length; a++) {
							tr +=
								`<tr>
					           <td class="EalarmTime"></td>     //报警时间
					           <td class="Estatus"></td>       //设备类型
					            <td class="EprojectName"></td>	//编号/位号
					            <td class="EalarmPv"></td>     //设备ADD/485地址
					           <td class="EdetectionTarget"></td>   //探测目标
					           <td class="Eranges"></td>            //量程
					           <td class="EalarmState"></td>        //状态
					           <!--<td class="EalarmPv"></td>           //检测值-->
					      </tr>`
						}
//						//console.log(tr)
					}
				}

				$('#callRecordtbd').append(tr)
			}

		}
	});

}
$("#search1").find("[id='searchTable']").on('click', function () {
	deviceType = actions.getInputVal('equipmentid'), deviceNum = actions.getInputVal('deviceNum')
		, startTime = actions.getInputVal('startTime'), endTime = actions.getInputVal('endTime'),
		khlist = actions.getInputVal("khlist"), prolist = actions.getInputVal("prolist"), citylist = actions.getInputVal("citylist");
	// 设备类型
	$('#callRecordtbd').empty()
	pagelimit(1)

})

$("#search2").find("[id='searchTable']").on('click', function () {
	deviceType = actions.getInputVal('equipmentid'), deviceNum = actions.getInputVal('deviceNum')
		, startTime = actions.getInputVal('startTime'), endTime = actions.getInputVal('endTime'),
		khlist = actions.getInputVal("khlist"), prolist = actions.getInputVal("prolist"), citylist = actions.getInputVal("citylist");
	// 设备类型
	$('#callRecordtbd').empty()
	pagelimit(1)

})




// 导出表
$("#search2").find("[id='exportTable']").on('click', function () {
	tableToExcel()
})

$("#search1").find("[id='exportTable']").on('click', function () {
	tableToExcel()
})

function tableToExcel() {
	var alarmTime_begin = actions.getInputVal("startTime");

	// 获取alarmTime_end	报警结束时间
	var alarmTime_end = actions.getInputVal("endTime")

	//console.log(alarmTime_begin, alarmTime_end)
	//获取oneAreaId省份id
	//console.log(user)
	//console.log(localStorage.getItem("provinceid"))
	var provinceid = localStorage.getItem("provinceid")
	//获取twoAreaId城市id
	//console.log(localStorage.getItem("cityid"))
	var cityid = localStorage.getItem("cityid")
	var alarmDeviceType = actions.getInputVal("equipmentid");
	// 客户IDcustomerId 1008
	//console.log(localStorage.getItem("khid"))
	var khid = localStorage.getItem("khid")
	var selectFloorValue = $('#selectFloor').val()
	var req = {
		userId: "",
		alarmTime_begin: alarmTime_begin,
		alarmTime_end: alarmTime_end,
		oneAreaId: $("#prolist").val(),
		twoAreaId: $("#citylist").val(),
		customerId: $("#khlist").val(),
		alarmDeviceType: alarmDeviceType,
		floorName: "",
		pageNo : pageIndex,
		pageSize : pageSize,
		deviceNum : $('#deviceNum').val()
	}
	if (isAdmin == 1)
		req.userId = user.userId
	else
		req.customerId = user.customerId;

	$.ajax({
		type: "get",
		url: requestConf.domain + "monitor/alarmRecord/exportXls",
		data: req,
		dataType: "json",
		error: function (data) {
			//console.log("连接超时!");
		},
		beforeSend: function (request) {
			request.setRequestHeader("X-Access-Token",
				user.token
			);
		},
		success: function (data) {
			//console.log(data.result)
			let dataResult = data.result;
			let str = "报警时间,设备类型/型号,客户名称,设备编号/位置,设备ADD,检测目标,量程,状态\n";
			for (let i = 0; i < dataResult.length; i++) {
				//console.log(dataResult[i]);
				var floor;
				if (dataResult[i].floorName == null) {
					floor = "-";
				} else {
					floor = dataResult[i].floorName;
				}
				str +=   dataResult[i].alarmTime + '\t,' +
					dataResult[i].alarmDeviceType + formatVal(dataResult[i].deviceModel, "/") +'\t,' +
					dataResult[i].name + '\t,' +
					(dataResult[i].deviceNum || '') + '\t,' +
                    dataResult[i].alarmAddress + '\t,' +
					(dataResult[i].detectionTarget || '') + '\t,' +
					(dataResult[i].ranges || '') + '\t,' +
					// dataResult[i].alarmPv + '\t,' +
					dataResult[i].tstatus.replace(/,/g, "，") + '\t,\n';
			}
			let uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);
			var link = document.createElement("a");
			link.href = uri;
			link.download = "历史记录 " + new Date().Format('yyyyMMdd hhmmss') + ".csv";
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	});

}

function bindPageHtml(total, page){
        // 示例1
        $("#myPage").sPage({
            page: page,//当前页码，必填
            total:total,//数据总条数，必填
            pageSize:15,//每页显示多少条数据，默认10条
            showTotal:true,//是否显示总条数，默认关闭：false
            totalTxt:"共{total}条",//数据总条数文字描述，{total}为占位符，默认"共{total}条"
            noData: false,//没有数据时是否显示分页，默认false不显示，true显示第一页
            //showSkip:true,//是否显示跳页，默认关闭：false
            showPN:true,//是否显示上下翻页，默认开启：true
            prevPage:"上一页",//上翻页文字描述，默认“上一页”
            nextPage:"下一页",//下翻页文字描述，默认“下一页”
            fastForward: 5,//快进快退页数，默认0表示不开启快进快退
            backFun:function(current_){
            	//点击分页按钮回调函数，返回当前页码
            	clearInterval(timestamps)
            	timestamps = null
        		pagelimit(current_);
            	pageIndex = current_;
//        		timestamps = setInterval(function () {
//        			var khValue = $("#khlist").val();
//        			if ($("#khlist").val() == null)
//        				khValue = '';
//        			baojing_7day(khValue)
//        			baojing_1day(khValue)
//        			pagelimit(current_)
//        		}, 10000)
            }
    });
}