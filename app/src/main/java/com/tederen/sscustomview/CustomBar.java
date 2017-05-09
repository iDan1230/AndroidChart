package com.tederen.sscustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by monkey on 2017/5/7 0007.
 *
 * 思路：
 *      1.绘制一个矩形作为背景
 *      2.在绘制一个矩形作为进度-- 按第一步绘制的矩形的比例计算长度
 *      3.绘制标记，为该标记添加触摸事件，控制标记移动、控制2绘制的进度长度
 */

public class CustomBar extends View{
    private Context context;
    private int viewWidth;  //控件的宽度
    private int viewHeight;//控件的高度
    private int progressHeight;//进度条的高度
    private int cursorWidth;//游标的宽
    private int cursorHeight;//游标的高
    private int pcHeight;//游标距离 进度调的距离
    private Drawable babckground_color;
    private Drawable progress_color;

    private Paint mPaint;//
    private View view;

    private int scale;//刻度

    public CustomBar(Context context) {
        this(context,null);
    }

    public CustomBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        //初始化自定义属性
        TypedArray t = context.obtainStyledAttributes(attrs,R.styleable.CustomBar);
        progressHeight = (int) t.getDimension(R.styleable.CustomBar_progressHeight,0);
        cursorWidth = (int) t.getDimension(R.styleable.CustomBar_cursorWidth,0);
        cursorHeight = (int) t.getDimension(R.styleable.CustomBar_cursorHeight,0);
        pcHeight = (int) t.getDimension(R.styleable.CustomBar_pcHeight,0);
        babckground_color = t.getDrawable(R.styleable.CustomBar_background_color);
        progress_color = t.getDrawable(R.styleable.CustomBar_progress_color);
        t.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getWidth();
        viewHeight = getHeight();
        Log.e("msg","onMeasure:  " + viewWidth);
    }

    public void init(){
        view = new View(context);
        //初始化游标的位置
        cursorRight = cursorWidth;
        mPaint = new Paint();
        //消除锯齿
        mPaint.setAntiAlias(true);
        //初始化背景颜色
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setStrokeWidth(5);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("msg","draw");
        Log.e("msg",cursorRight+"：draw");

        int progressWidth = viewWidth-cursorWidth;//进度条背景宽度
        int progressWithS = cursorLeft;//进度条显示的宽度

        float kedu = ((float) progressWithS/progressWidth) * 100;
        //progress背景
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawRect(cursorWidth/2,viewHeight - progressHeight,viewWidth-cursorWidth/2,viewHeight,mPaint);
        //实际进度
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawRect(cursorWidth/2,viewHeight - progressHeight,cursorRight - cursorWidth/2,viewHeight,mPaint);
        //绘制游标
        canvas.drawRect(cursorLeft,cursorTop,cursorRight,cursorHeight,mPaint);


        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setTextSize(30);
        //刻度
        canvas.drawText((int)kedu+"",cursorLeft,cursorHeight/2,mPaint);

        rect = new Rect(cursorLeft,cursorTop,cursorRight,cursorHeight);
    }

    private int cursorLeft;
    private int cursorTop;
    private int cursorRight;



    //记录按下时的X,Y
    private int downX;
    private int downY ;
    private Rect rect;//游标矩阵
    private boolean isDownCursor;//按下时是否实在游标的范围内
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

               downX = (int) event.getX();
               downY = (int) event.getY();
               isDownCursor =  rect.contains(downX,downY);
                Toast.makeText(context,isDownCursor+" ",Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDownCursor)
                {
                    cursorLeft = (int) event.getX()-cursorWidth/2;
                    cursorRight = (int) event.getX()+cursorWidth/2;
                    if (cursorLeft >= 0 && cursorRight <= viewWidth)
                   invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isDownCursor = false;
                break;

        }
        return true;
    }

    //设置固定刻度
    public void setScale(int scale) {
        this.scale = scale;

        mHandler.sendEmptyMessageDelayed(1,200);

    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }
    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
    }

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (scale >= 0 && scale <= 100)
            {
                float a = scale/100f;
                cursorRight = (int) ((viewWidth - cursorWidth) * a)+cursorWidth;
                cursorLeft = cursorRight - cursorWidth;
                Log.e("msg",cursorRight+"设置：" + a +"  " + viewWidth);
                invalidate();
            }

        }
    } ;
}
