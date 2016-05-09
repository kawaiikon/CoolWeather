package com.coolweather.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.coolweather.Prams;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.model.AddedCity;
import com.coolweather.coolweather.model.City;
import com.coolweather.coolweather.model.CoolWeatherDB;
import com.coolweather.coolweather.model.County;
import com.coolweather.coolweather.model.Province;
import com.coolweather.coolweather.util.HttpCallbackListener;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.LogUtil;
import com.coolweather.coolweather.util.MyActivityManager;
import com.coolweather.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/2/4.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;

    //是否从WeatherActivity中跳转过来的
    private boolean isFromWeatherActivity;

    private SharedPreferences preferences;
    private List<AddedCity> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_area);
//        MyActivityManager.finishAllActivity();
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (preferences.getBoolean("city_selected", false) && !isFromWeatherActivity) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }

        //从本地读取已添加城市
        mList = Utility.loadAddedCity(this);
        //第二次打开直接显示天气界面
        if (mList.size() > 0 && !isFromWeatherActivity){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    MyActivityManager.finishAllActivity();
                    String countyCode = countyList.get(position).getCountyCode();
                    //把添加的城市存到本地
                    AddedCity addedCity = new AddedCity();
                    addedCity.setCountyCode(countyCode);
                    addedCity.setName(countyList.get(position).getCountyName());
                    addedCity.setId(countyList.get(position).getId());
                    mList.add(addedCity);
                    Utility.saveAddedCity(ChooseAreaActivity.this, mList);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("from_choose_activity", true);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /*
    * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    * */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /*
    * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
    * */
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /*
    * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
    * */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCountries(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /*
    * 根据传入的代号和类型从服务器上查询省市县数据。
    * */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = Prams.QUERY_AREA + code + Prams.XML;
        } else {
            address = Prams.QUERY_ALL_AREA;
        }
        LogUtil.e("url", address);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                LogUtil.e("response", response);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    //通过runOnUiThread()方法回到主线程里逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程里逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /*
    * 显示进度对话框
    * */
    //
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获back按键，根据当前的级别来判断，此时应该返回城市列表、省列表、还是直接退出。
    * */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            super.onBackPressed();
        }
    }
}
