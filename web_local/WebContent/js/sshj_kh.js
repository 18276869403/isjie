var guster = JSON.parse(localStorage.getItem("guster"));
 $("#user_data_floor").find("span[id=name_data_floor]").text(guster.webName)
 
var name = guster.customerName;
var timestamp;

var sb1 = [];
var sb2 = [];
var sb3 = [];
$(function() {



	//跳转进来
	console.log(localStorage.getItem("sbdz"))
	if (localStorage.getItem("sbdz") != null) {
		singlesb()
		currentsb_7day()
		currentsb_1day()
	} else {
		console.log("正常进入")
		pagelimit(1)
		baojing_7day()
		baojing_1day()
        timestamp=setInterval(function(){
			pagelimit(1)
			baojing_7day()
			baojing_1day()
		},10000)
	}

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
	if (current_ < pagemax) {
		pagelimit(current_ + 1)
	}

}

function pageup() {
	let current_ = parseInt($("#currentpage").text())
	if (current_ > 1) {
		pagelimit(current_ - 1)
	}

}

function pagelimit(no) {
	var req = {
		customerId: guster.customerId,
		pageNo: no,
		pageSize: 12,
		deviceType: $("#deviceType").val(),
		deviceNum: $("#deviceNum").val(),
		acquisitionTime_begin: $("#startTime").val(),
		acquisitionTime_end: $("#endTime").val()
		
	}
	$.ajax({

		url: requestConf.domain + "monitor/testRecord/queryTest",
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
			if(data.result.records.length==12){
				
				 $("#divtable").css("border-bottom","0px")
				 $("#divtable").css("border","1px")
			}else if(data.result.records.length==0){
				$("#divtable").css("border","1px solid #007BFF")
			}else{
				$("#divtable").css("border","1px solid #007BFF")
			}
			$("#tbodydata").empty();
			console.log(data)
			var a = parseInt(data.result.total / 12)
			$("#currentpage").text(data.result.current)
			if ((data.result.total) % 12 == 0) {
				$("#pageall").text(a)
			} else {
				console.log(a + 1)
				$("#pageall").text(a + 1)
			}


			let tabledata = ""
			$.each(data.result.records, function(i, item) {
				let device = "";
				if (item.deviceType == "1") {
					device = "探测器"
				} else if (item.deviceType == "2") {
					device = "控制器"
				} else {
					device = "输出模块"
				}
				tabledata += '<tr>' +
					// '<td>' +
					// '<input class="check_item" type="checkbox" style="background-color: transparent;">' +
					// '</td>' +
					// '<td>' + item.oneAreaName + '</td>' +
					// '<td>' + item.twoAreaName + '</td>' +
					'<td style="width:300px">' + item.name + '</td>' +
					'<td>' + item.floorName + '</td>' +
					
					'<td>' + device + '</td>' +
					'<td>' + item.deviceNum + '</td>' +
					' <td>' + item.detectionTarget + '</td>' +
					'<td>' + item.ranges + '</td>' +
					'<td>' + item.unit + '</td>' +
					'<td>' + item.tstatus + '</td>' +
					'<td>' + item.testPv + '</td>' +
					'<td>' + item.acquisitionTime + '</td>' +
					'</tr>';


			})
			if (data.result.records.length < 12) {
				for (let a = 0; a < 12 - data.result.records.length; a++) {
					tabledata += '<tr>' +
			
						'<td style="width:300px"></td>' +
						'<td></td>' +
						
						'<td></td>' +
						'<td></td>' +
						' <td></td>' +
						'<td></td>' +
						'<td></td>' +
						'<td></td>' +
						'<td></td>' +
						'<td style="width:250px"></td>' +
						'</tr>';
				}
			
			}
			$("#tbodydata").append(tabledata)
		},
		error: function() {
			console.log("请求失败");
		}
	});


}
//其他报警次数统计
function baojing_7day() {

	var req = {
		customerId: guster.customerId,

	}
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectSevenDayAll",
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
			let xAxis_ = []
			$.each(data.result, function(i, item) {
				$.each(item, function(y, item_child) {
					if (i == "type3") {
						//输出模块
						xAxis_.push(item_child.days)
						sb3.push(item_child.count)

					} else if (i == "type2") {
						//控制器
						sb2.push(item_child.count)

					} else {
						//探测器
						sb1.push(item_child.count)

					}



				})

			})
			initoption(xAxis_)

		},
		error: function() {
			console.log("请求失败");
		}
	});
}

//当前客户一天报警统计
function baojing_1day() {

	var req = {
		customerId: guster.customerId,

	}
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectOneDayAll",
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
			let day1 = [];
			let x = [];
			if (data.success) {
				$.each(data.result.top0, function(i, item) {

					day1.push(item.count)
					x.push(item.days.substring(item.days.length - 5, item.days.length))

				})
			}else {
				x = ["00:00", "02:00", "04:00", "06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"];
				day1 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];

			}

			dat1(day1, x)

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
function exporttoexel_single() {
        console.log("单设备导出")
	$.ajax({
		type: "get",
		url: requestConf.domain + "monitor/testRecord/exportXlsTestAll",
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		data: {
			testDeviceImei: localStorage.getItem("testDeviceImei"),
			testAddress:localStorage.getItem("sbdz")
		},
		dataType: "json",
		error: function(data) {
			console.log("");
		},
		success: function(data) {
			console.log(data)
			let dataResult = data.result;
			let str = "报警时间,报警类型,省份,城市,客户名称,项目名称,设备IMEI,探测目标,量程,单位,状态,PV值,设备类型,楼层\n";
			for (let i = 0; i < dataResult.length; i++) {
				console.log("------------" + dataResult[i]);
				str += dataResult[i].acquisitionTime + '\t,' +
					dataResult[i].tstatus + '\t,' +
					dataResult[i].oneAreaName + '\t,' +
					dataResult[i].twoAreaName + '\t,' +
					dataResult[i].name + '\t,' +
					dataResult[i].projectName + '\t,' +
					dataResult[i].testDeviceImei + '\t,' +
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
			link.download = "实时数据.csv";
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	});

}
function exporttoexel() {
    if(localStorage.getItem("sbdz") != null){
		exporttoexel_single()
		return
	}
	$.ajax({
		type: "get",
		url: requestConf.domain + "monitor/testRecord/exportXlsTestAll",
		beforeSend: function(request) {
			request.setRequestHeader("X-Access-Token",
				guster.token
			);

		},
		data: {
			customerId: guster.customerId
		},
		dataType: "json",
		error: function(data) {
			console.log("");
		},
		success: function(data) {
			console.log(data)
			let dataResult = data.result;
			let str = "报警时间,报警类型,省份,城市,客户名称,项目名称,设备IMEI,探测目标,量程,单位,状态,PV值,设备类型,楼层\n";
			for (let i = 0; i < dataResult.length; i++) {
				console.log("------------" + dataResult[i]);
				str += dataResult[i].acquisitionTime + '\t,' +
					dataResult[i].tstatus + '\t,' +
					dataResult[i].oneAreaName + '\t,' +
					dataResult[i].twoAreaName + '\t,' +
					dataResult[i].name + '\t,' +
					dataResult[i].projectName + '\t,' +
					dataResult[i].testDeviceImei + '\t,' +
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

//当前设备1天曲线图  来自单个设备的跳转
function currentsb_1day() {
	console.log("success")
	var req = {
		alarmDeviceImei: localStorage.getItem("testDeviceImei"),
		alarmAddress: localStorage.getItem("sbdz")

	}
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectOneDay",
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
			console.log(data)
			let day1 = [];
			let x = [];
			if (data.success) {
				$.each(data.result, function(i, item) {

					day1.push(item.count)
					x.push(item.days.substring(item.days.length - 5, item.days.length))

				})
			} else {
				x = ["00:00", "02:00", "04:00", "06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"];
				day1 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];

			}
			console.log("success_____________")
			let deviceImei = localStorage.getItem("testDeviceImei");
			dat2(day1, x, deviceImei)

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
//当前设备7天曲线图  来自单个设备的跳转
function currentsb_7day() {
	var req = {
		alarmDeviceImei: localStorage.getItem("testDeviceImei"),
		alarmAddress: localStorage.getItem("sbdz")

	}
	$.ajax({

		url: requestConf.domain + "monitor/alarmRecord/selectSevenDayOne",
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
			console.log("---------------------")
			let x_7 = [];
			let data_value = [];
			let sbtype = localStorage.getItem("deviceType")
			$.each(data.result, function(i, item) {
				console.log(item)
				x_7.push(item.days)
				data_value.push(item.count)
			})
			initoption2(x_7, data_value, sbtype)

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
//当前设备实时数据  来自单个设备的跳转
function singlesb() {
	var req = {
		testDeviceImei: localStorage.getItem("testDeviceImei"),
		testAddress: localStorage.getItem("sbdz"),
		pageNo: 1,
		pageSize: 11,


	}
	$.ajax({

		url: requestConf.domain + "monitor/testRecord/queryTest",
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

			$("#tbodydata").empty();
			if (data.success) {
				let tabledata = ""
				$.each(data.result.records, function(i, item) {
					let device = "";
					if (item.deviceType == "1") {
						device = "探测器"
					} else if (item.deviceType == "2") {
						device = "控制器"
					} else {
						device = "输出模块"
					}
					tabledata += '<tr>' +
						// '<td>' +
						// '<input class="check_item" type="checkbox" style="background-color: transparent;">' +
						// '</td>' +
						// '<td>' + item.oneAreaName + '</td>' +
						// '<td>' + item.twoAreaName + '</td>' +
						'<td>' + item.name + '</td>' +
						'<td>' + item.floorName + '</td>' +
						
						'<td>' + device + '</td>' +
						'<td>' + item.deviceNum + '</td>' +
						' <td>' + item.detectionTarget + '</td>' +
						'<td>' + item.ranges + '</td>' +
						'<td>' + item.unit + '</td>' +
						'<td>' + item.tstatus + '</td>' +
						'<td>' + item.testPv + '</td>' +
						'<td>' + item.acquisitionTime + '</td>' +
						'</tr>';


				})
				if (data.result.records.length < 11) {
					for (let a = 0; a < 11 - data.result.records.length; a++) {
						tabledata += '<tr>' +
				
							'<td style="width:300px"></td>' +
							'<td></td>' +
							
							'<td></td>' +
							'<td></td>' +
							' <td></td>' +
							'<td></td>' +
							'<td></td>' +
							'<td></td>' +
							'<td></td>' +
							'<td style="width:250px"></td>' +
							'</tr>';
					}
				
				}
				$("#tbodydata").append(tabledata)
			}

		},
		error: function() {
			console.log("请求失败");
		}
	});
}
