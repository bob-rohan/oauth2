package rohan.oauth2.resourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@SpringBootApplication
@EnableResourceServer
public class ResourceServerApplication extends ResourceServerConfigurerAdapter{

	public static void main(String[] args){
		SpringApplication.run(ResourceServerApplication.class, args);
	}
	
	/**
	 * tell Spring Security that it is allowed to let it through
	 */
	
	@Override
	 public void configure(HttpSecurity http) throws Exception {
	    http.cors().and().authorizeRequests()
	      .anyRequest().authenticated();
	  }
	
	/**
	 * explicitly disable HTTP Basic in the resource server (to prevent the browser from popping up authentication dialogs)
	 * 
	 * Aside: an alternative, which would also prevent the authentication dialog, would be to keep HTTP Basic but change the 401 challenge to something other than "Basic". You can do that with a one-line implementation of AuthenticationEntryPoint in the HttpSecurity configuration callback.
	 * 
	 */
	
	/*@Override
	  protected void configure(HttpSecurity http) throws Exception {
	    http.httpBasic().disable();
	    http.authorizeRequests().anyRequest().authenticated();
	  } */
	
	/**
	 * a custom HttpSessionStrategy that looks in the header ("X-Auth-Token" by default) instead of the default (cookie named "JSESSIONID"). 
	 */
	
	/*@Bean
	  HeaderHttpSessionStrategy sessionStrategy() {
	    return new HeaderHttpSessionStrategy();
	  } */

}
