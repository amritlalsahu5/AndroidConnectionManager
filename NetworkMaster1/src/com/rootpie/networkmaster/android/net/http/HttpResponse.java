
package com.rootpie.networkmaster.android.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.rootpie.networkmaster.android.constants.Appconstants;



public class HttpResponse {
	


    private int status;
    private String url;
    private final String securityPolicyCheck;
    private final Map<String, List<String>> headers;
    private  byte[] body;
    private String strResponse;


    public HttpResponse(){
    	this.headers = null;
        this.securityPolicyCheck = null;
    }
    public HttpResponse(HttpURLConnection urlConnection, byte[] body) {
        try {
            this.status = urlConnection.getResponseCode();
            this.url = urlConnection.getURL().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.headers = urlConnection.getHeaderFields();
        this.securityPolicyCheck = urlConnection.getHeaderField(Appconstants.REQUEST_PROPERTY_X_MOBILE_CLIENT);
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
    
    public String getBodyAsString() {
        if (body != null) {
            try {
            	strResponse =  IOUtils.toString(body, "UTF-8");
            	body = null;
			} catch (IOException e) {
			}
        }
        return strResponse;
    }


	public String getSecurityPolicyCheck() {
		return securityPolicyCheck;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Response Code : "+status +" \nResponse Header : "+headers;
	}

}