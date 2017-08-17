package rohan.oauth2.mvcui;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.RequestEnhancer;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

public class CustomSsoConfigurer {

	private ApplicationContext applicationContext;

	CustomSsoConfigurer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void configure(HttpSecurity http) throws Exception {
		OAuth2SsoProperties sso = this.applicationContext
				.getBean(CustomOAuth2SsoProperties.class);
		// Delay the processing of the filter until we know the
		// SessionAuthenticationStrategy is available:
		http.apply(new OAuth2ClientAuthenticationConfigurer(oauth2SsoFilter(sso)));
		addAuthenticationEntryPoint(http, sso);
	}

	private void addAuthenticationEntryPoint(HttpSecurity http, OAuth2SsoProperties sso)
			throws Exception {
		ExceptionHandlingConfigurer<HttpSecurity> exceptions = http.exceptionHandling();
		ContentNegotiationStrategy contentNegotiationStrategy = http
				.getSharedObject(ContentNegotiationStrategy.class);
		if (contentNegotiationStrategy == null) {
			contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
		}
		MediaTypeRequestMatcher preferredMatcher = new MediaTypeRequestMatcher(
				contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
				new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
		preferredMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
		
		// When multiple entry points are provided the default is the first one
		exceptions.defaultAuthenticationEntryPointFor(
				new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
				new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
	}

	private OAuth2ClientAuthenticationProcessingFilter oauth2SsoFilter(
			OAuth2SsoProperties sso) {
		OAuth2RestOperations restTemplate = this.applicationContext
				.getBean(UserInfoRestTemplateFactory.class).getUserInfoRestTemplate();
		
		ResourceServerTokenServices tokenServices = this.applicationContext
				.getBean(ResourceServerTokenServices.class);
		OAuth2ClientAuthenticationProcessingFilter filter = new CustomOAuth2ClientAuthenticationProcessingFilter(
				sso.getLoginPath());
		filter.setRestTemplate(restTemplate);
		filter.setTokenServices(tokenServices);
		filter.setApplicationEventPublisher(this.applicationContext);
		return filter;
	}

	private static class OAuth2ClientAuthenticationConfigurer
			extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

		private OAuth2ClientAuthenticationProcessingFilter filter;

		OAuth2ClientAuthenticationConfigurer(
				OAuth2ClientAuthenticationProcessingFilter filter) {
			this.filter = filter;
		}

		@Override
		public void configure(HttpSecurity builder) throws Exception {
			OAuth2ClientAuthenticationProcessingFilter ssoFilter = this.filter;
			ssoFilter.setSessionAuthenticationStrategy(
					builder.getSharedObject(SessionAuthenticationStrategy.class));
			builder.addFilterAfter(ssoFilter,
					AbstractPreAuthenticatedProcessingFilter.class);
		}

	}
	
	static class AcceptJsonRequestEnhancer implements RequestEnhancer {

		@Override
		public void enhance(AccessTokenRequest request,
				OAuth2ProtectedResourceDetails resource,
				MultiValueMap<String, String> form, HttpHeaders headers) {
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		}

	}

}
