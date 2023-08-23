# WebSocket

不能send版

### JWebSocketClient

```java
package com.example.logintest;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {

    public JWebSocketClient(URI serverUri) {
        super(serverUri);
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.d("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("JWebSocketClient", "onError()");
    }

}
```



### JWebSocketClientService

```java
package com.example.logintest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClientService extends Service {
    private URI uri;
    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private OnMessageListener onMessageListener;//接口的声明
    
    //回调接口
	public interface OnMessageListener {
        void getMessage(String message);
    }
    
    //以回调接口为参数的方法
    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("服务启动", "onCreate: ");
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

    }

    class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        uri = URI.create("ws://124.222.224.186:8800");//放地址
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("JWebSocketClientService", "收到的消息：" + message);
				if (onMessageListener != null) {
                    onMessageListener.getMessage(message);
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.d("JWebSocketClientService", "websocket连接成功");
            }

            @Override
            public void onError(Exception ex) {
                super.onError(ex);
                Log.d("JWebSocketClientService", "onError: " + ex);
            }
        };
        connect();
    }

    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    Log.d("连接失败","错误");
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("JWebSocketClientService", "心跳包检测websocket连接状态");
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                } else {
                    //业务逻辑 这里如果服务端需要心跳包为了防止断开 需要不断发送消息给服务端
                    Log.d("心跳检测", "run: 正常");
                    client.send("");
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                Log.d("心跳检测", "run: client已空");
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
```



### MainActivity    

```java
package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private JWebSocketClient client;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务与活动成功绑定
            Log.e(TAG, "服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            
            //调接口为参数的方法
            jWebSClientService.setOnMessageListener(new JWebSocketClientService.OnMessageListener() {
                @Override
                public void getMessage(String message) {
                    System.out.println(message);
                    runOnUiThread(new Runnable() {//更新界面
                        @Override
                        public void run() {
                            textView = findViewById(R.id.tv);
                            textView.setText(message);
                        }
                    });

                }
            });
            
            client = jWebSClientService.client;
            client.send("11111111111111111111111");//发送信息
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服务与活动断开
            Log.e(TAG, "服务与活动成功断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
    }

    private void bindService() {
        Intent bindIntent = new Intent(MainActivity.this,
                JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }
}
```





### InitSocketThread	

```java
package com.example.logintest;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class InitSocketThread extends Thread {
    private static final String TAG = "InitSocketThread";
    BackService service;
    Handler serviceHandler;


    public InitSocketThread() {
    }

    public InitSocketThread(BackService service, Handler handler) {
        this.service = service;
        this.serviceHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        try {
            initSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private static final String WEBSOCKET_HOST_AND_PORT = "ws://124.222.224.186:8800";//可替换为自己的主机名和端口号
    public static WebSocket mWebSocket;

    public void initSocket() throws UnknownHostException, IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(6000, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder()
                .url(WEBSOCKET_HOST_AND_PORT).build();//地址
        Log.d(TAG, "initSocket: " + request.url().url());
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: onclose");
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                mWebSocket = webSocket;
                Log.e("INITSOCKET", "initsocket onFailure");
                t.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                Log.d(TAG, "onMessage: 返回的消息是 " + text);
                ChangeText.changeText.postValue(text);//livedata
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen: onopen");
                mWebSocket = webSocket;
                mWebSocket.send("11111111111111111111");  //向服务器发送id
            }
        });
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
    }

    private static long sendTime = 0L;
    // 发送心跳包
    private static Handler mHandler = new Handler();
    private static Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                if (mWebSocket == null) {
                    mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
                    return;
                }
                boolean isSuccess = mWebSocket.send("");//发送一个空消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
                if (!isSuccess) {//长连接已断开
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mWebSocket.cancel();//取消掉以前的长连接
                    new InitSocketThread().start();//创建一个新的连接
                } else {//长连接处于连接状态

                }
                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };

}


```





### BackService

```java
package com.example.logintest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackService extends Service {
    private static final String TAG = "BackService";
    protected Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service create");
        new InitSocketThread(this, handler).start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public BackService getMyService() {
            return BackService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (InitSocketThread.mWebSocket != null) {
            InitSocketThread.mWebSocket.close(1000, null);
        }
    }
}
```



### MainActivity

```java
package com.example.logintest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;


public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";
    BackService myService = null;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: 绑定成功");
            myService = ((BackService.MyBinder) iBinder).getMyService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent myServiceIntent = new Intent(this, BackService.class);
        bindService(myServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        textView = findViewById(R.id.tv);
        ChangeText.changeText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
    }
}
```



### ChangeText

```java
package com.example.logintest;

import androidx.lifecycle.MutableLiveData;

public class ChangeText {
    public static MutableLiveData<String> changeText = new MutableLiveData<>();

    public static MutableLiveData<String> getChangeText() {
        return changeText;
    }
} 
```