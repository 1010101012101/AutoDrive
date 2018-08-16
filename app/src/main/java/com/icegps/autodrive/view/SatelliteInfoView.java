package com.icegps.autodrive.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.icegps.autodrive.R;

import java.util.ArrayList;

import com.icegps.jblelib.ble.data.SatelliteData;

/**
 * Created by 111 on 2018/1/12.
 */

public class SatelliteInfoView extends View {

    private int width;
    private int height;
    private Paint paint;
    private float lineWidth;
    private float cx, cy;
    private int maxR;
    private Paint textPaint;
    private float sp10;
    private int degrees;
    private float sp8;
    ArrayList<SatelliteData> gpsData;
    ArrayList<SatelliteData> bdData;
    ArrayList<SatelliteData> gloData;
    private float dp5;
    private Paint rectPaint;

    public SatelliteInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        lineWidth = 1;
        rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
        rectPaint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        sp10 = getResources().getDimension(R.dimen.sp10);
        sp8 = getResources().getDimension(R.dimen.sp8);
        dp5 = getResources().getDimension(R.dimen.dp5);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(getLeft() + dp5,
                getTop() + dp5,
                getRight() - dp5,
                getBottom() - dp5);
        initData();
        canvas.drawRoundRect(rectF, dp5, dp5, rectPaint);
        canvas.drawCircle(cx, cy, maxR, paint);
        canvas.drawCircle(cx, cy, maxR - maxR / 3, paint);
        canvas.drawCircle(cx, cy, maxR - maxR / 3 * 2, paint);
        textPaint.setTextSize(getResources().getDimension(R.dimen.sp_14));
        textPaint.setFakeBoldText(true);
        textPaint.setColor(Color.BLACK);
        canvas.rotate(15, cx, cy);
        canvas.drawText("90°", cx, cy - maxR / 3 / 2, textPaint);
        canvas.drawText("60°", cx, cy - ((maxR / 3 * 2) - maxR / 3 / 2), textPaint);
        canvas.drawText("30°", cx, cy - (maxR - maxR / 3 / 2), textPaint);
        canvas.rotate(-15, cx, cy);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(sp8);
        for (int i = 0; i < 12; i++) {
            textPaint.setTextSize(sp10);
            canvas.rotate(i * 30, cx, cy);
            canvas.drawLine(cx, cy, cx, cy - maxR, paint);
            canvas.drawText(getValue(i), cx, cy - maxR - lineWidth * 3, textPaint);
            canvas.rotate(-i * 30, cx, cy);
        }


        if (gpsData != null)
            for (SatelliteData satelliteData : gpsData) {
                drawText(canvas, satelliteData, "G", Color.RED);
            }
        if (bdData != null)
            for (SatelliteData satelliteData : bdData) {
                drawText(canvas, satelliteData, "B", Color.parseColor("#8A633A"));
            }

        if (gloData != null)
            for (SatelliteData satelliteData : gloData) {
                drawText(canvas, satelliteData, "L", Color.parseColor("#006054"));
            }

    }

    private void drawText(Canvas canvas, SatelliteData satelliteData, String str, int color) {
        textPaint.setColor(color);
        canvas.rotate(satelliteData.getSatelliteAzimuth(), cx, cy);
        canvas.drawText(
                str + satelliteData.getSatelliteNumber(),
                cx,
                getSatelliteElevation(satelliteData.getSatelliteElevation()),
                textPaint);
        canvas.rotate(-satelliteData.getSatelliteAzimuth(), cx, cy);
    }

    private float getSatelliteElevation(short azimuth) {
        float sumLenght = maxR - sp10;
        float ave = sumLenght / 90;
        float currentValue = ave * azimuth;
        return cy - (sumLenght - currentValue);
    }

    private String getValue(int i) {
        String value;
        switch (i * 30) {
            case 0:
                value = "N";
                break;
            case 90:
                value = "E";
                break;
            case 180:
                value = "S";
                break;
            case 270:
                value = "W";
                break;
            default:
                value = "";
                break;
        }
        return value;
    }

    private void initData() {
        width = getWidth();
        height = getHeight();
        maxR = (int) (Math.min(width, height) / 2 * 0.8);
        cx = width / 2;
        cy = height / 2;
    }

    public void drawSatellite(ArrayList<SatelliteData> gpsData, ArrayList<SatelliteData> bdData, ArrayList<SatelliteData> gloData) {
        this.gpsData = gpsData;
        this.bdData = bdData;
        this.gloData = gloData;
        invalidate();
    }
}
