package rohan.oauth2.mvcui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcUiController {

	@Autowired
	OAuth2RestTemplate restTemplate;
	
	@GetMapping(path={"/", "/home"})
	public String home(Model model){
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8087/resource", String.class);
		String message = response.getBody();
		model.addAttribute("message", message);
		return "home";
	}
}
