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
package org.appverse.web.framework.backend.security.xs.xss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;


/**
 * Wraps an HttpServletRequest and override those methods that need to be sanitized
 * in order to avoid XSS attacks delegating on OWASP ESAPI
 */
public class XssProtectHttpRequestWrapper extends HttpServletRequestWrapper {

	public XssProtectHttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
    @Override
    public String getParameter(String name) {
    	return sanitize(super.getParameter(name));
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
    	Map<String, String[]> values = super.getParameterMap();
    	Map<String, String[]> cleanValues = new HashMap<String, String[]>();
    	if (values == null || values.isEmpty()){
    		return values;
    	}
    	Iterator<Entry<String, String[]>> entries = values.entrySet().iterator();
    	while (entries.hasNext()) {
    	  Entry<String, String[]> entry = entries.next();
    	  String key = entry.getKey();
    	  String[] value = entry.getValue();
    	  List<String> cleanListValues = new ArrayList<String>();
    	  for(String tmpValue : value){
    		  cleanListValues.add(sanitize(tmpValue));
    	  }
    	  cleanValues.put(key, cleanListValues.toArray(new String[cleanValues.size()]));    	  
    	}    	
    	return cleanValues;
    }    
	
    @Override
    public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values == null) {
			return null;
		}
		List<String> cleanValues = new ArrayList<String>(); 
		for (String value : values) {
			cleanValues.add(sanitize(value));
		}
        return cleanValues.toArray(new String[cleanValues.size()]);
    }
    
    @Override
    public String getHeader(String name) {
    	return sanitize(super.getHeader(name));
    }
    
    @Override
    public Enumeration<String> getHeaders(String name) {
    	Enumeration<String> headers = super.getHeaders(name);
    	List<String> cleanValues = new ArrayList<String>();    	
    	while(headers.hasMoreElements()){
    		cleanValues.add(sanitize(headers.nextElement()));
    	}
    	return Collections.enumeration(cleanValues);
    }
    
    @Override
    public String getQueryString() {
    	return sanitize(super.getQueryString());
    }
    
    @Override
    public StringBuffer getRequestURL() {
    	return new StringBuffer(sanitize(super.getRequestURL().toString()));
    }
    
    @Override
    public String getContextPath() {
    	return sanitize(super.getContextPath());
    }
    
	
	private String sanitize(String value) {
        if( value != null )
        {
            // Use the ESAPI library to avoid encoded attacks.
            value = ESAPI.encoder().canonicalize( value );

            // Avoid null characters
            value = value.replaceAll("\0", "");

            // Clean out HTML
            value = Jsoup.clean(value, Whitelist.none());
        }
        return value;
	}
}