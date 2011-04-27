///**
// *
// */
//package com.baker.vm.ui;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//
//import com.baker.vm.VMAccount;
//import com.jaygoel.virginminuteschecker.R;
//
///**
// * @author baker
// *
// */
//public final class MinutesBarGraph extends MinutesGraph
//{
//
//	public MinutesBarGraph(final Context context, final VMAccount account)
//	{
//		super(context, account);
//	}
//
//	@Override
//	protected void onDraw(final Canvas canvas)
//	{
//		super.onDraw(canvas);
//
//		final Paint minPaint = new Paint();
//		minPaint.setStyle(Paint.Style.FILL);
//		minPaint.setAntiAlias(true);
//		if (getDatePercent() < (getMinutesPercent() * 1.05F) &&
//			getDatePercent() > (getMinutesPercent() * .95F))
//		{
//			minPaint.setColor(getResources().getColor(R.color.warning));
//		}
//		else if (getDatePercent() < getMinutesPercent())
//		{
//			minPaint.setColor(getResources().getColor(R.color.error));
//		}
//		else
//		{
//			minPaint.setColor(getResources().getColor(R.color.info));
//		}
//		final int vertMiddle = getBottom() - getTop();
//		final RectF minRect = new RectF(getLeft() - getLeftPaddingOffset(), vertMiddle - (getHeight() / 2), getRight(), vertMiddle + (getHeight() / 2));
//
//		final int width = 20;
//		final Paint degPaint = new Paint();
//		degPaint.setColor(Color.BLACK);
//		degPaint.setStyle(Paint.Style.FILL);
//		degPaint.setAntiAlias(true);
//
//		final int degPosition = (int) (getWidth() * getDatePercent());
//		final RectF dateRect = new RectF(degPosition - (width / 2), getTop(), degPosition + (width / 2), getBottom());
//
//		canvas.drawRoundRect(minRect, 5f, 5f, minPaint);
//		canvas.drawRect(dateRect, degPaint);
//	}
//
//	@Override
//	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
//	{
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//		int w = widthMeasureSpec;
//		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED)
//		{
//			w = 200;
//		}
//		int h = heightMeasureSpec;
//		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED)
//		{
//			h = 40;
//		}
//
//		setMeasuredDimension(w, h);
//	}
//}
