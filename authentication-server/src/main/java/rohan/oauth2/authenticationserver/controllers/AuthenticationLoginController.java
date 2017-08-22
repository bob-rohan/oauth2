package rohan.oauth2.authenticationserver.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationLoginController {

	@GetMapping(path= {"/mvcLogin"})
	public String mvcLogin(){
		return "mvcLogin";
	}
	
	@GetMapping(path= {"/angularLogin"})
	public String angularLogin(){
		return "angularLogin";
	}
	
}
