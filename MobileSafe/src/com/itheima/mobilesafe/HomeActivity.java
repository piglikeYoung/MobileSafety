package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private GridView list_home;
	private MyAdapter adapter;
	private SharedPreferences sp;
	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

	private static int[] ids = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 8:// 进入设置中心
					Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;
				case 0://进入手机防盗页面
					showLostFindDialog();
					break;
				default:
					break;
				}
			}
		});

	}

	protected void showLostFindDialog() {
		//判断是否设置过密码
		if (isSetupPwd()) {
			//已经设置密码了，弹出的是输入对话框
			showEnterDialog();
		} else {
			//没有设置密码，弹出的是设置密码对话框
			showSetupPwdDialog();
		}
	}
	
	//设置密码对话框
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		//自定义一个布局文件
//		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
//		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
//		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
//		ok = (Button) view.findViewById(R.id.ok);
//		cancel = (Button) view.findViewById(R.id.cancel);
	}

	private void showEnterDialog() {
		
	}

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;

	/**
	 * 判断是否设置过密码
	 * @Title: isSetupPwd 
	 * @author JinHeng
	 * @date 2015年1月1日 下午5:39:33
	 * @throws
	 */
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			iv_item.setImageResource(ids[position]);
			tv_item.setText(names[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
