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
    private Boolean isDdingWei = false;//是不是定位的城市

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
        return isDdingWei;
    }

    public void setDdingWei(Boolean ddingWei) {
        isDdingWei = ddingWei;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("id", id);
//        bundle.putString("name", name);
//        bundle.putString("time", time);
//        bundle.putString("wen_du", wenDu);
//        bundle.putBoolean("is_ding_wei", isDdingWei);
//        dest.writeBundle(bundle);
//    }
//
//    public static final Creator<AddedCity> CREATOR = new Creator<AddedCity>() {
//        public AddedCity createFromParcel(Parcel in) {
//            return new AddedCity(in);
//        }
//
//        public AddedCity[] newArray(int size) {
//            return new AddedCity[size];
//        }
//    };
//
//    private AddedCity(Parcel in) {
//        Bundle bundle = in.readBundle();
//        id = bundle.getInt("id");
//        name = bundle.getString("name");
//        time = bundle.getString("time");
//        wenDu = bundle.getString("wen_du");
//        isDdingWei = bundle.getBoolean("is_ding_wei");
//    }
}
