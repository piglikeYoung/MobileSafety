package com.itheima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends Activity {

	private EditText ed_phone;
	private TextView result;

	/**
	 * 系统提供的振动服务
	 */
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);

		 ed_phone = (EditText) findViewById(R.id.ed_phone);
		 result = (TextView) findViewById(R.id.result);
		 vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}

	/**
	 * 查询号码归属地
	 * 
	 * @Title: numberAddressQuery
	 * @author JinHeng
	 * @date 2015年1月4日 下午4:31:13
	 * @throws
	 */
	public void numberAddressQuery(View view) {
		String phone = ed_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			ed_phone.startAnimation(shake);
			
			//当电话号码为空的时候，就去振动手机提醒用户
//			 vibrator.vibrate(2000);
			 long[] pattern = {200,200,300,300,1000,2000};
			 //-1不重复 0循环振动 1；
			 vibrator.vibrate(pattern, -1);
			 
			 return;
		} else {
			String address = NumberAddressQueryUtils.queryNumber(phone);
			result.setText(address);
			//去数据库查询号码归属地
			//1.网络查询 ；2.本地的数据库--数据库
			//写一个工具类，去查询数据库
			Log.i("NumberAddressQueryActivity", "您要查询的电话号码=="+phone);
		}
	}
}
