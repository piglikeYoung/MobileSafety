package com.itheima.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

public class Setup2Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
	}

	@Override
	public void showNext() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		// 要求在finish()或者startActivity(intent);后面执行；
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		// 要求在finish()或者startActivity(intent);后面执行；
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

}
