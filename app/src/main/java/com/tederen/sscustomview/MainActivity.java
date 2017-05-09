package com.tederen.sscustomview;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private ColorSelectionBar customBar;
    private BrokenLineBar broken_line;
    private ColumnarBar columnar_bar;
    private AnnulusView annulus;
    private AnnulusChart chart_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {


        customBar = (ColorSelectionBar) findViewById(R.id.custom_test);
        broken_line = (BrokenLineBar) findViewById(R.id.broken_line);
        columnar_bar = (ColumnarBar) findViewById(R.id.columnar_bar);
        annulus = (AnnulusView) findViewById(R.id.annulus);
        chart_id = (AnnulusChart) findViewById(R.id.chart_id);

        int[] ss = {15, 19, 20, 40};
        annulus.setStrPercent(ss);
        List<String> texts = new ArrayList<>();
        int rankings[] = {1, 25, 7, 30, 6, 13, 4, 9};//排名
        int[][] chengji = {{100, 60, 70, 95}, {50, 89, 70, 90}, {50, 60, 70, 100}, {50, 20, 100, 40}, {100, 60, 70, 40}, {100, 60, 70, 40}, {50, 88, 90, 100}};
        for (int i = 0; i < rankings.length; i++) {
            texts.add("第" + (i + 1) + "次");

        }
        broken_line.setRankings(rankings, texts, 50);
        columnar_bar.setData(chengji, texts);


        List<Integer> lists = new ArrayList<>();
        lists.add(24);
        lists.add(12);
        lists.add(42);
        lists.add(6);

        List<AnnulusChartBean> beans = new ArrayList<>();
        beans.add(new AnnulusChartBean(24, "A", "#4A90E2"));
        beans.add(new AnnulusChartBean(35, "A", "#0DD5B2"));
        beans.add(new AnnulusChartBean(17, "A", "#DFEFEE"));
        beans.add(new AnnulusChartBean(50, "A", "#CCCCCC"));
        chart_id.setChartBeans(beans);
//        chart_id.setNumbers(lists);

    }

}
