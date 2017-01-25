package cs.sii.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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

	@Autowired
	CsrfSecurityRequestMatcher kk;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.requiresChannel().antMatchers("/site/**").requiresSecure();
		http.requiresChannel().antMatchers("/bot/**").requiresSecure();
		http.requiresChannel().antMatchers("/cec/**").requiresSecure();
		
		// Enable csrf for login form
		http.csrf().requireCsrfProtectionMatcher(kk);
		// Configure login page
		http.formLogin().loginPage("/site/login").usernameParameter("ssoId").passwordParameter("password")
				.failureUrl("/login?error").defaultSuccessUrl("/site/index").loginProcessingUrl("/site/login");
		// Configure remember me
		http.rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
				.tokenValiditySeconds(86400);

		// Configure logout redirect
		http.logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("remember-me");
		// Ensure admin pages have correct role
		http.authorizeRequests().antMatchers("/site/user/**").hasAnyRole("ADMIN,USER");
		http.authorizeRequests().antMatchers("/site/admin/**").hasRole("ADMIN");
		http.authorizeRequests().antMatchers("/", "/bot**", "/cec**"/* ,"/resources/**" */).permitAll();

		// Configure access denied exception redirect
		http.exceptionHandling().accessDeniedPage("/404");

	}

	@Bean
	public CsrfSecurityRequestMatcher csrfMatch() {

		return new CsrfSecurityRequestMatcher();
	}

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

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}

}