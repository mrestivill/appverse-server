/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0�?). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.backend.frontfacade.mvc.swagger.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Controller
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=false)

/**
 * Simple controller that just shows the Swagger OAuth2 login form where the user can enter they credentials
 * to authenticate before obtaining an OAuth2 token (username and password).
 * This controller shows the Thymeleaf view (template) oauth2loginform.hmtl.
 * The oauth2 login endpoint, clientId and redirection url are filled in the template.   
 */
public class SwaggerOAuth2Controller {
	
	@Value("${appverse.frontfacade.swagger.oauth2.clientId}")
	private String swaggerClientId;
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String apiBasePath;
	
	@Value("${appverse.frontfacade.oauth2.loginEndpoint.path:/sec/login}")
	private String oauth2LoginEndpoint;

	@RequestMapping(value="/",method = RequestMethod.GET)
	public String showindexOAuth2LoginForm(Model model, HttpServletRequest req) {
		model.addAttribute("swaggerClientId", swaggerClientId);
		return "redirect:swagger-ui.html";
	}

	@RequestMapping(value="/swaggeroauth2login",method = RequestMethod.GET)
	public String showSwaggerOAuth2LoginForm(Model model, HttpServletRequest req) throws MalformedURLException{
		String contextPath = req.getContextPath();
		model.addAttribute("response_type", "token");
		Map<String, String[]> map = req.getParameterMap();

		model.addAllAttributes(convertParameters(map));
		model.addAttribute("redirect_uri", req.getParameter("redirect_uri"));
		model.addAttribute("swaggerLoginFormAction", convertToRelativePath(contextPath, apiBasePath + oauth2LoginEndpoint));
		model.addAttribute("swaggerClientId", swaggerClientId);

		return "oauth2loginform";
	}
	private Map<String,String> convertParameters(Map<String, String[]> map){
		Map<String,String>  data = new HashMap<String,String>();
		if (map!=null) {
			for (Map.Entry<String, String[]> entry : map.entrySet()) {
				data.put(entry.getKey(), StringUtils.arrayToCommaDelimitedString(entry.getValue()));
			}
		}
		return data;
	}

	private String convertToRelativePath(String contextPath, String url) {
		if (!StringUtils.isEmpty(contextPath)){
			if (contextPath.charAt(0)!='/'){
				return "/"+contextPath+url;
			}
			return contextPath+url;
		}
		return url;
	}

}