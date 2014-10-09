package com.iws.hq.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class APIResponse<T> {

	private ResponseStatus status;

	private T responseObj;

	private Set<String> errors;

	public APIResponse() {
	}

	public APIResponse(ResponseStatus status, Set<String> errors, T responseObj) {
		this.status = status;
		this.errors = errors;
		this.responseObj = responseObj;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public T getResponseObj() {
		return responseObj;
	}

	public void setResponseObj(T responseObj) {
		this.responseObj = responseObj;
	}

	public Set<String> getErrors() {
		return errors;
	}

	public void setErrors(Set<String> errors) {
		this.errors = errors;
	}
	
	@JsonIgnore
	public APIResponse<String> getSimpleSuccessResponse(String str) {
		APIResponse<String> res = new APIResponse<String>();
		res.setResponseObj(str);
		res.setStatus(ResponseStatus.SUCCESS);
		return res;
	}

	@JsonIgnore
	public static APIResponse<String> getSimpleSuccessResponse() {
		return successResponse("success");
	}

	@JsonIgnore
	public static <T> APIResponse<T> successResponse(T responseObj) {
		return new APIResponse<T>(ResponseStatus.SUCCESS, null, responseObj);
	}

	@JsonIgnore
	public static <T> APIResponse<T> failResponse(Class<T> clazz, String... errors) {
		return new APIResponse<T>(ResponseStatus.FAIL, new HashSet<String>(Arrays.asList(errors)), null);
	}

	@JsonIgnore
	public static <T> APIResponse<T> validationFailResponse(Class<T> clazz, String... errors) {
		return new APIResponse<T>(ResponseStatus.VALIDATION_ERROR, new HashSet<String>(Arrays.asList(errors)), null);
	}

	@JsonIgnore
	public static <T> APIResponse<T> exceptionResponse(Class<T> clazz, String... errors) {
		return new APIResponse<T>(ResponseStatus.EXCEPTION, new HashSet<String>(Arrays.asList(errors)), null);
	}

	@JsonIgnore
	public static <T> APIResponse<List<T>> exceptionResponseForList(Class<T> clazz, String... errors) {
		return new APIResponse<List<T>>(ResponseStatus.EXCEPTION, new HashSet<String>(Arrays.asList(errors)), null);
	}
}
