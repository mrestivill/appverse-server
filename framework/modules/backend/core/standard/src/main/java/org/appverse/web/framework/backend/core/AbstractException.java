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
package org.appverse.web.framework.backend.core;

/**
 * Base class for unchecked exceptions
 */
public abstract class AbstractException extends RuntimeException{

	private static final long serialVersionUID = -5547004129911718577L;

    private static Long code;

	/**
	 * Default constructor
	 */
	public AbstractException() {
		super();
	}

	/**
	 * Builds an AbstractException with the following parameters
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AbstractException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Builds an AbstractException with the following parameters
	 * @param message
	 * @param cause
	 */
	public AbstractException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Builds an AbstractException with the following parameters
	 * @param message
	 */
	public AbstractException(String message) {
		super(message);
	}

	/**
	 * Builds an AbstractException with the following parameters
	 * @param cause
	 */
	public AbstractException(Throwable cause) {
		super(cause);
	}

    /**
     * Builds an AbstractException with the following parameters
     * @param code
     */
    public AbstractException(Long code) {
        this();
        setCode(code);
    }

    /**
     * Builds an AbstractException with the following parameters
     * @param code
     * @param cause
     */
    public AbstractException(Long code, Throwable cause) {
        this(cause);
        setCode(code);
    }

    /**
     * Builds an AbstractException with the following parameters
     * @param code
     * @param cause
     */
    public AbstractException(Long code, String message, Throwable cause) {
        this(message, cause);
        setCode(code);
    }

    /**
     * Builds an AbstractException with the following parameters
     * @param code
     * @param message
     */
    public AbstractException(Long code, String message) {
        this(message);
        setCode(code);
    }

    /**
     * Obtains the error code
     * @return
     */
    public Long getCode(){
        return code;
    }
    /**
     * Sets the error code
     * @return
     */
    public void setCode(Long code){
        this.code = code;
    }
	
}