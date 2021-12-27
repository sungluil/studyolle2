package com.studyolle.infra.config;



import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.studyolle.modules.account.AccountService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final AccountService accountService; 
	private final DataSource dataSource; 
	
	//csrf기능이 활성화되어있음 기본적으로
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
				.mvcMatchers("/","/login","/sign-up","/check-email-token",
						"/email-login","/check-email-login","/login-link","/login-link"
						,"/login-email-token","/search/study").permitAll()
				.mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
				.anyRequest().authenticated();
		
		http.formLogin()
				.loginPage("/login").permitAll();
		
		http.logout()
				.logoutSuccessUrl("/");
		
		http.rememberMe()
				.userDetailsService(accountService)
				.tokenRepository(tokenRepository());
	}			
	
	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		web.ignoring()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
				.antMatchers("/favicon.ico", "/resources/**", "/error");
	}
}
