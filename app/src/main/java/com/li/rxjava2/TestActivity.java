package com.li.rxjava2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.li.rxjava2.api.Api;
import com.li.rxjava2.bean.UserInfo;
import com.li.rxjava2.httpRequest.RetrofitWrapper;
import com.li.rxjava2.httpRequest.UserBaseInfoRequest;
import com.li.rxjava2.httpRequest.UserBaseInfoResponse;
import com.li.rxjava2.httpRequest.UserExtraInfoRequest;
import com.li.rxjava2.httpRequest.UserExtraInfoResponse;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
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
    @BindView(R.id.single)
    Button single;
    @BindView(R.id.with_no_request)
    Button with_no_request;
    @BindView(R.id.BackpressureStrategy_BUFFER)
    Button BackpressureStrategy_BUFFER;
    @BindView(R.id.flowable_backpressure)
    Button flowableBackpressure;
    @BindView(R.id.disposable)
    Button mDisposableButton;
    private Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        api = RetrofitWrapper.getInstance().getRetrofit().create(Api.class);
    }

    @OnClick({R.id.concatmap, R.id.filter, R.id.observable_backpressure, R.id.zip, R.id.flatmap, R.id.concat, R.id.flowable_backpressure, R.id.disposable,R.id.single,R.id.with_no_request,R.id.BackpressureStrategy_BUFFER})
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
//                flowable_Backpressure();

                BackpressureStrategy_BUFFER();

                break;
            case R.id.zip:
                zip();
                break;
            case R.id.disposable:
                disposable();
                break;
            case R.id.single:
                single();
                break;
            case R.id.with_no_request:
                withNoRequest();
                break;
            case R.id.BackpressureStrategy_BUFFER:
                BackpressureStrategy_BUFFER();
                break;
        }
    }

    private void BackpressureStrategy_BUFFER(){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        subscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }




    private void withNoRequest(){

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
            for (int i = 0 ;i <128 ; i ++){
                emitter.onNext(i);
                Log.e(TAG, "subscribe: " + i );
            }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.e(TAG, "onSubscribe");
                        subscription = s;
//                        subscription.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });


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

    private Subscription subscription;
    private void flowable_Backpressure() {

        //        使用示例
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                Log.e(TAG,"start send data ");
                for(int i=0;i<1000;i++){
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)//指定背压策略
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        //1， onSubscribe 是2.x新添加的方法，在发射数据前被调用，相当于1.x的onStart方法
                        //2， 参数为  Subscription ，Subscription 可用于向上游请求发射多少个元素，也可用于取消请求
                        //3,  必须要调用Subscription 的request来请求发射数据，不然上游是不会发射数据的。
                        Log.e(TAG,"onSubscribe...");
                        subscription = s;

                        //先做完所有前提工作再请求

                        s.request(100);   //必须调用，否则上游不会发送数据
                    }

                    @Override
                    public void onNext(Integer integer) {

                        //处理完毕之后
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG,"onNext:"+integer);

                        subscription.request(100);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG,"onError..."+t);

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG,"onComplete...");

                    }
                });



        //在同一个线程中
//        Flowable.interval(1, TimeUnit.MILLISECONDS)
//                .subscribe(new Subscriber<Long>() {
//                    @Override
//                    public void onSubscribe(Subscription s) {
//
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.e(TAG, "onNext: " + aLong );
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        Log.e(TAG, "onError: " + t);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "onComplete: " );
//                    }
//                });


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
        Observable.interval(1, TimeUnit.MILLISECONDS)
                //.subscribeOn(Schedulers.newThread())
                //将观察者的工作放在新线程环境中
                .observeOn(Schedulers.newThread())
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {        //官方建议1000个事件为分界线
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "onNext: " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e );
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: " );
                    }
                });



        //当运行在同一个线程时
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                        Log.e(TAG, "subscribe: " +  Thread.currentThread().getName());
//
//                        for (int i = 0; ; i++) {    //无限循环发事件
//                            e.onNext(i);
//                        }
//
//                    }
//                })
//                //将观察者的工作放在io线程中
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())   //如果注释掉运行在同一个线程
//                //观察者处理每1000ms才处理一个事件
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Thread.sleep(1000);
//                        Log.e(TAG, "accept: " + integer );
//                    }
//                });



//       Observable.interval(1, TimeUnit.MILLISECONDS)
//               .subscribe(new Consumer<Long>() {
//                   @Override
//                   public void accept(Long aLong) throws Exception {
//                       Thread.sleep(1000);
//                       Log.e(TAG, "accept: " + aLong );
//                   }
//               });

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


    private void zip(){

        //获取基本信息
        Observable<UserBaseInfoResponse> observable1 = api.getUserBaseInfo(new UserBaseInfoRequest()).subscribeOn(Schedulers.io());


        //获取额外信息
        Observable<UserExtraInfoResponse> observable2 = api.getUserExtraInfo(new UserExtraInfoRequest()).subscribeOn(Schedulers.io());


        Observable.zip(observable1, observable2, new BiFunction<UserBaseInfoResponse, UserExtraInfoResponse, UserInfo>() {
            @Override
            public UserInfo apply(UserBaseInfoResponse baseInfo, UserExtraInfoResponse extraInfo) throws Exception {
                return new UserInfo(baseInfo, extraInfo);

            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        // do something

                    }
                });
    }



    private void single(){

        Single<Long> single = Single.just(1l);

        single.subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long value) {
                // 和onNext是一样的
            }

            @Override
            public void onError(Throwable e) {

            }
        });

    }


}
