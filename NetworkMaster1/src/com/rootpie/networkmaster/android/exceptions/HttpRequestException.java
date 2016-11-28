
package com.rootpie.networkmaster.android.exceptions;

import com.rootpie.networkmaster.android.net.http.HttpResponse;


public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -2413629666163901633L;

    private final HttpResponse httpResponse;

    /**
     * Constructs the exception with
     *
     * @param e
     * @param httpResponse
     */
    public HttpRequestException(Exception e, HttpResponse httpResponse) {
        super(e);
        this.httpResponse = httpResponse;
    }
    /*@Override
    public String getMessage() {
    	
    	return "Timeout waiting for server response";
    }*/
    
    @Override
    public String getMessage() {
    	
    	return "Server not responding";
    }

    /**
     * Access the response.
     *
     * @return Response object
     */
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}