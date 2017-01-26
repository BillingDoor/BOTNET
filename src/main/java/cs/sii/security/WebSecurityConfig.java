package cs.sii.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
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
				.failureUrl("/site/login").defaultSuccessUrl("/site/index").loginProcessingUrl("/site/login");
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
		
		return new PasswordEncoder() {
			
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
		        try {
		            MessageDigest md = MessageDigest.getInstance("SHA-512");
		            md.update(rawPassword.toString().getBytes());
		            byte[] bytes = md.digest(rawPassword.toString().getBytes());
		            StringBuilder sb = new StringBuilder();
		            for(int i=0; i< bytes.length ;i++)
		            {
		                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		            }
		            String generatedPassword = sb.toString();
		            System.out.println("encoded "+encodedPassword);
		            System.out.println("raw "+ rawPassword);
		            System.out.println("gene "+generatedPassword);
		            if (encodedPassword.equals(generatedPassword)) {
						System.out.println("tutto apposto fraaaaa");
						return true;
					}
		        } 
		        catch (NoSuchAlgorithmException e) 
		        {
		            e.printStackTrace();
		        }
		        return false;
			}
			
			@Override
			public String encode(CharSequence rawPassword) {
				 String generatedPassword = null;
			        try {
			            MessageDigest md = MessageDigest.getInstance("SHA-512");
			            md.update(rawPassword.toString().getBytes());
			            byte[] bytes = md.digest(rawPassword.toString().getBytes());
			            StringBuilder sb = new StringBuilder();
			            for(int i=0; i< bytes.length ;i++)
			            {
			                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			            }
			            generatedPassword = sb.toString();
			        } 
			        catch (NoSuchAlgorithmException e) 
			        {
			            e.printStackTrace();
			        }
			        return generatedPassword;
	
			    }
			};
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