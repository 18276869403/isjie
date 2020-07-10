var iPlay = new function(){
	this.use = false;
	this.runId = null;
	this.running = false;
	this.currentIndex = 0;
	this.id = "";
	this.imgList = null;
	this.time = 500;
	this.count = 11;//前11张实况，否则为预报
	this.errorSrc = "";
	
	this.init = function(id, data, time, count, errorSrc){
		var _this = iPlay;
		if (_this.running) {
			_this.stopRunning();
		}
		_this.currentIndex = 0;
		if(id){
			_this.id = id;
		}else{
			alert("id不能为空");
		}
		if(data){
			_this.imgList = data;
		}else{
			alert("data不能为空");
		}
		if(time)_this.time = time;
		if(count)_this.count = count;
		if(errorSrc)_this.errorSrc = errorSrc;
		var addCell = '';
		for (var i = 0; i < _this.imgList.length; i++) {
			addCell += '<li id=cell_' + i + ' onmouseover="iPlay.changeImg(' + i + ')" ></li>';
		}
		$("#"+id+" .img").html('<img id="pic_xxx" class="chartImage" src="'
				+ _this.imgList[_this.imgList.length - 1] + '" onerror="javascript: this.src = \'' + _this.errorSrc + '\'" />');
		
		//图片加载完成执行
		var defereds = [];
		$(".chartImage").each(function() {
			_this.stopRunning();
			var dfd = $.Deferred();
			$(this).load(dfd.resolve);
			defereds.push(dfd);
		});
		$.when.apply(null, defereds).done(function() {
			$("#"+id+" .cell").html(addCell); // 小方格插入到页面中
			$("#cell_0").addClass('on').siblings().removeClass('on'); // 刚加载完毕，默认第一个小方格的样式
			_this.stopRunning();
			for (var i = 0; i < _this.imgList.length; i++) {
				$("#pic_xxx").attr('src', _this.imgList[_this.imgList.length - i - 1]);
			}
			_this.playImg();
		});
		
		
	}
	this.playImg = function() { // 鼠标点击后会触发
		var _this = iPlay;
		if (_this.running) {
			_this.stopRunning();
		} else {
			_this.running = true;
			_this.startRunning();
		}
	};

	this.startRunning = function() { // 动画播放
		var _this = iPlay;
		if (_this.running == false)
			return;
		_this.running = true;

		$("#"+_this.id+" .role").css("background", "url(" + contextPath + "/qlib/ImgPlay/img/play.png)");
		for (var i = 0; i < _this.imgList.length; i++) {
			$("#cell_" + i).removeClass('red');
			// //先将所有小方格的样式设置成默认统一样式
			if (i >= _this.count-1) {
				$("#cell_" + i).addClass('red'); // 11-21是预报，统一另一种默认的样式
			}
		}
		if (_this.currentIndex > _this.imgList.length - 1) {
			_this.currentIndex = 0;
		}
		$("#cell_" + _this.currentIndex).addClass('on').siblings().removeClass('on'); // 当前小方格显示样式
		$("#pic_xxx").attr(
			'src',
			_this.imgList[_this.imgList.length
			- _this.currentIndex - 1]);
		_this.currentIndex++;
		window.clearTimeout(_this.runId);
		_this.runId = window.setTimeout(_this.startRunning, _this.time);
	};

	this.stopRunning = function() { // 动画播放终止
		var _this = iPlay;
		this.running = false;
		$("#"+_this.id+" .role").css("background", "url(" + contextPath + "/qlib/ImgPlay/img/start.png)");
		if (_this.runId != null) {
			window.clearTimeout(_this.runId);
			_this.runId = null;
		}
	};
	
	this.changeImg = function(index) { // 鼠标悬停时会触发,index: 获取当前鼠标悬停位置是正数第几个小方格
		var _this = iPlay;
		try {
			if (_this.use == true) { // 解决同步冲突问题
				return;
			} else {
				_this.use = true;
				_this.currentIndex = index;
				
				$("#cell_" + _this.currentIndex ).addClass('on').siblings().removeClass('on'); // 当前小方格显示样式

				$("#pic_xxx").attr(
						'src',
						_this.imgList[_this.imgList.length
						- _this.currentIndex - 1]);
				this.stopRunning(); // 鼠标悬停时，停止动画播放
				_this.use = false;
			}
		} catch (e) {
			alert("数据错误");
		}
	};
};