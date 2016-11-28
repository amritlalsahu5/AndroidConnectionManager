package com.rootpie.networkmaster.android;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Request<I,O> {

	
	public Map<String, String> getHeaders() {
        return new HashMap<String, String>();
    }

    public Map<String, String> getQueryParameters() {
        return new LinkedHashMap<String, String>();
    }

   /* public I getPayload() {
        return null;
    }*/
    public HttpMethod getHttpMethod(){
    	return HttpMethod.GET ;
    }
    public abstract String getPath();
     public abstract byte[] getPayload();
	
}
