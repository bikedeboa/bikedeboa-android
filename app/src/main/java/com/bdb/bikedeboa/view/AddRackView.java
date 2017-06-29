package com.bdb.bikedeboa.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bdb.bikedeboa.R;

public class AddRackView extends View {

	private VectorDrawableCompat addRack;
	private VectorDrawableCompat target;
	private float addRackScale = 0.1F;
	private float targetScale = 0.25F;

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

	@SuppressWarnings("ConstantConditions")
	private void init(Context context) {
		addRack = VectorDrawableCompat.create(context.getResources(), R.drawable.add_rack, null);
		addRack.setBounds(0, 0, addRack.getIntrinsicWidth(), addRack.getIntrinsicHeight());
		target = VectorDrawableCompat.create(context.getResources(), R.drawable.target, null);
		target.setBounds(0, 0, target.getIntrinsicWidth(), target.getIntrinsicHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = (int) Math.max(addRack.getIntrinsicWidth() * addRackScale, target.getIntrinsicWidth() * targetScale);
		int height = (int) (addRack.getIntrinsicHeight() * addRackScale + target.getIntrinsicHeight() * targetScale);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.scale(addRackScale, addRackScale);
		addRack.draw(canvas);
		canvas.restore();
		float widthTranslate = (addRack.getIntrinsicWidth() * addRackScale - target.getIntrinsicWidth() * targetScale) * 0.5F;
		float heightTranslate = (addRack.getIntrinsicHeight() * addRackScale) - (40 * targetScale); //offset
		canvas.translate(widthTranslate, heightTranslate);
		canvas.scale(targetScale, targetScale);
		target.draw(canvas);
	}
}
