package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.utils.MD5Utils;

/**
 * APP主界面
 * 
 * @ClassName: HomeActivity
 * @author JinHeng
 * @date 2015年1月1日 下午10:30:43
 */
public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";
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
				case 0:// 进入手机防盗页面
					showLostFindDialog();
					break;
				default:
					break;
				}
			}
		});

	}

	protected void showLostFindDialog() {
		// 判断是否设置过密码
		if (isSetupPwd()) {
			// 已经设置密码了，弹出的是输入对话框
			showEnterDialog();
		} else {
			// 没有设置密码，弹出的是设置密码对话框
			showSetupPwdDialog();
		}
	}

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;

	/**
	 * 设置密码对话框
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件，因为Android原生的dialog没有输入框，所以得自己定义布局文件
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 把这个对话框取消掉
				dialog.dismiss();
			}
		});

		ok.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
					return;
				}
				// 判断是否一致才去保存
				if (password.equals(password_confirm)) {
					// 一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));// 保存加密后的
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "一致的话，就保存密码，把对话框消掉，还要进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);
				} else {

					Toast.makeText(HomeActivity.this, "密码不一致", 0).show();
					return;
				}
			}
		});
		dialog = builder.create();//创建对话框
		dialog.setView(view, 0, 0, 0, 0);//将布局文件设置进对话框
		dialog.show();
	}

	/**
	 * 输入密码对话框
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件，因为Android原生的dialog没有输入框，所以得自己定义布局文件
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 把这个对话框取消掉
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");// 取出加密后的
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "密码为空", 1).show();
					return;
				}

				if (MD5Utils.md5Password(password).equals(savePassword)) {
					// 输入的密码是我之前设置的密码
					// 把对话框消掉，进入主页面；
					dialog.dismiss();
					Log.i(TAG, "把对话框消掉，进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
					startActivity(intent);

				} else {
					Toast.makeText(HomeActivity.this, "密码错误", 1).show();
					et_setup_pwd.setText("");
					return;
				}

			}
		});
		dialog = builder.create();//创建对话框
		dialog.setView(view, 0, 0, 0, 0);//将布局文件设置进对话框
		dialog.show();
	}

	/**
	 * 判断是否设置过密码
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
