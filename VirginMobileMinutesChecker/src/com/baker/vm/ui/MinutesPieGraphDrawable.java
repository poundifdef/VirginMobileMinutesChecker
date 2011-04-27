/**
 *
 */
package com.baker.vm.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.baker.vm.VMAccount;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class MinutesPieGraphDrawable extends MinutesGraphDrawable
{

	private static final int DEGREES = 360;

	private final Context context;
	private int minDeg;
	private int dateDeg;

	String buf = "";

	public MinutesPieGraphDrawable(final Context c)
	{
		super(null);

		context = c;
		updateModel(null);
	}

	public MinutesPieGraphDrawable(final Context c, final VMAccount account)
	{
		this(c);

		updateModel(account);
	}

	@Override
	protected void updateModel(final VMAccount account)
	{
		super.updateModel(account);

		if (hasMinutes())
		{
			minDeg = (int) (getMinutesPercent() * DEGREES);
		}
		else
		{
			minDeg = 0;
		}
		if (hasDates())
		{
			dateDeg = (int) (getDatePercent() * DEGREES);
		}
		else
		{
			dateDeg = 0;
		}
	}

	@Override
	public void draw(final Canvas c)
	{
		final Rect clip = squareIt(c.getClipBounds());

		drawOnCanvas(c, clip);

	}

	public void drawOnCanvas(final Canvas c, final Rect clip)
	{
		final RectF oval = new RectF(0, 0, clip.right, clip.bottom);

		final Paint minPaint = new Paint();
		minPaint.setAlpha(getOpacity());
		minPaint.setStyle(Paint.Style.FILL);
		minPaint.setAntiAlias(true);
		if (dateDeg < (minDeg * 1.05F) && dateDeg > (minDeg * .95F))
		{
			minPaint.setColor(context.getResources().getColor(R.color.warning));
		}
		else if (dateDeg < minDeg)
		{
			minPaint.setColor(context.getResources().getColor(R.color.error));
		}
		else
		{
			minPaint.setColor(context.getResources().getColor(R.color.info));
		}
		Log.e("INFO", oval.toString() + " : " + minDeg);


		final RectF degOval = new RectF(clip.left + 10, clip.top + 10, clip.right - 10, clip.bottom - 10);
		final Paint degPaint = new Paint();
		degPaint.setColor(Color.BLACK);
		degPaint.setStyle(Paint.Style.STROKE);
		degPaint.setStrokeWidth(10);
		degPaint.setAntiAlias(true);

		Log.e("INFO", degOval.toString() + " : " + dateDeg);

		c.drawArc(oval, 0, minDeg, true, minPaint);
		c.drawArc(degOval, 0, dateDeg, false, degPaint);

		/*
		final Paint text = new Paint();
		text.setColor(Color.WHITE);
		text.setTextSize(12);

		c.drawText(string, 5,60, text);
		*/
	}

	private Rect squareIt(final Rect clipBounds)
	{
		final Rect r = new Rect(clipBounds);
		final int w = r.right - r.left;
		final int h = r.bottom - r.top;
		final int size = Math.min(w, h);

		Log.w("squareIt", r.toString() + ": " + w + " x " + h + " = " + size);

		r.left += (w - size);
		r.right -= (w - size);
		r.top += (h - size);
		r.bottom -= (h - size);

		Log.w("squareIt", r.toString());

		return r;
	}

	@Override
	public int getOpacity() {
	    return 50;
	}


}
