# Service

## 异步消息处理机制

安卓不能在子线程中进行UI操作，会让程序崩溃

```java
package com.example.service;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text  = findViewById(R.id.text);
        Button changeText = findViewById(R.id.change_text);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {//不能这样
                    @Override
                    public void run() {
                        text.setText("666");
                    }
                }).start();
            }
        });
    }
}
```

修改后

子线程中不进行UI操作，而是给message赋值，然后handler收到message的之后执行相应的UI操作

```java
package com.example.service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    public static final int UPDATE_TEXT = 1;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {//收到发送的message
                case UPDATE_TEXT:
                    text.setText("666");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text  = findViewById(R.id.text);
        Button changeText = findViewById(R.id.change_text);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message);//将message发送出去
                    }
                }).start();
            }
        });
    }
}
```

Message：在线程之间传递消息，有what字段，还有arg1,arg2字段用来携带一些整型数据，obj字段用来携带object对象

Handler：用于发送和处理信息发送用SendMessage方法，最后会把信息传递到handleMessage方法里

MessageQueue：消息队列，用来存放发送的message，里面的message会按顺序被处理

Looper：调用loop方法就会进入到一个无限循环，发现MessageQueue里面有message就会把它取出来

![49750c9d3c7fe4d1b81aeab21012a25](C:\Users\86158\OneDrive\桌面\49750c9d3c7fe4d1b81aeab21012a25.jpg)

## AsyncTask

是抽象类，要被继承

![729192747ced967225d07ff62dab0cf](C:\Users\86158\OneDrive\桌面\729192747ced967225d07ff62dab0cf.jpg)

要启动任务就new DownloadTask().execute()

onPreExecute用于界面初始化操作，如显示对话框

doInBackground执行具体的耗时任务，代码在子线程运行，不可进行UI操作，publishProgress方法来完成更新UI的需求

onProgressUpdate进行UI操作

onPostExecute收尾工作，比如关掉对话框



onPostExecute(Result)： 相当于Handler处理UI的方式，当doInBackground()方法完成后，系统会自动调用OnPostExecute()方法，并将doInBackground()方法的返回值传给该方法。在这里可以使用doInBackground()方法得到的结果处理操作UI。此方法在主线程执行。
onProgressUpdate(Progress…)： 在doInBackground()方法中调用publishProgress()方法更新任务的执行进度，将触发该方法。该方法在主线程中执行，用于显示任务执行的进度。
onPreExecute()： 方法将在后台任务执行前被调用。通常该方法用于完成一些初始化准备工作，比如界面上显示进度条等。此方法在主线程中执行。
onCancelled()： 异步任务取消时要做的操作。

```java
package com.example.asynctask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int STATE_FINISH = 1;
    private static final int STATE_ERROR = -1;
    private Button exe, cancel;
    private ProgressBar progressBar;
    TextView textView;
    private MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exe = findViewById(R.id.btn1);
        cancel = findViewById(R.id.btn2);
        progressBar = findViewById(R.id.bar);
        textView = findViewById(R.id.tv);
        exe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTask = new MyTask();
                myTask.execute();
                exe.setEnabled(false);
                cancel.setEnabled(true);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTask.cancel(true);
            }
        });

    }

    private class MyTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("downloading...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            for (int i = 0; i < 100; i = i + 10) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return STATE_ERROR;
                }
                publishProgress(i);
            }
            return STATE_FINISH;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            textView.setText("downloading..." + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            int state = result.intValue();
            if (state == STATE_FINISH) {
                textView.setText("done");
            } else if (state == STATE_ERROR) {
                textView.setText("error");
            }
            exe.setEnabled(true);
            cancel.setEnabled(false);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            textView.setText("cancelled!");
            progressBar.setProgress(0);
            exe.setEnabled(true);
            cancel.setEnabled(false);

        }
    }
}
```



```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"

    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/btn1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="EXECUTE" />

    <Button
        android:id="@+id/btn2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:enabled="false"
        android:text="CANCEL" />

    <ProgressBar
        android:id="@+id/bar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:max="100"
        android:progress="0" />

    <TextView
        android:id="@+id/tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```



## 服务

直接new Service

```java
package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {//服务创建时调用
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//服务启动是调用
        // 启动服务后执行的操作代码写这里
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {//服务销毁时调用
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
```



### 启动和停止活动

```java
package com.example.service;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.start_service);
        Button stop = findViewById(R.id.stop_service);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(MainActivity.this, MyService.class);
                startService(startIntent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent(MainActivity.this, MyService.class);
                stopService(stopIntent);
            }
        });
    }
}

```



### 绑定活动

```java
package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private DownloadBinder mBinder = new DownloadBinder();
    private static final String TAG = "MyService";
    public MyService() {
    }

    @Override
    public void onCreate() {//服务创建时调用
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//服务启动是调用
        // 启动服务后执行的操作代码写这里
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
        
    }

    @Override
    public void onDestroy() {//服务销毁时调用
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class DownloadBinder extends Binder {
        public void start() {
            Log.d(TAG, "start: 开始下载");
        }

        public void progress() {
            Log.d(TAG, "progress: 进度");
        }
    }
}
```



```java
package com.example.service;


import androidx.appcompat.app.AppCompatActivity;


import android.content.ComponentName;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.Provider;


public class MainActivity extends AppCompatActivity {
    private MyService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {//在绑定成功后自动调用
            downloadBinder = (MyService.DownloadBinder) iBinder;//向下转型
            downloadBinder.start();
            downloadBinder.progress();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {//解绑后自动调用

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        Button bind = findViewById(R.id.bind_service);
        Button unbind = findViewById(R.id.unbind_service);
        
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bindIntent = new Intent(MainActivity.this,MyService.class);
                //onCreate()会执行，onStartCommand()不会执行
                bindService(bindIntent,connection,BIND_AUTO_CREATE);//第三个参数表示活动和服务绑定后自动创建服务
            }
        });

        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(connection);
            }
        });
    }
}



```



### IntentService

异步的，会自动停止的服务

要无参构造函数

要实现onHandleIntent方法，这个方法就是在子线程的，在里面写具体逻辑，执行完会自动停止服务

```java
package com.example.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {
    private static final String TAG = "MyIntentService";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "thread:" + Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: thread");
    }
}

```

```java
start_intent_service.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Log.d(TAG, "thread" + Thread.currentThread().getId());
        Intent intentService = new Intent(MainActivity.this,MyIntentService.class);
        startService(intentService);
    }
});
```
