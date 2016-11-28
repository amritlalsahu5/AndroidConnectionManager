package com.rootpie.networkmaster.android;

import com.rootpie.networkmaster.android.exceptions.HttpRequestException;


public interface Callbacks<V> {

    /**
     * Call abck for request is successfull or(response code =200)
     * 
     * @param data
     *            Response data
     */
    public void onSuccess(V data);

    /**
     * Call back for Request failed for some Request exception
     * 
     * @param re
     */
    public void onFailure(HttpRequestException requestException);

    /**
     * Call back for Request failed for request was intrrupted.
     * 
     * @param re
     */
    public void onFailure(InterruptedException interruptedException);


}
