package com.itheima.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	/**
	 * 设备策略服务
	 */
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		// 设备策略服务
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

		for (Object obj : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			// 发送者
			String sender = sms.getOriginatingAddress();// 15555555556
			String safenumber = sp.getString("safenumber", "");// 5556

			Log.i(TAG, "====sender==" + sender);
			String body = sms.getMessageBody();

			// if (sender.contains(safenumber)) {
			if ("#*location*#".equals(body)) {
				// 得到手机的GPS
				Log.i(TAG, "得到手机的GPS");
				// 启动服务
				Intent i = new Intent(context, GPSService.class);
				context.startService(i);
				SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				String lastlocation = sp.getString("lastlocation", null);
				if (TextUtils.isEmpty(lastlocation)) {
					// 位置没有得到
					SmsManager.getDefault().sendTextMessage(sender, null, "geting loaction.....", null, null);
				} else {
					SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
				}

				// 终止这个广播
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				// 播放报警影音
				Log.i(TAG, "播放报警影音");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setLooping(false);// 循环播放
				player.setVolume(1.0f, 1.0f);
				player.start();

				// 终止这个广播
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				// 远程清除数据
				Log.i(TAG, "远程清除数据");

				wipeData();// 擦出数据

				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {
				// 远程锁屏
				Log.i(TAG, "远程锁屏");

				lockscreen(context);// 锁屏

				abortBroadcast();
			}
			// }
		}
	}

	/**
	 * 用代码去开启管理员
	 * 
	 * @Title: openAdmin
	 * @author JinHeng
	 * @date 2015年1月4日 下午1:52:01
	 * @throws
	 */
	private void openAdmin(Context context) {
		// 创建一个Intent
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// 我要激活谁
		ComponentName mDeviceAdminSample = new ComponentName(context, MyAdmin.class);

		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		// 劝说用户开启管理员权限
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哥们开启我可以一键锁屏，你的按钮就不会经常失灵");
		context.startActivity(intent);
	}

	/**
	 * 一键锁屏
	 * 
	 * @Title: lockscreen
	 * @author JinHeng
	 * @date 2015年1月4日 下午1:57:44
	 * @throws
	 */
	private void lockscreen(Context context) {
		ComponentName who = new ComponentName(context, MyAdmin.class);
		if (dpm.isAdminActive(who)) {
			dpm.lockNow();// 锁屏
			dpm.resetPassword("", 0);// 设置屏蔽密码
		} else {
			Toast.makeText(context, "还没有打开管理员权限", 1).show();
			return;
		}
	}

	/**
	 * 擦出数据
	 * 
	 * @Title: wipeData
	 * @author JinHeng
	 * @date 2015年1月4日 下午2:03:54
	 * @throws
	 */
	private void wipeData() {
		// 清除Sdcard上的数据
		// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		// 恢复出厂设置
		// dpm.wipeData(0);
	}

	/**
	 * 卸载当前软件
	 * 
	 * @Title: uninstall
	 * @author JinHeng
	 * @date 2015年1月4日 下午2:00:44
	 * @throws
	 */
	private void uninstall(Context context) {
		// 1.先清除管理员权限
		ComponentName mDeviceAdminSample = new ComponentName(context, MyAdmin.class);
		dpm.removeActiveAdmin(mDeviceAdminSample);
		// 2.普通应用的卸载
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + context.getPackageName()));
		context.startActivity(intent);
	}

}
