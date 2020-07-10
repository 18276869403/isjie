// var USER_ID; //websocket用户标示
// var nums = 0;
// con = '';
// var hearttime = null; //心跳定时
// var ws_type = {
// 	LOGON: "logon",
// 	LOGOUT: "logout",
// 	HEARTBEAT: "heartbeat",
// 	WARNING: "warning",
// 	DATAWARN: "dataWarning",
// 	SIGLEDATA: "sigleData",
// 	MOREDATA: "moreData",
//     NODATAWARN:"noDataWarn"
// };
// $(function() {
// 	var loginName = window.sessionStorage.getItem("loginName");
// 	var userSign = window.sessionStorage.getItem("userSign");
// 	USER_ID = loginName + userSign;
// 	initWebsocket();

// })
/**
 * 初始化WebSocket
 */
function initWebsocket() {
	var protocol = window.location.protocol == "https:" ? "wss://" : "ws://";
	var domain = "47.100.201.100:8080/ZYunWebServer";
	var socketURL = protocol + domain + "/websocket";
	socket = new WebSocket(socketURL);

	socket.onopen = function() {
		mylog("WS is opened. ");
		logon();
	};

	socket.onmessage = function(evt) {
		// 收到消息回调f
		if(evt.data != "X") {
			
		}
	};

	socket.onclose = function(event) {
		mylog("WS is closed. " + event);
		clearInterval(hearttime);
		socket = undefined;
	};

	socket.onerror = function(event) {
		mylog("WS is error. " + event.data);
		clearInterval(hearttime);
		socket = undefined;
	};
}



/**
 * 登出
 */
function loginOut() {
	var params = {
		type: ws_type.LOGOUT,
		user: USER_ID
	};
	sendWebsocket(params);
}

/**
 * 签入
 */
function logon() {
	var params = {
		type: ws_type.LOGON,
		user: USER_ID
	};
	sendWebsocket(params);
}


/**
 * 心跳
 */
function heartbeat() {
	var params = {
		type: ws_type.HEARTBEAT,
		user: USER_ID
	}
	sendWebsocket(params);
}

/**
 * 发送WebScoket请求
 * 
 * @param params
 */
function sendWebsocket(params) {
	if(socket) {
		var message = JSON.stringify(params);
		mylog("send websocket message: " + message)
		socket.send(message);
	} else {
		mylog("websocket is closed!");
	}

}



/**
 * 打印控制台日志
 * 
 * @param message
 */
function mylog(message) {
	console.log("------> " + message);
}