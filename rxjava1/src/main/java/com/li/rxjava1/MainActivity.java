package com.li.rxjava1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.activity_main_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressure();
            }
        });
    }

    private void backPressure(){
        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .onBackpressureDrop()    //启用背压策略之后使Observable支持背压
//                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e("TAG","---->"+aLong);
                    }
                });



//        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .observeOn(Schedulers.newThread())
//                //这个操作符简单理解就是把100毫秒内的事件打包成list发送
//                .buffer(100,TimeUnit.MILLISECONDS)
//                .subscribe(new Subscriber<List<Long>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: " + e );
//                    }
//
//                    @Override
//                    public void onNext(List<Long> longs) {
//                        Log.e(TAG, "onNext: " + longs );
//                    }
//                });



            //响应式拉取
//        Observable.interval(1, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Long>() {
//
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        request(1);    //要在onStart中通知被观察者先发送一个事件
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//
//                        //do something
//                        request(1); //处理完毕之后，在通知被观察者发送下一个事件
//                    }
//                });

    }

    private void test(){
        Observer observer =new Observer() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        };
    }
}
