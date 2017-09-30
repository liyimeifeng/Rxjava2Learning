# rxjava2 #


## 有什么不同？ ##
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

##### 2、Action命名变化 #####

Rx1.0-----------Rx2.0

Action1--------Consumer

Action2--------BiConsumer

而Action3 - Action9都不再使用了，ActionN变成了Consumer<Object[]> 。

Func--------Function

Func2--------BiFunction

Func3 - Func9 改名成 Function3 - Function9

###各种被观察者
* **Observable**
* **Single**

	和Observable，Flowable一样会发送数据，不同的是订阅后只能接受到一次:

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

普通Observable可以使用toSingle转换:Observable.just(1).toSingle()


	

* **Completable**

与Single类似，只能接受到完成(onComplete)和错误(onError)

同样也可以由普通的Observable转换而来:Observable.just(1).toCompletable()

* Behavisor
* **Flowable**  （启用背压策略）
* AsyncSubject
* ReplaySubject
* PublishSubject

###观察者
* Observer
* Subcriber
* Single/SingleObserver
* DisposableObserver（可被CompositeDisposable管理）
* Disposable（由Subscription改名）

###存放订阅者信息
* CompositeSubscription（在1中）

		CompositeSubscription.unsubscribe();  （解除订阅）

* CompositeDisposable  （在2中）
  
		CompositeDisposable.dispose();





###[操作符简述](http://reactivex.io/documentation/operators.html)


* **creat**

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

将一个发射数据的`Observable`变换为多个`Observables`，然后将它们发射的数据合并后放进一个单独的`Observable`（**不保证事件顺序**）

flatMap使用一个指定的函数对原始Observable发射的每一项数据之行相应的变换操作，这个函数返回一个本身也发射数据的Observable，然后FlatMap合并这些Observables发射的数据，最后将合并后的结果当做它自己的数据序列发射。

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

把两个甚至多个发射器连接成一个发射器，也就是接收若干个Observables，发射数据是有序的，不会交叉，并且只有前一个 Observable 终止(**onComplete**) 后才会订阅下一个 Observable。

多用来实现多数据源的例子，比如一个商品详情需要展示商品的信息、艺术家信息、与该商品类似的商品，可能需要访问三个接口。这个时候就可以使用concat操作符。

![](https://i.imgur.com/746pHlO.png)

[进阶用法](http://www.jianshu.com/p/7474950af2df)

## 先读取缓存数据并展示UI再获取网络数据刷新UI ##

这里需要依赖另一个操作符：**Concat**

`concat`可以做到不交错的发射两个或多个`Observable`的发射物，并且只有前一个`Observable`终止(onComleted)才会订阅下一个`Obervable`

利用这个特性，我们就可以依次的读取缓存数据展示UI，然后再获取网络数据刷新UI

1. 首先创建一个从cache获取数据的observable
2. 再创建一个从网络获取数据的Observable(可以通过map等方法转换数据类型)
3. 通过concat方法将多个observable结合起来
4. 通过subscribe订阅每一个observable


		Observable<List<String>> cache = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                CacheManager manager = CacheManager.getInstance();
                List<String> data = manager.query();
                e.onNext(data);
                //一定要有onComplete，不然不会执行第二个Observale
                e.onComplete();
            }
        });

        Observable<List<String>> network = Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Request.Builder builder = new Request.Builder()
                        .url("url")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                e.onNext(response);
                e.onComplete();
            }
        }).map(new Function<Response, List<String>>() {
            @Override
            public List<String> apply(@NonNull Response response) throws Exception {
                //解析数据
            }
        });

		//两个observable的泛型应该保持一致
        Observable.concat(cache, network)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> strings) throws Exception {
                        //refresh ui
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        //get error
                    }
                });

## 获取网络数据前先读取缓存 ##

其实和上面的那种类似，只需要稍微修改一下逻辑即可：
当缓存的Observable获取到数据时，只执行onNext，获取不到则只执行onComplete

		Observable<String> cache = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                CacheManager manager = CacheManager.getInstance();
                String data = manager.queryForPosition(0);
                if (data != null) {
                    e.onNext(data);
                } else {
                    //调用onComplete之后会执行下一个Observable
                    //如果缓存为空，那么直接结束，进行网络请求
                    e.onComplete();
                }
            }
        });

        Observable<String> network = Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                Request.Builder builder = new Request.Builder()
                        .url("url")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                e.onNext(response);
                e.onComplete();
            }
        }).map(new Function<Response, String>() {
            @Override
            public String apply(@NonNull Response response) throws Exception {
                //解析数据
            }
        });

		//两个observable的泛型应该保持一致
        Observable.concat(cache, network)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String strings) throws Exception {
                        //refresh ui
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        //get error
                    }
                });

* **fromIterable/fromArray**  (由from拆分而来)

将数组或者Iterable中的元素逐个取出然后在逐个依次发射

![](https://i.imgur.com/4w3HOJz.png)

* **timer**
![](https://i.imgur.com/ErMycvy.png)

**延迟执行**

	 Observable.timer(5, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                LogUtils.d("------->onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

            }
        });

* **interval**

![](https://i.imgur.com/AvHCbjH.png)

（和timer均默认运行在新线程）

		Observable.interval(0,5,TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                LogUtils.d("------>along："+aLong+" time:"+SystemClock.elapsedRealtime());
            }
        });

* **doOnNext**

让订阅者在接收到数据之前做点其他事

	Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                      //本地保存等等
                        Log.e(TAG, "doOnNext 保存 " + integer + "成功" + "\n");
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {

                Log.e(TAG, "doOnNext :" + integer + "\n");
            }
        });


* **take**
 
接受一个 long 型参数 count ，只发送指定数量的observable

	Observable.just(1, 2, 3, 4, 5)
                .take(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + integer );
                        // 1 2 3
                       
                    }
                });


* **repeat**

![](https://i.imgur.com/qdW1krp.png)


* **distinct**

去重

	Observable.just(1, 1, 1, 2, 2, 3, 4, 5)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e(TAG, "distinct : " + integer + "\n");
						// 1 2 3 4 5
                    }
                });

* **debounce**

去除发送频率过快的项

	//去除发送间隔时间小于 500 毫秒的发射事件
	Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1); // skip
                Thread.sleep(400);
                emitter.onNext(2); // deliver
                Thread.sleep(505);
                emitter.onNext(3); // skip
                Thread.sleep(100);
                emitter.onNext(4); // deliver
                Thread.sleep(605);
                emitter.onNext(5); // deliver
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {

                        Log.e(TAG,"debounce :" + integer + "\n");
						// 2 4 5
                    }
                });

* **last**

仅取出可观察到的最后一个值，或者是满足某些条件的最后一项

	Observable.just(1, 2, 3)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        mRxOperatorsText.append("last : " + integer + "\n");
                        Log.e(TAG, "last : " + integer + "\n");
                    }
                });

* **merge**

把多个 Observable 结合起来，接受可变参数，也支持迭代器集合。它和 concat 的区别在于，不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。

	Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        mRxOperatorsText.append("merge :" + integer + "\n");
                        Log.e(TAG, "accept: merge :" + integer + "\n" );
                    }
                });




* **reduce**
 
对所有数据进行处理，最终emit一个数据

		Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {

                Log.e(TAG, "accept: reduce : " + integer + "\n");
				//1 + 2 = 3  + 3 = 6
            }
        });
![](https://i.imgur.com/Eeux3mb.png)

* **scan**

把每一个步骤都输出

	Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {

                Log.e(TAG, "accept: scan : " + integer + "\n");

            }
        }); 
![](https://i.imgur.com/xESpJY8.png)

* **skip**

跳过前n个observable
![](https://i.imgur.com/1RUvm91.png)

* **sample**

每隔指定的时间就从上游中取出一个事件发送给下游.

![](https://i.imgur.com/BoL3e1h.png)

* **zip**   

![](https://i.imgur.com/w16wgpf.png)
用于合并事件，两两配对，最终配对出来的observable发射事件数目只和少的那个相同

	
	     Observable.zip(Observable.just(1, 2, 3), Observable.just(6,7, 8, 9), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                Log.e(TAG, "apply: " +  integer + "+" +integer2 );
                return integer + integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: " + integer );
            }
        });
![](https://i.imgur.com/Xlc3DdU.png)


常用情景：一个页面显示的数据来源于多个接口，可以将多个 Observable 的数据结合为一个数据源再发射出去

举个例子：

	/**
     * 基本信息
     * @param request
     * @return
     */
    @GET
    Observable<UserBaseInfoResponse> getUserBaseInfo(@Body UserBaseInfoRequest request);


    /**
     * 额外信息
     * @param request
     * @return
     */
    @GET
    Observable<UserExtraInfoResponse> getUserExtraInfo(@Body UserExtraInfoRequest request);

用`Zip`来打包请求

		//获取基本信息
        Observable<UserBaseInfoResponse> observable1 = api.getUserBaseInfo(new UserBaseInfoRequest()).subscribeOn(Schedulers.io());


        //获取额外信息
        Observable<UserExtraInfoResponse> observable2 = api.getUserExtraInfo(new UserExtraInfoRequest()).subscribeOn(Schedulers.io());


        Observable.zip(observable1, observable2, new BiFunction<UserBaseInfoResponse, UserExtraInfoResponse, UserInfo>() {
            @Override
            public UserInfo apply(UserBaseInfoResponse baseInfo, UserExtraInfoResponse extraInfo) throws Exception {
                return new UserInfo(baseInfo,extraInfo);
                
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        // do something

                    }
                });




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
![](https://i.imgur.com/2DDou9x.png)

> **MissingBackpressureException**



什么是**backPressure**

划重点：**在异步场景中，当观察者处理事件的速度低于被观察者发送事件的速度时，一种告诉上游被观察者降低发射产生事件速度的策略，也就是流速控制的策略。**


![](https://i.imgur.com/rxQ7NVj.png)

使用`Observable`无限循环发送数据
	
	 Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        for (int i = 0; ; i++) {    //无限循环发事件
                            e.onNext(i);
                        }
                    }
                })
                //将观察者的工作放在新线程环境中
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())   //如果注释掉运行在同一个线程
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(1000);
                        Log.e(TAG, "accept: " + integer );
                    }
                });

![](https://i.imgur.com/2ukUpfo.png)


使`Flowable`测试

	 Flowable.interval(1, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Thread.sleep(2000);
                        Log.e(TAG, "accept: " + aLong);
                    }
                });

![](https://i.imgur.com/74Awqs1.png)


### Observable使用场景: ###

* 数据流最长不超过1000个元素，即随着时间的流逝，应用没有机会报OOME(OutOfMemoryError)错误。

* 处理诸如鼠标移动或触摸事件之类的GUI事件

### Flowable使用场景: ###

* 处理超过10K+ 的元素流
* 从磁盘读取（解析文件）
* 从数据库读取数据
* 从网络获取数据流


### Flowable示例 ###

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

![](https://i.imgur.com/2d5rpdW.png)
##### 注意事项

> 在Rx2中，我们在onSubscribe()回调中必须调用s.request()方法去请求资源，参数就是要请求的数量，一般如果不限制请求数量，可以写成Long.MAX_VALUE，之后会立即触发onNext()方法！所以当你在onSubscribe()/onStart()中做了一些初始化的工作，而这些工作是在request()后面时，会出现一些问题，在onNext()执行时，你的初始化工作的那部分代码还没有执行。为了避免这种情况，请确保你调用request()时，已经把所有初始化工作做完了。


当使用request响应式拉取时


> 在Flowable里默认有一个大小为128的水缸, 当上下游工作在不同的线程中时, 上游就会先把事件发送到这个水缸中, 因此, 下游虽然没有调用request, 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.