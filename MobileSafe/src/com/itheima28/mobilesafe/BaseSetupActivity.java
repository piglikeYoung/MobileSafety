package com.itheima28.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {

	// 1.定义一个手势识别器
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 2.实例化这个手势识别器
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				// 屏蔽在X滑动很慢的情形
				if (Math.abs(velocityX) < 200) {
					Toast.makeText(getApplicationContext(), "滑动得太慢了", 0).show();
					return true;
				}

				// 屏蔽斜滑这种情况
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					Toast.makeText(getApplicationContext(), "不能这样滑", 0).show();
					return true;
				}

				if ((e2.getRawX() - e1.getRawX()) > 200) {
					// 显示上一个页面：从左往右滑动
					System.out.println("显示上一个页面：从左往右滑动");
					showPre();
					return true;
				}

				if ((e1.getRawX() - e2.getRawX()) > 200) {
					// 显示上一个页面：从右往左滑动
					System.out.println("显示下一个页面：从右往左滑动");
					showNext();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}

	public abstract void showNext();

	public abstract void showPre();

	/**
	 * 上一步
	 * 
	 * @Title: showPre
	 * @author JinHeng
	 * @date 2014年12月30日 下午5:07:19
	 * @throws
	 */
	public void pre() {
		showPre();
	}

	/**
	 * 下一步
	 * 
	 * @Title: showNext
	 * @author JinHeng
	 * @date 2014年12月30日 下午5:07:58
	 * @throws
	 */
	public void next() {
		showNext();
	}

	// 3.使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
