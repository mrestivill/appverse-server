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
package org.appverse.web.framework.backend.core.enterprise.aop.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.aop.advices.ExceptionAdvice;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.ExceptionManager;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.impl.live.ExceptionManagerImpl;
import org.springframework.context.annotation.Bean;


/**
 * Autoconfiguration class for the Exception Manager component.
 * In case your app wants to use the Exception Manager:
 * 	1- Add the core-enterprise-module to your pom
 *  2- Define your aop config in the application.xml file as follows:
 *		<aop:advisor advice-ref="exceptionAdvice" pointcut="org.appverse.web.framework.backend.core.enterprise.aop.pointcut.AppverseAOPPointcuts.allServicesCalls()" />
 * In case you don't want to use it, just do not set the module as dependency, and if that's not possible, exlude this autoconfiguration
 * 	@EnableAutoConfiguration(exclude={AOPProfileManagerAutoConfiguration.class})
 *  
 * @author RRBL
 *
 */
public class AOPExceptionManagerAutoConfiguration {
	@Bean
	public static ExceptionManager exceptionManager() {
		return new ExceptionManagerImpl();
	}
	@Bean
	public static ExceptionAdvice exceptionAdvice() {
		return new ExceptionAdvice();
	}
}
