var state = false;
var timestamp, timestamp1 = null;
var imgurl = requestConf.domain + 'sys/common/view/';
var loucengId = getQueryString('id') || "";
var timuImage = null;
var driverList = [];
var firstLoad  = true;
var floorDataList = [];
var firstQuery = true;
$(function () {
	var req = {
		uid: user.userId
	}
	$.ajax({
		url: requestConf.domain + "monitor/customers/queryTowVoAllById",
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
			console.log(data)
			if (data.success && data.result && data.result.length > 0) {
				localStorage.setItem("khid", data.result[data.result.length - 1].id);
				
			    getloucengname();
			    var refushData = function () {
			        selectDeviceCount()
			        query(loucengId)
			        shuaxindevinlouceng(loucengId);
			        loadRec();
			    }
			    refushData();
			    timestamp = setInterval(refushData, 5000);
			}
		},
		error: function (e) {
			panduan(JSON.parse(e.responseText));
			console.log("请求失败" , e);
		}
	});
})

function selectDeviceCount() {
    var req = {
        customerId: localStorage.getItem("khid"),
    }
    if (isAdmin == 0){
//        console.log("用户")
        req.customerId = user.customerId;
    }

    $.ajax({

        url: requestConf.domain + "monitor/device/selectDeviceCount",
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
            if (data.success) {
                console.log(req.customerId)
                console.log(data.result)
                $("#allcount").text(data.result.deviceCount)
                $("#normalcount").text(data.result.deviceNormalCount)
                $("#whsb").text(data.result.deviceDefendCount)
                $("#gzsb").text(data.result.deviceFaultCount)
                $("#txgz").text(data.result.deviceCommFailCount)
                $("#bjsb").text(data.result.deviceAlertCount)
            }
        },
        error: function () {
            console.log("请求失败");
        }
    });
}

//当前页码
var currentPage = 1, totalPage = 0;
var actions = {
    //获取用户id
    getUid: function () {
        var uid = "";
        if (isAdmin == 0)
            uid = user.customerId
        else
            uid = user.userId;
        return uid;
    },
    pagedown: function () {
        if (currentPage + 1 > totalPage)
            return;
        currentPage++;
        getloucengname();
    },
    pageup: function () {
        if (currentPage <= 1)
            return;
        currentPage--;
        getloucengname();
    }
}

var preleft = 0, pretop = 0, distanceDgqX = 0, distanceDgqY = 0;
//箭头移动图片
function imgmove(tag) {
    if(tag == 1){
        console.log("up");
        console.log($("#timg").offset().left,$("#timg").offset().top);
    }else if(tag == 2){
        console.log("down");
    }else if(tag == 3){
        console.log("left");
    }else{
        console.log("right");
    }

}
function darg(obj) {
    document.oncontextmenu = function () {
        window.event.returnValue = false;
        // 设置仅当次有效
//        document.oncontextmenu = null;
        return false;
    }
    //鼠标按下
    obj.onmousedown = function (ev) {
        //IE直接用event或者window.event得到事件本身,而在其他浏览器函数要获取到事件本身需要从函数中传入
        var oevent = ev || window.event;
        if(oevent.which != 3){
        	// 仅支持右键拖动
        	return; 
        }
        var distanceX = oevent.clientX - this.offsetLeft;
        var distanceY = oevent.clientY - this.offsetTop;
        distanceDgqY  = distanceY;
        distanceDgqX  = distanceX;
        //鼠标移动
        document.onmousemove = function (ev) {
            var oevent = ev || window.event;

            var $d = $(document);
            var imgLeft = Math.min(oevent.clientX - distanceX, 0);//
            var maxWidth = $d.width()  - $('#left_content').width() - $(obj).width();
            if($(obj).width() < $d.width() - $('#left_content').width()){
            	imgLeft = 0;
            }  else  if(imgLeft < maxWidth){// 高度大于时，底部距离不能留白
            	imgLeft = maxWidth;
            } 
            obj.style.left = imgLeft + 'px';
            
            var imgTop = Math.min(oevent.clientY - distanceY, 0);//
            var maxTop = $d.height()  - $('#head').height() - $(obj).height();
            if($d.height()  - $('#head').height() > $(obj).height()){
            	imgTop = 0;
            }  else  if(imgTop < maxTop){// 高度大于时，底部距离不能留白
            	imgTop = maxTop;
            } 
            obj.style.top  = imgTop + 'px';
            let left = oevent.clientX - distanceX;
            let top = oevent.clientY - distanceY;

            var wLength = parseFloat(obj.style.left.replace("px", "")) - preleft;
            distanceDgqX = wLength;
            var hLength = parseFloat(obj.style.top.replace("px", "")) - pretop;
            distanceDgqY = hLength;
            var loucengImg = $(".sbinloucengimg");
            var louceng = $(".sbinlouceng");
            var tips = $(".tipssanjiao");
            $.each(loucengImg, function (index, item) {
                $(item).css("left", parseFloat($(item).css("left").replace("px", "")) + wLength + "px");
                $(item).css("top", parseFloat($(item).css("top").replace("px", "")) + hLength + "px");
            })
            $.each(louceng, function (index, item) {
                $(item).css("left", parseFloat($(item).css("left").replace("px", "")) + wLength + "px");
                $(item).css("top", parseFloat($(item).css("top").replace("px", "")) + hLength + "px");
            })
            $.each(tips, function (index, item) {
                $(item).css("left", parseFloat($(item).css("left").replace("px", "")) + wLength + "px");
                $(item).css("top", parseFloat($(item).css("top").replace("px", "")) + hLength + "px");
            })
            preleft = parseFloat(obj.style.left.replace("", ""));
            pretop = parseFloat(obj.style.top.replace("", ""));

        };
        //鼠标放开
        document.onmouseup = function () {
            document.onmousemove = function (event) {
                var sbinloucengimg = $(".sbinloucengimg");
                $.each(sbinloucengimg, function (index, item) {
                    isHover(event, item, $(item).attr("attr"), $(item).attr("attrid"))
                })
            }
            document.onmouseup = null;
        };
    };
}
function reCaleImgLT(){
}

//楼层的名称
function getloucengname() {
    var req = {
        cid: localStorage.getItem("khid"),
    }
    if (isAdmin == 0)
        req = { cid: user.customerId };
    $.ajax({
        url: requestConf.domain + "monitor/floorPlan/queryFloorNameAndIdByCid",
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
            $("#currentpage").html(currentPage);
            $("#loucengall").html("");
            if (data.success) {
                let louceng = "";
                var dataLength = data.result.length;
                if(dataLength > 1){
                	$("#nextPage,#prevPage").show();
                }
                floorDataList = data.result;
                //分页
                totalPage = parseInt(dataLength / 7);
                if (dataLength % 7 != 0)
                    totalPage = totalPage + 1;
                $("#pageall").html(totalPage);
                $.each(data.result, function (y, item) {
                    if (y >= (currentPage - 1) * 7 && y < currentPage * 7) {
                        if (!loucengId && y == 0) {
                        	loucengId = item.id;
//                            devinlouceng(item.id);
//                            // var img = context.drawImage(imgurl + data.result[0].planPic,0,0,400,300);
//                            $("#timg").attr("src", imgurl + data.result[0].planPic);
//                            var tImg = document.getElementById("timg");
//                            darg(tImg);
//                            preleft = parseFloat($("#timg").css("left").replace("px", ""));
//                            pretop = parseFloat($("#timg").css("top").replace("px", ""));
                        }
                        louceng += '<tr>' +
	                        '<td style="cursor: pointer;border: 1px solid #0052ab">' + (item.showIndex || "") + '</td>' +
                            '<td id="' + item.id + '" onclick="query(\'' + item.id + '\', true)" style="cursor: pointer;border: 1px solid #0052ab">' + item.floorName + '</td>' +
                            '</tr>';
                    }
                })
                $("#loucengall").append(louceng);
                var trLength = $("#loucengall").find("tr");
                if (trLength.length < 7) {
                    for (var i = 0; i < 7 - trLength.length; i++) {
                        $("#loucengall").append('<tr style="height:45px"><td style="cursor: pointer;border: 1px solid #0052ab"></td><td style="cursor: pointer;border: 1px solid #0052ab"></td></tr>')
                    }
                }
                query(loucengId);
            }
        },
        error: function () {
            console.log("请求失败");
        }
    });
}

function query(id, click) {
	if(click){
		firstLoad = true;
//		resetImgSize();
	}
	loucengId = id;
    $("#loucengall").find("td").css("color", "");
    // 兼容自动分页选中
    if(firstQuery && totalPage > 0 && $("#loucengall").find("td[id=" + id + "]").length == 0){
    	actions.pagedown();
    	firstQuery = false;
    }
	$("#loucengall").find("td[id=" + id + "]").css("color", "#e31c14");
    //更新右边的视图
    devinlouceng(id);
    var req = {
        customerId: localStorage.getItem("khid"),
        floorId: id
    }
    if (isAdmin == 0)
        req.customerId = user.customerId;
    $.ajax({
        url: requestConf.domain + "monitor/device/selectCountDeviceByFlor",
        type: "get",
        dataType: "json",
        data: req,
        beforeSend: function (request) {
            request.setRequestHeader("X-Access-Token",
                user.token
            );
        },
        success: function (data) {
            if (data.success) {
                //$("#self_all").text(data.result.deviceCount)
                $("#self_normal").text(data.result.deviceNormalCount);
                $("#self_whsb").text(data.result.deviceDefendCount);
                $("#self_gzsb").text(data.result.deviceFaultCount);
                $("#self_txgz").text(data.result.deviceCommFailCount);
                $("#self_bjsb").text(data.result.deviceAlertCount);

            	var allCount = data.result.deviceNormalCount;
	            	allCount += data.result.deviceDefendCount;
	            	allCount += data.result.deviceFaultCount;
	            	allCount += data.result.deviceCommFailCount;
	            	allCount += data.result.deviceAlertCount;
                $("#self_all").text(allCount);
            }
        },
        error: function () {
            console.log("请求失败");
        }
    });
}
//中间显示的楼层设备
function devinlouceng(id) {
//    console.log("楼层id",id);
    var req = {
        fid: id,
    }
    $.ajax({
        url: requestConf.domain + "monitor/floorPlan/queryFloorDeviceByFid",
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
        	driverList = [];
            //更换楼层图纸
            if(data && data.result){
	        	$('#louceng_dev').empty();
	            var src = imgurl + data.result.picurl;
	            $("#timg").attr("src", src);
	            $("#timg").unbind("load").on("load", function(){
		            refushLouchen(src, null, function(){
			        	driverList = data.result.deviceInfoDTOS;
		            });
	            });
            } 
        },
        error: function () {
            console.log("请求失败");
        }
    });
}
function refushLouchen(src, oldHeight, cb){
    // $("#planPic").css("background-image", "url('" + imgurl + data.result.picurl + "')")
    var tImg = document.getElementById("timg");
    darg(tImg);
    preleft = parseFloat($("#timg").css("left").replace("px", ""));
    pretop = parseFloat($("#timg").css("top").replace("px", ""));
    $("#hideImg").attr("src", src);
    var image = new Image()
    image.src = src;
    image.onload = function () {
    	setImgInitScale(image);
    	if(cb){
    		cb(); // 设置数据
    	}
        louchenImgLoad(image, oldHeight);
    }
}
function resetImgSize(){
    $("#timg").css({
		'width'  :  $("#planPic").width(),
		'height' :  $("#planPic").height(),
		'left'   :  "0px",
		'top'    :  "0px"
	});
	louchenImgLoad();
}
function louchenImgLoad(image, preHeight){
	if(image){
		timuImage = image;
	}
	var image = image || timuImage;
	if(!image) return;
    // 原始大小
    var naturalWidth  = image.width;
    var naturalHeight = image.height;
    // 当前图片缩放大小
    var currentWidth  = $("#timg").width();
    var currentHeight = $("#timg").height();

//    scaleX = (currentWidth  / naturalWidth ).toFixed(2);
//    scaleY = (currentHeight / naturalHeight).toFixed(2);

    scaleX = (currentWidth  / naturalWidth );
    scaleY = (currentHeight / naturalHeight);
    
//    if(firstLoad){
//        var $d = $(document);
//        $("#timg").css({
//            //"height": $d.height() - 60 + "px"
//            "height": naturalHeight * scaleX + "px"
//        });
//        firstLoad = false;
//    }

//    var scale = scaleX;
//    if(naturalHeight * scaleY >= currentHeight){
//        //按高度等比缩放宽度
//        $("#timg").width(naturalWidth * scaleY);
//        scale = scaleY;
//    } else if(naturalWidth * scaleX >= currentWidth){
//        //按高度等比缩放高度
//        $("#timg").height(naturalHeight * scaleX);
//        scale = scaleX;
//    }
    
//    var scale = Math.min(Math.max(scaleX, scaleY), 1);
    
    var scale = Math.min(scaleX, scaleY);
    $("#timg").width(naturalWidth * scale);
    $("#timg").height(naturalHeight * scale);
    
    currentWidth  = $("#timg").width();
    currentHeight = $("#timg").height();
    $("#timg").attr('initWidth',  currentWidth)
        .attr('initHeight', currentHeight)
        .attr('realWidth',  naturalWidth)
        .attr('realHeight', naturalHeight)
        .attr('scale', scale);
    if(!$("#timg").attr('scaleX')){
    	$("#timg").attr('scaleX', scaleX).attr('scaleY', scaleY);
    }
    if (driverList != null && driverList.length > 0){ //data.success) {
        //图纸上黑色弹窗显示检测值
        appendDriver(preHeight);
    }
}
function appendDriver(preHeight){
    let sbinlouceng = "";
    let px = "px";
    var x = [];
    var xx = {};
    $.each(driverList, function (y, item) {
        let aaa = JSON.stringify(item)
        xx.id = item.id;
        xx.x = item.xposition;
        xx.y = item.yposition;
        x.push(xx)
        var dituLeft = document.getElementById('timg').offsetLeft;
        var dituTop = document.getElementById('timg').offsetTop;
        localStorage.setItem("arr", JSON.stringify(x))
        var jcval = item.testPv == 0 ? "-" : item.testPv;
		jcval = item.testPv == null ? "-" : item.testPv;
		if(jcval){
			jcval = jcval.replace('null', '');
		}
        // var unit = item.testPv == "-" ? "" : item.unit
        var replace = item;
        var picUrl = replace.picUrl;
        replace.picUrl = "";
        aaa = JSON.stringify(replace);
        item.picUrl = picUrl;
        /*黑色三角形*/
        var lt = getIconLT(item, preHeight);
        var pv = jcval.split(item.unit)[0];
        if(item.deviceType == '输出模块' && pv > 0){
        	var text = "未动作";
        	if(item.testPv){ 
        		var pv = item.testPv.replace(item.unit + "", "");
        		if(pv > 0){
        			text = "已动作";
        		}
        	} 
        	sbinlouceng += '<div style="top:' + lt.top + ';left:' + lt.left + ';position:absolute;z-index:100" class="sbinlouceng">' + text + '</div>' +
	            '<img id="img' + item.id + '"  style="position:absolute;margin-top:-3px;z-index:99;top:' + lt.top142 + ';left:' + lt.left +
	            '!important" src="./img/tips_sanjiao.png" class="tipssanjiao" onmouseover=sbck(' + aaa + ',' + item.id + ')>' +
	            '<img id="sbinloucengimg' + item.id + '" attrid="' + item.id + '" onclick=tosingle(this) attr=\'' + aaa + '\'    style="top:' + lt.lTop + ';left:' + lt.lLeft +
	            '" class="sbinloucengimg" src="' + imgurl + picUrl + '" >';
        } else if(item.deviceType == '控制器'  && pv > 0){
        	var text = "无继电器动作";
        	if(item.testPv){ 
        		var pv = item.testPv.replace(item.unit + "", "");
        		if(pv > 0){
        			text = convert2binStatus(pv);
        		}
        	} 
        	sbinlouceng += '<div style="width:auto;white-space: nowrap; padding: 0 8px;;top:' + lt.top + ';left:' + lt.left + ';position:absolute;z-index:100" class="sbinlouceng">' + text + '</div>' +
	            '<img id="img' + item.id + '"  style="position:absolute;margin-top:-3px;z-index:99;top:' + lt.top142 + ';left:' + lt.left +
	            '!important" src="./img/tips_sanjiao.png" class="tipssanjiao" onmouseover=sbck(' + aaa + ',' + item.id + ')>' +
	            '<img id="sbinloucengimg' + item.id + '" attrid="' + item.id + '" onclick=tosingle(this) attr=\'' + aaa + '\'    style="top:' + lt.lTop + ';left:' + lt.lLeft +
	            '" class="sbinloucengimg" src="' + imgurl + picUrl + '" >';
        } else if(pv > 0){
        	sbinlouceng += '<div style="top:' + lt.top + ';left:' + lt.left + ';position:absolute;z-index:100" class="sbinlouceng">' + jcval + '</div>' +
	            '<img id="img' + item.id + '"  style="position:absolute;margin-top:-3px;z-index:99;top:' + lt.top142 + ';left:' + lt.left +
	            '!important" src="./img/tips_sanjiao.png" class="tipssanjiao" onmouseover=sbck(' + aaa + ',' + item.id + ')>' +
	            '<img id="sbinloucengimg' + item.id + '" attrid="' + item.id + '" onclick=tosingle(this) attr=\'' + aaa + '\'    style="top:' + lt.lTop + ';left:' + lt.lLeft +
	            '" class="sbinloucengimg" src="' + imgurl + picUrl + '" >';
        } else {
        	sbinlouceng +=
                '<img id="img' + item.id + '"  style="top:' + lt.top142 + ';left:' + lt.left + '!important"  onmouseover=sbck(' + aaa + ',' + item.id + ')>' +
                '<img id="sbinloucengimg' + item.id + '" attrid="' + item.id + '" onclick=tosingle(this) attr=\'' + aaa + '\'    style="top:' + lt.lTop + ';left:' + lt.lLeft +
                '" class="sbinloucengimg" src="' + imgurl + picUrl + '" >';
        }
    });
    $("#louceng_dev").empty();
    $("#louceng_dev").append(sbinlouceng);
    document.onmousemove = function (event) {
        var sbinloucengimg = $(".sbinloucengimg");
        $.each(sbinloucengimg, function (index, item) {
            isHover(event, item, $(item).attr("attr"), $(item).attr("attrid"))
        })
    }
}
function getIconLT(item, preHeight){
    var timg = $("#timg");
    var scale         = timg.attr('scale')  * 1;
    var dituLeft      = timg[0].offsetLeft;
    var dituTop       = timg[0].offsetTop;
    var px  = 'px';
    
    var exTop = 40;
    var res = {
		// 黑色值top
        top   : (item.yposition * scale - 80 + dituTop)  + px,
        top142: item.yposition * scale - exTop + dituTop - 20 + px,
        left  : (item.xposition * scale - 50  + dituLeft) + px,
        // sbinloucengimg 设备标注图
        lTop  : item.yposition * scale -  exTop +  dituTop  + px,
        lLeft : item.xposition * scale - 50  + dituLeft + px,

        reTop    : (item.yposition * scale - 160 + dituTop)  + px,
        reTop142 : (item.yposition * scale - 102 + dituTop)  + px,
        reLeft   : (item.xposition * scale - 50  + dituLeft) + px,
    };
    if(item.id == '114'){
    	res.lTop = item.yposition * scale -  exTop +  dituTop + px;
    }
    return res;
}
function refushLocation(oldHeight){
//	var loucengImg = $(".sbinloucengimg");
//    var louceng = $(".sbinlouceng");
//    var tips = $(".tipssanjiao");
//    $.each(loucengImg, function (index, item) {
//    	var itemData = JSON.parse($(item).attr("attr"));
//    	var lt = getIconLT(itemData);
//        var jcval = itemData.testPv == 0 ? "-" : itemData.testPv;
//        jcval = itemData.testPv == null ? "-" : itemData.testPv;
//        if(jcval != "-"){
//        	$(item).css({ "left": lt.reLeft, "top": lt.reTop });
//        } else {
//        	$(item).css({ "left": lt.reLeft, "top": lt.reTop142 });
//        }
//    })
//	$.each(louceng, function (index, item) {
//    	var lt = getIconLT(JSON.parse($(item).next().next().attr("attr")));
//		$(item).css({ "left": lt.lLeft, "top": lt.top });
//	})
//	$.each(tips, function (index, item) {
//    	var lt = getIconLT(JSON.parse($(item).next().attr("attr")));
//		$(item).css({ "left": lt.lLeft, "top": lt.top142 });
//	});
    refushLouchen($('#timg').attr('src'), oldHeight);
}
function shuaxindevinlouceng(id) {
    var req = {
        fid: id,
    }
    $.ajax({
        url: requestConf.domain + "monitor/floorPlan/queryFloorDeviceByFid",
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
            // $("#louceng_dev").empty()
            if (data.success) {
                let sbinlouceng = "";
                let px = "px";
                var x = [];
                var xx = {};
                // position: absolute;top: 50px;left: 30px;
                //图纸上黑色弹窗显示检测值
                if(data.result.deviceInfoDTOS != null){
	                $.each(data.result.deviceInfoDTOS, function (y, item) {
	                    var replace = item;
	                    var picUrl = replace.picUrl;
	                    /*更换设备图标*/
	                    $("#sbinloucengimg"+item.id).attr("src", imgurl + picUrl);
	                });
                }
            }
        },
        error: function () {
            console.log("请求失败");
        }
    });
}

//鼠标移除,设备窗口消失
var outid = "";
function out(id) {
    if (id == outid) {
        ajaxTime = 1;
        $("#dev_popuo").css("display", "none")
    }
}

var scaleX = 1, scaleY = 1;
var ajaxTime = 1;

//设备上弹出的窗口  鼠标悬浮在上面
function sbck(obj, id) {
    obj = JSON.parse(obj);
    ajaxTime++;
    var parm = {
        addressNumber: obj.addressNumber,
        deviceImei: obj.deviceImei,
        iccid: obj.iccid,
        id: obj.id
    }
    if (ajaxTime == 2) {
        $.ajax({
            url:  requestConf.domain + "monitor/testRecord/queryFloorDetector",
            type: "post",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(parm),
            beforeSend: function (request) {
                request.setRequestHeader("X-Access-Token",
                    user.token
                );
            },
            success: function (data) {
                if (data.success) {
                	setRealPv(data, obj, id);
                }
            },
            error: function () {
                console.log("请求失败");
            }
        });
    }
}
function setRealPv(data, obj, id){
	 // console.log("显示文本框")
    $("#sanjiaoshangimg").hide()
    $("#sanjiaoimg").hide()
    $("#sanjiaoleftimg").hide()
    $("#sanjiaorightimg").hide()
    let left = parseFloat($("#sbinloucengimg" + id + "").css("left").replace("px", ""))
    let top  = parseFloat($("#sbinloucengimg" + id + "").css("top").replace("px", ""))
    var pos  = $("#sbinloucengimg" + id + "")[0].getBoundingClientRect();
    
    if($(document).width() - pos.left <= 200){
        $("#dev_popuo").css("top", top - 50 + "px")
        $("#dev_popuo").css("left", left - 180 + "px")
        $("#sanjiaorightimg").show()
    } else if(top > 185 && left > 40){ // 上方
        $("#sanjiaoimg").show()
        $("#dev_popuo").css("top", top - 205+ "px")
        $("#dev_popuo").css("left", left - 40 + "px")
    }else if(top <= 185 && left > 40){ // 下方
        $("#sanjiaoshangimg").show()
        $("#dev_popuo").css("top", top + 50 + "px")
        $("#dev_popuo").css("left", left - 40 + "px")
    }else if(top>185 && left<=40){// 左边三角
        $("#dev_popuo").css("top", top-25+ "px")
        $("#dev_popuo").css("left", left + 86 + "px")
        $("#sanjiaoleftimg").show()
    }else if(top<=185&&left<=40){// 无三角
        $("#dev_popuo").css("top", top+20 + "px")
        $("#dev_popuo").css("left", left  + 70 + "px")
    } else {
        $("#sanjiaoimg").show()
        $("#dev_popuo").css("top", top-185+ "px")
        $("#dev_popuo").css("left", left - 38 + "px")
    }
    $("#dev_popuo").css("display", "block")
    $("#dev_popuo").find("span[id='deviceImei']").text(obj.deviceNum)
    $("#dev_popuo").find("span[id='ranges']").text((obj.detectionTarget || "") + " " + (obj.ranges || ""))
    $("#dev_popuo").find("span[id='deviceType']").text(obj.deviceType + " " + (obj.deviceModel || ""))
    
    obj.testPv = obj.testPv.replace(obj.unit + "", "");
    
	var jcval = data.result.testPv ? data.result.testPv.replace("null", "") : "";
	$("#dev_popuo").find("span[id='testPv']").text(jcval);
    if(obj.deviceType == "输出模块"){
    	$(".outPvDiv").show();
    	$(".testPvDiv").hide();
	  	if(jcval != null && jcval != "0" && jcval != "" && jcval != "null"){
	  		$("#outPv").html("已动作");
	  	} else {
	  		$("#outPv").html("未动作");
	  	}
    } else if(obj.deviceType == "控制器"){
    	$(".outPvDiv").show();
    	$(".testPvDiv").hide();
    	if(jcval != null && jcval != "0" && jcval != "" && jcval != "null"){
	  		$("#outPv").html(convert2binStatus(jcval));
	  	} else {
	  		$("#outPv").html("无继电器动作");
	  	}
    } else {
    	$(".outPvDiv").hide();
    	$(".testPvDiv").show();
    } 
    $("#dev_popuo").find("span[id='statusType']").text(data.result.statusType);
    $("#louceng_dev").find("img[id=" + obj.id + "]").attr("src", imgurl + data.result.picUrl);
}
// 转换控制器2进制状态显示
function convert2binStatus(testPv){
	// 2进制数字
	testPv = parseInt(testPv).toString(2);
	while(testPv.length < 8){
		testPv = "0" + testPv;
	}
	var tpArray = testPv.split("");
	var res = "";
	for(var a = 0; a < tpArray.length; a++){
		if(tpArray[a] == '1'){
			res += (a + 1) + "#";
		}
	}
	return res + "继电器动作";
}
//开始全屏
function fullScrollOpen()  
{  
    var docElm = document.documentElement;  
    //W3C   
    if (docElm.requestFullscreen) {  
        docElm.requestFullscreen();  
    }  
        //FireFox   
    else if (docElm.mozRequestFullScreen) {  
        docElm.mozRequestFullScreen();  
    }  
        //Chrome等   
    else if (docElm.webkitRequestFullScreen) {  
        docElm.webkitRequestFullScreen();  
    }  
        //IE11   
    else if (elem.msRequestFullscreen) {  
        elem.msRequestFullscreen();  
    }  
}  

//退出全屏
function fullScrollClose() {  
    if (document.exitFullscreen) {  
        document.exitFullscreen();  
    }  
    else if (document.mozCancelFullScreen) {  
        document.mozCancelFullScreen();  
    }  
    else if (document.webkitCancelFullScreen) {  
        document.webkitCancelFullScreen();  
    }  
    else if (document.msExitFullscreen) {  
        document.msExitFullscreen();  
    }  
}  

function setImgInitScale(image){
	if(!firstLoad) return;
    image = image || timuImage;
	// 原始大小
    var naturalWidth  = image.width
    var naturalHeight = image.height;
    // 当前容器大小
	// 兼容切换图片时缩放了大小后的重新初始化
    var $d = $(document);
    currentWidth  = $d.width()  - $('#left_content').width();
    currentHeight = $d.height() - $('#head').height();

//    scaleX = (currentWidth   / naturalWidth).toFixed(2);
//    scaleY = (currentHeight / naturalHeight).toFixed(2);

    scaleX = (currentWidth   / naturalWidth);
    scaleY = (currentHeight / naturalHeight);


    scale = Math.min(scaleX, scaleY);
    
    $("#timg").css({
        //"height": $d.height() - 60 + "px"
        "height": naturalHeight * scale + "px",
        "width" : naturalWidth  * scale + "px"
    })
    .attr('initWidth',  naturalWidth  * scale + "px")
    .attr('initHeight', naturalHeight * scale + "px")
    .attr('realWidth',  naturalWidth)
    .attr('realHeight', naturalHeight)
    .attr('scale', scale)
    .attr('scaleX', scaleX)
    .attr('scaleY', scaleY);;
    firstLoad = false;
}
//事件监听
document.addEventListener("fullscreenchange", screenChange, false);  
document.addEventListener("mozfullscreenchange", screenChange, false);  
document.addEventListener("webkitfullscreenchange", screenChange, false);  
document.addEventListener("msfullscreenchange", screenChange, false);

function screenChange(){
	state = document.fullscreen;
	$('#state').html(state ? '退出' : '全屏');
	setTimeout(() => {
		firstLoad = true;
	    setImgInitScale();
	    refushLocation();
	}, 100);
}
// 
function prevPage(){
	var preId = loucengId;
	$.each(floorDataList, function(i, item){
		if(item.id == preId){
			if(i == 0){
				query(floorDataList[floorDataList.length - 1].id, true);
			} else {
				query(floorDataList[i - 1].id, true);
			}
		}
	});
}

function nextPage(){
	var preId = loucengId;
	$.each(floorDataList, function(i, item){
		if(item.id == preId){
			if(i == floorDataList.length - 1){
				query(floorDataList[0].id, true);
			} else {
				query(floorDataList[i + 1].id, true);
			}
		}
	});
}
