function imgPlay(cid) {
	var id = "";
	//降雨估测
	if (cid == "jygc") {
		id = 'img_jygc';
		szqbl.map.image.init(id, [ 108.505, 19.0419 ], [ 117.505, 25.9719 ], [ 1, 20 ], true);
		szqbl.map.init(id, [ 114.2, 22.647 ], 8, [ new AMap.TileLayer(), szqbl.map.image.layer[id] ]);
		szqbl.map.image.loadData(id, 'szImages/radarRain/radarRain_png.js', 'SZ121_RadarRain', 1000, function(time, index, size) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").text(time);
		}, function(l1, l2) {
			var li = '';
			for (var i = 0; i < l1; i++) {
				li += '<li style="width:' + (100 / (l1 + l2)).toFixed(3) + '%">' + (i + 1) + '</li>';
			}
			for (var i = 0; i < l2; i++) {
				li += '<li class="gt10" style="width:' + (100 / (l1 + l2)).toFixed(3) + '%">' + (l1 + i + 1) + '</li>';
			}
			$(".box_" + id + " .roll").html(li);
			$(".box_" + id + " .legend").html('<img class="legend" style="width: 250px;" src="'+Local_Root +'/map/radarRain.png" />');
		});
	}
	//卫星云图
	if (cid == "wxyt") {
		id = 'img_wxyt';
		szqbl.map.image.init(id, [ 62, 0 ], [ 162, 56.4 ], [ 1, 20 ], true);
		szqbl.map.initAdv(id, {
			center : [ 114.2, 22.647 ],
			zoom : 5,
			layers : [ szqbl.map.image.layer[id] ],
			crs : 'EPSG4326'
		});
		szqbl.map.image.loadData(id, 'szImages/satelliteFY4A/4km_ir_png/satellite.js', 'SZ121_Satellite', 1000, function(time, index, size) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").text(time);
		}, function(size) {
			var li = '';
			for (var i = 0; i < size; i++) {
				li += '<li style="width:' + (100 / size).toFixed(3) + '%">' + (i + 1) + '</li>';
			}
			$(".box_" + id + " .roll").html(li);
		});

	}

	//雷达图象
	if (cid == "ldtx") {
		id = 'img_ldtx';
		szqbl.map.image.init(id, [ 108.505, 19.0419 ], [ 117.505, 26.0419 ], [ 1, 20 ], true);
		szqbl.map.init(id, [ 114.2, 22.647 ], 8, [ new AMap.TileLayer(), szqbl.map.image.layer[id] ]);
		szqbl.map.image.loadData(id, 'szImages/radar/radar_png.js', 'SZ121_Radar', 1000, function(time, index, size) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").text(time);
		}, function(size) {
			var li = '';
			for (var i = 0; i < size; i++) {
				li += '<li style="width:' + (100 / size).toFixed(3) + '%">' + (i + 1) + '</li>';
			}
			$(".box_" + id + " .roll").html(li);
			$(".box_" + id + " .legend").html('<img class="legend" style="width: 250px;" src="'+Local_Root +'map/radar.png" />');
		});
	}
	//气温
	if (cid == "wd") {
		id = 'img_wd';
		szqbl.map.image.init(id, [ 108.505, 19.0419 ], [ 117.505, 26.0419 ], [ 1, 20 ]);
		szqbl.map.init(id, [ 113.0, 22.647 ], 6, [ new AMap.TileLayer(), szqbl.map.image.layer[id] ]);
		szqbl.map.image.loadData(id, 'szImages/temperature/temperature.js', 'SZ121_Temperature', 1000, function(time, index, size) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").text(time);
		}, function(size) {
			var li = '';
			for (var i = 0; i < size; i++) {
				li += '<li style="width:' + (100 / size).toFixed(3) + '%">' + (i + 1) + '</li>';
			}
			$(".box_" + id + " .roll").html(li);
			$(".box_" + id + " .legend").html('<img class="legend" style="width: 250px;" src="'+Local_Root +'map/Temperature.png" />');
		});
	}

	$(".box_" + id + " .play-btn .play").click(function() {
		if (szqbl.map.image.isPlay[id]) {
			szqbl.map.image.stop(id);
			$(this).css('background', 'url('+Local_Root+'qlib/ImgPlay/img/c-btn.png) no-repeat center center');
			$(this).css('background-size', 'auto 25px');
		} else {
			szqbl.map.image.start(id, 1000, function(time, index) {
				$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
				$(".box_" + id + " .time").text(time);
			});
			$(this).css('background', 'url('+Local_Root+'qlib/ImgPlay/img/c-btn_1.png) no-repeat center center');
			$(this).css('background-size', 'auto 25px');
		}
	});
	$(".box_" + id + " .play-btn .prev").click(function() {
		szqbl.map.image.prev(id, function(time, index) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").html(time);
		});
		$(".box_" + id + " .play-btn .play").css('background', 'url('+Local_Root+'qlib/ImgPlay/img/c-btn.png) no-repeat center center');
		$(".box_" + id + " .play-btn .play").css('background-size', 'auto 25px');
	});
	$(".box_" + id + " .play-btn .next").click(function() {
		szqbl.map.image.next(id, function(time, index) {
			$(".box_" + id + " .roll li:eq(" + index + ")").addClass('on').siblings().removeClass('on');
			$(".box_" + id + " .time").html(time);
		});
		$(".box_" + id + " .play-btn .play").css('background', 'url('+Local_Root+'qlib/ImgPlay/img/c-btn.png) no-repeat center center');
		$(".box_" + id + " .play-btn .play").css('background-size', 'auto 25px');
	});
}