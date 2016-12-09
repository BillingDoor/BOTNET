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
	 @Order('1')
	protected void configure(HttpSecurity http) throws Exception {


	    // Enable csrf for login form
	    http.csrf().requireCsrfProtectionMatcher(kk );
	    // Configure login page
	    http.formLogin().loginPage("/site/login").usernameParameter("ssoId").passwordParameter("password").failureUrl("/login?error").defaultSuccessUrl("/site/index").loginProcessingUrl("/site/login");
	    //Configure remember me
	    http.rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository).tokenValiditySeconds(86400);
	    
	    // Configure logout redirect
	    http.logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("remember-me");
	    // Ensure admin pages have correct role
	    http.authorizeRequests().antMatchers("/site/user/**").access("hasRole('ADMIN') and hasRole('USER')");
	    http.authorizeRequests().antMatchers("/site/admin/**").hasRole("ADMIN");
	    http.authorizeRequests().antMatchers("/", "/bot**", "/site/login*"/* ,"/resources/**" */).permitAll();
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

	@Bean
	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}
	/*
	 * http .authorizeRequests() .antMatchers("/",
	 * "/home","/welcome","/hmac","/index","/test","/list","/userlist",
	 * "/testing","/index","forms","maps").permitAll() .and() .formLogin()
	 * .loginPage("/login") .permitAll() .and() .logout() .permitAll();
	 * http.csrf().disable();
	 */
	
	// http.authorizeRequests().antMatchers("/","/welcome/*","/hmac").permitAll().
	// antMatchers("/index").access("hasRole('ADMIN')").and().
	// formLogin().loginPage("/login").defaultSuccessUrl("/index").loginProcessingUrl("/login").usernameParameter("ssoId").passwordParameter("password").and()
	// .rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
	// .tokenValiditySeconds(86400).and().exceptionHandling().accessDeniedPage("/Access_Denied");
	// http.csrf().disable();
	////
	//
	
	// http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
	// .antMatchers("/**").hasRole("USER").and().formLogin();
	/*
	 * http.authorizeRequests().antMatchers("/newuser/**",
	 * "/delete-user-*").permitAll().antMatchers("/edit-user-*")
	 * 
	 * .permitAll().and().formLogin().loginPage("/login").and().csrf().
	 * requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher());
	 * .loginProcessingUrl("/login").usernameParameter("ssoId").
	 * passwordParameter("password").and()
	 * .rememberMe().rememberMeParameter("remember-me").tokenRepository(
	 * tokenRepository)
	 * .tokenValiditySeconds(86400).and().csrf().and().exceptionHandling().
	 * accessDeniedPage("/Access_Denied");
	 */

	// .authorizeRequests()

	// .and()
	// @Autowired
	// private DataSource dataSource;
	//
	// @Autowired
	// public void configureGlobal(AuthenticationManagerBuilder auth) throws
	// Exception {
	// auth
	// .jdbcAuthentication()
	// .dataSource(dataSource)
	// .withDefaultSchema()
	// .withUser("user").password("password").roles("USER").and()
	// .withUser("admin").password("password").roles("USER", "ADMIN");
	//
	//
	//
	////

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http.headers().addHeaderWriter(
	// new XFrameOptionsHeaderWriter(
	// new WhiteListedAllowFromStrategy(Arrays.asList("localhost:8080",
	// "http://localhost"))))
	// .and().csrf().requireCsrfProtectionMatcher(new
	// CsrfSecurityRequestMatcher())
	// .and().authorizeRequests()
	// .antMatchers("/login", "/logout.do","/console/**").permitAll()
	// .antMatchers("/**").authenticated()
	// .and()
	// .formLogin()
	// .loginProcessingUrl("/login.do")
	// .usernameParameter("name")
	// .loginPage("/login")
	// .and()
	// .logout()
	// .logoutRequestMatcher(new AntPathRequestMatcher("/logout.do"))
	// .and()
	// .userDetailsService(userDetailsService());
	// }

	// http
	// .csrf().disable()
	// .authorizeRequests()
	// .antMatchers("/anonymous*").anonymous()
	// .antMatchers("/login*").permitAll()
	// .anyRequest().authenticated()
	// .and()
	// .formLogin()
	// .loginPage("/login.html")
	// .loginProcessingUrl("/login")
	// .successHandler(successHandler())
	// .failureUrl("/login.html?error=true")
	// .and()
	// .logout().deleteCookies("JSESSIONID")
	// .and()
	// .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)
	// .and()
	// .sessionManagement()
	// .sessionFixation().migrateSession()
	// .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	// .invalidSessionUrl("/invalidSession.html")
	// .maximumSessions(2)
	// .expiredUrl("/sessionExpired.html");
	//

	// http
	// .csrf().disable()
	// .authorizeRequests()
	// .antMatchers("/anonymous*").anonymous()
	// .antMatchers("/login*").permitAll()
	// .anyRequest().authenticated()
	// .and()
	// .requiresChannel()
	// .antMatchers("/login*", "/perform_login").requiresSecure()
	// .anyRequest().requiresInsecure()
	// .and()
	// .sessionManagement()
	// .sessionFixation()
	// .none()
	// .and()
	// .formLogin()
	// .loginPage("/login.html")
	// .loginProcessingUrl("/perform_login")
	// .defaultSuccessUrl("/homepage.html",true)
	// .failureUrl("/login.html?error=true")
	// .and()
	// .logout()
	// .logoutUrl("/perform_logout")
	// .deleteCookies("JSESSIONID")
	// .logoutSuccessHandler(logoutSuccessHandler());
	// @formatter:on
	// }

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http.authorizeRequests().anyRequest().authenticated().and().x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)").userDetailsService(userDetailsService());
	// }
	//
	// @Bean
	// public UserDetailsService userDetailsService() {
	// return new UserDetailsService() {
	// @Override
	// public UserDetails loadUserByUsername(String username) throws
	// UsernameNotFoundException {
	// if (username.equals("cid")) {
	// return new User(username, "",
	// AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
	// }
	// throw new UsernameNotFoundException("User not found!");
	// }
	// };
	// }

}