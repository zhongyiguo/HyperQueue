package com.iws.hq.service;

public class Product {
	private String messg;
	private long timeStamp;
	
	
	public Product(String msg,long timeStamp){
		this.setMessg(msg);
		this.setTimeStamp(timeStamp);
	}

	public String getMessg() {
		return messg;
	}

	public void setMessg(String messg) {
		this.messg = messg;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}


}
