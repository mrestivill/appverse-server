package org.appverse.web.framework.backend.frontfacade.mvc.swagger.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/swaggeroauth2login")
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=false)
/**
 * Simple controller that just shows the Swagger OAuth2 login form where the user can enter they credentials
 * to authenticate before obtaining an OAuth2 token (username and password).
 * This controller shows the Thymeleaf view (template) oauth2loginform.hmtl.
 * The oauth2 login endpoint, clientId and redirection url are filled in the template.   
 */
public class SwaggerOAuth2LoginController {
	
	@Value("${appverse.frontfacade.swagger.oauth2.clientId}")
	private String swaggerClientId;
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String apiBasePath;
	
	@Value("${appverse.frontfacade.oauth2.loginEndpoint.path:/sec/login}")
	private String oauth2LoginEndpoint;	
	
	@RequestMapping(method = RequestMethod.GET)
	public String showSwaggerOAuth2LoginForm(Model model) {
		model.addAttribute("swaggerLoginFormAction", apiBasePath + oauth2LoginEndpoint);
		model.addAttribute("swaggerClientId", swaggerClientId);
		model.addAttribute("swaggerRedirectUri", "/o2c.html");		
		return "oauth2loginform";
	}
}