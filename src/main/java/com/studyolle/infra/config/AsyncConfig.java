package com.studyolle.infra.config;

import java.util.concurrent.Executor;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		// TODO Auto-generated method stub
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int processors = Runtime.getRuntime().availableProcessors();//프로세스 갯수
		log.info("processor count {}",processors);
		/**
		 * 수영장에 존재하는 튜브 갯수 예)10개
		 */
		executor.setCorePoolSize(processors);
		
		/**
		 * 큐가 즉 대기열이 50까지 꽉찼을때 그다음 51번째면 
		 * 1첫번째 대기열에 있는놈한테 튜브만들어서줌 2배 즉 20개가 될때까지
		 */
		executor.setMaxPoolSize(processors*2);
		
		
		/**
		 * 튜브가 10개라고 가정할때 꽉찼을때 대기열을 50까지
		 */
		executor.setQueueCapacity(50);
		
		/**
		 * 첨에 10개 추가로 10개로 만들어낸 그 10개에 대하여 얼마뒤에 
		 * 정리할것인가에 대한 1분 이라고 설정(즉 몇초나 살려둘것인가)
		 */
		executor.setKeepAliveSeconds(60);
		
		/**
		 * 쓰레드 이름 지정
		 */
		executor.setThreadNamePrefix("AsyncExecutor-");
		executor.initialize();
		return executor;
	}
}
