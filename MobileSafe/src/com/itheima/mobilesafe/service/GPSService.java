package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {
	// 用到位置服务
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class MyLocationListener implements LocationListener {

		/**
		 * 当位置改变的时候回调
		 */
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a" + location.getAccuracy() + "\n";

			// 发短信给安全号码

			// 把标准的GPS坐标转换成火星坐标
			// InputStream is;
			// try {
			// is = getAssets().open("axisoffset.dat");
			// ModifyOffset offset = ModifyOffset.getInstance(is);
			// PointDouble double1 = offset.s2c(new PointDouble(location
			// .getLongitude(), location.getLatitude()));
			// longitude ="j:" + offset.X+ "\n";
			// latitude = "w:" +offset.Y+ "\n";
			//
			// } catch (IOException e) {
			// e.printStackTrace();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude + latitude + accuracy);
			editor.commit();

		}

		/**
		 * 当状态发生改变的时候回调 开启--关闭 ；关闭--开启
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		/**
		 * 某一个位置提供者可以使用了
		 */
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		/**
		 * 某一个位置提供者不可以使用了
		 */
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

}
