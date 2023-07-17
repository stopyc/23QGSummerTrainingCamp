# Gson

导依赖    implementation 'com.google.code.gson:gson:2.8.6'



### 序列化&&反序列化

序列化是把字符串变成json格式

反序列化相反

嵌套就是内部类

{
    "age": 18,
    "isStudent": false,
    "job": {
        "Salary": 10000,
        "workName": "worker"
    },
    "name": "lance",
    "password": "123"
}

```
package com.example.gson;

import com.google.gson.annotations.SerializedName;

public class l {

    @SerializedName("age")//将注解中的当作Key,而不是变量名当Key
    private int age;
    @SerializedName("isStudent")
    private boolean isStudent;
    @SerializedName("job")
    private JobDTO job;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;

	//serialize：是否参与序列化，deserialize是否参与反序列化
	@Expose(serialize = false, deserialize = false)
    private int test1;
	

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isIsStudent() {
        return isStudent;
    }

    public void setIsStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class JobDTO {
        @SerializedName("Salary")
        private int salary;
        @SerializedName("workName")
        private String workName;

        public int getSalary() {
            return salary;
        }

        public void setSalary(int salary) {
            this.salary = salary;
        }

        public String getWorkName() {
            return workName;
        }

        public void setWorkName(String workName) {
            this.workName = workName;
        }
    }
}


```



```java
package com.example.gson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User lance = new User("lance", "123", 18, false);
        User.Job worker = new User.Job("worker", 10000);
        lance.setJob(worker);
        Gson gson = new Gson();
        String json = gson.toJson(lance);
        System.out.println(json);

        User user = gson.fromJson(json,User.class);
        System.out.println(user.getAge()+user.getName());
    }
}
```



json数组

反序列化固定格式，要有bean类

new TypeToken<List<Bean>>() {
        }.getType();返回的是一个集合

```java
package com.example.gson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<User> list1 = new ArrayList<>();
        list1.add(new User("Lance", "123", 18, false));
        list1.add(new User("Alex", "123", 88, true));
        list1.add(null);
        //Gson提供的Gson对象
        Gson gson = new Gson();
        //序列化
        String json = gson.toJson(list1);
        System.out.println("序列化:" + json);

        
        Type type = new TypeToken<List<User>>() {
        }.getType();
        List<User> list2 = gson.fromJson(json, type);
        System.out.println("反序列化0:" + list2.get(0).getName());
        System.out.println("反序列化1:" + list2.get(1));
        System.out.println("反序列化2:" + list2.get(2));

    }
}
```







各种类型的json数据的解析方法

1.没有数组

```
{
    "code": 200001,
    "msg": "success",
    "data": {
        "JSON": {
            "mydata": {
                "name": "张三",
                "age": "21",
                "tel": "1539324****",
                "blance": "100"
            }
        }
    }
}
```

写好bean

```java
public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();

                        Test test = gson.fromJson(response.body().string(), Test.class);
                        Log.d(TAG, "onResponse: " + test.getData().getJson().getMydata().getName());


                    }



```

2.较复杂类型

```
{
    "code": 200001,
    "msg": "success",
    "data": {
        "JSON": {
            "test": [
                {
                    "bqcbxg": "OIzh",
                    "sajenltf": "Eu2u3Vp"
                }
            ]
        }
    }
}
```

```
public void onResponse(Call call, Response response) throws IOException {
                //aa就是bean
                Gson gson = new Gson();
                String jsonData = response.body().string();

                //GSON直接解析成对象
                aa aa1 = gson.fromJson(jsonData, aa.class);
                Log.d(TAG, "onResponse: "+aa1.getCode());//不是数组直接取值
                //对象中拿到集合
                List<aa.DataDTO.JSONDTO.TestDTO> aaList = aa1.getData().getJson().getTest();//取数组里面的值，最后是get数组名
                Log.d(TAG, "onResponse: " + aaList.get(0).getBqcbxg());



            }
        });
```
