package org.appverse.web.framework.backend.security.authentication.userpassword.autoconfigure;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManagerImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for user and password authentication security.
 */
@Configuration
@ConditionalOnClass(UserAndPasswordAuthenticationManager.class)
public class UserAndPasswordAuthenticationAutoConfiguration {
	
	@Bean
	public UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager() {
		return new UserAndPasswordAuthenticationManagerImpl();
	}
}