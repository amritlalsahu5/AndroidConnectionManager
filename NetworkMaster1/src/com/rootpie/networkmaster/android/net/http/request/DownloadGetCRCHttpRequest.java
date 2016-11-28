package com.rootpie.networkmaster.android.net.http.request;

import com.rootpie.networkmaster.android.constants.Appconstants;



public class DownloadGetCRCHttpRequest extends HttpRequest {
	public static final String BLANK_SPACE = " ";
	public static final String HYPHEN = "-";
	public static final String FORWARD_SLASH = "/";
	public static final String COLON = ":";
	public DownloadGetCRCHttpRequest(String path,long offset) {
		
		super(path);
	    this.httpMethod =com.rootpie.networkmaster.android.HttpMethod.HEAD;   	    
	    addHeader(Appconstants.REQUEST_PROPERTY_FILE_TRNSFER_X_GET_CRC, Appconstants.REQUEST_PROPERTY_FILE_TRNSFER_X_GET_CRC_VALUE);
		if(offset>0){
			addHeader( Appconstants.REQUEST_PROPERTY_FILE_TRNSFER_FILE_RANGE,"bytes=0-"+offset);		
		}
	}



	

}
