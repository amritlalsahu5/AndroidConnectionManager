package com.rootpie.networkmaster.android.net.http.request;

import java.util.Map;
import java.util.TreeMap;

import com.rootpie.networkmaster.android.HttpMethod;
import com.rootpie.networkmaster.android.net.http.ParameterMap;


public abstract class HttpRequest {

	public static final String URLENCODED = "";
	public static final String MULTIPART = "";
    protected String path = ""; // avoid null in URL
    protected HttpMethod httpMethod;
    protected String contentType;
    protected byte[] payload;
    private  Map<String, String> requestHeaders = new TreeMap<String, String>();
    /**
     * Constructs a request with optional params appended
     * to the query string.
     *
     * @param path
     * @param params
     */
    

    public HttpRequest(String path, ParameterMap params) {
        String queryString = null;
        if (path != null) {
            this.path = path;
        }
        if (params != null) {
            queryString = params.urlEncode();
            this.path += "?" + queryString;
        }
       
 
    }
    public HttpRequest(String path) {
    	this(path,null);
    }


    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    public void setHttpMethod(HttpMethod method) {
        httpMethod =method;
    }


    public String getContentType() {
        return contentType;
    }

    public byte[] getPayload() {
        return payload;
    }
    
    public void addHeader(String name, String value) {
        requestHeaders.put(name, value);
       
    }

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	

}
