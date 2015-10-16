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
package org.appverse.web.framework.backend.frontfacade.mvc.swagger.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class holds default swagger configuration using Swagger Sprinfox API.
 * You can enable / disable this autoconfiguration by using the property
 * "appverse.frontfacade.swagger.enabled" which is false by default.
 * 
 * It creates by default a default group showing all your API (the patterns you have included).
 * It provides support both for basic auth and OAuth2 using a login endpoint.
 * Properties:
 * - appverse.frontfacade.swagger.enabled: allows enable / disable Appverse Swagger feature
 * - appverse.frontfacade.swagger.oauth2.defaultscope: default scope that will be used with swagger using oauth2
 * - 
 * 
 * Example of more complex setup:
 * https://github.com/springfox/springfox/blob/master/springfox-spring-config/src/main/java/springfox/springconfig/Swagger2SpringBoot.java
 */
@EnableSwagger2
@Configuration
@ConditionalOnProperty(value="appverse.frontfacade.swagger.enabled", matchIfMissing=false)
public class SwaggerDefaultSetup implements EnvironmentAware {
	
	public static final String securitySchemaOAuth2 = "oauth2schema";
    @Value("${appverse.frontfacade.rest.api.basepath:/api}")
    private String apiPath;
    @Value("${appverse.frontfacade.oauth2.apiprotection.enabled:false}")
    private boolean oauth2Enabled;
    @Value("${appverse.frontfacade.swagger.oauth2.defaultscope:}")
    private String swaggerDefaultScope;
	@Value("${appverse.frontfacade.swagger.oauth2.clientId:}")
	private String swaggerClientId;
	private RelaxedPropertyResolver propertyResolver;
	
	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "appverse.frontfacade.swagger.");
	}

	
	@Bean
	@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=false)
	public SecurityConfiguration securityConfiguration(){
		SecurityConfiguration config = new SecurityConfiguration(swaggerClientId, "oauth2-resource", swaggerClientId, "apiKey", "notused");
		return config;
	}


	@Bean
	public Docket apiDocumentationV2Security() {
		Docket docket =  new Docket(DocumentationType.SWAGGER_2).groupName("default-group").apiInfo(apiInfo())
				.select().paths(defaultGroup()).build();
		if (oauth2Enabled) {
			// This causes duplicated contextpath in Swagger UI .pathMapping(apiPath)
			docket.securitySchemes(Arrays.asList(securitySchema()))
					.securityContexts(Arrays.asList(securityContext()));
		}
		return docket;
	}
		
	private OAuth securitySchema() {
		LoginEndpoint loginEndpoint = new LoginEndpoint("swaggeroauth2login");
		GrantType grantType = new ImplicitGrant(loginEndpoint, "access_token");
		return new OAuth(securitySchemaOAuth2, Arrays.asList(getOauth2Scopes()), Arrays.asList(grantType));
	}

	private SecurityContext securityContext() {
		SecurityContextBuilder builder = SecurityContext.builder();
		if (oauth2Enabled){
			List<SecurityReference> defaultOAuthSecurityReference = Arrays.asList(new SecurityReference(securitySchemaOAuth2, getOauth2Scopes()));
			if (defaultOAuthSecurityReference != null){
				builder.securityReferences(defaultOAuthSecurityReference);
			}
		}
		return builder.forPaths(defaultGroup()).build();
	}
	
	private AuthorizationScope[] getOauth2Scopes() {
		String[] scopes = getSwaggerScopes();
		AuthorizationScope[] authorizationScopes = null;
		if (scopes != null) {
			authorizationScopes = new AuthorizationScope[scopes.length];
			int cnt=0;
			for (String scope:scopes){				
				AuthorizationScope authScope = new AuthorizationScope(scope, "");
				authorizationScopes[cnt] = authScope;
				cnt++;
			}
		}
		return authorizationScopes;
	}	
	
	@SuppressWarnings("unchecked")
	private Predicate<String> defaultGroup() {
		String[] includePatterns = getIncludePatterns();
		List<Predicate<String>> predicateList = new ArrayList<Predicate<String>>();	    	
		if (includePatterns != null) {
			for (String pattern:includePatterns){
				predicateList.add(PathSelectors.regex(pattern));
			}	    		
			return Predicates.or(predicateList);
		}
		else{
			return Predicates.or(PathSelectors.regex("/*"));
		}
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title(propertyResolver.getProperty("title"))
				.description(propertyResolver.getProperty("description"))
				.termsOfServiceUrl(propertyResolver.getProperty("termsOfServiceUrl"))
				.contact(propertyResolver.getProperty("contact"))
				.license(propertyResolver.getProperty("license"))
				.licenseUrl(propertyResolver.getProperty("licenseUrl"))
				.version("version")
				.build();
	}
	
	private String[] getSwaggerScopes(){
		String includePatterns = propertyResolver.getProperty("oauth2.scopes");
		if (includePatterns == null){
			return null;
		}
		return includePatterns.split(",");		 
	}

	private String[] getIncludePatterns(){
		String includePatterns = propertyResolver.getProperty("defaultGroupIncludePatterns");
		if (includePatterns == null){
			return null;
		}
		return includePatterns.split(",");		 
	}
}