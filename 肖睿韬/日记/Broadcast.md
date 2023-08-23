# Broadcast

使用广播我们首先要进行广播的注册，注册广播有两种方式，动态注册和静态注册。动态注册的优点是可以实现灵活的广播注册和注销，但缺点就是必须要程序启动后才能接收到广播。如果想要在程序未启动时，比如刚开机的情况下接受到系统的开机广播，那就需要使用静态注册，但静态注册长期监听，消耗更多资源，因此大部分情况建议优先使用动态注册解决问题。

### 动态注册

广播动态注册需要我们在代码中进行注册，首先写一个广播接收器，继承自BroadcastReceiver类

```java
package com.example.lambda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
		//事务处理代码
        //在这里写上相关的处理代码，一般来说，不要此添加过多的逻辑或者是进行任何的耗时操作
        //因为广播接收器中是不允许开启多线程的，过久的操作就会出现报错
        //因此广播接收器更多的是扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动某个服务

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            //屏幕关闭
            Log.d(TAG, "屏幕关闭");
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d(TAG, "屏幕打开");
        }

    }
}
```



activity

```java
package com.example.lambda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBroadcastReceiver = new MyBroadcastReceiver();
 		//这里意图过滤器简单点解释就是选择你想要监听的广播，因为系统中广播的使用是非常频繁的，而我们某一个广播接收器其实只处理某一个或者某几个广播，而过滤的内容其实就是这个功能，上述我们addAction加入的就是屏幕亮起和熄灭的广播内容。
        intentFilter = new IntentFilter();//意图过滤器
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(myBroadcastReceiver, intentFilter);//注册
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);//注销
    }
}
```