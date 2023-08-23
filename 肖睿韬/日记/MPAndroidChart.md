# MPAndroidChart

导依赖  implementation 'com.github.PhilJay:MPAndroidChart:v3.0.3'

在settings.gradle

```java
repositories {
    google()
    mavenCentral()
    maven { url "https://www.jitpack.io" }    111
}
```



### 折线图

```xml
<com.github.mikephil.charting.charts.LineChart
    android:id="@+id/line_chart"
    android:layout_width="match_parent"
    android:layout_height="320dp" />
```

LineChartManager

```java
package com.example.roompractice;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LineChartManager {
    private LineChart lineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
    private List<String> timeList = new ArrayList<>(); //存储x轴的时间
	111
    public LineChartManager(LineChart mLineChart, String name, int color) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();//左轴
        rightAxis = lineChart.getAxisRight();//右轴
        xAxis = lineChart.getXAxis();//X轴
        initLineChart();
        initLineDataSet(name, color);
    }

    //多条曲线
    public LineChartManager(LineChart mLineChart, List<String> names, List<Integer> colors) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart();
        initLineDataSet(names, colors);
    }

    /**
     * 初始化LineChar
     */111
    private void initLineChart() {
        //设置是否绘制网格背景，默认为false，即不绘制。
        lineChart.setDrawGridBackground(true);
        //显示边界
        lineChart.setDrawBorders(true);
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//设置图例的垂直对齐方式为底部对齐
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);//设置图例的水平对齐方式为左对齐
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//设置图例的方向为水平方向。
        legend.setDrawInside(false);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);//设置X轴的最小间隔为1f。
        xAxis.setLabelCount(10);//设置X轴的标签数量为10个。


        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
    }

    /**
     * 初始化折线(一条线)
     *
     * @param name
     * @param color
     */111
    private void initLineDataSet(String name, int color) {

        lineDataSet = new LineDataSet(null, name);//创建一个LineDataSet对象，传入的参数为数据集和名称。
        lineDataSet.setLineWidth(1.5f);//设置线条的宽度为1.5f。
        lineDataSet.setCircleRadius(1.5f);//设置线条的宽度为1.5f。
        lineDataSet.setColor(color);//设置线条的颜色。
        lineDataSet.setCircleColor(color);//设置圆圈的颜色
        lineDataSet.setHighLightColor(color);//设置高亮的颜色。
        //设置曲线填充
        lineDataSet.setDrawFilled(false);//设置是否绘制曲线的填充，默认为false，即不绘制。
        lineDataSet.setDrawValues(true);//设置是否绘制数据值，默认为false，即不绘制
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);//设置数据集依赖于左轴
        lineDataSet.setValueTextSize(10f);//设置数据值的文字大小为10f
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线的模式为CUBIC_BEZIER，即贝塞尔曲线。
        //添加一个空的 LineData
        lineData = new LineData();//创建一个空的LineData对象。
        lineChart.setData(lineData);//将LineData对象设置给LineChart对象
        lineChart.invalidate();//刷新LineChart的显示。

    }

    /**
     * 初始化折线（多条线）
     *
     * @param names
     * @param colors
     */
    private void initLineDataSet(List<String> names, List<Integer> colors) {

        for (int i = 0; i < names.size(); i++) {
            lineDataSet = new LineDataSet(null, names.get(i));
            lineDataSet.setColor(colors.get(i));
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(colors.get(i));

            lineDataSet.setDrawFilled(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setCircleColor(colors.get(i));
            lineDataSet.setHighLightColor(colors.get(i));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);
            lineDataSets.add(lineDataSet);

        }
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */111
    public void addEntry(int number) {

        //如果数据集中还没有数据点，则将lineDataSet添加到lineData中。这个判断是为了保证最开始的时候只添加一次数据集。
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        //将更新后的lineData设置给lineChart
        lineChart.setData(lineData);
        //如果timeList中的数据点数量超过11个，则清空timeList。这个判断是为了避免集合数据过多
        if (timeList.size() > 11) {
            timeList.clear();
        }
        //将当前时间的格式化字符串添加到timeList中。
        timeList.add(df.format(System.currentTimeMillis()));
        //创建一个Entry对象，表示一个数据点，其中x轴的值为lineDataSet中数据点的数量，y轴的值为传入的number参数。
        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        //将entry添加到lineData中的索引为0的数据集中
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(10);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
    }

    /**
     * 动态添加数据（多条折线图）
     *
     * @param numbers
     */
    public void addEntry(List<Float> numbers) {

        if (lineDataSets.get(0).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            lineChart.setData(lineData);
        }
        if (timeList.size() > 100) {
            timeList.clear();
        }
        timeList.add(df.format(System.currentTimeMillis()));
        for (int i = 0; i < numbers.size(); i++) {
            Entry entry = new Entry(lineDataSet.getEntryCount(), numbers.get(i));
            lineData.addEntry(entry, i);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(6);
            lineChart.moveViewToX(lineData.getEntryCount() - 5);
        }
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        lineChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }


}
```

Activity

```java
package com.example.roompractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LineChart mLineChart;

    private LineChartManager mChartManager;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.bt1);
        initView();
        mChartManager = new LineChartManager(mLineChart, "sensors", Color.BLACK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mLineChart = findViewById(R.id.line_chart);
    }

    public void addEntry(View view) {
        mChartManager.addEntry((int) (Math.random() * 100));
    }


}
```



### 饼状图

```xml
<com.github.mikephil.charting.charts.PieChart
    android:id="@+id/pie_chart"
    android:layout_width="300dp"
    android:layout_height="300dp" />
```



```java
package com.example.roompractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private PieChart mPieChart;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initPieChart(mPieChart);
        button = findViewById(R.id.bt2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }

    private void initPieChart(PieChart pieChart) {
        //创建一个PieEntry对象的列表，用于存储饼图的数据点。这里创建了两个数据点，分别表示男生和女生的比例。
        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(30f, "男生"));
        strings.add(new PieEntry(70f, "女生"));
        //创建一个PieDataSet对象，传入的参数为数据点列表和数据集的标签。这里将数据集的标签设置为空字符串。
        PieDataSet dataSet = new PieDataSet(strings, "");
        //创建一个Integer类型的列表，用于存储饼图每个数据点的颜色。这里创建了两个颜色，分别为红色和蓝色。
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        //将颜色列表设置给数据集，用于设置饼图每个数据点的颜色。
        dataSet.setColors(colors);
        //创建一个PieData对象，传入的参数为数据集
        PieData pieData = new PieData(dataSet);
        //设置是否绘制数据值，默认为true，即绘制
        pieData.setDrawValues(true);
        //设置数据值的格式化器为百分比格式化器，用于将数据值格式化为百分比形式。
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0");
        pieData.setValueFormatter(new PercentFormatter(decimalFormat));

        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
        //创建一个Description对象，用于设置饼图的描述信息。
        Description description = new Description();
        description.setText("");
        //将描述信息设置给饼图。
        pieChart.setDescription(description);
        //设置饼图的中心空白圆的半径为0f，即没有中心空白圆。
        pieChart.setHoleRadius(0f);
        //设置饼图的透明圆的半径为0f，即没有透明圆。
        pieChart.setTransparentCircleRadius(0f);
        //设置饼图的数据值使用百分比形式。
        pieChart.setUsePercentValues(true);
        //将更新后的PieData对象设置给PieChart。
        pieChart.setData(pieData);
        //刷新PieChart的显示。
        pieChart.invalidate();
    }

    private void initView() {
        mPieChart = findViewById(R.id.pie_chart);
    }

}
```



### 柱状图

```xml
<com.github.mikephil.charting.charts.BarChart
    android:id="@+id/bar_chart"
    android:layout_width="match_parent"
    android:layout_height="300dp"/>
```



```java
package com.example.roompractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private BarChart mBarChart;
    private List<BarEntry> mBarEntries;
    private BarData mBarData;
    private BarDataSet mBarDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
        initBarEntries();
        initBarData();
    }

    private void initView() {
        mBarChart = findViewById(R.id.bar_chart);
        //设置当BarChart没有数据时显示的文本
        mBarChart.setNoDataText("正在初始化...");
    }

    private void initBarEntries() {
        //创建一个BarEntry对象，表示一个柱状图的数据点，其中x轴的值为0f，y轴的值为30f。
        BarEntry barEntry1 = new BarEntry(0f, 30f);
        BarEntry barEntry2 = new BarEntry(1f, 40f);
        BarEntry barEntry3 = new BarEntry(2f, 50f);
        BarEntry barEntry4 = new BarEntry(3f, 60f);
        BarEntry barEntry5 = new BarEntry(4f, 70f);
        //创建一个ArrayList对象，用于存储BarEntry对象。
        mBarEntries = new ArrayList<>();
        mBarEntries.add(barEntry1);
        mBarEntries.add(barEntry2);
        mBarEntries.add(barEntry3);
        mBarEntries.add(barEntry4);
        mBarEntries.add(barEntry5);
    }

    private void initBarData() {
        //创建一个BarDataSet对象，传入的参数为数据集和数据集的标签。这里将数据集设置为mBarEntries列表，标签设置为"sleep"。
        mBarDataSet = new BarDataSet(mBarEntries, "sleep");
        //创建一个BarData对象，传入的参数为数据集。
        mBarData = new BarData(mBarDataSet);

        // mBarDataSet.setColors(colors); 给柱状图设置颜色
        //调用initXY  方法，用于初始化X轴的一些属性
        initX(mBarChart);
        initY(mBarChart);
        //创建一个Description对象，用于设置BarChart的描述信息。
        Description desc = new Description();
        desc.setText("");
        //将描述信息设置给BarChart。
        mBarChart.setDescription(desc);
        //将更新后的BarData对象设置给BarChart。
        mBarChart.setData(mBarData);
        //刷新BarChart的显示
        mBarChart.invalidate();
    }

    private void initX(BarChart barChart) {
        final String[] weeks = {"周一", "周二", "周三", "周四", "周五"};
        //获取BarChart对象的X轴对象。
        XAxis xAxis = barChart.getXAxis();
        //设置X轴的显示位置为底部。
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //创建一个IAxisValueFormatter对象，并将其设置为X轴的值格式化器。
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return weeks[(int) value];
            }
        });
        //禁用图例，即不显示图例。
        barChart.getLegend().setEnabled(false);
        //设置X轴的标签数量为星期名称的数量，并禁用强制绘制标签。
        xAxis.setLabelCount(weeks.length, false);
    }

    private void initY(BarChart barChart) {
        barChart.getAxisLeft().setDrawZeroLine(true);
        //去掉左侧Y轴刻度
        barChart.getAxisLeft().setDrawLabels(false);
        //去掉左侧Y轴
        barChart.getAxisLeft().setDrawAxisLine(false);
        //去掉中间竖线
        barChart.getXAxis().setDrawGridLines(false);
        //去掉中间横线
        barChart.getAxisLeft().setDrawGridLines(false);
    }

}
```