package com.itheima.mobilesafe.ui;

import com.itheima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我们自定义的组合控件，它里面有两个TextView ，还有一个ImageVeiw,还有一个View
 * 
 * @ClassName: SettingClickView
 * @author JinHeng
 * @date 2015年1月6日 下午5:16:18
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_desc;
	private TextView tv_title;

	private String desc_on;
	private String desc_off;

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * 带有两个参数的构造方法，布局文件使用的时候调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "mtitle");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobilesafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}

	private void iniView(Context context) {
		// 把一个布局文件--->View 并且加载在SettingItemView
		View.inflate(context, R.layout.setting_click_view, this);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}

	/**
	 * 设置 组合控件的描述信息
	 */
	public void setDesc(String text) {
		tv_desc.setText(text);
	}

	/**
	 * 设置组合控件的状态
	 */

	public void setChecked(boolean checked) {
		if (checked) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}
	}

	/**
	 * 设置组合控件的标题
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

}
