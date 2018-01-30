package com.hekang.hkcxn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hekang.R;

public class MyDianyaView extends View implements Runnable {
	Bitmap bitmap, dstbmp,bitmapq,dstbmpq;
	Matrix matrix;
	float dianya ;
	int x = 0, y = 0, b = 282;

	public void setB(int b) {
		this.b = b;
	}

	public void setbackground(float dianya){
		if(dianya > 6){
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.biaoback2);
			dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
	}

	public MyDianyaView(Context context, AttributeSet set) {
		super(context, set);

		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.biaoback);
		bitmapq= BitmapFactory.decodeResource(getResources(),
				R.drawable.biao);
		matrix = new Matrix();
		matrix.postScale(0.7f, 0.7f);
		dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		dstbmpq = Bitmap.createBitmap(bitmapq, 0, 0, bitmapq.getWidth(),
				bitmapq.getHeight(), matrix, true);
		new Thread(this).start();
	}

	@Override
	// 重写该方法，进行绘图
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 把整张画布绘制成白色
		// canvas.drawColor(Color.WHITE);
		// canvas.
		Paint paint = new Paint();
		// 去锯齿
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#800000"));

		// 画笔风格
		paint.setStyle(Paint.Style.STROKE);
		// 画笔大小
		paint.setStrokeWidth(6);
		canvas.drawBitmap(dstbmp, x, y, null);
		canvas.drawBitmap(dstbmpq, x, y + b, null);
		//canvas.drawLine(x + 10, y + 5, x + 10, y + b, paint);
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
