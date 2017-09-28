# rxjava2 #
#### 链式操作 ####
##### 1、创建上游Observable被观察者对象在Rxjava 1中： #####
    Observable<Object> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //Emit Data
				subscriber.onNext("hello world");
            }
        })；
#####   在Rxjava 2中： #####
    Observable<Object> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
				//Emit data
				e.onNext("hello world");
            }
        });
##### 2、创建下游Observser观察者对象在Rxjava 1中 #####
    Observer observer = new Observer() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
				Log.e(TAG,"下游接收到的数据---->" + o);
            }
        };
#####   在Rxjava 2中： #####
    Observer observer = new Observer() {
      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onNext(Object value) {
		Log.e(TAG,"下游接收到的数据---->" + value);
      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onComplete() {

      }
  };

###各种被观察者
* Observable
* Single
* Completable
* Behavisor
* **Flowable**  （启用背压策略）
* AsyncSubject
* ReplaySubject
* PublishSubject

###观察者
* Observer
* Subcriber
* DisposableObserver（可被CompositeDisposable管理）
* Disposable（由Subscription改名）

###存放订阅者信息
* CompositeSubscription（在1中）

		CompositeSubscription.unsubscribe();  （解除订阅）

* CompositeDisposable  （在2中）
  
		CompositeDisposable.dispose();





###[操作符简述](http://reactivex.io/documentation/operators.html)


* **creat**

[ "create an Observable from scratch by means of a function"](http://reactivex.io/documentation/operators/create.html)

![](https://i.imgur.com/UrUrw5E.png)
		
		//不关心onComplete、onError时
	 	Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
                e.onNext("B");
                e.onComplete();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "accept: " + s );
            }
        });
![](https://i.imgur.com/q5rwnrd.png)

		//需要处理omComplete、onError情况时
		Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG,"onNext: "  + value);
            }

            @Override
            public void onError(Throwable e) {
                //do something
            }

            @Override
            public void onComplete() {
                //do something
                Log.e(TAG, "onComplete: " );
            }
        });
![](https://i.imgur.com/sdgyaBA.png)

* **just**

["convert an object or a set of objects into an Observable that emits that or those objects"
](http://reactivex.io/documentation/operators/just.html)

**发射单个数据**
![](https://i.imgur.com/ofjjJPN.png)		

**发射多个数据**
![](https://i.imgur.com/AI1CPPt.png)

		Observable.just(1,2,3,4)
               .subscribe(new Observer<Integer>() {
                   @Override
                   public void onSubscribe(Disposable d) {

                   }

                   @Override
                   public void onNext(Integer value) {
                       Log.e(TAG, "onNext: " + value );
                   }

                   @Override
                   public void onError(Throwable e) {

                   }

                   @Override
                   public void onComplete() {
                       Log.e(TAG, "onComplete: " );
                   }
               });

![小标](https://i.imgur.com/Ktss34c.png)


* **map**

对数据执行一些比如类型转换的操作后再发射出去

![](http://upload-images.jianshu.io/upload_images/1407686-78ccca7dda44aeea.png?imageMogr2/auto-orient/strip%7CimageView2/2)

![](https://i.imgur.com/DNUnju5.png)

		Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.e(TAG, "accept : " + s +"\n" );
            }
        });
![](https://i.imgur.com/dQcqMY2.png)

* **filter**

按照一定的规则过滤数据,只返回判断为true的数据

![](https://i.imgur.com/yf4sKD7.png)



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
                        Log.e(TAG, "accept: " + integer );
                    }
                });
	
![](https://i.imgur.com/e2kVNlz.png)

* **flatmap**

把一个发射器  Observable 通过某种方法转换为多个 Observables，然后再把这些分散的 Observables装进一个单一的发射器 Observable（**不保证事件顺序**）

![](https://i.imgur.com/mBf0Lac.png)

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

![](https://i.imgur.com/KsWeO0M.png)

* **concatmap**

`concatmap` 与 `FlatMap` 的唯一区别就是 `concatMap` **保证了事件顺序**。

![](https://i.imgur.com/oAaQ7mM.png)

* **concat**

单一的把两个发射器连接成一个发射器

![](https://i.imgur.com/746pHlO.png)

* **fromIterable/fromArray**  (由from拆分而来)


* **interval**

* zip   用于合并事件，两两配对，最终配对出来的observable发射事件数目只和少的那个相同
* take（int value）只发送指定数量的observable
* timer     延迟发送
* buffer
* reduce
* simple
* skip
* scan

* defer
* merge
* last
* debounce
* windows
* distinct
* throttleFirst
* throttleLast


###关于背压backpressure
#####先从场景出发
Rxjava是一个观察者模式的结构，当这个架构的被观察者（Observable）和观察者（Subscriber、Observer）
处在不同的线程环境中，也就是**异步**的情况下，可能会出现产生的事件和处理的事件的速度不一致，就会出现两种情况：

* 观察者产生事件的速度，观察者处理事件很快，那么观察者就会等待被观察者发送事件（**上游慢。下游快，下游水管等待上游水管往下流水，没问题**）

* 被观察者产生事件的速度很快，而观察者处理很慢，如果不做处理，事件堆积堆积....,最后挤爆内存，OOM。（**上游块，下游慢，上游流下的水充满了下游水管，最后溢出**）

用**Rxjava1**代码模拟一下：

	//被观察者在主线程中，每1ms发送一个事件
	Observable.interval(1, TimeUnit.MILLISECONDS)
                //.subscribeOn(Schedulers.newThread())
                //将观察者的工作放在新线程环境中
                .observeOn(Schedulers.newThread())
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Action1<Long>() {
                      @Override
                      public void call(Long aLong) {
                          try {
                              Thread.sleep(1000);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }
                          Log.w("TAG","---->"+aLong);
                      }
                  });

这种被观察者发送事件的速度是观察者处理速度的1000倍，观察者来不及处理事件，最终....

> **MissingBackpressureException**

什么是**backPressure**

划重点：**在异步场景中，当观察者处理事件的速度低于被观察者发送事件的速度时，一种告诉上游被观察者降低发射产生事件速度的策略，也就是流速控制的策略。**


##### 注意事项
在Rx2中，我们在onSubscribe()回调中必须调用s.request()方法去请求资源，参数就是要请求的数量，一般如果不限制请求数量，可以写成Long.MAX_VALUE，之后会立即触发onNext()方法！所以当你在onSubscribe()/onStart()中做了一些初始化的工作，而这些工作是在request()后面时，会出现一些问题，在onNext()执行时，你的初始化工作的那部分代码还没有执行。为了避免这种情况，请确保你调用request()时，已经把所有初始化工作做完了。
