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
package org.appverse.web.framework.backend.security.oauth2.configuration.memory;

import org.appverse.web.framework.backend.security.oauth2.common.token.enhancers.PrincipalCredentialsTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * OAuth2 Authorization Server that uses an InMemory to keep the tokens.
 * !!! This should not be used on production!!!
 * This way you only need to override
 * configure method to register the clients as shown in the following example.
 *
 * Take into account that ClientDetailsStore, TokenStore, AuthorizationCodeStore
 * etc, could be in-memory instead of be persisted in database if you had just a
 * node for the AuthorizationServer (for instance). You can use the setup
 * provided by this class but remember that you need to assess your scenario
 * taking into account how you are going to scale your Authorization and
 * Resource server, performance requirements, etc. Depending on this you will be
 * able to use in-memory or not and you will need to think if you require sticky
 * sessions (for Authorization server if you are using stateful grant types) or
 * need to use another solution as Spring Session in combination with this
 * setup. Take this setup just as an starting point.
 *
 * @Override public void configure(ClientDetailsServiceConfigurer clients)
 *           throws Exception { clients.jdbc(dataSource)
 *           .passwordEncoder(passwordEncoder) .withClient("my-trusted-client")
 *           .authorizedGrantTypes("password", "authorization_code",
 *           "refresh_token", "implicit") .authorities("ROLE_CLIENT",
 *           "ROLE_TRUSTED_CLIENT") .scopes("read", "write", "trust")
 *           .resourceIds("oauth2-resource") .accessTokenValiditySeconds(60); }
 *           JDBCTokenStore requires a particular database model provided by
 *           Spring as an SQL script that you need to create in your database
 *           schema. You can find a schema example here:
 *           https://github.com/spring
 *           -projects/spring-security-oauth/blob/master
 *           /spring-security-oauth2/src/test/resources/schema.sql
 */
public class AuthorizationServerInMemoryStoreConfigurerAdapter extends AuthorizationServerConfigurerAdapter {

 @Autowired
 protected AuthenticationManager auth;


 @Bean
 protected TokenStore tokenStore() {
  return new InMemoryTokenStore();
 }

 @Bean
 protected AuthorizationCodeServices authorizationCodeServices() {
  return new InMemoryAuthorizationCodeServices();
 }

 @Bean
 protected PrincipalCredentialsTokenEnhancer principalCredentialsTokenEnhancer(){
  return new PrincipalCredentialsTokenEnhancer();
 }


 @Override
 public void configure(AuthorizationServerEndpointsConfigurer endpoints)
         throws Exception {
  endpoints.authorizationCodeServices(authorizationCodeServices())
          .authenticationManager(auth).tokenStore(tokenStore())
          .tokenEnhancer(principalCredentialsTokenEnhancer())
          .approvalStoreDisabled();
 }
}
