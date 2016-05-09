package com.coolweather.coolweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.coolweather.CommonRes;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.adapter.AreaMangerAdapter;
import com.coolweather.coolweather.model.AddedCity;
import com.coolweather.coolweather.util.Utility;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AreaManagerActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.add_city_text)
    TextView mAddCityText;
    @Bind(R.id.edit_city_text)
    TextView mEditCityText;
    @Bind(R.id.city_recycler_view)
    RecyclerView mCityRecyclerView;

    private List<AddedCity> mList;
    private AreaMangerAdapter areaMangerAdapter;
    private Boolean isEdit = false;//判断是否在编辑状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_manager);
        ButterKnife.bind(this);

        //从本地读取已添加城市
        mList = Utility.loadAddedCity(this);

        areaMangerAdapter = new AreaMangerAdapter(mList, AreaManagerActivity.this);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mCityRecyclerView.setAdapter(areaMangerAdapter);

        mAddCityText.setOnClickListener(this);
        mEditCityText.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mList = Utility.loadAddedCity(this);
        areaMangerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_city_text:
                if (!isEdit) {
                    if (mList.size() <= 9) {
                        Intent intent = new Intent(AreaManagerActivity.this, ChooseAreaActivity.class);
                        intent.putExtra("from_weather_activity", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AreaManagerActivity.this, "最多添加9个城市", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mList = Utility.loadAddedCity(AreaManagerActivity.this);
                    areaMangerAdapter.setList(mList);
                    mEditCityText.setText("编辑");
                    mAddCityText.setText("添加");
                    isEdit = !isEdit;
                    areaMangerAdapter.isEdit(isEdit);
                }
                break;
            case R.id.edit_city_text:
                if (!isEdit) {
                    mEditCityText.setText("完成");
                    mAddCityText.setText("取消");
                    isEdit = !isEdit;
                    areaMangerAdapter.isEdit(isEdit);
                } else {
                    mEditCityText.setText("编辑");
                    mAddCityText.setText("添加");
                    isEdit = !isEdit;
                    areaMangerAdapter.isEdit(isEdit);
                    Utility.saveAddedCity(AreaManagerActivity.this, mList);
                    //如果删除了全部城市重新选择
                    sendBroadcast(new Intent(CommonRes.DELETE_CITY_BROADRECAST));
                    if (mList.size() == 0) {
                        Intent intent = new Intent(AreaManagerActivity.this, ChooseAreaActivity.class);
                        intent.putExtra("from_weather_activity", true);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }
}
