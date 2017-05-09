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
 */

public class BrokenLineBar extends View {

    private Context context;
    private int viewWidth;  //控件的宽度
    private int viewHeight;//控件的高度
    private int coordinateColor;//坐标的颜色
    private int brokenColor;//折线的颜色

    private float studentNum;

    private int scale = 40; //编辑人数--Y方向刻度 默认40

    private int examNum; //考试次数 -- X方向刻度

    private int rankings[];//排名
    private List<String> texts;//lable

    private Paint mPaint;//

    private int padding = 50;//坐标系距离控件上下左右的边距

    private List<Point> points = new ArrayList<>();

    private final int TOTAL_PAINT_TIMES = 5; //控制绘制速度,分100次完成绘制
    private int timeCount = 0;//计数器
    private int ainmCount;//绘制完成的个数




    public BrokenLineBar(Context context) {
        this(context, null);
    }

    public BrokenLineBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrokenLineBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineBar);
        //坐标系颜色
        coordinateColor = t.getColor(R.styleable.BrokenLineBar_coordinateColor, Color.BLACK);
        //折线颜色
        brokenColor = t.getColor(R.styleable.BrokenLineBar_brokenColor, Color.BLUE);

        init();
    }

    private void init() {
        mPaint = new Paint();
        //消除锯齿
        mPaint.setAntiAlias(true);
        //初始化背景颜色
        mPaint.setColor(coordinateColor);
        mPaint.setStrokeWidth(5);


    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        viewWidth = getWidth();
//        viewHeight = getHeight();
////        Log.e("msg", "onMeasure:  " + viewWidth);
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
        timeCount++;

        if (rankings != null)
            examNum = rankings.length;
        mPaint.setStrokeWidth(DisplayUtil.dip2px(context,1));
        mPaint.setTextSize(DisplayUtil.dip2px(context,10));

        scale = (int) (studentNum / 10);
        for (int i = 0; i <= scale; i++) {
            //X轴
            canvas.drawLine(padding, viewHeight - padding - (viewHeight - padding * 2) / scale * i, viewWidth - padding, viewHeight - padding - (viewHeight - padding * 2) / scale * i, mPaint);

            Rect rect = new Rect(0,viewHeight - padding - (viewHeight - padding * 2) / scale * i - padding,padding,viewHeight - padding - (viewHeight - padding * 2) / scale * i+padding);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
            int baseLineY = (int) (rect.bottom+rect.top - top - bottom)/2;//基线中间点的y轴计算公式
            mPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(i * 10 + "",rect.centerX(),baseLineY,mPaint);
//            canvas.drawText(i * 10 + "", 10, viewHeight - padding - (viewHeight - padding * 2) / scale * i, mPaint);
        }

        float xWidth = (viewWidth - padding*2)/ examNum;

        //X 方向的刻度
        for (int i = 0; i <= examNum-1; i ++) {
            mPaint.setColor(coordinateColor);
            // 10 是刻度的高度
            canvas.drawLine(padding + (viewWidth - padding * 2) / (examNum - 1) * i + xWidth/2, viewHeight - padding, padding + (viewWidth - padding * 2) / (examNum - 1) * i+ xWidth/2, viewHeight - padding + 5, mPaint);
            if (texts.size() > i+1){

                mPaint.setTextSize(DisplayUtil.dip2px(context,12));
                Rect rect = new Rect((viewWidth - padding * 2) / (examNum - 1) * i+ (int)xWidth/2 , viewHeight - padding ,padding+padding+(viewWidth - padding * 2) / (examNum - 1) * i+ (int)xWidth/2,viewHeight);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
                float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
                int baseLineY = (int) (rect.bottom+rect.top - top - bottom)/2;//基线中间点的y轴计算公式
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(texts.get(i),rect.centerX(),baseLineY,mPaint);
//                canvas.drawText(texts.get(i), padding + (viewWidth - padding * 2) / (examNum - 1) * i, viewHeight - 10, mPaint);
            }



            if (i <= examNum - 3) {
                int x = padding + (viewWidth - padding * 2) / (examNum - 1) * i + (int)xWidth/2 ;
                int y = (int) ((viewHeight - padding) - (viewHeight - padding) * rankings[i] / studentNum);
                int x2 = padding + (viewWidth - padding * 2) / (examNum - 1) * (i + 1)+ (int)xWidth/2 ;
                int y2 = (int) ((viewHeight - padding) - (viewHeight - padding) * rankings[i + 1] / studentNum);

                int moveX = x + calculateX();
                int moveY = (int) (y - calculateY(y,y2));


                mPaint.setStrokeWidth(DisplayUtil.dip2px(context,2));
                mPaint.setColor(brokenColor);

                if (i < ainmCount){
                    //绘制直线 -- 连接点形成折线
                    canvas.drawLine(x, y, x2, y2, mPaint);
                    //绘制点
                    canvas.drawCircle(x, y, DisplayUtil.dip2px(context,3), mPaint);
                }else if (i == ainmCount){
                    //绘制直线 -- 连接点形成折线
                    canvas.drawLine(x, y, moveX, moveY, mPaint);
                    if (timeCount == TOTAL_PAINT_TIMES) {
                        i++;
                        ainmCount++ ;

                        timeCount = 0;
                    }
                }


            }
        }
        if (timeCount < TOTAL_PAINT_TIMES) {
            invalidate();
        }


    }

    public int calculateX() {

        //底部刻度宽度
        int xWidth = (viewWidth - padding * 2) / (examNum-1);
        return xWidth * timeCount/TOTAL_PAINT_TIMES;

    }

    /**
     *
     * @param y 左边点的y
     * @param y2 右边点的y
     * @return
     */
    public float calculateY(int y,int y2) {

       if (y > y2)
       {
           //计算点到底线的高度
           float xHeight = viewHeight - padding*2f - y2;
           return xHeight * timeCount/TOTAL_PAINT_TIMES;
       }else {
           //计算点到底线的高度
           float xHeight = viewHeight - padding*2f - y;
           return - xHeight * timeCount/TOTAL_PAINT_TIMES;
       }



    }

    public void setRankings(int[] rankings, List<String> texts, float studentNum) {
        this.rankings = rankings;
        this.studentNum = studentNum;
        this.texts = texts;
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
