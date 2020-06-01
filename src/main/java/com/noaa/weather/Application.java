package com.noaa.weather;

import com.noaa.weather.forecastor.Forecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;


public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {

        logger.info("Starting NOOA Weather Forecasting com.noaa.weather.Application \n");

        Scanner in = new Scanner(System.in);

        logger.info("Enter latitude , longitude pair : ");

        String input = in.nextLine();
        logger.debug(" Non-null input received {}",input);
        logger.info( " Forecasting ..");
        Forecast forecast = new Forecast();
        forecast.getForecast(input);
    }


}
