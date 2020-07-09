/**
 * 通用播放器
 */
$(function(){	
	var pointer = 0; 				
	var speed = 500;				
	var palyerObj = null;
	var runStat = 0;	
	var lock = false;
	var timeFlag = Math.random();
	
	function palyer(){
		palyerObj = setInterval(function(){
			next();			
		}, speed);
	}
	
	function start(){
		if(runStat != 1){
			runStat = 1;	
			lock = true;
			palyer();
		}
		//改变图标
		$('.controller ul li').eq(1).children(0).attr('src',Local_Root+'images/stop.png');
	}

	function stop(){
		if(runStat != 0){
			clearInterval(palyerObj);
			lock = false;
			runStat = 0;
		}
		$('.controller ul li').eq(1).children(0).attr('src',Local_Root+'images/start.png');
	}

	function next(){
		pointer++;
		if(pointer >= playerQueue.length){
			pointer = 0;
		}
		var img =  $('.data-box .dataImg');		
		if(pointer == (playerQueue.length-1) && runStat == 1){
			stop();
			setTimeout(function(){
				start();
			}, 2000);
		}
		if(runStat == 3){
				lock = true;
				$(img).animate({opacity: 0.3 }, 300);
				$(img).attr('src',prefix+playerQueue[pointer]);		
				$(img).animate({opacity: 1 }, 500 , function(){lock = false; });				
			return;
		}
		$(img).attr('src',prefix+playerQueue[pointer]);	
	}
	
	$('.data-box .dataImg').load(function(){
		$('#loading').css('background','rgb(0,0,255)');
		$('#loading').css('width',(pointer + 1)*(100/playerQueue.length)+'%');
	});

	var img =  $('.data-box .dataImg');
	
	/*$(img).error(function(){
		console.log(prefix+playerQueue[0])
		$('.data-box .dataImg').attr('src',prefix+playerQueue[9]);
		$('#loading').css('background','rgb(255,0,0)');
	});*/
	
	function back(){
		pointer--;
		if(pointer < 0){
			pointer = playerQueue.length - 1;
		}

		var img =  $('.data-box .dataImg');
		if(runStat == 3){
			lock = true;
			$(img).animate({opacity: 0.3 }, 300);
			$(img).attr('src',prefix+playerQueue[pointer]);
			$(img).animate({opacity: 1 }, 500 , function(){lock = false; });			
			return;
		}
		$(img).attr('src',prefix+playerQueue[pointer]);
	}
	//后退
	$('.controller ul li').eq(0).click(function(){
		stop();
		back();
	});
	
	//播放 暂停
	$('.controller ul li:eq(1) img').click(function(){
		if(runStat==0){
			start();
		}else{
			stop();
		}
	});
	
	//前进
	$('.controller ul li').eq(2).click(function(){
		stop();
		next();
	});
	
	var s = $('.controller ul li').eq(1);
	$('.data-box .dataImg').error(function(){
		//加载图片异常。使用默认图片
	});
	
	//网页后退
	$('.go-back').click(function(){
		window.history.back();
	});
	
	$('.data-box .dataImg').bind('swipeleft',function(e){
		if(!lock){
			runStat = 3;
			back();
		}
	});
	
	$('.data-box .dataImg').bind('swiperight',function(e){
		if(!lock){
			runStat = 3;
			next();
		}		
	});

});
