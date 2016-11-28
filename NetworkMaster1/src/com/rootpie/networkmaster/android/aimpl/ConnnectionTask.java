package com.rootpie.networkmaster.android.aimpl;

import java.io.IOException;

import android.os.AsyncTask;

import com.rootpie.networkmaster.android.Callbacks;
import com.rootpie.networkmaster.android.Request;
import com.rootpie.networkmaster.android.exceptions.HttpRequestException;

public class ConnnectionTask<I, O> extends AsyncTask<Request<I, O>, Void, O>{

	
	private Callbacks<O> callback;
	public <V, T>  ConnnectionTask(Callbacks<O> callback) {
		this.callback = callback;

	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected O doInBackground(Request<I, O >... params) {
		
		BasicHttpClientImpl client = new BasicHttpClientImpl();
		try {
			client.execute(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	protected void onPostExecute(O result) {
		
	}

}
