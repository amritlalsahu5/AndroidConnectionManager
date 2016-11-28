package com.rootpie.networkmaster.android.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientHandler {

	
	@SuppressWarnings("deprecation")
	public void sendGet(){
		
		try {
			URL url = new URL("");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet getReq = new HttpGet("url");
		getReq.setHeader("", "");
		
		try {
			HttpResponse response = client.execute(getReq);
			response.getStatusLine().getStatusCode();
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPost(){
		
		HttpClient client = new DefaultHttpClient();
		HttpPost  post =null;
		try {
		  post = new HttpPost(new URI(""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
