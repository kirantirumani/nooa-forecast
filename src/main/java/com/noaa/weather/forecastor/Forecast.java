package com.noaa.weather.forecastor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noaa.weather.dao.nooaExceptionDao.NOOAException;
import com.noaa.weather.dao.pointsDao.Points;
import com.noaa.weather.dao.pointsDao.PointsApiObject;
import com.noaa.weather.utilities.APIConnector;
import com.noaa.weather.utilities.APIResponse;
import com.noaa.weather.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Forecast {

    private Logger logger = LoggerFactory.getLogger(Forecast.class);
    private String pointsApi = "https://api.weather.gov/points/%s,%s";
    private String forecastApi = "https://api.weather.gov/gridpoints/%s/%s,%s/forecast";
    private Points points;
    private PointsApiObject pointsApiObject;
    private NOOAException nooaException;
    private List<Object> forecasts;
    private int numberOfDays = 5;   // number of days for which you need to print forecast

    //constructor
    public Forecast() {
        points = new Points();
        pointsApiObject = new PointsApiObject();
        nooaException = new NOOAException();
        forecasts = new ArrayList<Object>();
    }

    /**
     * Orchestrator method for forecasting weather
     *
     * @param input
     */
    public void getForecast(String input) {

        points.setCoordinates(input);
        try {
            List<String> coordinates = extractCoordinates(input);
            if (coordinates.size() == 2) {
                points.setLatitude(coordinates.get(0));
                points.setLongitude(coordinates.get(1));
            } else {
                if (coordinates.size() > 2) {
                    points.setLatitude(coordinates.get(0));
                    points.setLongitude(coordinates.get(1));
                    logger.warn("More than 2 comma separated values provided: Assuming the first two as Valid Input values {}, {}", points.getLatitude(), points.getLongitude());
                } else {
                    logger.error("Invalid Number of params provided: Atleast 2 valid coordinates needed");
                }
            }
            Validator validator = new Validator();
            Boolean flag = validator.validate(points);
            if (flag) {
                getPoints();

                if (checkPoints()) {
                    getForecasts();
                    printForecasts();
                } else {
                    logger.error("Unable to make forecast: Insufficient Data \n");
                    logger.info("Points API did not return sufficient data to call the Forecast API \n");
                    logger.info("Points API must provide {office} {Grid X} and {Grid Y} values");
                }

            } else {
                logger.info("Unable to forecast weather for input due failure in Validation");
            }
        } catch (IllegalStateException e) {
            logger.error("Validation for latitude and longitude Failed {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unable to forecast weather for input {}", e.getMessage());

        }

    }

    /**
     * Method takes comma separated String input and extracts latitude and longitude from it
     *
     * @param input
     * @return List containing latitude and longitude values
     */
    private List<String> extractCoordinates(String input) {

        List<String> coordinates = new ArrayList<String>();
        logger.info("Extracting latitude and longitude from input {}", input);
        try {
            coordinates = Arrays.asList(input.split(","));
        } catch (Exception e) {
            logger.error("Unable to extract latitude and longitude from input {}", e.getMessage());
        }
        return coordinates;
    }

    /**
     * Method that consumes the Points Rest API : https://api.weather.gov/points/{latitude},{longitude}
     * and maps it to a POJO
     */
    private void getPoints() {


        String url = String.format(pointsApi, points.getLatitude(), points.getLongitude());
        logger.info("Calling url: {}", url);
        try {
            APIConnector connect = new APIConnector();
            APIResponse apiResponse = connect.sendGetRequest(url);
            if (apiResponse.getResponseCode() == 200) {
                logger.info("Successfully retrieved data from Points API, API Response Code {}", apiResponse.getResponseCode());
                Map<String, Object> pointsApiMap = parseJsonInputasMap(apiResponse.getResponseString());
                pointsApiObject.setCwa(((Map) pointsApiMap.get("properties")).get("cwa").toString());
                pointsApiObject.setGridX(((Map) pointsApiMap.get("properties")).get("gridX").toString());
                pointsApiObject.setGridY(((Map) pointsApiMap.get("properties")).get("gridY").toString());

            } else {
                Map<String, Object> pointsApiMap = parseJsonInputasMap(apiResponse.getResponseString());
                nooaException.setStatus(pointsApiMap.get("status").toString());
                nooaException.setTitle(pointsApiMap.get("title").toString());
                nooaException.setDetail(pointsApiMap.get("detail").toString());
                logger.info("Unable to correctly retrieve data from Points API \n Status: {} Title: {} \n Detail: {} \n ", nooaException.getStatus(), nooaException.getTitle(), nooaException.getDetail());

                logger.info("Unable to correctly retrieve data from Points API \n Status: {} Title: {} \n Detail: {} \n ", nooaException.getStatus(), nooaException.getTitle(), nooaException.getDetail());
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve data from {} API, Reason: {}", url, e.getMessage());
        }
    }


    /**
     * Method to print the forecasts for the given input coordinates
     */
    private void printForecasts() {


    }

    /**
     * Method that consumes the Points Rest API : https://api.weather.gov/gridpoints/{office}/{grid X},{grid Y}/forecast
     * and maps it to a POJO
     */
    private void getForecasts() {

        String url = String.format(forecastApi, pointsApiObject.getCwa(), pointsApiObject.getGridX(), pointsApiObject.getGridY());
        logger.info("Calling url: {}", url);
        try {
            APIConnector connect = new APIConnector();
            APIResponse apiResponse = connect.sendGetRequest(url);
            if (apiResponse.getResponseCode() == 200) {
                logger.info("Successfully retrieved data from Forecast API, API Response Code {}", apiResponse.getResponseCode());
                Map<String, Object> forecastApiMap = parseJsonInputasMap(apiResponse.getResponseString());
                forecasts = (List) ((Map) forecastApiMap.get("properties")).get("periods");
                ObjectMapper obj = new ObjectMapper();

                logger.info("Detailed forecast : ");
                StringBuffer sb = new StringBuffer();
                for (Object o : forecasts) {
                    sb.append(obj.writerWithDefaultPrettyPrinter().writeValueAsString(o));
                }

                logger.info(" {} ", sb);


            } else {
                Map<String, Object> forecastApiMap = parseJsonInputasMap(apiResponse.getResponseString());
                nooaException.setStatus(forecastApiMap.get("status").toString());
                nooaException.setTitle(forecastApiMap.get("title").toString());
                nooaException.setDetail(forecastApiMap.get("detail").toString());
                logger.info("Unable to correctly retrieve data from Points API \n Status: {} Title: {} \n Detail: {} \n ", nooaException.getStatus(), nooaException.getTitle(), nooaException.getDetail());
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve data from {} API, Reason: {}", url, e.getMessage());
        }
    }

    /**
     * Method to check if values for Office, GridX , GridY were retrieved post Points API call
     *
     * @return true/false
     */
    private Boolean checkPoints() {

        if (pointsApiObject.getCwa() == null || pointsApiObject.getGridX() == null || pointsApiObject.getGridY() == null)
            return false;
        else
            return true;
    }


    /**
     * Converts the input Json into a Map Object
     *
     * @param inputString
     * @return
     */
    public Map<String, Object> parseJsonInputasMap(String inputString) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> input = new HashMap<String, Object>();
        try {
            input = objectMapper.readValue(inputString,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception ex) {
            System.out.println("Unable to convert Input File to Map");
            ex.printStackTrace();

        }
        return input;
    }
}
