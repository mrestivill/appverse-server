package org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * This class implements a CsrfTokenRepository that can be used only for tests.
 * It returns a fixed csrf token value so you do not need to disable Csrf in your tests.
 * This way you will be testing your services with Csrf enabled but you will know a fixed 
 * token value you can pass in your tests (DEFAULT_TEST_CSRF_TOKEN_VALUE)
 */

// Problem we have with the approach of having a TestCsrfTokenRepository is that in Basic auth enpoints we need to retrieve
// the respository (default an httpSesssioncsrftokenrepository)
// we might use in tests this: http://docs.spring.io/autorepo/docs/spring-security/4.0.1.RELEASE/apidocs/org/springframework/security/test/web/support/WebTestUtils.html
// We load the token and we add the same token
public class TestCsrfTokenRepository implements CsrfTokenRepository {

	private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    
    private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = TestCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");
    
    private static final String DEFAULT_TEST_CSRF_TOKEN_VALUE = "test_csrf_token";

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;
	
    private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;
    
    private String defaultTestCsrfTokenValue = DEFAULT_TEST_CSRF_TOKEN_VALUE;

	/* This method generates a fixed token (DEFAULT_TEST_CSRF_TOKEN_VALUE) - Just for testing purposes
	 * @see org.springframework.security.web.csrf.CsrfTokenRepository#generateToken(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public CsrfToken generateToken(HttpServletRequest request) {
		return new DefaultCsrfToken(headerName, parameterName, defaultTestCsrfTokenValue);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.csrf.CsrfTokenRepository#saveToken(org.springframework.security.web.csrf.CsrfToken, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void saveToken(CsrfToken token, HttpServletRequest request,
			HttpServletResponse response) {
        if (token == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(sessionAttributeName);
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(sessionAttributeName, token);
        }
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.csrf.CsrfTokenRepository#loadToken(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public CsrfToken loadToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (CsrfToken) session.getAttribute(sessionAttributeName);
	}

}
