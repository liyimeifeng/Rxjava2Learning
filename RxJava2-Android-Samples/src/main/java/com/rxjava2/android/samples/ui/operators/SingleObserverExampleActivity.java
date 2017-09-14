package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.utils.AppConstant;

import javax.security.auth.login.LoginException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class SingleObserverExampleActivity extends AppCompatActivity {

    private static final String TAG = SingleObserverExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomeWork();
            }
        });
    }

    /*
     * simple example using SingleObserver
     */
    private void doSomeWork() {
        Single.just("Amit")
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: " + d.isDisposed());
                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        textView.append(" onNext : value : " + s);
                        textView.append(AppConstant.LINE_SEPARATOR);
                        Log.d(TAG, " onNext value : " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        textView.append(" onError : " + e.getMessage());
                        textView.append(AppConstant.LINE_SEPARATOR);
                        Log.d(TAG, " onError : " + e.getMessage());
                    }
                });
    }


}