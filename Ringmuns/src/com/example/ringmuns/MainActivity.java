package com.example.ringmuns;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * メインアクティビティー。
 * 
 * @author 2014/08 matsushima
 *
 */
public class MainActivity extends Activity {

	public static MainActivity activity;

	/** ビュー */
	private MyView view;

	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	//private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); // アクティビティーにビューを設定
		activity = this;
		// myView
		this.view = new MyView(this);
		((LinearLayout)findViewById(R.id.layout)).addView(this.view, new LinearLayout.LayoutParams(FP, FP));
		// buttonKey
		findViewById(R.id.buttonLeft).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameMain.keyLeft = true;
			}
		});
		findViewById(R.id.buttonRight).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameMain.keyRight = true;
			}
		});
		findViewById(R.id.buttonDown).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameMain.keyDown = true;
			}
		});
		findViewById(R.id.buttonFlip).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameMain.keyFlip = true;
			}
		});
		findViewById(R.id.buttonStart).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GameMain.keyStart = true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//System.out.println(event.toString() + "\r\n");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			GameMain.mouseLeft = (MotionEvent.ACTION_DOWN == event.getAction());
		case MotionEvent.ACTION_MOVE:
			GameMain.mouseTime = event.getEventTime();
			GameMain.mouseX = (int)(event.getX() * 30 / view.getWidth());
			GameMain.mouseY = (int)(event.getY() * 30 / view.getWidth());
			break;
		}
		return true;
	}
}
