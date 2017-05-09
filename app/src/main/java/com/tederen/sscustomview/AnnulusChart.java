package com.tederen.sscustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tederen on 2017/5/9.
 */

public class AnnulusChart extends View {
    private Paint mPaint;
    private Paint textPaint;
    private Paint labelPaint;

    private Context context;
    private List<String> texts = new ArrayList<>();
    private List<Integer> numbers = new ArrayList<>();
    private String[] mColor = new String[]{"#4A90E2", "#0DD5B2", "#DFEFEE", "#CCCCCC"};
    private float sum;//总数

    private int annulus_radius;
    private int annulus_width;
    private int viewWidth;
    private int viewHeight;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private List<AnnulusChartBean> chartBeans;

    private int minWidth;


    private final int TOTAL_PAINT_TIMES = 100; //控制绘制速度,分100次完成绘制
    private int timeCount = 0;//计数器
    private int ainmCount;//绘制完成的个数

    public AnnulusChart(Context context) {
        this(context, null);
    }

    public AnnulusChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnulusChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AnnulusChart);
        annulus_radius = (int) t.getDimension(R.styleable.AnnulusChart_annulus_radius, 0);
        annulus_width = (int) t.getDimension(R.styleable.AnnulusChart_annulus_width, 0);

        init();
    }

    private void init() {

        texts.add("a");
        texts.add("a");
        texts.add("a");
        texts.add("a");
        numbers.add(25);
        numbers.add(40);
        numbers.add(15);
        numbers.add(50);

        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor(mColor[3]));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(DisplayUtil.dip2px(context, 14));


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(annulus_width);
        mPaint.setColor(Color.parseColor(mColor[0]));
        mPaint.setStyle(Paint.Style.STROKE);
        paddingLeft = DisplayUtil.dip2px(context, 25);
        paddingTop = DisplayUtil.dip2px(context, 25);
        paddingRight = DisplayUtil.dip2px(context, 25);
        paddingBottom = DisplayUtil.dip2px(context, 25);


        //标注画笔
        labelPaint = new Paint();
        labelPaint.setAntiAlias(true);
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setStrokeWidth(2);

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        viewWidth = getWidth();
//        viewHeight = getHeight();
//        if (viewWidth <= viewHeight)
//        {
//            minWidth = viewWidth;
//        }else {
//            minWidth = viewHeight;
//        }
//
//
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
        if (viewWidth <= viewHeight) {
            minWidth = viewWidth;
        } else {
            minWidth = viewHeight;
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        //移动画布到圆环的左上角
//        canvas.translate(0, 0);
        float starAngle2 = 0;
        float starAngle = 0;
        float endAngle = 0;

        sum = 0;
        //计算总和
        for (int i = 0; i < chartBeans.size(); i++) {
            sum += chartBeans.get(i).number;
        }
        //以下是以左上角为基准来设值
        int left = minWidth / 2 - annulus_radius;
        int top = minWidth / 2 - annulus_radius;
        int bottom = minWidth / 2 + annulus_radius;
        int right = minWidth / 2 + annulus_radius;


        for (int i = 0; i < ainmCount; i++) {
            //计算驾驶画的角度
            starAngle += endAngle;
            //计算角度增量
            endAngle = 360 * chartBeans.get(i).number / sum;
            if (mColor.length > i)
                mPaint.setColor(Color.parseColor(chartBeans.get(i).color));
            /**
             * 1.
             * 2.开始画的角度
             * 3.需要画的角度-- 从开始画的角度 开始计算
             */
            canvas.drawArc(new RectF(left, top, right, bottom), starAngle, endAngle, false, mPaint);
            starAngle2 += endAngle;
        }


        if (ainmCount < chartBeans.size()) {
            timeCount++;
            //计算角度增量
            float ends = 360 * chartBeans.get(ainmCount).number / sum;
            float end = ends * timeCount / TOTAL_PAINT_TIMES;
            if (mColor.length > ainmCount)
                mPaint.setColor(Color.parseColor(chartBeans.get(ainmCount).color));
            /**
             * 1.
             * 2.开始画的角度
             * 3.需要画的角度-- 从开始画的角度 开始计算
             */
            canvas.drawArc(new RectF(left, top, right, bottom), starAngle2, end, false, mPaint);

            if (timeCount == TOTAL_PAINT_TIMES) {
                //计算驾驶画的角度
                starAngle += endAngle;
                ainmCount++;
                timeCount = 0;
            }

        }


        /**
         * 绘制圆环中心的文字
         */
        Rect rect = new Rect(left, top, right, bottom);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textTop = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float textBottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rect.bottom + rect.top - textTop - textBottom) / 2;//基线中间点的y轴计算公式
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("圆环", rect.centerX(), baseLineY, textPaint);

        drawTextAndLabel(canvas);


        if (timeCount < TOTAL_PAINT_TIMES)
            invalidate();

    }

    /**
     * 画文字和标注
     *
     * @param canvas
     */
    private void drawTextAndLabel(Canvas canvas) {
        for (int i = 0; i < chartBeans.size(); i++) {
//
            //画标注,标注离右边环边距为40,y轴则要减去半径（10）的一半才能对齐文字
            labelPaint.setColor(Color.parseColor(mColor[i]));
            canvas.drawCircle(minWidth + DisplayUtil.dip2px(context, 10), minWidth / 2 - annulus_radius + i * ((annulus_radius + annulus_width) * 2 / chartBeans.size()), DisplayUtil.dip2px(context, 3), labelPaint);
            //文字离右边环边距为60，文字与文字之间的距离为40
            canvas.drawText(chartBeans.get(i).numName, minWidth + DisplayUtil.dip2px(context, 10) + DisplayUtil.dip2px(context, 20), minWidth / 2 - annulus_radius + i * ((annulus_radius + annulus_width) * 2 / chartBeans.size()), textPaint);
        }
    }


    public void setNumbers(List<Integer> numbers) {


        if (numbers != null) {
            this.numbers.clear();
            this.numbers = numbers;

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


    public void setChartBeans(List<AnnulusChartBean> chartBeans) {
        this.chartBeans = chartBeans;
        mHandler.sendEmptyMessageDelayed(1, 200);
    }


}
