package com.noaa.weather.utilities;

public class APIResponse {
    private String responseString;
    private int responseCode;

    public APIResponse() {
    }

    public APIResponse setResponseString(String strResponse) {
        this.responseString = strResponse;
        return this;
    }

    public String getResponseString() {
        return this.responseString;
    }

    public APIResponse setResponseCode(int value) {
        this.responseCode = value;
        return this;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}

