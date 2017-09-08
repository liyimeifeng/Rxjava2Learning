package com.li.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.li.rxjava2.api.ApiManager;
import com.li.rxjava2.bean.Constant;
import com.li.rxjava2.bean.WeatherData;
import com.li.rxjava2.httpRequest.RetrofitWrapper;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.activity_main_button_getWeather)
    Button button_Getweather;
    @BindView(R.id.activity_main_textview_weatherDescription)
    TextView Text_WeatherDescription;
    @BindView(R.id.activity_main_text_temperature)
    TextView Text_Temperature;
    @BindView(R.id.activity_main_text_pressure)
    TextView Text_Pressure;
    @BindView(R.id.activity_main_text_humidity)
    TextView Text_Humidity;
    @BindView(R.id.activity_main_text_sunset)
    TextView Text_Sunset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.activity_main_button_getWeather})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_button_getWeather:
                getWeatherInfo();
                break;
        }
    }

    private void getWeatherInfo() {
        ApiManager apiManager = RetrofitWrapper.getInstance().getRetrofit().create(ApiManager.class);
        apiManager.getWeatherByName(Constant.city)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {  //默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，它将执行在离它最近的 subscribeOn() 所指定的线程。
                    @Override
                    public void call() {
                        Log.e(TAG, "call: ----->" + Thread.currentThread() );
                        Toast.makeText(MainActivity.this, "正在获取信息....", Toast.LENGTH_SHORT).show();
                    }
                })
                .map(new Func1<WeatherData, WeatherData>() {
                    @Override
                    public WeatherData call(WeatherData weatherData) {
                        return null;
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WeatherData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: e============" + e);
                    }

                    @Override
                    public void onNext(WeatherData weatherData) {
                        Log.e(TAG, "onNext: ==========" + weatherData.toString());
                        Text_Temperature.setText( "温度（华氏度）：" + weatherData.getMain().getTemp());
                        Text_WeatherDescription.setText("天气：" + weatherData.getWeather().get(0).getDescription());
                        Text_Humidity.setText( "湿度：" + weatherData.getMain().getHumidity());
                        Text_Pressure.setText( "气压：" + weatherData.getMain().getPressure());
                        Text_Sunset.setText("日落时间：" + dataFormate(weatherData.getSys().getSunset()));
                    }
                });

    }


    private String  dataFormate(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

}
