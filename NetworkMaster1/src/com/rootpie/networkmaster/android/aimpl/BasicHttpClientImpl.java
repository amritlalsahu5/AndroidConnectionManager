package com.rootpie.networkmaster.android.aimpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;



import android.util.Log;

import com.rootpie.networkmaster.android.HttpMethod;
import com.rootpie.networkmaster.android.Request;
import com.rootpie.networkmaster.android.constants.Appconstants;
import com.rootpie.networkmaster.android.exceptions.HttpRequestException;
import com.rootpie.networkmaster.android.net.BasicRequestHandler;
import com.rootpie.networkmaster.android.net.RequestHandler;
import com.rootpie.networkmaster.android.net.http.HttpResponse;
import com.rootpie.networkmaster.android.net.http.ParameterMap;
import com.rootpie.networkmaster.android.net.http.request.HttpRequest;





public  class BasicHttpClientImpl {

	public static final String URLENCODED = "";
	public static final String MULTIPART = "";
	protected String baseUrl = "";

	protected  HttpRequest httpRequest = null;
	protected BasicRequestHandler requestHandler;
	private final Map<String, String> requestHeaders = new TreeMap<String, String>();
	
	/**
	 * SSL Context clas
	 */
	public static SSLContext ssl_ctx;

	/**
	 * Default timeout should be set to 10 secs 
	 */
	protected int connectionTimeout = 10000;//5000;

	/**
	 * Default timeout for read timeout is 60 secs
	 */
	protected int readTimeout = 60000;//5000;

	protected int connectionTimeoutTransfer = 20000;//5000;    
	protected int readTimeoutTransfer = 60000;//5000;
	/**
	 * Indicates connection status, used by timeout logic
	 */
	protected boolean isConnected = false;

	protected InputStream in = null;
	protected OutputStream out = null;
	protected HttpsURLConnection uc;



	/**
	 * Construct a client with baseUrl and RequestHandler.
	 *
	 * @param baseUrl
	 * @param requestHandler
	 */
	public BasicHttpClientImpl( BasicRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	/**
	 * Constructs a client with empty baseUrl. Prevent sub-classes from calling
	 * this as it doesn't result in an instance of the subclass.
	 */

	public BasicHttpClientImpl() {
		requestHandler = new BasicRequestHandler(){};
	}



	/**
	 * This method wraps the call to doHttpMethod and invokes the custom error
	 * handler in case of exception. It may be overridden by other clients such
	 * {@link AsyncHttpClient} in order to wrap the exception handling for
	 * purposes of retries, etc.
	 *
	 * @param httpRequest
	 * @return Response object (may be null if request did not complete)
	 * @throws IOException 
	 */
	public <I,O> HttpResponse execute(Request<I,O> httpRequest)throws  IOException,HttpRequestException  {
		
		HttpResponse httpResponse = null;
		prepareConnection(httpRequest);
		connect(httpRequest.getHttpMethod());
		
		byte[] content = httpRequest.getPayload();
		if(httpRequest.getHttpMethod().getDoOutput()  && content != null){
			writeOutputStream(uc, content);
		}
		
		
		if (httpRequest.getHttpMethod().getDoInput() == true) {
			httpResponse = readInputStream(uc);
		} else {
			httpResponse = new HttpResponse(uc, null);
		}
		this.handleResponseCode(httpResponse);
		return httpResponse;
	}
	
	/**
	 * Creates a secured URL Connection
	 * @param uc
	 * @param path
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private HttpsURLConnection createSSLChannel(String path)  throws HttpRequestException, IOException{
		HttpsURLConnection uc = null;
		System.setProperty("https.protocols", "TLSv1");
		

		HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx .getSocketFactory());
		HttpsURLConnection.setFollowRedirects(false);
		
		uc = openConnection(path);
		uc.setInstanceFollowRedirects(false);
		uc.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		return uc;
	}

	
	/**
	 * This is the method that drives each request. It implements the request
	 * lifecycle defined as open, prepare, write, read. Each of these methods in
	 * turn delegates to the {@link RequestHandler} associated with this client.
	 *
	 * @param path Whole or partial URL string, will be appended to baseUrl
	 * @param httpMethod Request method
	 * @param contentType MIME type of the request
	 * @param payload Request data
	 * @param req 
	 * @return Response object
	 */
	@SuppressWarnings("finally")
	public void prepareConnection(Request httpRequest) throws HttpRequestException, IOException {
			uc = createSSLChannel(httpRequest.getPath());
			prepareConnection(uc, httpRequest.getHttpMethod());
			appendRequestHeaders(uc,httpRequest.getHeaders());
	}
	
	public HttpResponse connect(HttpMethod httpMethod) throws IOException {
		HttpResponse httpResponse = null;
		uc.connect();
		isConnected = true;
		// If output is true then do not fetch response code.
		if (httpMethod.getDoOutput() == false) {
			httpResponse = new HttpResponse(uc, null);
		}
		return httpResponse;
	}
	
	public boolean setConnectionProperty(byte key, String value){
		boolean isSet = true;
		if(value == null){
			return isSet = false;
		}
		switch(key){
		case Appconstants.CONNECTION_PROPERTY_FIXED_STREAMING:
			int size = 0;
			try{
				size = Integer.valueOf(value);
			}catch(Throwable th){
				// Ignore
				return isSet = false;
			}
//			uc.setChunkedStreamingMode(AppConstants.UPLOAD_CHUNK_SIZE);
			uc.setFixedLengthStreamingMode(size);
			break;
		case Appconstants.CONNECTION_PROPERTY_FOLLOW_REDIRECTS:
			boolean property;
			try{
				property = Boolean.valueOf(value);
			}catch(Throwable th){
				// Ignore
				return isSet = false;
			}
			uc.setInstanceFollowRedirects(property);
			break;
		case Appconstants.CONNECTION_PROPERTY_KEEP_ALIVE:
			uc.setRequestProperty("Connection", "Keep-Alive");
			break;
		case Appconstants.CONNECTION_PROPERTY_FILE_TRNSFER_EXPECT:
            uc.setRequestProperty(Appconstants.REQUEST_PROPERTY_FILE_TRNSFER_EXPECT,Appconstants.REQUEST_PROPERTY_FILE_TRNSFER_EXPECT_VALUE);
            break;
		}
		return isSet;
		
	}
	
	

	/**
	 * Validates a URL and opens a connection. This does not actually connect
	 * to a server, but rather opens it on the client only to allow writing
	 * to begin. Delegates the open operation to the {@link RequestHandler}.
	 *
	 * @param path Appended to this client's baseUrl
	 * @return An open connection (or null)
	 * @throws IOException
	 */
	protected HttpsURLConnection openConnection(String path) throws IOException, HttpRequestException {
		String requestUrl = baseUrl + path;
		try {
			new URL(requestUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(requestUrl + "", e);
		}
		return requestHandler.openConnection(requestUrl);
	}

	/**
	 * Prepares the Initial Connection
	 * @param urlConnection
	 * @param httpMethod
	 * @param contentType
	 * @throws IOException
	 */
	protected void prepareConnection(HttpURLConnection urlConnection, HttpMethod httpMethod) throws IOException {
		urlConnection.setConnectTimeout(connectionTimeout);
		urlConnection.setReadTimeout(readTimeout);
		requestHandler.prepareConnection(urlConnection, httpMethod,null);
	}

	/**
	 * Append all headers added with {@link #addHeader(String, String)} to the
	 * request.
	 *
	 * @param urlConnection
	 * @param req 
	 */
	protected void appendRequestHeaders(HttpURLConnection urlConnection, Map<String, String> req) {
		for (String name : req.keySet()) {
			String value = req.get(name);
			urlConnection.setRequestProperty(name, value);
			Log.d(BasicHttpClientImpl.class.toString(), name + " : " + value);
		}
	}

	/**
	 * Writes the request to the server. Delegates I/O to the {@link RequestHandler}.
	 *
	 * @param urlConnection
	 * @param content to be written
	 * @return HTTP status code
	 * @throws IOException 
	 * @throws Exception in order to force calling code to deal with possible
	 *             NPEs also
	 */
	protected int writeOutputStream(HttpURLConnection urlConnection, byte[] content) throws IOException{
		//OutputStream out = null;
		try {
			out = requestHandler.openOutput(urlConnection);
			if (out != null) {
				requestHandler.writeStream(out, content);
			}
			return urlConnection.getResponseCode();
		} finally {
			// catch not necessary since method throws Exception
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// Swallow to show first cause only
				}
			}
		}
	}
	
	/**
	 * Reads from file and writes to the server output Stream
	 *
	 * @param urlConnection
	 * @param payload to be written
	 * @return HTTP status code
	 * @throws Exception in order to force calling code to deal with possible
	 *             NPEs also
	 */
	protected int writeOutputStream(HttpURLConnection urlConnection, InputStream in){
		// Not implemented for Basic Http Client Impl.
		return 0;
	}
	
	
	/**
	 * Reads the input stream. Delegates I/O to the {@link RequestHandler}.
	 *
	 * @param urlConnection
	 * @return HttpResponse, may be null
	 * @throws Exception
	 */
	protected HttpResponse readInputStream(HttpURLConnection urlConnection, OutputStream out){
		// Not implemented for Basic Http Client Impl.
		return null;
	}
	
	
	/**
	 * Reads the input stream. Delegates I/O to the {@link RequestHandler}.
	 *
	 * @param urlConnection
	 * @return HttpResponse, may be null
	 * @throws IOException 
	 * @throws Exception
	 */
	protected HttpResponse readInputStream(HttpURLConnection urlConnection) throws IOException{
		byte[] responseBody = null;
		try {
			in = requestHandler.openInput(urlConnection);
			if (in != null) {
				responseBody = requestHandler.readStream(in);
				
			}
			return new HttpResponse(urlConnection, responseBody);
		}catch(Exception e){
			return new HttpResponse(urlConnection, null);
		}finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// Swallow to avoid dups
				}
			}
		}
	}
	
	
	public InputStream openReadStream() throws IOException{
		in = requestHandler.openInput(uc);
		return in;
	}
	
	public OutputStream openWriteStream() throws IOException{
		out = requestHandler.openOutput(uc);
		return out;
	}
	

	/**
	 * Reads the error stream to get an HTTP status code like 404.
	 * Delegates I/O to the {@link RequestHandler}.
	 *
	 * @param urlConnection
	 * @return HttpResponse, may be null
	 * @throws Exception
	 */
	protected HttpResponse readErrorStream(HttpURLConnection urlConnection) throws Exception {
		InputStream err = null;
		byte[] responseBody = null;
		try {
			err = urlConnection.getErrorStream();
			if (err != null) {
				responseBody = requestHandler.readStream(err);
			}
			return new HttpResponse(urlConnection, responseBody);
		} finally {
			if (err != null) {
				try {
					err.close();
				} catch (Exception e) {
					// Swallow to avoid dups
				}
			}
		}
	}

	/**
	 * Convenience method creates a new ParameterMap to hold query params
	 *
	 * @return Parameter map
	 */
	public ParameterMap newParams() {
		return new ParameterMap();
	}

	/**
	 * Adds to the headers that will be sent with each request from this client
	 * instance. The request headers added with this method are applied by
	 * calling {@link HttpURLConnection#setRequestProperty(String, String)}
	 * after {@link #prepareConnection(HttpURLConnection, HttpMethod, String)},
	 * so they may supplement or replace headers which have already been set.
	 * Calls to {@link #addHeader(String, String)} may be chained. To clear all
	 * headers added with this method, call {@link #clearHeaders()}.
	 *
	 * @param name
	 * @param value
	 * @return this client for method chaining
	 */
	public BasicHttpClientImpl addHeader(String name, String value) {
		requestHeaders.put(name, value);
		return this;
	}

	/**
	 * Clears all request headers that have been added using
	 * {@link #addHeader(String, String)}. This method has no effect on headers
	 * which result from request properties set by this class or its associated
	 * {@link RequestHandler} when preparing the {@link HttpURLConnection}.
	 */
	public void clearHeaders() {
		requestHeaders.clear();
	}

	/**
	 * Returns the {@link CookieManager} associated with this client.
	 *
	 * @return CookieManager
	 */
	public static CookieManager getCookieManager() {
		return (CookieManager) CookieHandler.getDefault();
	}




	/**
	 * Initialize the app-wide {@link CookieManager}. This is all that's
	 * necessary to enable all Web requests within the app to automatically send
	 * and receive cookies.
	 */
	protected static void ensureCookieManager() {
		if (CookieHandler.getDefault() == null) {
			CookieHandler.setDefault(new CookieManager());
		}
	}

	/**
	 * Determines whether an exception was due to a timeout. If the elapsed time
	 * is longer than the current timeout, the exception is assumed to be the
	 * result of the timeout.
	 *
	 * @param t Any Throwable
	 * @return true if caused by connection or read timeout
	 */
	protected boolean isTimeoutException(Throwable t, long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime + 10; // fudge

		if (isConnected) {
			return elapsedTime >= readTimeout;
		} else {
			return elapsedTime >= connectionTimeout;
		}
	}

	/**
	 * Sets the connection timeout in ms. This is the amount of time that
	 * {@link HttpURLConnection} will wait to successfully connect to the remote
	 * server. The read timeout begins once connection has been established.
	 *
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Sets the read timeout in ms, which begins after connection has been made.
	 * For large amounts of data expected, bump this up to make sure you allow
	 * adequate time to receive it.
	 *
	 * @param readTimeout
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}


	public void handleResponseCode(HttpResponse httpResponse)throws IOException{

		Log.d("HttpClient ", "response Code :"+ httpResponse.getStatus() );

		if(httpResponse.getStatus() == HttpURLConnection.HTTP_OK||httpResponse.getStatus()==HttpURLConnection.HTTP_CREATED 
				|| httpResponse.getStatus()==HttpURLConnection.HTTP_PARTIAL)
			return;

		switch (httpResponse.getStatus()) {


		case HttpURLConnection.HTTP_NOT_FOUND:
			throw new FileNotFoundException();


		case HttpURLConnection.HTTP_CLIENT_TIMEOUT:

		case HttpURLConnection.HTTP_UNAUTHORIZED:
		case HttpURLConnection.HTTP_MOVED_TEMP:

		case HttpsURLConnection.HTTP_FORBIDDEN:

		case HttpsURLConnection.HTTP_BAD_REQUEST:

		case HttpsURLConnection.HTTP_NOT_ACCEPTABLE:

		case HttpsURLConnection.HTTP_ENTITY_TOO_LARGE:

		case HttpsURLConnection.HTTP_REQ_TOO_LONG:

		case HttpsURLConnection.HTTP_INTERNAL_ERROR:
			throw new IOException();
		default:
			throw new IOException();


		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	
	public int getHttpResponseCode() throws IOException{
		if (uc == null){
			return -1;
		}
		return uc.getResponseCode();
	}

	
	public HttpsURLConnection getConnectionInstance(){
		return uc;
	}
	

	/**
	 * Download Chunks of a file and writes to the a file on the device.
	 * @return
	 * @throws Exception
	 */
	public HttpResponse readChunk() throws  IOException,
			Exception {
		HttpResponse httpResponse = null;
		byte[] responseBody = null;
		if (in != null) {
			responseBody = requestHandler.readStream(in);
			httpResponse = new HttpResponse(uc, responseBody);
			this.handleResponseCode(httpResponse);
		}
		return httpResponse;
	}

	/**
	 * Closes and releases all resources
	 */
	public void close(){
		if(in != null){
			try {
				in.close();
				in = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(out != null){
			try {
				out.close();
				out = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(uc != null){
			uc.disconnect();
			uc = null;
		}
		requestHandler.cleanBuffers();
	}
	
	public void disconnect(){
		if(uc != null){
			uc.disconnect();
			uc = null;
		}
	}
	
}
