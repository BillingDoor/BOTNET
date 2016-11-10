package cs.sii.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 	
@Autowired
@Qualifier("customUserDetailsService")
UserDetailsService userDetailsService;	
	
@Autowired
PersistentTokenRepository tokenRepository;

@Override
protected void configure(HttpSecurity http) throws Exception {

	http
    .authorizeRequests()
        .antMatchers("/", "/home","/welcome","/hmac").permitAll()
        .and()
    .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
    .logout()
        .permitAll();
	//http.csrf().disable(); 
	
/*
 	http.authorizeRequests().antMatchers("/newuser/**", "/delete-user-*").permitAll().antMatchers("/edit-user-*")
 
			.permitAll().and().formLogin().loginPage("/login")
			.loginProcessingUrl("/login").usernameParameter("ssoId").passwordParameter("password").and()
			.rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
			.tokenValiditySeconds(86400).and().csrf().and().exceptionHandling().accessDeniedPage("/Access_Denied");
*/
}

//
//@Override
//protected void configure(HttpSecurity http) throws Exception {
//    http.headers().addHeaderWriter(
//            new XFrameOptionsHeaderWriter(
//                    new WhiteListedAllowFromStrategy(Arrays.asList("localhost:8080", "http://localhost"))))
//            .and().csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher())
//            .and().authorizeRequests()
//        .antMatchers("/login", "/logout.do","/console/**").permitAll()
//        .antMatchers("/**").authenticated()
//    .and()
//        .formLogin()
//            .loginProcessingUrl("/login.do")
//            .usernameParameter("name")
//            .loginPage("/login")
//    .and()
//        .logout()
//            .logoutRequestMatcher(new AntPathRequestMatcher("/logout.do"))
//    .and()
//            .userDetailsService(userDetailsService());
//}



@Bean
public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
}

@Bean
public DaoAuthenticationProvider authenticationProvider() {
	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	authenticationProvider.setUserDetailsService(userDetailsService);
	authenticationProvider.setPasswordEncoder(passwordEncoder());
	return authenticationProvider;
}

@Bean
public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices() {
	PersistentTokenBasedRememberMeServices tokenBasedservice = new PersistentTokenBasedRememberMeServices(
			"remember-me", userDetailsService, tokenRepository);
	return tokenBasedservice;
}

@Bean
public AuthenticationTrustResolver getAuthenticationTrustResolver() {
	return new AuthenticationTrustResolverImpl();
}

	
}