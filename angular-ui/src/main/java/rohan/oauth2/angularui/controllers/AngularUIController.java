package rohan.oauth2.angularui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AngularUIController {

	@GetMapping("/")
	public String getHome(){
		return "index";
	}
	
}
