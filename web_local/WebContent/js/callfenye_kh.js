var state = false;
$(function() {
	pagelimit(1)
	console.log(guster)
})

function sure() {
	let pagemax = parseInt($("#pageall").text())
	let topage = parseInt($("#topage").val())
	if (topage <= pagemax) {
		pagelimit(topage)
	}
}

function pagedown() {
	let current_ = parseInt($("#currentpage").text())
	let pagemax = parseInt($("#pageall").text())
	console.log(pagemax)
	console.log('下一页')
	console.log(current_ < pagemax)
	if (current_ < pagemax) {
		pagelimit(current_ + 1)
	}

}

function pageup() {
	console.log('上一页')
	let current_ = parseInt($("#currentpage").text())
	console.log(current_)
	if (current_ > 1) {
		pagelimit(current_ - 1)
	}
}

function pagelimit(num) {

	// 获取alarmTime_begin	报警开始时间
	var alarmTime_begin = $('#startTime').val()
	if (alarmTime_begin == "") {
		alarmTime_begin = ""
	}
	console.log(alarmTime_begin)
	// 获取alarmTime_end	报警结束时间
	var alarmTime_end = $('#endTime').val()
	console.log(alarmTime_end)
	if (alarmTime_end == "") {
		alarmTime_end = ""
	}
	console.log(alarmTime_begin, alarmTime_end)

	var provinceid = localStorage.getItem("provinceid")

	var cityid = localStorage.getItem("cityid")

	var khid = guster.customerId
	// 获取设备类型alarmDeviceType
	var alarmDeviceType = document.getElementById("equipmentid").value
	if (alarmDeviceType == 0) {
		alarmDeviceType = ""
	}
	console.log(alarmDeviceType)
	// 获取楼层名floorName
	var selectFloorValue = $('#selectFloor').val()
	console.log(selectFloorValue)
	if (selectFloorValue == null) {
		selectFloorValue = ""
	}
	console.log(guster)
	var req = {
		alarmTime_begin: alarmTime_begin,
		alarmTime_end: alarmTime_end,
		
		customerId: guster.customerId,
		alarmDeviceType: alarmDeviceType,
		floorName: selectFloorValue,
		pageNo: num,
		pageSize: 12,
	}
	$.ajax({
		url: requestConf.domain + "monitor/alarmRecord/queryAlarm",
		type: "GET",
		dataType: "json", //返回格式为json
		// contentType: "application/json",
		data: req,
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);
		},
		success: function(data) {

			console.log(data.result.total)
			var a = parseInt(data.result.total / 12)
			console.log(a)
			$("#currentpage").text(data.result.current)
			if ((data.result.total) % 12 == 0) {
				if (data.result.total == 0) {
					$("#pageall").text(1)
				} else {
					$("#pageall").text(a)
				}
			
			} else {
			
				$("#pageall").text(a + 1)
			}
			// console.log(data.result.records)
			
			
			var callRecordtbdValue = data.result.records;
			var tr = "";
			console.log(state)
			if (callRecordtbdValue.length == 0 && state) {
				return;
			} else {
				$('#callRecordtbd').empty()
				state = true;
				callRecordtbdValue.forEach((value) => {
			
					tr +=
						` <tr>
			               
			                <td class="EalarmTime">${value.alarmTime}</td>     //报警时间
			                <td class="Estatus">${value.tstatus}</td>       //警报类型
			                
			                <td class="Ename">${value.name}</td>          //客户名称
			                <td class="EporjectName">${value.projectName}</td>   //项目名称
			                 <td class="EporjectName">${value.deviceNum}</td>
			                <td class="EdetectionTarget">${value.detectionTarget}</td>   //探测目标
			                <td class="Eranges">${value.ranges}</td>            //量程
			                <td class="Eunit">${value.unit}</td>              //单位
			                <td class="EalarmState">${value.alarmState}</td>        //状态
			                <!--<td class="EalarmPv">${value.alarmPv}</td>           //检测值-->
			            </tr>`
			
			
				})
				if (data.result.records.length < 12) {
					 console.log("=================================")
					for (let a = 0; a < 12 - data.result.records.length; a++) {
						tr +=
							`<tr>
					          
					           <td class="EalarmTime"></td>     //报警时间
					           <td class="Estatus"></td>       //警报类型
					           
					           <td class="Ename"></td>          //客户名称
					           <td class="EporjectName"></td>   //项目名称
					            <td class="EporjectName"></td>
					           <td class="EdetectionTarget"></td>   //探测目标
					           <td class="Eranges"></td>            //量程
					           <td class="Eunit"></td>              //单位
					           <td class="EalarmState"></td>        //状态
					           <!--<td class="EalarmPv"></td>           //检测值-->
					      </tr>`
					}
			           console.log(tr)
				}
				$('#callRecordtbd').append(tr)
			}
		}
	});

}
$('#searchTable').on('click', function() {
	// 设备类型

	$('#callRecordtbd').empty()
	pagelimit(1)
	// 获取alarmTime_begin	报警开始时间
	// 	var alarmTime_begin = $('#startTime').val()
	// 	alarmTime_begin = alarmTime_begin.replace(/-/g, '/');
	// 	var alarmTime_begin = new Date(alarmTime_begin);
	// 	alarmTime_begin = alarmTime_begin.getTime() / 1000;
	// 	console.log(alarmTime_begin)
	// 	// 获取alarmTime_end	报警结束时间
	// 	var alarmTime_end = $('#endTime').val()
	// 	alarmTime_end = alarmTime_end.replace(/-/g, '/');
	// 	var alarmTime_end = new Date(alarmTime_end);
	// 	alarmTime_end = alarmTime_end.getTime() / 1000;
	// 	console.log(alarmTime_end)
	// 	console.log(alarmTime_begin, alarmTime_end)
	// 	//获取oneAreaId省份id
	// 	console.log(user)
	// 	console.log(localStorage.getItem("provinceid"))
	// 	var provinceid = localStorage.getItem("provinceid")
	// 	//获取twoAreaId城市id
	// 	console.log(localStorage.getItem("cityid"))
	// 	var cityid = localStorage.getItem("cityid")
	// 	// 客户IDcustomerId 1008
	// 	console.log(localStorage.getItem("khid"))
	// 	var khid = localStorage.getItem("khid")
	// 	// 获取设备类型alarmDeviceType
	// 	var alarmDeviceType = $('#equipmentid').val()
	// 	// 获取楼层名floorName
	// 	var selectFloorValue = $('#selectFloor').val()

	// 	console.log(window.localStorage)
	// 	var req = {
	// 		alarmTime_begin: alarmTime_begin,
	// 		alarmTime_end: alarmTime_end,
	// 		oneAreaId: provinceid,
	// 		twoAreaId: cityid,
	// 		customerId: khid,
	// 		alarmDeviceType: alarmDeviceType,
	// 		floorName: selectFloorValue,
	// 		pageNo: num,
	// 		pageSize: 8,
	// 	}

})




// 导出表
$('#exportTable').on('click', function() {
	tableToExcel()
})

function tableToExcel() {
	var alarmTime_begin = $('#startTime').val()

	// 获取alarmTime_end	报警结束时间
	var alarmTime_end = $('#endTime').val()

	console.log(alarmTime_begin, alarmTime_end)
	//获取oneAreaId省份id


	var provinceid = localStorage.getItem("provinceid")

	var cityid = localStorage.getItem("cityid")
	var alarmDeviceType = $('#equipmentid').val()
	// 客户IDcustomerId 1008

	var khid = guster.customerId
	var selectFloorValue = $('#selectFloor').val()
	var req = {
		alarmTime_begin: alarmTime_begin,
		alarmTime_end: alarmTime_end,

		customerId: khid,
		alarmDeviceType: alarmDeviceType,
		floorName: selectFloorValue,
	}

	$.ajax({
		type: "get",
		url: requestConf.domain + "monitor/alarmRecord/exportXls",
		data: req,
		dataType: "json",
		error: function(data) {
			console.log("连接超时!");
		},
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);
		},
		success: function(data) {
			console.log(data.result)
			let dataResult = data.result;
			let str = "报警时间,报警类型,省份,城市,客户名称,项目名称,设备IMEI,探测目标,量程,单位,状态,PV值,设备类型,楼层\n";
			for (let i = 0; i < dataResult.length; i++) {
				console.log(dataResult[i]);
				str += dataResult[i].alarmTime + '\t,' +
					dataResult[i].tstatus + '\t,' +
					dataResult[i].oneAreaName + '\t,' +
					dataResult[i].twoAreaName + '\t,' +
					dataResult[i].name + '\t,' +
					dataResult[i].projectName + '\t,' +
					dataResult[i].alarmDeviceImei + '\t,' +
					dataResult[i].detectionTarget + '\t,' +
					dataResult[i].ranges + '\t,' +
					dataResult[i].unit + '\t,' +
					dataResult[i].alarmState + '\t,' +
					dataResult[i].alarmPv + '\t,' +
					dataResult[i].alarmDeviceType + '\t,' +
					dataResult[i].floorName + '\t,\n';
			}
			let uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);
			var link = document.createElement("a");
			link.href = uri;
			link.download = "报警记录表.csv";
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	});

}
