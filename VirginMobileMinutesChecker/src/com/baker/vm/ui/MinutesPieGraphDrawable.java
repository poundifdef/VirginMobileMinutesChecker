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
	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;

    private static final int DRAWABLE_PADDING = 4;
    private static final int TIME_STROKE_WIDTH = 6;
    private static final int DRAWABLE_STROKE_WIDTH = 1;
    private static final int BACKGROUND_ALPHA = 100;

	private static final int DEGREES = 360;

	private final Context context;
	private int minDeg;
	private int dateDeg;

	private int alignment = ALIGN_CENTER;

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
		final RectF oval =
		    new RectF(clip.left + DRAWABLE_PADDING,
		              clip.top + DRAWABLE_PADDING,
		              clip.right - DRAWABLE_PADDING,
		              clip.bottom - DRAWABLE_PADDING);

		Log.e("drawOnCanvas", oval.toString());

		drawBackground(c, oval);
		drawMinutesChart(c, oval);

		drawTimeChart(c, oval);

		drawStroke(c, oval);

		drawText(c, clip);
	}

	public void setAlignment(final int iAlignment)
	{
		alignment = iAlignment;
	}

    private void drawText(final Canvas c, final Rect clip)
    {
        final Paint white = new Paint();
        white.setColor(Color.WHITE);
        white.setTextSize(12);

        final Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setTextSize(12);

        String text = ((int) (100 * getMinutesPercent())) + "%";
        text = getAccount().getMinutesTotal() - getAccount().getMinutesUsedInt() + "";

        final int textHeight = (int) black.getFontMetrics().top / 2;

        c.drawText(text,
        		clip.left + (clip.right - clip.left - black.measureText(text)) / 2,
        		clip.top + (clip.bottom - clip.top - textHeight) / 2,
        		black);
    }

    private void drawBackground(final Canvas c, final RectF clip)
    {
        final Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(BACKGROUND_ALPHA);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        c.drawOval(clip, p);
    }

    private void drawStroke(final Canvas c, final RectF clip)
    {
        final Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(DRAWABLE_STROKE_WIDTH);

        c.drawOval(clip, p);
    }

    private void drawMinutesChart(final Canvas c, final RectF clip)
    {
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

        c.drawArc(clip, 0, minDeg, true, minPaint);
    }

    private void drawTimeChart(final Canvas c, final RectF clip)
    {
        final RectF degOval = new RectF(clip.left + 10, clip.top + 10, clip.right - 10, clip.bottom - 10);
        final Paint degPaint = new Paint();
        degPaint.setColor(Color.BLACK);
        degPaint.setStyle(Paint.Style.STROKE);
        degPaint.setStrokeWidth(TIME_STROKE_WIDTH);
        degPaint.setAntiAlias(true);

        c.drawArc(degOval, 0, dateDeg, false, degPaint);

        // stroke the front of the time / minutes
        /*
        degPaint.setStrokeWidth(0);
        final int x = (int) ((clip.right - clip.left) / 2);
        final int y = (int) ((clip.bottom - clip.top) / 2);
        c.drawLine(clip.left + x, clip.top + y, clip.right, clip.top + y, degPaint);
        */
    }

    private Rect squareIt(final Rect clipBounds)
	{
		final Rect r = new Rect(clipBounds);
		final int w = r.right - r.left;
		final int h = r.bottom - r.top;
		final int size = Math.min(w, h);

		switch (alignment)
		{
			case ALIGN_LEFT:
				r.left = 0;
				r.right = size;
				break;
			case ALIGN_RIGHT:
				r.left = w - size;
				r.right = w;
				break;
			case ALIGN_CENTER:
			default:
				r.left += (w - size);
				r.right -= (w - size);
				break;
		}

		// Always vertically align
		r.top += (h - size);
		r.bottom -= (h - size);

		return r;
	}

}
