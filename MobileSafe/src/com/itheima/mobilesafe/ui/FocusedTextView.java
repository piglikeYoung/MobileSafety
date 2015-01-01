package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 自定一个TextView 一出生就有焦点，有焦点跑马灯才可以滚动
 * 
 * @ClassName: FocusedTextView
 * @author JinHeng
 * @date 2014年12月28日 上午10:35:05
 */
public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusedTextView(Context context) {
		super(context);
	}

	/**
	 * 当前并没有焦点，我只是欺骗了Android系统 ，不管有没有焦点，都返回true，表示肯定有焦点
	 * 
	 * @Title: isFocused
	 * @author JinHeng
	 * @date 2014年12月28日 上午10:36:06
	 * @throws
	 */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
