package rohan.oauth2.authenticationserver.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthenticationRestController {

	@RequestMapping("/user")
	public Principal user(Principal user){
		return user;
	}
	
	/*@RequestMapping("/token")
	public String token(HttpSession session){
		return session.getId();
	}*/
}
