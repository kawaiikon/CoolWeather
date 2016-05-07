package com.coolweather.coolweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.coolweather.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bian on 2016/5/4 16:56.
 */
public class WeatherAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mList;

    public WeatherAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.timeText.setText(mList.get(position) + "时");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.time_text)
        TextView timeText;
        @Bind(R.id.weather_img)
        ImageView weatherImg;
        @Bind(R.id.weather_text)
        TextView weatherText;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
