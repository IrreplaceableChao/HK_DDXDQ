package com.hekang.hkcxn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.hekang.R;

public class MyCanvas extends View implements Runnable{
	Bitmap bitmap,dstbmp;
	private Canvas myCanvas;
	private Paint myPaint=new Paint();
	int angle=0 ;

	public void setAngle(int x) {
		this.angle = x;
	}


	public MyCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.zhongligaobian);
	}

	public MyCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.zhongligaobian);
		Matrix matrix = new Matrix();
		matrix.postScale(0.7f, 0.7f);
		dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		new Thread(this).start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.myCanvas=canvas;
		myPaint.setAntiAlias(true);
		myCanvas.drawBitmap(dstbmp, 0, 0, myPaint);
		myCanvas.rotate(angle, 174, 170);
		drawAL(174, 170, 174, 80);
	}

	/**
	 * 设置画笔默认样式
	 */
	public void setPaintDefaultStyle(){
		myPaint.setAntiAlias(true);
		myPaint.setColor(Color.RED);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(3);
	}


	/**
	 * 画圆
	 * @param x x坐标
	 * @param y	y坐标
	 * @param radius	圆的半径
	 */
	public void drawCircle(float x,float y,float radius){
		myCanvas.drawCircle(x, y, radius, myPaint);
		invalidate();
	}

	/**
	 * 画一条直线
	 * @param fromX 起点x坐标
	 * @param fromY	起点Y坐标
	 * @param toX	终点X坐标
	 * @param toY	终点Y坐标
	 */
	public void drawLine(float fromX,float fromY,float toX,float toY){
		Path linePath=new Path();
		linePath.moveTo(fromX, fromY);
		linePath.lineTo(toX, toY);
		linePath.close();
		myCanvas.drawPath(linePath, myPaint);
		invalidate();
	}


	/**
	 * 画箭头
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void drawAL(int sx, int sy, int ex, int ey)
	{
		double H = 8; // 箭头高度
		double L = 3.5; // 底边的一半
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // 箭头角度
		double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// 画线
		myCanvas.drawLine(sx, sy, ex, ey,myPaint);
		Path triangle = new Path();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.close();
		myCanvas.drawPath(triangle,myPaint);

	}
	// 计算
	public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
	{
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			// 使用postInvalidate可以直接在线程中更新界面
			postInvalidate();
		}
	}


}
