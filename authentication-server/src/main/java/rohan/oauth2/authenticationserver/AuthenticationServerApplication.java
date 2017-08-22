package rohan.oauth2.authenticationserver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class AuthenticationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServerApplication.class, args);
	}

	/**
	 * Custom FilterRegistrationBean bean permits all OPTIONS requests.
	 * 
	 * TODO: On review AL queried this as potentially being a bit heavy handed. BR to follow up with investigation.
	 */
	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	/**
	 * Configuration class to provide form login for angularui
	 */
	@Configuration
	@Order(99)
	public static class AngularUiSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			//@formatter:off		
			http
				.requestMatchers().requestMatchers(getRequestMatchers())
				.and().formLogin().loginPage("/angularLogin").loginProcessingUrl("/login").permitAll()
				.and().authorizeRequests().anyRequest().authenticated();
			//@formatter:on

		}
		
		/*
		 * Requests are valid for this filter chain if 
		 *  - the url pattern matches ....
		 *  - the request paramaeter "client_id" matches ...
		 */
		private RequestMatcher getRequestMatchers(){
			List<RequestMatcher> matchers = new ArrayList<>();
			
			for (String pattern : new String[]{"/login", "/angularLogin"}) {
				matchers.add(new AntPathRequestMatcher(pattern, null));
			}
			
			matchers.add(new ClientRequestMatcher("angularui"));
			
			return new OrRequestMatcher(matchers);
		}
	}
	
	/**
	 * Configuration class to provide form login for mvcui
	 */
	@Configuration
	@Order(98)
	public static class MvcUiSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			//@formatter:off		
			http
				.requestMatchers().requestMatchers(getRequestMatchers())
				.and().formLogin().loginPage("/mvcLogin").loginProcessingUrl("/login").permitAll()
				.and().authorizeRequests().anyRequest().authenticated();
			//@formatter:on

		}
		
		/*
		 * Requests are valid for this filter chain if 
		 *  - the url pattern matches ....
		 *  - the request paramaeter "client_id" matches ...
		 */
		private RequestMatcher getRequestMatchers(){
			List<RequestMatcher> matchers = new ArrayList<>();
			
			for (String pattern : new String[]{"/login", "/mvcLogin"}) {
				matchers.add(new AntPathRequestMatcher(pattern, null));
			}
			
			matchers.add(new ClientRequestMatcher("mvcui"));
			
			return new OrRequestMatcher(matchers);
		}
	}
	
	protected static class ClientRequestMatcher implements RequestMatcher{

		private String clientId;
		
		public ClientRequestMatcher(final String clientId) {
			this.clientId = clientId.toUpperCase();
		}
		
		@Override
		public boolean matches(HttpServletRequest request) {
			String requestClientId = request.getParameter("client_id");
						
			return (requestClientId != null && requestClientId.toUpperCase().matches(clientId));
		}
		
	}

	/**
	 * Configuration class to protect resources.
	 * 
	 * NB: extending ResourceServerConfigurer as suggested by
	 * '@EnableResourceServer interferes with the formLogin provider configured
	 * by
	 * {@link rohan.oauth2.authenticationserver.AuthenticationServerApplication.LoginSecurityConfiguration }
	 */
	@Configuration
	@EnableResourceServer
	public static class ResourceSecurityConfiguration {

	}

	@Configuration
	@EnableAuthorizationServer
	public static class CustomSecurityConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("customUserDetailsService")
		private UserDetailsService userDetailsService;

		@Autowired
		TokenStore tokenStore;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

			// hooks our custom user details service into the basic
			// authentication filter
			endpoints.userDetailsService(userDetailsService);

			// allows logout to reference the token store
			endpoints.tokenStore(tokenStore);
		}

		/*
		 * registers the application (... aka the "client") as an approved
		 * requester
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients.inMemory()
						.withClient("mvcui")
							.secret("secret")
							.scopes("open")
							.authorizedGrantTypes("authorization_code")
							.autoApprove(true)
							.accessTokenValiditySeconds(120)
							.refreshTokenValiditySeconds(500)
							.redirectUris("http://localhost:8333/")
					.and()
						.withClient("angularui")
							.scopes("open")
							.authorizedGrantTypes("implicit")
							.autoApprove(true)
							.accessTokenValiditySeconds(120)
							.redirectUris("http://localhost:8089/html/oauth_callback.html");
							
			// @formatter:on
		}

		@Bean
		public TokenStore tokenStore() {
			return new InMemoryTokenStore();
		}
	}
}
