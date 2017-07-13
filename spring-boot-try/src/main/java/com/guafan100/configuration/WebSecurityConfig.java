package com.guafan100.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.guafan100.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// @formatter:off
		
		http.authorizeRequests()
				.antMatchers(
						"/",
						"/about",
						"/register")
				.permitAll()
				.antMatchers(
					"/js/*",
					"/css/*",
					"/img/*")
				.permitAll()
				.antMatchers(
						"/addstatus",
						"/editstatus",
						"/deletestatus",
						"/viewstatus")
				.hasRole("ADMIN")
				.antMatchers(
						"/profile",
						"/profile/*",
						"/edit-profile-about",
						"/upload-profile-photo",
						"/profilephoto/*",
						"/save-interest",
						"/delete-interest"
						)
				.authenticated()
				.anyRequest()
				.denyAll()
				.and()
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.permitAll()
				.and()
			.logout()
				.permitAll();
						
		// @formatter:on
	}
	
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		// @formatter:off
//		auth
//			.inMemoryAuthentication()
//			.withUser("luo")
//			.password("hello")
//			.roles("USER");
//			
//		// @formatter:on
//	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}

	
}
