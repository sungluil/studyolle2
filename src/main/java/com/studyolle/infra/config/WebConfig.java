package com.studyolle.infra.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.studyolle.modules.notification.NotificationInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final NotificationInterceptor notificationInterceptor; 
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		List<String> staticResourcesPath =
				Arrays.stream(StaticResourceLocation.values())
					.flatMap(StaticResourceLocation::getPatterns)
					.collect(Collectors.toList());
		staticResourcesPath.add("/node_modules/**");
		
		System.out.println(staticResourcesPath);
		registry.addInterceptor(notificationInterceptor)
			.excludePathPatterns(staticResourcesPath);
	}
}
