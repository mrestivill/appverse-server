package org.appverse.web.framework.backend.frontfacade.mvc.swagger.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", havingValue="false", matchIfMissing=true)
/**
 * Simple controller that redirect to the index view (thymeleaf template) when oauth2 is not used.
 * When oauth2 is used, an specific controller is required for swagger to be able to handle oauth2
 */
public class SwaggerBasicController {

	@RequestMapping(value="/",method = RequestMethod.GET)
	public String showindexOAuth2LoginForm(Model model, HttpServletRequest req) {
		return "redirect:swagger-ui.html";
	}
}