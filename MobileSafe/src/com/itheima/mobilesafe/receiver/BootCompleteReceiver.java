package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * 开机启动的广播接收者，用于判断sim卡是否改变
 * 
 * @ClassName: BootCompleteReceiver
 * @author JinHeng
 * @date 2015年1月4日 下午2:47:03
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			// 开启防盗保护才执行
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// 读取之前保存的SiM信息；
			String saveSim = sp.getString("sim", "") + "afu";
			// 读取当前的sim卡信息
			String realSim = tm.getSimSerialNumber();

			// 比较是否一样
			if (saveSim.equals(realSim)) {
				// sim没有变更，还是同一个哥们
			} else {
				// sim 已经变更 发一个短信给安全号码
				Toast.makeText(context, "sim 已经变更", 1).show();
				SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);
			}
		}
	}

}
