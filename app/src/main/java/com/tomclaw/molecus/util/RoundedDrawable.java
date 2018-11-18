package com.tomclaw.molecus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * A Drawable that draws an oval with given {@link Bitmap}
 */
public class RoundedDrawable extends Drawable {
    private final Bitmap mBitmap;
    private final Paint mPaint;
    private final RectF mRectF;
    private final int mBitmapWidth;
    private final int mBitmapHeight;
    private final BitmapShader bitmapShader;
    private final Matrix shaderMatrix = new Matrix();

    public RoundedDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(bitmapShader);

        // NOTE: we assume bitmap is properly scaled to current density
        mBitmapWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        mBitmapHeight = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(mRectF, mPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int width = bounds.width();
        int height = bounds.height();

        shaderMatrix.reset();
        if (width != mBitmapWidth || height != mBitmapHeight) {

            //initially taken from  https://github.com/vinc3m1/RoundedImageView
            //for scaleType CENTER_CROP
            float dx = 0;
            float dy = 0;
            float scale;

            if (mBitmapWidth * height > width * mBitmapHeight) {
                scale = height / (float) mBitmapHeight;
                dx = (width - mBitmapWidth * scale) * 0.5f;
            } else {
                scale = width / (float) mBitmapWidth;
                dy = (height - mBitmapHeight * scale) * 0.5f;
            }

            shaderMatrix.setScale(scale, scale);
            shaderMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        }
        bitmapShader.setLocalMatrix(shaderMatrix);
        mRectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    public void setAntiAlias(boolean aa) {
        mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    // TODO allow set and use target density, mutate, constant state, changing configurations, etc.
}
