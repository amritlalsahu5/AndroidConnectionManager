package com.rootpie.networkmaster.android.aimpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rootpie.networkmaster.android.AllCatParam;
import com.rootpie.networkmaster.android.HttpMethod;
import com.rootpie.networkmaster.android.Request;

public class AllItemsFetchRequest extends Request<AllCatParam, AllCategoryItemResult>{

	
	public Map<String, String> getHeaders() {
        return new HashMap<String, String>();
    }

    public Map<String, String> getQueryParameters() {
        return new LinkedHashMap<String, String>();
    }
    
    public HttpMethod getHttpMethod(){
    	return HttpMethod.GET;
    }

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public byte[] getPayload() {
		// TODO Auto-generated method stub
		return null;
	}
}
