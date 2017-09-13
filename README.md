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
  
###操作符简述
* map
* flatmap
* concatmap
* take（int value）只发送指定数量的observable
* timer     延迟发送
* buffer
* interval
* filter
* zip   用于合并事件，两两配对，最终配对出来的observable发射事件数目只和少的那个相同

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



