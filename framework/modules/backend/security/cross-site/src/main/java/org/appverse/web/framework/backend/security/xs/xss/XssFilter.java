package org.appverse.web.framework.backend.security.xs.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import jodd.typeconverter.Convert;
import jodd.typeconverter.TypeConversionException;
import jodd.util.StringUtil;
import jodd.util.Wildcard;

public class XssFilter implements Filter {
	
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (isXssCheckEligible(req)){
        	chain.doFilter(new XssProtectHttpRequestWrapper(req), response);
        }
        else{
        	chain.doFilter(request, response);
        }
    }

    protected String[] matches;
    protected String[] excludes;
    protected boolean wildcards;

    /**
     * Filter initialization.
     */
    public void init(FilterConfig config) throws ServletException {

        try {
            wildcards = Convert.toBooleanValue(config.getInitParameter("wildcards"), false);
        } catch (TypeConversionException ignore) {
            wildcards = false;
        }

        // match string
        String uriMatch = config.getInitParameter("match");

        if ((uriMatch != null) && (uriMatch.equals("*") == false)) {
            matches = StringUtil.splitc(uriMatch, ',');
            for (int i = 0; i < matches.length; i++) {
                matches[i] = matches[i].trim();
            }
        }

        // exclude string
        String uriExclude = config.getInitParameter("exclude");

        if (uriExclude != null) {
            excludes = StringUtil.splitc(uriExclude, ',');
            for (int i = 0; i < excludes.length; i++) {
                excludes[i] = excludes[i].trim();
            }
        }        
    }

    public void destroy() {
        matches = null;
        excludes = null;
    }

    /**
     * Determine if uri is eligible for being checked against XSRF attacks
     */
    private boolean isXssCheckEligible(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) {
            return false;
        }

        boolean result = false;

        if (matches == null) {							// match=*
            result = true;
        } else {
            if (wildcards) {
                result = Wildcard.matchPathOne(uri, matches) != -1;
            } else {
                for (String match : matches) {
                    if (uri.contains(match)) {
                        result = true;
                        break;
                    }
                }
            }
        }

        if ((result == true) && (excludes != null)) {
            if (wildcards) {
                if (Wildcard.matchPathOne(uri, excludes) != -1) {
                    result = false;
                }
            } else {
                for (String exclude : excludes) {
                    if (uri.contains(exclude)) {
                        result = false;						// excludes founded
                        break;
                    }
                }
            }
        }
        return result;
    }
	

/*	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XSSProtectHttpRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {
	}
*/
}