package rohan.oauth2.mvcui;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;

public class CustomOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

	public OAuth2RestOperations restTemplate;

	private ResourceServerTokenServices tokenServices;

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();

	private ApplicationEventPublisher eventPublisher;
	
	private AccessTokenProvider accessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();

	/**
	 * Reference to a CheckTokenServices that can validate an OAuth2AccessToken
	 * 
	 * @param tokenServices
	 */
	public void setTokenServices(ResourceServerTokenServices tokenServices) {
		this.tokenServices = tokenServices;
	}

	/**
	 * A rest template to be used to obtain an access token.
	 * 
	 * @param restTemplate a rest template
	 */
	public void setRestTemplate(OAuth2RestOperations restTemplate) {
		super.setRestTemplate(restTemplate);
		this.restTemplate = restTemplate;
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		super.setApplicationEventPublisher(eventPublisher);
	}
	
	public CustomOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		setAuthenticationManager(new NoopAuthenticationManager());
		setAuthenticationDetailsSource(authenticationDetailsSource);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.state(restTemplate != null, "Supply a rest-template");
		super.afterPropertiesSet();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		OAuth2AccessToken accessToken;
		try {
			ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
			BeanUtils.copyProperties(restTemplate.getResource(), resource);
			
			accessToken = accessTokenProvider.obtainAccessToken(resource, restTemplate.getOAuth2ClientContext().getAccessTokenRequest());
		} catch (OAuth2Exception e) {
			BadCredentialsException bad = new BadCredentialsException("Could not obtain access token", e);
			publish(new OAuth2AuthenticationFailureEvent(bad));
			throw bad;			
		}
		try {
			OAuth2Authentication result = tokenServices.loadAuthentication(accessToken.getValue());
			if (authenticationDetailsSource!=null) {
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, accessToken.getValue());
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, accessToken.getTokenType());
				result.setDetails(authenticationDetailsSource.buildDetails(request));
			}
			publish(new AuthenticationSuccessEvent(result));
			return result;
		}
		catch (InvalidTokenException e) {
			BadCredentialsException bad = new BadCredentialsException("Could not obtain user details from token", e);
			publish(new OAuth2AuthenticationFailureEvent(bad));
			throw bad;			
		}

	}

	private void publish(ApplicationEvent event) {
		if (eventPublisher!=null) {
			eventPublisher.publishEvent(event);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		// Nearly a no-op, but if there is a ClientTokenServices then the token will now be stored
		restTemplate.getAccessToken();
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof AccessTokenRequiredException) {
			// Need to force a redirect via the OAuth client filter, so rethrow here
			throw failed;
		}
		else {
			// If the exception is not a Spring Security exception this will result in a default error page
			super.unsuccessfulAuthentication(request, response, failed);
		}
	}
	
	private static class NoopAuthenticationManager implements AuthenticationManager {

		@Override
		public Authentication authenticate(Authentication authentication)
				throws AuthenticationException {
			throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
		}
		
	}
	
}
