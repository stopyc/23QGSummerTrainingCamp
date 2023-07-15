# Glide

导依赖    implementation 'com.github.bumptech.glide:glide:4.15.1'

### 加载图片

首先，调用Glide.with()方法用于创建一个加载图片的实例。with()方法可以接收Context、Activity或者Fragment类型的参数。也就是说我们选择的范围非常广，不管是在Activity还是Fragment中调用with()方法，都可以直接传this。注意with()方法中传入的实例会决定Glide加载图片的生命周期，如果传入的是Activity或者Fragment的实例，那么当这个Activity或Fragment被销毁的时候，图片加载也会停止。如果传入的是ApplicationContext，那么只有当应用程序被杀掉的时候，图片加载才会停止。

接下来看一下load()方法，这个方法用于指定待加载的图片资源。Glide支持加载各种各样的图片资源，包括网络图片、本地图片、应用资源、二进制流、Uri对象等等。


```java
package com.example.glide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image);
    }

    public void loadImage(View view) {
        String url = "https://profile.csdnimg.cn/8/9/D/1_weixin_44226181";
        Glide.with(this)
                .load(url)
           		.transition(DrawableTransitionOptions.withCrossFade(3000))//设置占位图到真正图片的动画效果
            	.override(500,500)//设置图片大小
            	.transform(new CircleCrop())//改变图片形状
				/*
				RoundedCorners：四个角度统一指定
				GranularRoundedCorners：四个角度单独指定
				Rotate：旋转
				*/
                .placeholder(R.mipmap.ic_launcher)//占位图，在图片还没加载时放别的图片占位
            /*
            placeholder

			正在请求图片时展示的图片

			error

			请求失败时展示的图片(如果没有设置，还是展示placeholder的占位符)
	
			fallback

			如果请求的url/model为null时展示的图片(如果没有设置，还是展示placeholder的占位符)
            */
                .into(imageView);
    }

}
        
        
// 加载本地图片File file = getImagePath();
Glide.with(this).load(file).into(imageView);

// 加载应用资源
int resource = R.drawable.image;
Glide.with(this).load(resource).into(imageView);

// 加载二进制流
byte[] image = getImageBytes();
Glide.with(this).load(image).into(imageView);

// 加载Uri对象
Uri imageUri = getImageUri();
Glide.with(this).load(imageUri).into(imageView);


}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200108113828251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2ODI4ODIy,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200108114131943.png)