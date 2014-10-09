package com.iws.hq.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Static methods for converting objects to/from a JSON string.
 */
public class JsonUtil {
	private JsonUtil() {
		// do not need to instantiate
	}

	/**
	 * Convert Object to JSON
	 * @param obj the object need to convert to JSON
	 * @return a JSON string representing the object
	 */
	public static String getSimpleObjAsJSON(Object obj) {
		if (obj == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Hibernate4Module());
		try {
			return mapper.writeValueAsString(obj);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convert an object to JSON with type info in order to support polymorphism. 
	 */
	public static String getJsonWithTypeInfo(Object obj) {
		if (obj == null) {
			return null;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(); // defaults for defaults (see below); include as wrapper-array, non-concrete types
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT); // all non-final types
		mapper.registerModule(new Hibernate4Module());
		
		try {
			return mapper.writeValueAsString(obj);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Convert a JSON with type info back into an object.
	 */
	public static <T> T getObject(String json, Class<T> clazz) {
		if (json == null || json.length() == 0) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(); // defaults for defaults (see below); include as wrapper-array, non-concrete types
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT); // all non-final types
		mapper.registerModule(new Hibernate4Module());
		try {
			return mapper.readValue(json, clazz);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convert JSON to Object
	 * @param str the JSON string
	 * @param cls the expect class
	 * @return the expect class object
	 */
	public static <T> T getObjectFromJSON(String str, Class<T> cls) {
		if (str == null || str.length() == 0) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Hibernate4Module());
		try {
			return mapper.readValue(str, cls);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
