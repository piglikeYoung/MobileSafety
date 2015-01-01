package com.itheima.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private TextView tv_update_info;

	/**
	 * 新版本的下载地址
	 */
	private String apkurl;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号" + getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已经关闭
			handler.postDelayed(new Runnable() {

				public void run() {
					enterHome();
				}
			}, 2000);
		}

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).setAnimation(aa);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// 显示升级的对话框
				Log.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME:// 进入主界面
				enterHome();
				break;
			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case NETWORK_ERROR:// 网络异常
				enterHome();
				Toast.makeText(SplashActivity.this, "网络异常", 0).show();
				break;

			case JSON_ERROR:// JSON解析出错
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON解析出错", 0).show();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 检查是否有新版本，如果有就升级
	 * 
	 * @Title: checkUpdate
	 * @author JinHeng
	 * @date 2014年12月27日 下午10:57:06
	 * @throws
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {
				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();

					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转成String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功了" + result);
						// json解析
						JSONObject obj = new JSONObject(result);
						// 得到服务器的版本信息
						String version = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// 校验是否有新版本
						if (getVersionName().equals(version)) {
							// 版本一致，没有新版本，进入主页面
							mes.what = ENTER_HOME;
						} else {
							// 有新版本，弹出一升级对话框
							mes.what = SHOW_UPDATE_DIALOG;

						}
					}

				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
					mes.what = JSON_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					// 我们花了多少时间
					long dTime = endTime - startTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					handler.sendMessage(mes);
				}
			}
		}.start();
	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();
	}

	/**
	 * 弹出升级对话框
	 * 
	 * @Title: showUpdateDialog
	 * @author JinHeng
	 * @date 2014年12月28日 上午9:30:53
	 * @throws
	 */
	protected void showUpdateDialog() {
		//this = Activity.this
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle("提示升级");
		// builder.setCancelable(false);//点击返回键、屏幕别的地方无效，只有点击立即升级或取消有效
		builder.setOnCancelListener(new OnCancelListener() {//监听返回键、屏幕别的地方，当点击后进入主页面
			public void onCancel(DialogInterface dialog) {
				// 进入主界面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// 下载APK，并且替换安装
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// sdcard存在
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, //
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk", //
							new AjaxCallBack<File>() {
								public void onFailure(Throwable t, int errorNo, String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "下载失败", 1).show();
									super.onFailure(t, errorNo, strMsg);
								};

								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									tv_update_info.setVisibility(View.VISIBLE);
									// 当前下载百分比
									int progress = (int) (current * 100 / count);
									tv_update_info.setText("下载进度：" + progress + "%");
								};

								public void onSuccess(File t) {
									super.onSuccess(t);
									installAPK(t);
								}

								/**
								 * 安装APK
								 * 
								 * @Title: installAPK
								 * @author JinHeng
								 * @date 2014年12月28日 上午9:45:57
								 * @throws
								 */
								private void installAPK(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");

									startActivity(intent);
								};
							});
				} else {
					Toast.makeText(getApplicationContext(), "没有sdcard，请安装上在试", 0).show();
					return;
				}
			}
		});

		builder.setNegativeButton("下次再说", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();// 进入主页面
			}
		});

		builder.show();
	}

	/**
	 * 得到应用程序的版本名称
	 * 
	 * @Title: getVersionName
	 * @author JinHeng
	 * @date 2014年12月27日 下午10:24:57
	 * @throws
	 */
	private String getVersionName() {
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();
		// 得到知道APK的功能清单文件

		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}
