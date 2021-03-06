package rohan.oauth2.angularui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
//NB: we have nothing in this application which requires authentication currently, but budget-plus-angular-web has some controllers we'll want to secure.
@EnableOAuth2Sso
public class AngularUIApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(AngularUIApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.httpBasic()
				.and()
					.authorizeRequests()
						.antMatchers("/css/**", "/js/**", "/html/**", "/").permitAll()
						.anyRequest().authenticated()
				.and()
					.csrf()
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
			;
			// @formatter:on
	}
}
