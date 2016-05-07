package com.coolweather.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.coolweather.coolweather.R;
import com.coolweather.coolweather.adapter.AreaMangerAdapter;
import com.coolweather.coolweather.model.AddedCity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AreaManagerActivity extends Activity implements View.OnClickListener{

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

        Intent intent = getIntent();
        mList = intent.getParcelableArrayListExtra("added_city_list");

        areaMangerAdapter = new AreaMangerAdapter(mList, AreaManagerActivity.this);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mCityRecyclerView.setAdapter(areaMangerAdapter);

        mAddCityText.setOnClickListener(this);
        mEditCityText.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_city_text:
                break;
            case R.id.edit_city_text:
                if (!isEdit){
                    mEditCityText.setText("完成");
                    isEdit = !isEdit;
                    areaMangerAdapter.isEdit(isEdit);
                }else {
                    mEditCityText.setText("编辑");
                    isEdit = !isEdit;
                    areaMangerAdapter.isEdit(isEdit);
                }
                break;
        }
    }
}
