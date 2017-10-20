package com.crayfish.uidraw2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/10/12.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class taiji extends View{

    private Paint blackPaint;//黑色
    private Paint whitePaint;//白色

    private int width;
    private int height;

    private float degree = 0;

    public taiji(Context context) {
        super(context,null);
    }

    public taiji(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        init();
    }

    public taiji(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setColor(Color.BLACK);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);

//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                degree+=5;
//                postInvalidate();
//            }
//        },3000);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        canvas.translate(width/2,height/2);
        canvas.rotate(degree);

        int radius = Math.min(width,height)/2 - 100;
        RectF rectF = new RectF(-radius,-radius,radius,radius);
        canvas.drawArc(rectF,90,180,true,blackPaint);
        canvas.drawArc(rectF,-90,180,true,whitePaint);

        canvas.drawCircle(0,-radius/2,radius/2,blackPaint);
        canvas.drawCircle(0,radius/2,radius/2,whitePaint);

        canvas.drawCircle(0,-radius/2,50,whitePaint);
        canvas.drawCircle(0,radius/2,50,blackPaint);

        degree += 5;
        invalidate();

    }

    public void setDegree(float degree){
        this.degree = degree;
        invalidate();
    }


}
