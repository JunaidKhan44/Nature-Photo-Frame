package com.example.naturephotoframe.BackgrounEraser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.example.naturephotoframe.Utils.Common;

import java.io.IOException;

public class MyCustomView extends View {
    private Bitmap mSourceBitmap;
    private Canvas mSourceCanvas = new Canvas();
    private Paint mDestPaint = new Paint();
    private Path mDestPath = new Path();

    public MyCustomView(Context context)
    {
        super(context);

        //convert drawable file into bitmap
        Bitmap rawBitmap = null;
        try {
            rawBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Common.imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //convert bitmap into mutable bitmap
        mSourceBitmap = Bitmap.createBitmap(rawBitmap.getWidth(), rawBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        mSourceCanvas.setBitmap(mSourceBitmap);
        mSourceCanvas.drawBitmap(rawBitmap, getMatrix(),  null);

        mDestPaint.setAlpha(0);
        mDestPaint.setAntiAlias(true);
        mDestPaint.setStyle(Paint.Style.STROKE);
        mDestPaint.setStrokeJoin(Paint.Join.ROUND);
        mDestPaint.setStrokeCap(Paint.Cap.ROUND);
        mDestPaint.setStrokeWidth(50);
        mDestPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //Draw path
        mSourceCanvas.drawPath(mDestPath, mDestPaint);

        //Draw bitmap
        canvas.drawBitmap(mSourceBitmap, 0, 0, null);

        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mDestPath.moveTo(xPos, yPos);
                break;

            case MotionEvent.ACTION_MOVE:
                mDestPath.lineTo(xPos, yPos);
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }
}