package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 高级工具
 * 
 * @ClassName: AtoolsActivity
 * @author JinHeng
 * @date 2015年1月4日 下午4:20:30
 */
public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * 点击事件，进入号码归属地查询的页面
	 * 
	 * @Title: numberQuery
	 * @author JinHeng
	 * @date 2015年1月4日 下午4:25:14
	 * @throws
	 */
	public void numberQuery(View view) {
		Intent intentv = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intentv);
	}
}
