package rohan.oauth2.authenticationserver.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthenticationRestController {

	@Autowired
	private TokenStore tokenStore;
	
	@GetMapping("/user")
	public Principal user(Principal user){
		return user;
	}
	
	@PostMapping("/ssologout")
	@ResponseStatus(HttpStatus.OK)
	public void logout(@RequestHeader("authorization") String authorizationHeader){
		
		removeTokenFromTokenStore(getToken(authorizationHeader));
		
	}
	
	private OAuth2AccessToken getToken(final String authorizationHeader){
		final String bearerToken = authorizationHeader.replaceAll("(?i)bearer " , "");
		return tokenStore.readAccessToken(bearerToken);
	}
	
	private void removeTokenFromTokenStore(final OAuth2AccessToken token){
		tokenStore.removeAccessToken(token);
	}
	
}
