package rohan.oauth2.authenticationserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@EnableAuthorizationServer
// serves the user resource, used during client authentication
@EnableResourceServer
public class AuthenticationServerApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServerApplication.class, args);
	}
	
	@Configuration
	public static class CustomSecurityConfiguration extends AuthorizationServerConfigurerAdapter{
	
		@Autowired
		@Qualifier("customUserDetailsService")
		private UserDetailsService userDetailsService;
		
		@Autowired
		private AuthenticationManager authenticationManager;
		
		@Autowired
		TokenStore tokenStore;
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			
			// provides the password grants - I'm not sure why, but it's needed.
			endpoints.authenticationManager(authenticationManager);
			
			// hooks our customer user details service into the basic authentication filter
			endpoints.userDetailsService(userDetailsService);
			
			// allows logout to reference the token store	
			endpoints.tokenStore(tokenStore);
		}
		
		// registers the application (... aka the "client") as an approved requester
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("acme").scopes("open").autoApprove(true);
		}
		
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
		
		@Bean
		public TokenStore tokenStore() {
		    return new InMemoryTokenStore();
		}
	}
}
