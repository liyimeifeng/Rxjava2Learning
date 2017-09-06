package com.li.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.li.rxjava2.api.ApiManager;
import com.li.rxjava2.bean.Constant;
import com.li.rxjava2.bean.WeatherData;
import com.li.rxjava2.httpRequest.RetrofitService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.button_getweather)
    Button button_Getweather;
    @BindView(R.id.text_weatherInfo)
    TextView text_WeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_getweather})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.button_getweather:
                getWeather();
                break;
        }
    }

    private void getWeather() {
        ApiManager apiManager = RetrofitService.getInstance().getRetrofit().create(ApiManager.class);
        apiManager.getWeather(Constant.city,Constant.Appid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WeatherData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e============" + e );
                    }

                    @Override
                    public void onNext(WeatherData weatherData) {
                        Log.e(TAG, "onNext: ==========" + weatherData.toString() );
                        text_WeatherInfo.setText(weatherData.toString());
                    }
                });
    }
}
