package com.rootpie.networkmaster.android.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.rootpie.networkmaster.android.HttpMethod;
import com.rootpie.networkmaster.android.aimpl.BasicHttpClientImpl;
import com.rootpie.networkmaster.android.constants.Appconstants;
import com.rootpie.networkmaster.android.exceptions.HttpRequestException;
import com.rootpie.networkmaster.android.net.http.HttpResponse;

/**
 * Default {@link RequestHandler} used by {@link BasicHttpClientImpl}. It is
 * intended to be used for simple requests with small amounts of data only (a
 * few kB), as it does no buffering, chunking, streaming, etc. Only character
 * set supported is UTF-8. Only {@link String} content is supported. All
 * responses are treated as {@link String}s. This class is abstract so that
 * it can be easily extended in an anonymous inner class when constructing
 * a client.
 *
 * 
 */
public class BasicRequestHandler implements RequestHandler {

	protected OutputStream outStream;
	protected byte[] chunkData;
	protected boolean isAppendFile ;
	protected File cachedFile;
    @Override
    public HttpsURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        return uc;
    }

    @Override
    public void prepareConnection(HttpURLConnection urlConnection, HttpMethod httpMethod,
            String contentType) throws IOException {
        // Configure connection for request method
    	urlConnection.setDoOutput(httpMethod.getDoOutput());
        urlConnection.setDoInput(httpMethod.getDoInput());
        // Don't use cache copy
        urlConnection.setUseCaches(false);
    	urlConnection.setRequestMethod(httpMethod.getHttpMethod());
        
    	
        if (contentType != null) {
            urlConnection.setRequestProperty("Content-Type", contentType);
        }
        
        // Set additional properties
     //  urlConnection.setRequestProperty("Accept-Charset", UTF8);
    }

    @Override
    public OutputStream openOutput(HttpURLConnection urlConnection)
                throws IOException {
                return urlConnection.getOutputStream();
    }

    @Override
    public void writeStream(OutputStream out, byte[] content) throws IOException {
        out.write(content);
    }
    
    @Override
    public void writeStream(DataOutputStream out, byte[] content, int offset,
    		int count) throws IOException {
    }
    
    @Override
    public void writeStream(OutputStream out, byte[] content, int offset, int count) throws IOException{
    	out.write(content, offset, count);
    }

    @Override
    public InputStream openInput(HttpURLConnection urlConnection)
                throws IOException {
                return urlConnection.getInputStream();
    }

    @Override
    public byte[] readStream(InputStream in) throws IOException {
        int nRead;
        byte[] bufferData;
        byte[] data = new byte[Appconstants.DOWNLOAD_CHUNK_SIZE];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while ((nRead = in.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        bufferData = buffer.toByteArray();
        buffer.flush();
        return bufferData;
    }

    @Override
    public boolean onError(HttpRequestException e) {
        HttpResponse res = e.getHttpResponse();
      
        if (res != null) {
            int status = res.getStatus();
            if (status > 0) {
                // Perhaps a 404, 501, or something that will be fixed later
                return true;
            }
        }
        // Connection refused, host unreachable, etc.
        return false;
    }
    public  void loadDestination() {
		this.chunkData = new byte[Appconstants.DOWNLOAD_CHUNK_SIZE];
		try {
			outStream = new FileOutputStream(cachedFile, isAppendFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void cleanBuffers(){
    	
    }
    public void closeDestination() {
		this.chunkData = null;
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outStream = null;
		}
	}

}
