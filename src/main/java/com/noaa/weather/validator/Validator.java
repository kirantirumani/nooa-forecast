package com.noaa.weather.validator;


import com.noaa.weather.dao.pointsDao.Points;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Validator {

    /**
     * Method validating the input coordinates provided
     * @param points
     * @return : true/false
     */
    private Logger logger = LoggerFactory.getLogger(Validator.class);
    public Boolean validate(Points points) throws IllegalStateException {

        try {
            double latitude = Double.parseDouble(points.getLatitude());
            double longitude = Double.parseDouble(points.getLongitude());
            if ( longitude>180 || longitude<-180 ) throw new IllegalStateException("The longitude of a point must be between -180 and 180");
            if ( latitude>90 || latitude<-90 ) throw new IllegalStateException("The latitude of a point must be between -90 and 90");
            return true;
        }
        catch (NumberFormatException n){
            logger.error(" Input is not in the correct Numerical format {}",n.getMessage());
            return false;
        }
    }

}
