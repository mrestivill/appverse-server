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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import io.swagger.annotations.Authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger1.annotations.EnableSwagger;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.regex.*;

/*
import springfox.documentation.authorization.AuthorizationContext;
import springfox.documentation.configuration.SpringSwaggerConfig;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@EnableSwagger
@Configuration
@ConditionalOnProperty(value="appverse.frontfacade.swagger.enabled", matchIfMissing=false)
@ComponentScan("org.appverse.web.framework.backend.frontfacade.mvc.swagger")
*/

// https://github.com/springfox/springfox/issues/767

/**
 * This class holds default swagger configuration for swagger.
 * It sets SwaggerSpringMVCPlugin properly by default.
 * You can enable / disable this autoconfiguration by using the property
 * "appverse.frontfacade.swagger.enabled" which is false by default.
 * It provides support both for basic auth and OAuth2 using the login endpoint.
 */

@EnableSwagger2
// This will go in the project that extends this class @ComponentScan("org.appverse.web.framework.backend.frontfacade.mvc.swagger")
public class SwaggerDefaultSetup implements EnvironmentAware {

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
	}

	 public static void main(String[] args) {
		    ApplicationContext ctx = SpringApplication.run(SwaggerDefaultSetup.class, args);
		  }

/*
	 @Bean
	    public Docket petApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .groupName("full-petstore-api")
	                .apiInfo(apiInfo())
	                .select()
	                .paths(petstorePaths())
	                .build();
	    }

	    @Bean
	    public Docket userApi() {
	        AuthorizationScope[] authScopes = new AuthorizationScope[1];
	        authScopes[0] = new AuthorizationScopeBuilder()
	                .scope("read")
	                .description("read access")
	                .build();
	        SecurityReference securityReference = SecurityReference.builder()
	                .reference("test")
	                .scopes(authScopes)
	                .build();

	        
	        ArrayList<SecurityReference> arrayListSecurityReference = new ArrayList<SecurityReference>();
	        arrayListSecurityReference.add(securityReference);
	        	        
	        ArrayList<SecurityContext> securityContexts = new ArrayList<SecurityContext>();
	        securityContexts.add(SecurityContext.builder().securityReferences(arrayListSecurityReference).build());
	        
	        SecurityScheme securitySchemeBasic= new BasicAuth("test");
	        
	        
	        
	        List<Authorization> authorizations = new ArrayList<Authorization>();

	        List<AuthorizationScope> authorizationScopeList = new ArrayList<AuthorizationScope>();
	        authorizationScopeList.add(new AuthorizationScope("trust", "This is the only scope which provides acccess to your REST API"));
	        
	        List<GrantType> grantTypes = new ArrayList<GrantType>();
	        LoginEndpoint loginEndpoint = new LoginEndpoint("swaggeroauth2login");
	        grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));
	        
	        SecurityScheme securitySchemeOauth= new OAuth("testOAuth2", authorizationScopeList, grantTypes);	        
	        
	        ArrayList<SecurityScheme> securitySchemes = new ArrayList<SecurityScheme>();
	        // securitySchemes.add(securitySchemeBasic);
	        securitySchemes.add(securitySchemeOauth);
	        
	        return new Docket(DocumentationType.SWAGGER_2)
	                .securitySchemes(securitySchemes)
	                .securityContexts(securityContexts)
	                .groupName("user-api")
	                .apiInfo(apiInfo())
	                .select()
	                .paths(userOnlyEndpoints())
	                .build();
	    }
*/
	 
	    @Bean
	    public Docket apiDocumentationV2() {
	    	// TODO: Works! Only check this:
	    	// https://github.com/springfox/springfox/issues/767
	    	return new Docket(DocumentationType.SWAGGER_2).groupName("full-api").apiInfo(apiInfo())
	    			.select().paths(userOnlyEndpoints()).build()
	    			.securitySchemes(Arrays.asList(securitySchema()))
	    			.securityContexts(Arrays.asList(securityContext()));
	    }

	    public static final String securitySchemaOAuth2 = "oauth2schema";
	    public static final String authorizationScopeGlobal = "global";
	    public static final String authorizationScopeGlobalDesc ="accessEverything";

	    private OAuth securitySchema() {
	    	AuthorizationScope authorizationScope = new AuthorizationScope(authorizationScopeGlobal, authorizationScopeGlobal);
	    	LoginEndpoint loginEndpoint = new LoginEndpoint("swaggeroauth2login");
	    	GrantType grantType = new ImplicitGrant(loginEndpoint, "access_token");
	    	return new OAuth(securitySchemaOAuth2, Arrays.asList(authorizationScope), Arrays.asList(grantType));
	    }

	    private SecurityContext securityContext() {
	    	return SecurityContext.builder()
	    			.securityReferences(defaultAuth())
	    			.forPaths(userOnlyEndpoints())
	    			.build();
	    }

	    private List<SecurityReference> defaultAuth() {
	    	AuthorizationScope authorizationScope
	    	= new AuthorizationScope(authorizationScopeGlobal, authorizationScopeGlobalDesc);
	    	AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	    	authorizationScopes[0] = authorizationScope;
	    	return Arrays.asList(
	    			new SecurityReference(securitySchemaOAuth2, authorizationScopes));
	    }
	    

	    private Predicate<String> petstorePaths() {
	    	/*
	    	CharMatcher charmatch = new CharMatcher();
	    	return null;
	    	*/
	    	
	    	Predicate<String> matchesWithRegexAll = new Predicate<String>() {
	            @Override 
	            public boolean apply(String str) {
	                return str.matches("/*");
	            }       
	    	};
	    	
	    	return Predicates.or(Predicates.containsPattern("/*blabla"),
	    			             Predicates.containsPattern("/*"));	 
	    	
	    	/*
	        return Predicates.or(
	                Predicate("/api/pet.*"),
	                regex("/api/user.*"),
	                regex("/api/store.*")
	        );
	        */
	    }
	    private Predicate<String> userOnlyEndpoints() {
	    	return Predicates.or(Predicates.containsPattern("/*"));	 
	    	/*
	        return new Predicate<String>() {
	            @Override
	            public boolean apply(String input) {
	                return input.contains("user");
	            }
	        };
	        */
	    }

	    private ApiInfo apiInfo() {
	        return new ApiInfoBuilder()
	                .title("Springfox petstore API")
	                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum " +
	                        "has been the industry's standard dummy text ever since the 1500s, when an unknown printer "
	                        + "took a " +
	                        "galley of type and scrambled it to make a type specimen book. It has survived not only five " +
	                        "centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
	                        "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum " +
	                        "passages, and more recently with desktop publishing software like Aldus PageMaker including " +
	                        "versions of Lorem Ipsum.")
	                .termsOfServiceUrl("http://springfox.io")
	                .contact("springfox")
	                .license("Apache License Version 2.0")
	                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
	                .version("2.0")
	                .build();
	    }


/*
public class SwaggerAutoConfiguration implements EnvironmentAware {

  private SpringSwaggerConfig springSwaggerConfig;
  
  private RelaxedPropertyResolver propertyResolver;
  
  @Value("${appverse.frontfacade.oauth2.apiprotection.enabled:false}")
  private boolean oauth2Enabled;  
  
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
    SwaggerSpringMvcPlugin swaggerSpringMVCPlugin =	new SwaggerSpringMvcPlugin(springSwaggerConfig)
    		.includePatterns(getIncludePatterns())
            .apiInfo(apiInfo());
    if (oauth2Enabled){
    	swaggerSpringMVCPlugin.authorizationTypes(authorizationTypes())
        .authorizationContext(authorizationContext());
    }
    return swaggerSpringMVCPlugin.build();
  }

  private String[] getIncludePatterns(){
	  String includePatterns = propertyResolver.getProperty("includePatterns");
	  if (includePatterns == null){
		  String[] pattern = {".*?"};
		  return pattern;
	  }
	  return includePatterns.split(",");		 
  }

  private ApiInfo apiInfo() {
      return new ApiInfo(
              propertyResolver.getProperty("title"),
              propertyResolver.getProperty("description"),
              propertyResolver.getProperty("termsOfServiceUrl"),
              propertyResolver.getProperty("contact"),
              propertyResolver.getProperty("license"),
              propertyResolver.getProperty("licenseUrl"));
  }

  private List<AuthorizationType> authorizationTypes() {
    ArrayList<AuthorizationType> authorizationTypes = new ArrayList<AuthorizationType>();

    List<AuthorizationScope> authorizationScopeList = new ArrayList<AuthorizationScope>();
    authorizationScopeList.add(new AuthorizationScope("trust", "This is the only scope which provides acccess to your REST API"));

    List<GrantType> grantTypes = new ArrayList<GrantType>();

    LoginEndpoint loginEndpoint = new LoginEndpoint("swaggeroauth2login");
    
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
    authorizationScopeList.add(new AuthorizationScope("trust", "This is the only scope which provides acccess to your REST API"));    
    
    authorizations.add(new Authorization("oauth2", authorizationScopeList.toArray(new AuthorizationScope[authorizationScopeList.size()])));
    AuthorizationContext authorizationContext =
            new AuthorizationContext.AuthorizationContextBuilder(authorizations).build();
    return authorizationContext;
  }
*/

}