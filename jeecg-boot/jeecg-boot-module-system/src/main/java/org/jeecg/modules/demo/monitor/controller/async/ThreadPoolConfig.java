package org.jeecg.modules.demo.monitor.controller.async;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {
	@Bean("simpleThreadPool")
    public ThreadPoolTaskExecutor simpleThreadPool(){
        ThreadPoolTaskExecutor simpleThreadPool = new ThreadPoolTaskExecutor();
        simpleThreadPool.setCorePoolSize(5);//<!--核心线程数 -->
        simpleThreadPool.setMaxPoolSize(10000);//<!--最大线程数 -->
        simpleThreadPool.setQueueCapacity(10 * 10);//<!-- 队列大小 -->
        simpleThreadPool.setKeepAliveSeconds(3000);//<!--线程最大空闲时间 -->
        //配置拒绝策略
//        rejectedExecutionHandler字段用于配置拒绝策略，常用的拒绝策略如下：
//        AbortPolicy，用于被拒绝任务的处理程序，它将抛出RejectedExecutionException。
//        CallerRunsPolicy，用于被拒绝任务的处理程序，它直接在execute方法的调用线程中运行被拒绝的任务。
//        DiscardOldestPolicy，用于被拒绝任务的处理程序，它放弃最旧的未处理请求，然后重试execute。
//        DiscardPolicy，用于被拒绝任务的处理程序，默认情况下它将丢弃被拒绝的任务。
        simpleThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        simpleThreadPool.initialize();
        return simpleThreadPool;
    }
}
