package com.li.rxjava2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class TestActivity extends AppCompatActivity {

    private final static String TAG = TestActivity.class.getSimpleName();
    @BindView(R.id.concatmap)
    Button concatmap;
    @BindView(R.id.filter)
    Button filter;
    @BindView(R.id.observable_backpressure)
    Button buffer;
    @BindView(R.id.zip)
    Button zip;
    @BindView(R.id.activity_test)
    LinearLayout activityTest;
    @BindView(R.id.flatmap)
    Button flatmap;
    @BindView(R.id.concat)
    Button concat;
    @BindView(R.id.flowable_backpressure)
    Button flowableBackpressure;
    @BindView(R.id.disposable)
    Button mDisposableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.concatmap, R.id.filter, R.id.observable_backpressure, R.id.zip, R.id.flatmap, R.id.concat, R.id.flowable_backpressure, R.id.disposable})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.flatmap:
                flatmap();
                break;
            case R.id.concatmap:
                concatmap();
                break;
            case R.id.concat:
                concat();
                break;
            case R.id.filter:
                filter();
                break;
            case R.id.observable_backpressure:
                observable_Backpressure();
                break;
            case R.id.flowable_backpressure:
                flowable_Backpressure();
                break;
            case R.id.zip:
                break;
            case R.id.disposable:
                disposable();
        }
    }

    private Disposable disposable;

    private void disposable() {
        Observable.just(1, 2, 3, 4)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Integer value) {
                        if (value > 3) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();


    }

    private void flowable_Backpressure() {


        Flowable.interval(1, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "onNext: " + aLong );
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: " );
                    }
                });


        /**
         * 报异常
         */
//        Flowable.interval(1, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        Thread.sleep(2000);
//                        Log.e(TAG, "accept: " + aLong);
//                    }
//                });
    }

    private void observable_Backpressure() {


        //理论上说Observable订阅Observer是不支持背压的
//        Observable.interval(1, TimeUnit.MILLISECONDS)
//                //.subscribeOn(Schedulers.newThread())
//                //将观察者的工作放在新线程环境中
//                .observeOn(Schedulers.newThread())
//                //观察者处理每1000ms才处理一个事件
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Long value) {        //官方建议1000个事件为分界线
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Log.e(TAG, "onNext: " + value);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: " + e );
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "onComplete: " );
//                    }
//                });


        Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        Log.e(TAG, "subscribe: " +  Thread.currentThread().getName());

                        for (int i = 0; ; i++) {    //无限循环发事件
                            e.onNext(i);
                        }

                    }
                })
                //将观察者的工作放在io线程中
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())   //如果注释掉运行在同一个线程
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(1000);
                        Log.e(TAG, "accept: " + integer );
                    }
                });


    }

    private void contains() {
        Observable.just(1, 2, 3)
                .contains(2)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e(TAG, "accept: " + aBoolean);
                        // true
                    }
                });
    }

    private void concat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e(TAG, "concat : " + integer + "\n");
                    }
                });

    }

    private void filter() {

        Observable.just(1, 2, 3, 4, 5, 6)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 0;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer);
                    }
                });

    }


    private void flatmap() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                //延迟500毫秒
                return Observable.fromIterable(list).delay(500, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "flatMap : accept : " + s + "\n");
                    }
                });


    }

    private void concatmap() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                //延迟500毫秒
                return Observable.fromIterable(list).delay(500, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "flatMap : accept : " + s + "\n");
                    }
                });


    }


}
