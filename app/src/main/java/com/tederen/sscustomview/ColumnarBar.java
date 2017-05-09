package com.tederen.sscustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tederen on 2017/5/8.
 * 多柱状图
 */

public class ColumnarBar extends View {

    private Context context;
    private int viewWidth;  //控件的宽度
    private int viewHeight;//控件的高度
    private int coordinateColor;//坐标的颜色
    private int brokenColor;//折线的颜色

    private float studentNum = 40;

    private int scale = 40; //编辑人数--Y方向刻度 默认40

    private int examNum; //考试次数 -- X方向刻度

    private int rankings[];//排名
    private List<String> texts;//lable

    private Paint mPaint;//

    private int padding = 50;//坐标系距离控件上下左右的边距

    private List<Point> points = new ArrayList<>();

    //边框颜色和标注颜色
    private int[] mColor = new int[]{0xFFF06292, 0xFF9575CD, 0xFFE57373, 0xFF4FC3F7};

    private int[][] chengji;


    private final float TOTAL_PAINT_TIMES = 100; //控制绘制速度,分100次完成绘制
    private int timeCount = 0;//计数器




    public ColumnarBar(Context context) {
        this(context, null);
    }

    public ColumnarBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnarBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray t = context.obtainStyledAttributes(attrs,R.styleable.BrokenLineBar);
        //坐标系颜色
        coordinateColor = t.getColor(R.styleable.BrokenLineBar_coordinateColor, Color.BLACK);
        //折线颜色
        brokenColor = t.getColor(R.styleable.BrokenLineBar_brokenColor,Color.BLUE);

        init();
    }

    private void init() {
        mPaint = new Paint();
        //消除锯齿
        mPaint.setAntiAlias(true);
        //初始化背景颜色
        mPaint.setColor(coordinateColor);
        mPaint.setStrokeWidth(DisplayUtil.dip2px(context,5));


    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        viewWidth = getWidth();
//        viewHeight = getHeight();
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (chengji != null)
            examNum = chengji.length;
        else
            examNum = 7;
        mPaint.setStrokeWidth(2);
        mPaint.setTextSize(DisplayUtil.dip2px(context,12));

        scale = 3;
        timeCount++;

        for (int i = 0; i <= scale; i++) {
            //X轴
            canvas.drawLine(padding, viewHeight - padding - (viewHeight - padding * 2) / scale * i, viewWidth - padding, viewHeight - padding - (viewHeight - padding * 2) / scale * i, mPaint);

//            canvas.drawText(i * 10 + "", 10, viewHeight - padding - (viewHeight - padding * 2) / scale * i, mPaint);
        }

        //X 方向的刻度
        for (int i = 0; i < examNum; i++) {

            //底部刻度宽度
            int xWidth = (viewWidth - padding * 2) / examNum;
            //柱状宽度
            int columnarWidth = (int) (xWidth*0.6f);
            //第一柱体的left
            int startX = padding+ i*xWidth + (xWidth - columnarWidth)/2;


            for (int j = 0; j < 4; j++) {

                float c = 1 - chengji[i][j]/100f;
                int top = (int) ((viewHeight-padding*2)*c)+ padding;

                //柱体的实际高度
                int lenth = (int) (chengji[i][j]/100f * (viewHeight-padding*2));

                //动画效果下柱体的高度

                int anim = (int) (lenth * timeCount / TOTAL_PAINT_TIMES);

                int top2 = (viewHeight-padding)- anim;

                mPaint.setColor(mColor[j]);
                canvas.drawRect(startX+j*columnarWidth/4,top2,startX+(j+1)*columnarWidth/4,viewHeight-padding,mPaint);

            }

            mPaint.setColor(coordinateColor);
            // 10 是刻度的高度
//            canvas.drawLine(padding + (viewWidth - padding * 2) / examNum * i + xWidth/2, viewHeight - padding, padding + (viewWidth - padding * 2) / examNum  * i+ xWidth/2, viewHeight - padding + 10, mPaint);
            if (texts!= null && texts.size() > i){

                mPaint.setTextSize(DisplayUtil.dip2px(context,12));
                Rect rect = new Rect(padding + (viewWidth - padding * 2) / examNum * i , viewHeight - padding+10 ,padding + (viewWidth - padding * 2) / examNum  * i+ xWidth,viewHeight);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
                float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
                int baseLineY = (int) (rect.bottom+rect.top - top - bottom)/2;//基线中间点的y轴计算公式
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(texts.get(i),rect.centerX(),baseLineY,mPaint);

//                canvas.drawText(texts.get(i), padding + (viewWidth - padding * 2) / examNum * i, viewHeight - 10, mPaint);

            }


        }


        if (timeCount < TOTAL_PAINT_TIMES)
        {
            invalidate();
        }
    }

    public void setData(int[][] chengji, List<String> texts) {
        this.texts = texts;
        this.chengji = chengji;
        if (rankings != null) {
            mHandler.sendEmptyMessageDelayed(1, 200);
        }

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            invalidate();
        }
    };
}
