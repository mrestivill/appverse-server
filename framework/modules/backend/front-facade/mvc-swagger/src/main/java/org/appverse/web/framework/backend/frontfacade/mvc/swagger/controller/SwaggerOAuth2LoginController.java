package org.appverse.web.framework.backend.frontfacade.mvc.swagger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/swaggeroauth2login")
public class SwaggerOAuth2LoginController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String showSwaggerOAuth2LoginForm() {
		return "oauth2loginform";
	}
}