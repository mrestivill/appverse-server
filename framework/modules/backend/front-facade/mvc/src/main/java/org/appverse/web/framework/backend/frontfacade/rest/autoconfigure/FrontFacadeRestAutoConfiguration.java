/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this
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
package org.appverse.web.framework.backend.frontfacade.rest.autoconfigure;

import org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.configuration.AppverseWebHttpBasicConfigurerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Front Facade module
 */
@Configuration
@ConditionalOnClass(FrontFacadeRestAutoConfiguration.class)
@ComponentScan("org.appverse.web.framework.backend.frontfacade.rest")
public class FrontFacadeRestAutoConfiguration {

    @Value("${appverse.frontfacade.rest.api.basepath:/api}")
    private String apiPath;

    @Value("${appverse.frontfacade.rest.cors.allowedorigins:*}")
    //comma separated allowed origins, by default *
    private String allowedOrigins;
    @Value("${appverse.frontfacade.rest.cors.allowedheaders:*}")
    //comma separated allowed origins, by default *
    private String allowedHeaders;

    @Value("${appverse.frontfacade.rest.cors.allowedmethods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    //comma separated allowed methods, by default
    private String allowedMethods;
    @Value("${appverse.frontfacade.rest.cors.allowcredentials:true}")
    //comma separated allowed methods, by default
    private String allowedCredentials;
    @Value("${appverse.frontfacade.rest.cors.maxAge:-1}")
    //comma separated allowed methods, by default
    private String maxAge;

    @Value("${appverse.frontfacade.rest.cors.path:}")
    //Path to be applied cors, by default api.basepath
    private String corsPath;
	
    /**
     * Spring4 Cors filter
     *  By default disabled
     * @return
     */
    @Bean
    @ConditionalOnProperty(value="appverse.frontfacade.rest.cors.enabled", matchIfMissing=false)
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String path = apiPath;
                if (!StringUtils.isEmpty(corsPath)){
                    path = corsPath;
                }
                registry.addMapping(path + "/**")
                        .allowedOrigins(StringUtils.commaDelimitedListToStringArray(allowedOrigins))
                        .allowedMethods(StringUtils.commaDelimitedListToStringArray(allowedMethods))
                        .allowedHeaders(StringUtils.commaDelimitedListToStringArray(allowedHeaders))
                        .allowCredentials(Boolean.valueOf(allowedCredentials))
                        .maxAge(Long.valueOf(maxAge));
            }
        };
    }

	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	@ConditionalOnProperty(value="appverse.frontfacade.rest.http.basic.default.setup.enabled", matchIfMissing=true)
	protected static class AppverseWebHttpBasicConfiguration extends AppverseWebHttpBasicConfigurerAdapter {
	}
}
