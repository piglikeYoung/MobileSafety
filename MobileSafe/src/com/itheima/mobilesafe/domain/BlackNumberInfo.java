package com.itheima.mobilesafe.domain;

/**
 * 黑名单号码的业务bean
 * 
 * @ClassName: BlackNumberInfo
 * @author JinHeng
 * @date 2015年1月9日 上午10:32:46
 */
public class BlackNumberInfo {

	private String number;
	private String mode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}
}
