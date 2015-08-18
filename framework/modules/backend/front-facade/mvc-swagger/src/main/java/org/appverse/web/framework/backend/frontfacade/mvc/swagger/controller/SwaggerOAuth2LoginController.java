package org.appverse.web.framework.backend.frontfacade.mvc.swagger.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
public class SwaggerOAuth2LoginController {
	
	@Value("${appverse.frontfacade.swagger.oauth2.clientId}")
	private String swaggerClientId;
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String apiBasePath;
	
	@Value("${appverse.frontfacade.oauth2.loginEndpoint.path:/sec/login}")
	private String oauth2LoginEndpoint;

	@RequestMapping(value="/",method = RequestMethod.GET)
	public String showindexOAuth2LoginForm(Model model, HttpServletRequest req) {
		model.addAttribute("swaggerClientId", swaggerClientId);
		return "index";
	}

	@RequestMapping(value="/swaggeroauth2login",method = RequestMethod.GET)
	public String showSwaggerOAuth2LoginForm(Model model, HttpServletRequest req) throws MalformedURLException{
		String contextPath = req.getContextPath();
		model.addAttribute("response_type", "token");
		model.addAttribute("redirect_uri", obtainBaseServer(req.getParameter("redirect_uri"))+convertToRelativePath(contextPath, "/o2c.html"));
		Map<String, String[]> map = req.getParameterMap();

		model.addAllAttributes(convertParameters(map));
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
	private String obtainBaseServer(String urlString) throws MalformedURLException {
		if (!StringUtils.isEmpty(urlString)) {
			URL url = new URL(urlString);
			return url.getProtocol() + "://" + url.getAuthority();
		}
		return "";

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