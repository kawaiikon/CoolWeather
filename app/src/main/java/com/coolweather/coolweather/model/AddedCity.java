package com.coolweather.coolweather.model;

/**
 * Created by bian on 2016/5/6 11:43.
 */
public class AddedCity {

    private int id;
    private String name;
    private String time;
    private String wenDu = "--°";
    private String countyCode;
    private Boolean isDingWei = false;//是不是定位的城市

    public AddedCity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWenDu() {
        return wenDu;
    }

    public void setWenDu(String wenDu) {
        this.wenDu = wenDu;
    }

    public Boolean getDdingWei() {
        return isDingWei;
    }

    public void setDdingWei(Boolean ddingWei) {
        isDingWei = ddingWei;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

}
