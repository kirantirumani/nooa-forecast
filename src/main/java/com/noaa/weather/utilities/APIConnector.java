package com.noaa.weather.utilities;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class APIConnector {

    private ObjectMapper objectMapper;

    public APIConnector() {
        this.objectMapper = null;
    }

    public ObjectMapper getObjectMapper() {
        if (this.objectMapper != null) {
            return this.objectMapper;
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            return objectMapper;
        }
    }

    /**
     * Return the response from a REST Webservice
     * @param URL
     * @return
     */
    public APIResponse sendGetRequest(String URL) {
        APIResponse apiResponse = null;
        try {
            apiResponse = new APIResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpUriRequest req = new HttpGet(URL);
            HttpResponse response = null;
            response = httpclient.execute(req);
            int statusCode = response.getStatusLine().getStatusCode();
            String getResult = null;
            if (response.getEntity() != null) {
                getResult = EntityUtils.toString(response.getEntity());
            }
            apiResponse.setResponseCode(statusCode).setResponseString(getResult);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }
}