package com.itheima.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;

/**
 * 拦截黑名单短信和电话 服务
 * 
 * @ClassName: CallSmsSafeService
 * @author JinHeng
 * @date 2015年1月22日 上午11:48:17
 */
public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 注册手机通话状态监听

		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);// 注册短信广播接收者
		super.onCreate();
	}

	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	/**
	 * 短信的广播接收者
	 * 
	 * @ClassName: InnerSmsReceiver
	 * @author JinHeng
	 * @date 2015年1月22日 上午11:50:22
	 */
	private class InnerSmsReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "内部广播接受者， 短信到来了");
			// 检查发件人是否是黑名单号码，设置短信拦截全部拦截。
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				// 得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if ("2".equals(result) || "3".equals(result)) {
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
			}
		}

	}

	/**
	 * 手机通话监听
	 * 
	 * @ClassName: MyListener
	 * @author JinHeng
	 * @date 2015年1月22日 上午11:50:51
	 */
	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 铃响状态。
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result) || "3".equals(result)) {
					Log.i(TAG, "挂断电话。。。。");

					// 删除呼叫记录
					// 另外一个应用程序联系人的应用的私有数据库,所以需要内容提供者
					// 观察呼叫记录数据库内容的变化
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber, new Handler()));
					endCall();// 挂断电话 另外一个进程里面运行的
								// 远程服务的方法。方法调用后,呼叫记录可能还没有生成,所以监听数据库是否变化
				}
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

	/**
	 * 监听通话记录
	 * 
	 * @ClassName: CallLogObserver
	 * @author JinHeng
	 * @date 2015年1月24日 下午2:10:22
	 */
	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;

		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			System.out.println("数据库产生了呼叫记录");
			getContentResolver().unregisterContentObserver(this);// 取消注册
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}

	}

	/**
	 * 利用内容提供者删除呼叫记录
	 * 
	 * @Title: deleteCallLog
	 * @author JinHeng
	 * @date 2015年1月24日 下午2:10:15
	 * @throws
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		// 呼叫记录uri的路径
		Uri uri = Uri.parse("content://call_log/calls");
		// CallLog.CONTENT_URI;
		resolver.delete(uri, "number=?", new String[] { incomingNumber });
	}

	/**
	 * 挂断电话方法,安卓1.5版本以前可直接调用endCall(),但之后阉割掉了,现在利用反射调用方法
	 * 
	 * @Title: endCall
	 * @author JinHeng
	 * @date 2015年1月24日 上午11:41:44
	 * @throws
	 */
	private void endCall() {
		try {
			// 加载servicemanager的字节码
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
