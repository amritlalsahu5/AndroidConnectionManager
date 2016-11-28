package com.rootpie.networkmaster.android;

public enum HttpMethod {
    GET(true, false),
    POST(true, true),
    PUT(true, true),
    DELETE(true, false),
    HEAD(false, false),
    MKCOL(true,false);

    private boolean doInput;
    private boolean doOutput;

    private HttpMethod(boolean doInput, boolean doOutput) {
        this.doInput = doInput;
        this.doOutput = doOutput;
    }

    public boolean getDoInput() {
        return doInput;
    }

    /**
     * Whether the client should do the write phase, or just read
     *
     * @return doOutput
     */
    public boolean getDoOutput() {
        return this.doOutput;
    }

    /**
     * Accessor method.
     *
     * @return HTTP method name (GET, PUT, POST, DELETE)
     */
    public String getHttpMethod() {
        return this.toString();
    }

}
