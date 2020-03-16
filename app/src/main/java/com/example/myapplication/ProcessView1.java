package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import static com.example.myapplication.Utils.*;
import static com.example.myapplication.Utils.dpToPixel;


public class ProcessView1 extends View {
    final float radius = dpToPixel(50);


    float progress = 0;
    float nowprogress = 0;
    RectF rect= new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ObjectAnimator animator= ObjectAnimator.ofFloat(this, "progress", nowprogress, getProgress());


    public ProcessView1(Context context) {
        super(context);
    }

    public ProcessView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProcessView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        paint.setTextSize(dpToPixel(20));
        paint.setTextAlign(Paint.Align.CENTER);
        //     animator.setRepeatCount(-1);
        animator.setDuration(1000);
        animator.setInterpolator(new FastOutSlowInInterpolator());
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        nowprogress = progress;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.end();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX=getWidth()/4*3;
        float centerY=getHeight()/2;
        int [] colors={Color.GREEN,Color.YELLOW,Color.RED};
        Shader shader =new LinearGradient(centerX - radius,centerY,centerX+radius,centerY,colors,null,Shader.TileMode.CLAMP);
        paint.setShader(shader);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dpToPixel(20));

        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rect,135,getProgress()*2.7f,false,paint);

        paint.setShader(null);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText((int) getProgress() + "%", centerX, centerY , paint);
        canvas.drawText("湿度", centerX, centerY+radius , paint);
    }
}
