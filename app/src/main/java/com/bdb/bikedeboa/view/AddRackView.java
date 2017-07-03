package com.bdb.bikedeboa.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bdb.bikedeboa.R;

public class AddRackView extends View {

	private AnimatedVectorDrawableCompat addRack;
	private VectorDrawableCompat target;
	private float addRackScale = 0.15F;
	private float addRackIntrinsicScale = 0.7F;
	private float targetScale = 0.25F;
	private float innerTargetOffset = 125 * targetScale;

	private AddRackViewClickListener listener;

	public AddRackView(Context context) {
		super(context);
		init(context);
	}

	public AddRackView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AddRackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void setListener(AddRackViewClickListener listener) {
		this.listener = listener;
	}

	@SuppressWarnings("ConstantConditions")
	private void init(Context context) {
		addRack = AnimatedVectorDrawableCompat.create(context, R.drawable.animated_add_rack);
		addRack.setBounds(0, 0, addRack.getIntrinsicWidth(), addRack.getIntrinsicHeight());
		target = VectorDrawableCompat.create(context.getResources(), R.drawable.target, null);
		target.setBounds(0, 0, target.getIntrinsicWidth(), target.getIntrinsicHeight());
		addRack.start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = (int) Math.max(addRack.getIntrinsicWidth() * addRackScale, target.getIntrinsicWidth() * targetScale);
		int height = (int) (addRack.getIntrinsicHeight() * addRackScale + target.getIntrinsicHeight() * targetScale - innerTargetOffset);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.scale(addRackScale, addRackScale);
		addRack.draw(canvas);
		canvas.restore();

		canvas.save();
		float widthTranslate = (addRack.getIntrinsicWidth() * addRackScale - target.getIntrinsicWidth() * targetScale) * 0.5F;
		float heightTranslate = (addRack.getIntrinsicHeight() * addRackScale) - innerTargetOffset;
		canvas.translate(widthTranslate, heightTranslate);
		canvas.scale(targetScale, targetScale);
		target.draw(canvas);
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getActionMasked();
		float touchX = event.getX();
		float touchY = event.getY();

		if (!insidePin(touchX, touchY)) {
			return false;
		}

		switch (eventAction) {
			case MotionEvent.ACTION_DOWN:
				int centerX = (int) ((getLeft() + getRight()) * 0.5F);
				int bottom = (int) (getBottom() - (target.getIntrinsicHeight() * targetScale * 0.5F));
				Point clickPoint = new Point(centerX, bottom);
				if (listener != null) {
					listener.onClick(clickPoint);
				}
				return true;
		}
		return false;
	}

	public boolean insidePin(float x, float y) {
		// check if inside the bounds of the pin
		// calculate the radius from the coordinates to the center of the pin
		double pinRadius = addRack.getIntrinsicWidth() * addRackScale * addRackIntrinsicScale * 0.5F;
		double exactCenterX = getWidth() * 0.5F;
		double exactCenterY = getHeight() * 0.5F;
		double cathetusXSquared = Math.pow(exactCenterX - x, 2);
		double cathetusYSquared = Math.pow(exactCenterY - y, 2);
		double hypotenuse = Math.sqrt(cathetusXSquared + cathetusYSquared);
		return hypotenuse < pinRadius;
	}

	public interface AddRackViewClickListener {
		void onClick(Point point);
	}
}
