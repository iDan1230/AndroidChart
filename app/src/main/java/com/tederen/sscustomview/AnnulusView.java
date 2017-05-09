package com.tederen.sscustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tederen on 2017/5/3.
 */

public class AnnulusView extends View {
    //-------------必须给的数据相关-------------
    private String[] str = new String[]{"100%", "100%", "100%", "100%"};
    private String[] str2 = new String[]{"A ", "B ", "C ", "D "};
    //分配比例大小，总比例大小为100,由于经过运算后最后会是99.55左右的数值，导致圆不能够重合，会留出点空白，所以这里的总比例大小我们用101
    private int[] strPercent = new int[]{15, 25, 38, 22};
    //圆的直径
    private float mRadius;
    //圆的粗细
    private float mStrokeWidth = 30;
    //文字大小
    private int textSize = 20;
    //-------------画笔相关-------------
    //圆环的画笔
    private Paint cyclePaint;
    //文字的画笔
    private Paint textPaint;
    //标注的画笔
    private Paint labelPaint;
    //-------------颜色相关-------------
    //边框颜色和标注颜色
    private int[] mColor = new int[]{0xFFF06292, 0xFF9575CD, 0xFFE57373, 0xFF4FC3F7,0xFFC0CB};
    //文字颜色
    private int textColor = 0xFF000000;
    //-------------View相关-------------
    //View自身的宽和高
    private int mHeight;
    private int mWidth;

    int sum = 0;

    private final int TOTAL_PAINT_TIMES = 300; //控制绘制速度,分100次完成绘制
    private int timeCount = 0;//计数器
    private int ainmCount;//绘制完成的个数


    public AnnulusView(Context context) {
        this(context, null);
    }

    public AnnulusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnulusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AnnulusView);
        mRadius = t.getDimension(R.styleable.AnnulusView_radius, DisplayUtil.dip2px(context, 100));
        t.recycle();

    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = w;
//        mHeight = h;
//
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();

    }

    public void setStrPercent(int[] strPercent) {
        this.strPercent = strPercent;
        sum = 0;
        for (int i = 0; i < strPercent.length; i++) {

            sum += strPercent[i];
        }

        for (int i = 0; i < strPercent.length ; i++) {

            str[i] = (int)(((float)strPercent[i]/sum)*100) +"%";
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动画布到圆环的左上角
        canvas.translate(mHeight / 2 - mRadius, mHeight / 2 - mRadius);
        //初始化画笔
        initPaint();
        //画圆环
        drawCycle(canvas);
        //画文字和标注
        drawTextAndLabel(canvas);

        drawCenterText(canvas);
    }

    /**
     * 环形中心的问文字
     *
     * @param canvas
     */
    private void drawCenterText(Canvas canvas) {


        Rect targetRect = new Rect(0, 0, mHeight, mHeight);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("555", targetRect.centerX(), baseline, textPaint);
        canvas.drawRect(0, 0, mHeight, mHeight,textPaint);



    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        //边框画笔
        cyclePaint = new Paint();
        cyclePaint.setAntiAlias(true);
        cyclePaint.setStyle(Paint.Style.STROKE);
        cyclePaint.setStrokeWidth(mStrokeWidth);
        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(textSize);
        //标注画笔
        labelPaint = new Paint();
        labelPaint.setAntiAlias(true);
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setStrokeWidth(2);
    }

    /**
     * 画圆环
     *
     * @param canvas
     */
    private void drawCycle(Canvas canvas) {
        timeCount++;
        float startPercent = 0;
        float sweepPercent = 0;
        for (int i = 0; i < strPercent.length; i++) {
            cyclePaint.setColor(mColor[i]);
            startPercent = sweepPercent + startPercent;
            //这里采用比例占100的百分比乘于360的来计算出占用的角度，使用先乘再除可以算出值
            sweepPercent = ((float) strPercent[i] / sum) * 360 ;
//            Log.e("msg", startPercent+"   "+ sweepPercent);


//            if (i < ainmCount)
//            {
                canvas.drawArc(new RectF(0, 0, mRadius*2, mRadius*2), startPercent, sweepPercent, false, cyclePaint);

//            }else {
//                float endPercent = startPercent + ((float) strPercent[i] / sum) * 360 * timeCount/TOTAL_PAINT_TIMES;
//                canvas.drawArc(new RectF(0, 0, mRadius, mRadius), startPercent, endPercent, false, cyclePaint);
//
//                if (timeCount == TOTAL_PAINT_TIMES)
//                {
//                    ainmCount++;
//                    timeCount = 0;
//                }
//            }

        }
//        if (timeCount < TOTAL_PAINT_TIMES)
//        {
//            invalidate();
//        }
    }

    /**
     * 画文字和标注
     *
     * @param canvas
     */
    private void drawTextAndLabel(Canvas canvas) {
        for (int i = 0; i < strPercent.length; i++) {
            //文字离右边环边距为60，文字与文字之间的距离为40
            canvas.drawText(str2[i] + str[i], mRadius + mRadius / (str.length - 1) + 30, i * mRadius / (str.length - 1), textPaint);
            //画标注,标注离右边环边距为40,y轴则要减去半径（10）的一半才能对齐文字
            labelPaint.setColor(mColor[i]);
            canvas.drawCircle(mRadius + mRadius / (str.length - 1), i * mRadius / (str.length - 1) - 5, 10, labelPaint);
        }
    }


}
