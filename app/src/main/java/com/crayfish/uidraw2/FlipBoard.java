package com.crayfish.uidraw2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * ============================
 * 作    者：crayfish(徐杰)
 * 创建日期：2017/10/20.
 * 描    述：
 * 修改历史：
 * ===========================
 */

public class FlipBoard extends View{

    private Bitmap bitmap;
    private Paint paint;
    private Camera camera;
    private int bitmapWidth;
    private int bitmapHeight;
    private Matrix matrix;
    private int degress;
    private int degressZ;
    private int degressY;

    private AnimatorSet animatorSet;
    private ObjectAnimator animator1;
    private ObjectAnimator animator2;
    private ObjectAnimator animator3;


    public FlipBoard(Context context) {
        this(context,null);
    }

    public FlipBoard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlipBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.FlipBoard);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) array.getDrawable(R.styleable.FlipBoard_drawable);
        array.recycle();

        if(bitmapDrawable != null){
            bitmap = bitmapDrawable.getBitmap();
        }else {
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.maps);
        }
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        init();
    }

    {

    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();
        matrix = new Matrix();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 6;
        camera.setLocation(0, 0, newZ);

        animatorSet = new AnimatorSet();
        animator1 = ObjectAnimator.ofInt(this,"degress",0,-45);
        animator1.setDuration(1000);
        animator2 = ObjectAnimator.ofInt(this,"degressZ",0,270);
        animator2.setDuration(1000);
        animator3 = ObjectAnimator.ofInt(this,"degressY",0,45);
        animator3.setDuration(1000);
        animatorSet.playSequentially(animator1,animator2,animator3);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.cancel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                start();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void start(){
        animatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mWidth = getWidth();
        int mHeight = getHeight();
        int x = mWidth/2 - bitmapWidth/2;
        int y = mHeight/2 - bitmapHeight/2;

        //画变换的一半
        //先旋转，再裁切，再使用camera执行3D动效,**然后保存camera效果**,最后再旋转回来
//        canvas.save();
//        camera.save();
//        canvas.translate(mWidth/2, mHeight/2);
//        canvas.rotate(-degressZ);
//        camera.rotateY(-45);
//        camera.applyToCanvas(canvas);
//        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
//        canvas.clipRect(0, -mHeight/2, mWidth/2, mHeight/2);
//        canvas.rotate(degressZ);
//        canvas.translate(-mWidth/2, -mHeight/2);
//        camera.restore();
//        canvas.drawBitmap(bitmap, x, y, paint);
//        canvas.restore();

        //画不变换的另一半
//        canvas.save();
//        camera.save();
//        canvas.translate(mWidth/2, mHeight/2);
//        canvas.rotate(-degressZ);
////        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
//        canvas.clipRect(-mWidth/2, -mHeight/2, 0, mHeight/2);
////        //此时的canvas的坐标系已经旋转，所以这里是rotateY
////        camera.rotateY(fixDegreeY);
//        camera.applyToCanvas(canvas);
//        canvas.rotate(degressZ);
//        canvas.translate(-mWidth/2, -mHeight/2);
//        camera.restore();
//        canvas.drawBitmap(bitmap, x, y, paint);
//        canvas.restore();

        canvas.save();
        matrix.reset();
        camera.save();
        camera.rotateZ(degressZ);
        camera.rotateY(degress);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-mWidth/2,-mHeight/2);
        matrix.postTranslate(mWidth/2,mHeight/2);
        canvas.concat(matrix);
        canvas.clipRect(mWidth/2,0,mWidth,mHeight);
        canvas.rotate(degressZ,mWidth/2,mHeight/2);
        canvas.drawBitmap(bitmap,x,y,paint);
        canvas.restore();

        canvas.save();
        matrix.reset();
        camera.save();
        camera.rotateX(-degressY);
        camera.rotateZ(degressZ);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-mWidth/2,-mHeight/2);
        matrix.postTranslate(mWidth/2,mHeight/2);
        canvas.concat(matrix);
        canvas.clipRect(0,0,mWidth/2,mHeight);
        canvas.rotate(degressZ,mWidth/2,mHeight/2);
        canvas.drawBitmap(bitmap,x,y,paint);
        canvas.restore();
    }

    public void setDegress(int degress){
        this.degress = degress;
        invalidate();
    }
    public int getDegress(){
        return degress;
    }

    public int getDegressZ() {
        return degressZ;
    }

    public void setDegressZ(int degressZ) {
        this.degressZ = degressZ;
        invalidate();
    }

    public int getDegressY() {
        return degressY;
    }

    public void setDegressY(int degressY) {
        this.degressY = degressY;
        invalidate();
    }
}
