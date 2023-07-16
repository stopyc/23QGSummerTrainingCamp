# Retrofit

导依赖  implementation 'com.squareup.retrofit2:retrofit:2.9.0'

```
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
```



写接口

注解

 @FormUrlEncoded 注解

表示请求体是一个 Form 表单。



@Multipart 注解

表示请求体是一个支持文件上传的 Form 表单。

```java
 @POST("?service=App.CDN.UploadImg")
 @Multipart
    Call<UploadImgBean> testFileUpload1(@Part MultipartBody.Part file, @Part("app_key") RequestBody appKey);
/*
用了 @Part 注解，它属于第三类注解，用于表单字段，适用于有文件上传的情况。这里使用了@Part 的两种类型，MultipartBody.Part 表示上传一个文件，RequestBody 表示传一个键值对，其中 app_key 表示键，appKey 表示值。

*/

//Map同理
@POST("?service=App.CDN.UploadImg")
@Multipart
    Call<UploadImgBean> testFileUpload2(@PartMap Map<String, RequestBody> map);

```



@Streaming 注解

表示响应体的数据用流的形式返回，如果没有使用该注解，默认会把数据全部载入内存，之后获取数据就从内存中读取，所以该注解一般用在返回数据比较大的时候，例如下载大文件。下载文件不需要创建一个实体类，直接用 ResponseBody 来接收服务器返回的数据。

```java
public interface FileDownloadService {
    @Streaming
    @GET("medias/avatars/avatar.jpg")
    Call<ResponseBody> testFileDownload();
}



Retrofit retrofit = new Retrofit.Builder()
		.baseUrl("https://wildma.github.io/")
		.addConverterFactory(GsonConverterFactory.create())
		.build();

FileDownloadService service = retrofit.create(FileDownloadService.class);
Call<ResponseBody> call = service.testFileDownload();
call.enqueue(new Callback<ResponseBody>() {
	@Override
	public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
		InputStream is = response.body().byteStream();
		// 保存文件...
	}

	@Override
	public void onFailure(Call<ResponseBody> call, Throwable t) {

	}
});

```





![image-20230713182430676](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230713182430676.png)

![image-20230713192125780](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230713192125780.png)

![image-20230713192211467](C:\Users\86158\AppData\Roaming\Typora\typora-user-images\image-20230713192211467.png)

path里面就是get注解里面的东西



```java
package com.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpbinService {
    @POST("post")
    @FormUrlEncoded
    Call<ResponseBody> post(@Field("userName") String userName, @Field("passWord") String pwd);
/*
@Field里面的是键，参数是值，键值对
参数里的注解可以是@FieldMap  参数Map<String,Object> map 也是键值对
Map<String, Object> map = new HashMap<>();
map.put("username", "wildma");
map.put("password", "123456");
retrofit2.Call<ResponseBody> call2 = httpbinService.post(map);
 
 
 
 
*/  
    
    
    
    @GET("get")
    Call<ResponseBody> get(@Query("userName") String userName, @Query("passWord") String pwd);
    /*
   @Query 与 @QueryMap 注解
简介： 用于表单字段，功能与 @Field、@FiledMap 一样，区别在于 @Query、@QueryMap 的数据体现在 URL 上，而 @Field、@FiledMap 的数据体现在请求体上，但生成的数据是一样的。
    */

}
```



注释掉的是okhttp,作用一样

```
package com.example.retrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://httpbin.org/").build();
    HttpbinService httpbinService = retrofit.create(HttpbinService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.tongbu_get);
        Button button2 = findViewById(R.id.yibu_get);
        Button button3 = findViewById(R.id.tongbu_post);
        Button button4 = findViewById(R.id.yibu_post);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            
            case R.id.yibu_get:
                retrofit2.Call<ResponseBody> call1 = httpbinService.get("lance", "123");
                call1.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, "  " + responseData);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

                    }
                });
//                Request request = new Request.Builder()
//                        .url("https://httpbin.org/get?a=1&b=2")
//                        .build();
//                Call call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String responseData = response.body().string();
//                            Log.d(TAG, "  " + responseData);
//                        }
//                    }
//                });
                break;
            
            case R.id.yibu_post:
                retrofit2.Call<ResponseBody> call2 = httpbinService.post("lance", "123");
                call2.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        String responseData = null;
                        try {
                            responseData = response.body().string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, "  " + responseData);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("a","1")
//                        .add("b","2")
//                        .build();
//                request = new Request.Builder()
//                        .url("https://httpbin.org/post")
//                        .post(requestBody)
//                        .build();
//                call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            String responseData = response.body().string();
//                            Log.d(TAG, "  " + responseData);
//                        }
//                    }
//                });
                break;
            default:
                break;
        }
    }
}
```