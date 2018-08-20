package com.icegps.autodrive.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.icegps.autodrive.R;

/**
 * Created by 111 on 2017/12/22.
 */

public class RulerView extends View {
    private final Path path;
    private final Paint linePaint;
    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(getResources().getDimension(R.dimen.lineWidth));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0, getHeight() / 2 - 10);
        path.lineTo(0, getHeight() / 2);
        path.lineTo(getWidth(), getHeight() / 2);
        path.lineTo(getWidth(), getHeight() / 2 - 10);
        canvas.drawPath(path, linePaint);
    }

}
