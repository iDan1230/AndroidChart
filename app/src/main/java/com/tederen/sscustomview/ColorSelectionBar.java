package com.tederen.sscustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by monkey on 2017/5/7 0007.
 *
 * 颜色选择器
 */

public class ColorSelectionBar extends View{
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

    //边框颜色和标注颜色
    private int[] mColor = new int[]{0xFFF06292, 0xFF9575CD, 0xFFE57373, 0xFF4FC3F7};

    public ColorSelectionBar(Context context) {
        this(context,null);
    }

    public ColorSelectionBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorSelectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        viewWidth = getWidth();
//        viewHeight = getHeight();
//        Log.e("msg","onMeasure:  " + viewWidth);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
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
        //每段的宽度
        int size = (viewWidth - cursorWidth) / mColor.length;
        //绘制多段颜色
        for (int i = 0; i < mColor.length; i++) {
            //progress背景
            mPaint.setColor(mColor[i]);
            canvas.drawRect(cursorWidth/2 + size*i,viewHeight - progressHeight,cursorWidth/2 + size*(i+1),viewHeight,mPaint);
        }
        //判断游标的颜色
        for (int i = 0; i < mColor.length; i++) {
            if ((cursorLeft+cursorWidth/2) >= cursorWidth/2 + size*i && (cursorLeft+cursorWidth/2) <= cursorWidth/2 + size*(i+1)){
                mPaint.setColor(mColor[i]);
            }
        }
        //绘制游标
        canvas.drawRect(cursorLeft,cursorTop,cursorRight,cursorHeight,mPaint);


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


    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }
    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
    }


}
