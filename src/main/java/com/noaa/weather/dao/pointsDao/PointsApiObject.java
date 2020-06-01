package com.noaa.weather.dao.pointsDao;

public class PointsApiObject {

    public String getCwa() {
        return cwa;
    }

    public void setCwa(String cwa) {
        this.cwa = cwa;
    }

    public String getGridX() {
        return gridX;
    }

    public void setGridX(String gridX) {
        this.gridX = gridX;
    }

    public String getGridY() {
        return gridY;
    }

    public void setGridY(String gridY) {
        this.gridY = gridY;
    }

    private String cwa;
    private String gridX;
    private String gridY;


}
