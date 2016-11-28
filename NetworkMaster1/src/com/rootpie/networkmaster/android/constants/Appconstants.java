package com.rootpie.networkmaster.android.constants;

public class Appconstants {

	// Download chunk size : 16 k
	public static final int DOWNLOAD_CHUNK_SIZE = 16384;// = 1048576/10;//1
														// MB;//Max allowed
														// Chunk size 16384
	// Upload Chuck size : 8 k
	public static final int UPLOAD_CHUNK_SIZE = 16384;
	public static final String REQUEST_PROPERTY_X_MOBILE_CLIENT = "X-Mobile-Client-Settings";
	
	
	public final static byte CONNECTION_PROPERTY_FIXED_STREAMING = 1;
	public final static byte CONNECTION_PROPERTY_FOLLOW_REDIRECTS = 2;
	public final static byte CONNECTION_PROPERTY_KEEP_ALIVE = 3;
	public final static byte CONNECTION_PROPERTY_FILE_TRNSFER_EXPECT = 4;
	
	
	// File Transfer Operation
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_X_GET_CRC = "X-GET-CRC";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_X_GET_CRC_VALUE = "true";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_FILE_RANGE = "Range";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_CONTENT_LENGTH = "Content-Length";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_CONTENT_RANGE = "Content-Range";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_X_CRC = "X-CRC";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_EXPECT = "Expect";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_EXPECT_VALUE = "100-continue";
		public static final String REQUEST_PROPERTY_FILE_TRNSFER_MODIFIED_DATE = "Last-Modified";



}
