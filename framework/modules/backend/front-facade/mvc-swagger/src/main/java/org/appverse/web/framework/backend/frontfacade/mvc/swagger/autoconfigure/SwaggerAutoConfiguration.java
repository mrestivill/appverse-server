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
import java.util.List;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.Authorization;
import com.mangofactory.swagger.models.dto.AuthorizationScope;
import com.mangofactory.swagger.models.dto.AuthorizationType;
import com.mangofactory.swagger.models.dto.GrantType;
import com.mangofactory.swagger.models.dto.ImplicitGrant;
import com.mangofactory.swagger.models.dto.LoginEndpoint;
import com.mangofactory.swagger.models.dto.OAuth;
import com.mangofactory.swagger.models.dto.builder.OAuthBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/*http://developers-blog.helloreverb.com/enabling-oauth-with-swagger */

@EnableSwagger
@Configuration
@ConditionalOnProperty(value="swagger.enabled", matchIfMissing=true)
@ComponentScan("org.appverse.web.framework.backend.frontfacade.mvc.swagger")
public class SwaggerAutoConfiguration implements EnvironmentAware {

  private SpringSwaggerConfig springSwaggerConfig;
  
  private RelaxedPropertyResolver propertyResolver;
  
  @Override
  public void setEnvironment(Environment environment) {
      this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
  }

  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Bean
  public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin() {
    return new SwaggerSpringMvcPlugin(springSwaggerConfig)
    		/* Example: including groups and patterns
    		.swaggerGroup("group")
            .includePatterns(
                    "/business.*",
                    "/some.*",
                    "/contacts.*",
                    "/pet.*",
                    "/springsRestController.*",
                    "/test.*"
            )
            */
    		.includePatterns(getIncludePatterns())
            .apiInfo(apiInfo())
            .authorizationTypes(authorizationTypes())
            .authorizationContext(authorizationContext())
            .build();
  }

  /**
   * Patterns to be included to appear in the swagger-ui page
   */
  private String[] getIncludePatterns(){
	  String includePatterns = propertyResolver.getProperty("includePatterns");
	  if (includePatterns == null){
		  String[] pattern = {".*?"};
		  return pattern;
	  }
	  return includePatterns.split(",");		 
  }

  /**
   * API Info as it appears on the swagger-ui page
   */
  private ApiInfo apiInfo() {
	/*
    ApiInfo apiInfo = new ApiInfo(
            "Demo Spring MVC swagger 1.2 api",
            "Sample spring mvc api based on the swagger 1.2 spec",
            "http://en.wikipedia.org/wiki/Terms_of_service",
            "somecontact@somewhere.com",
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0.html"
    );
    */
      return new ApiInfo(
              propertyResolver.getProperty("title"),
              propertyResolver.getProperty("description"),
              propertyResolver.getProperty("termsOfServiceUrl"),
              propertyResolver.getProperty("contact"),
              propertyResolver.getProperty("license"),
              propertyResolver.getProperty("licenseUrl"));
  }

/* TODO: This needs to be parametrized */
  private List<AuthorizationType> authorizationTypes() {
    ArrayList<AuthorizationType> authorizationTypes = new ArrayList<AuthorizationType>();

    List<AuthorizationScope> authorizationScopeList = new ArrayList<AuthorizationScope>();
    authorizationScopeList.add(new AuthorizationScope("trust", "This is the only scope which provides acccess to your REST API - 1"));

    List<GrantType> grantTypes = new ArrayList<GrantType>();

//     LoginEndpoint loginEndpoint = new LoginEndpoint("http://localhost:8080/oauth2loginform.html");
    
    LoginEndpoint loginEndpoint = new LoginEndpoint("http://localhost:8080/swaggeroauth2login");
    
    grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));
    
    OAuth oAuth = new OAuthBuilder()
            .scopes(authorizationScopeList)
            .grantTypes(grantTypes)
            .build();

    authorizationTypes.add(oAuth);
    return authorizationTypes;
  }

  @Bean
  public AuthorizationContext authorizationContext() {
    List<Authorization> authorizations = new ArrayList<Authorization>();

    List<AuthorizationScope> authorizationScopeList = new ArrayList<AuthorizationScope>();
    authorizationScopeList.add(new AuthorizationScope("trust", "This is the only scope which provides acccess to your REST API - 2"));    
    
    authorizations.add(new Authorization("oauth2", authorizationScopeList.toArray(new AuthorizationScope[authorizationScopeList.size()])));
    AuthorizationContext authorizationContext =
            new AuthorizationContext.AuthorizationContextBuilder(authorizations).build();
    return authorizationContext;
  }
  
  @Bean
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(500000);
    return multipartResolver;
  }

}