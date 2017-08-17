package rohan.oauth2.mvcui;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2SsoProperties extends OAuth2SsoProperties {

	@Override
	public String getLoginPath() {
		return "/loginProcess";
	}
}