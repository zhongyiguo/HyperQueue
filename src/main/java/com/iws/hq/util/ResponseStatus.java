package com.iws.hq.util;


public enum ResponseStatus {
	
	SUCCESS ("success"),
	FAIL ("fail"),
	VALIDATION_ERROR ("error"), 
	EXCEPTION ("exception");
	
	private String v;
	
	ResponseStatus(String v){
		this.v = v;
	}
	
	public String getValue(){
		return v;
	}
	
}
