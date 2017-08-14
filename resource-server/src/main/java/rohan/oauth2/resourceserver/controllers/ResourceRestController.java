package rohan.oauth2.resourceserver.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// for it to be able to accept the custom header from remote clients
@CrossOrigin(allowedHeaders={"x-auth-token", "x-requested-with"})
public class ResourceRestController {
	
	@GetMapping("/resource")
	public String getMessage(){
		return UUID.randomUUID().toString();
	}

}
