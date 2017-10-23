package com.crayfish.uidraw2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/10/23.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class GoodJob extends View{

    private int number;
    private Paint mPaint;
    private Paint mDefaultPaint;
    private Paint mBitmapPaint;
    private Rect bounds;
    private int duration = 300;

    private boolean isSelected = false;
    private float textDy;
    private float textAlpha;
    private float textScale;

    private float textOldDy;
    private float textOldAlpha;
    private float textOldScale;

    private Bitmap unselected;
    private Bitmap selected;
    private Bitmap shining;

    private float handScale = 1.0f;
    private float shiningAlpha;
    private float shiningScale;

    private int dy;

    public GoodJob(Context context) {
        this(context,null);
    }

    public GoodJob(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GoodJob(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(spToPx(16));
        mPaint.setTypeface(Typeface.DEFAULT);

        mDefaultPaint = new Paint();
        mDefaultPaint.setColor(Color.BLACK);
        mDefaultPaint.setTextSize(spToPx(16));
        mDefaultPaint.setTypeface(Typeface.DEFAULT);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        number = 109;
        bounds = new Rect();

        unselected = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        selected = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
        shining = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unselected.recycle();
        selected.recycle();
        shining.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap handBitmap = isSelected ? selected : unselected;
        int handBitmapWidth = handBitmap.getWidth();
        int handBitmapHeight = handBitmap.getHeight();

        //画小手
        int handTop = (getHeight() - handBitmapHeight) / 2;
        canvas.save();
        canvas.scale(handScale, handScale, handBitmapWidth / 2, getHeight()/2);
        canvas.drawBitmap(handBitmap, 0, handTop, mBitmapPaint);
        canvas.restore();

        //画shining
        int shiningTop = handTop - shining.getHeight() + dpToPx(6);//手动加上6dp的margin
        mBitmapPaint.setAlpha((int) (255 * shiningAlpha));
        canvas.save();
        canvas.scale(shiningScale, shiningScale, handBitmapWidth / 2, handTop);
        canvas.drawBitmap(shining, 0, shiningTop, mBitmapPaint);
        canvas.restore();

        //恢复bitmapPaint透明度
        mBitmapPaint.setAlpha(255);

        String StrNum = String.valueOf(number);
        String oldStrNum;
        if(isSelected){
            oldStrNum = String.valueOf(number-1);
        }else{
            oldStrNum = String.valueOf(number+1);
        }
        int length = StrNum.length();
        mPaint.getTextBounds(StrNum,0,length,bounds);
        int textY = getHeight()/2+handBitmapHeight/2-(bounds.bottom-bounds.top)/2;
        int textX = handBitmapWidth;
        dy = bounds.bottom-bounds.top;
        if (length != oldStrNum.length() || Math.abs(textDy) == 0) {
            //直接绘制文字 没找到即刻App里面对这种情况的处理效果
            canvas.drawText(StrNum, textX, textY, mDefaultPaint);
            return;
        }

        float[] widths = new float[6];
        mPaint.getTextWidths(StrNum,0,length,widths);
        char[] chars = StrNum.toCharArray();
        char[] oldChars = oldStrNum.toCharArray();
        for (int i = 0;i<chars.length;i++){
            if(oldChars[i] == chars[i]){
                canvas.drawText(String.valueOf(chars[i]),textX,textY,mDefaultPaint);
            }else{
                if(isSelected){
                    canvas.save();
                    canvas.translate(0,textDy);
                    canvas.scale(textScale,textScale,textX,textY+textDy);
                    mPaint.setAlpha((int)(255*textAlpha));
                    canvas.drawText(String.valueOf(chars[i]),textX,textY,mPaint);
                    canvas.restore();

                    canvas.save();
                    canvas.translate(0,textOldDy);
                    canvas.scale(textOldScale,textOldScale,textX,textY+textOldDy);
                    mPaint.setAlpha((int)(255*textOldAlpha));
                    canvas.drawText(String.valueOf(oldChars[i]),textX,textY,mPaint);
                    canvas.restore();
                }else{
                    canvas.save();
                    canvas.translate(0,textDy);
                    canvas.scale(textScale,textScale,textX,textY+textDy);
                    mPaint.setAlpha((int)(255*textAlpha));
                    canvas.drawText(String.valueOf(chars[i]),textX,textY,mPaint);
                    canvas.restore();

                    canvas.save();
                    canvas.translate(0,textOldDy);
                    canvas.scale(textOldScale,textOldScale,textX,textY+textOldDy);
                    mPaint.setAlpha((int)(255*textOldAlpha));
                    canvas.drawText(String.valueOf(oldChars[i]),textX,textY,mPaint);
                    canvas.restore();
                }
            }
            textX+=widths[i];
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                toggle();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void toggle(){
        isSelected = !isSelected;
        if(isSelected) {
            ObjectAnimator handScaleAnim = ObjectAnimator.ofFloat(this, "handScale", 1f, 0.8f, 1f);
            handScaleAnim.setDuration(duration);

            ObjectAnimator shiningAlphaAnim = ObjectAnimator.ofFloat(this, "shiningAlpha", 0f, 1f);
            handScaleAnim.setDuration(duration);

            ObjectAnimator shiningScaleAnim = ObjectAnimator.ofFloat(this, "shiningScale", 0f, 1f);
            handScaleAnim.setDuration(duration);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(handScaleAnim, shiningAlphaAnim, shiningScaleAnim);
            set.start();
            ++number;
            addAnimator();
        }else{
            ObjectAnimator handScaleAnim = ObjectAnimator.ofFloat(this, "handScale", 1f, 0.8f, 1f);
            handScaleAnim.setDuration(duration);
            handScaleAnim.start();

            setShiningAlpha(0);
            --number;
            subAnimator();
        }
    }

    public void addAnimator(){
        ObjectAnimator textAlphaAnim = ObjectAnimator.ofFloat(this, "textAlpha", 0f, 1f);
        textAlphaAnim.setDuration(duration);
        ObjectAnimator dyAnim = ObjectAnimator.ofFloat(this, "textDy", dy*1.5f,0);
        dyAnim.setDuration(duration);
        dyAnim.setInterpolator(new LinearInterpolator());
        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(this, "textScale", 0.9f, 1f);
        dyAnim.setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(textAlphaAnim, dyAnim,scaleAnim);
        set.start();

        ObjectAnimator textOldAlphaAnim = ObjectAnimator.ofFloat(this, "textOldAlpha", 1f, 0f);
        textOldAlphaAnim.setDuration(duration);
        ObjectAnimator dyOldAnim = ObjectAnimator.ofFloat(this, "textOldDy", 0,-dy*1.5f);
        dyAnim.setDuration(duration);
        dyAnim.setInterpolator(new LinearInterpolator());
        ObjectAnimator scaleOldAnim = ObjectAnimator.ofFloat(this, "textOldScale", 1f, 0.9f);
        dyAnim.setDuration(duration);
        AnimatorSet oldSet = new AnimatorSet();
        oldSet.playTogether(textOldAlphaAnim, dyOldAnim,scaleOldAnim);
        oldSet.start();
        invalidate();
    }

    public void subAnimator(){
        ObjectAnimator textInAlphaAnim = ObjectAnimator.ofFloat(this, "textAlpha", 0f, 1f);
        textInAlphaAnim.setDuration(duration);
        ObjectAnimator dyAnim = ObjectAnimator.ofFloat(this, "textDy", -dy*1.5f,0);
        dyAnim.setDuration(duration);
        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(this, "textScale", 0f, 1f);
        dyAnim.setDuration(duration);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(textInAlphaAnim, dyAnim,scaleAnim);
        set.start();

        ObjectAnimator textOldAlphaAnim = ObjectAnimator.ofFloat(this, "textOldAlpha", 1f, 0f);
        textOldAlphaAnim.setDuration(duration);
        ObjectAnimator dyOldAnim = ObjectAnimator.ofFloat(this, "textOldDy", 0,dy*1.5f);
        dyAnim.setDuration(duration);
        ObjectAnimator scaleOldAnim = ObjectAnimator.ofFloat(this, "textOldScale", 1f, 0.9f);
        dyAnim.setDuration(duration);
        AnimatorSet oldSet = new AnimatorSet();
        oldSet.playTogether(textOldAlphaAnim, dyOldAnim,scaleOldAnim);
        oldSet.start();
        invalidate();
    }

    public float getTextDy() {
        return textDy;
    }

    public void setTextDy(float textDy) {
        this.textDy = textDy;
        invalidate();
    }

    public float getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(float textAlpha) {
        this.textAlpha = textAlpha;
        invalidate();
    }

    public float getTextScale() {
        return textScale;
    }

    public void setTextScale(float textScale) {
        this.textScale = textScale;
        invalidate();
    }

    public float getTextOldDy() {
        return textOldDy;
    }

    public void setTextOldDy(float textOldDy) {
        this.textOldDy = textOldDy;
        invalidate();
    }

    public float getTextOldAlpha() {
        return textOldAlpha;
    }

    public void setTextOldAlpha(float textOldAlpha) {
        this.textOldAlpha = textOldAlpha;
        invalidate();
    }

    public float getTextOldScale() {
        return textOldScale;
    }

    public void setTextOldScale(float textOldScale) {
        this.textOldScale = textOldScale;
        invalidate();
    }

    public float getHandScale() {
        return handScale;
    }

    public void setHandScale(float handScale) {
        this.handScale = handScale;
        invalidate();
    }

    public float getShiningAlpha() {
        return shiningAlpha;
    }

    public void setShiningAlpha(float shiningAlpha) {
        this.shiningAlpha = shiningAlpha;
        invalidate();
    }

    public float getShiningScale() {
        return shiningScale;
    }

    public void setShiningScale(float shiningScale) {
        this.shiningScale = shiningScale;
        invalidate();
    }

    private int dpToPx(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    public int spToPx(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
