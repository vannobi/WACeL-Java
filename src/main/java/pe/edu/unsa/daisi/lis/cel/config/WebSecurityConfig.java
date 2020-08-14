package pe.edu.unsa.daisi.lis.cel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	PersistentTokenRepository tokenRepository;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").permitAll()
        .and()
        .authorizeRequests().antMatchers("/assets/css").permitAll()
        .and()
        .authorizeRequests().antMatchers("/assets/img").permitAll()
        .and()
        .authorizeRequests().antMatchers("/assets/icons").permitAll()
        .and()
        .authorizeRequests().antMatchers("/index").permitAll()
        .and()
        .authorizeRequests().antMatchers("/login").permitAll()
        .and()
        .authorizeRequests().antMatchers("/login?logout").permitAll()
        .and()
        .authorizeRequests().antMatchers("/logout").permitAll()
        .and()
        .authorizeRequests().antMatchers("/timeout").permitAll()
        
        .and()
		.authorizeRequests().antMatchers("/view/**").authenticated() //hasAnyRole("ADMIN", "USER")
		.and()
		.authorizeRequests().antMatchers("/view/index**").permitAll()
		.and()
		.formLogin().loginPage("/index").loginProcessingUrl("/login").usernameParameter("login").passwordParameter("password").defaultSuccessUrl("/home/mainPage").failureUrl("/index?error").permitAll()
		.and()
		.logout().logoutUrl("/home/logout").logoutSuccessUrl("/index?logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll()
		.and()
		.rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
		.tokenValiditySeconds(86400)
		.and().exceptionHandling().accessDeniedPage("/Access_Denied")
		.and().csrf().disable();

		/*
     http.authorizeRequests().anyRequest().hasAnyRole("ADMIN", "USER")
    .and()
    .authorizeRequests().antMatchers("/login**").permitAll()
    .and()
    .formLogin().loginPage("/login").loginProcessingUrl("/loginAction").permitAll()
    .and()
    .logout().logoutSuccessUrl("/login").permitAll()
    .and()
    .csrf().disable();
		 */
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
