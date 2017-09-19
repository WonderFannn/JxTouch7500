package com.jinxin.jxtouchscreen.cmd.response;

import android.text.TextUtils;


import com.jinxin.jxtouchscreen.model.ZigbeeResponse;
import com.jinxin.jxtouchscreen.util.L;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 结果解析工厂
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class ResponseParserFactory {
	/**
	 * 解析结果
	 * @param response 返回结果
	 * @return 字符串解析后的对象
	 */
	public static ZigbeeResponse parseContent(String response, String regex) {
		ZigbeeResponse wcResponse = new ZigbeeResponse();
		if(TextUtils.isEmpty(response) || TextUtils.isEmpty(regex) || response.length() < 3) {
			L.d(null, "response is empty");
			wcResponse.setPayload("-1 -1  获取失败 ");
			return wcResponse;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(response);
		Map<String, String> contenMap = setValue(matcher);
		wcResponse = setDeclaredFields(wcResponse, contenMap);
		
		return wcResponse;
	}

	/**
	 * 解析匹配结果，放入到map集合
	 */
	private static Map<String, String> setValue(Matcher matcher) {
		Map<String, String> contenMap = new HashMap<String, String>();
		if (matcher != null && matcher.find()) {
			int count = matcher.groupCount();
			for (int i = 1; i <= count; i++) {
				contenMap.put(matcher.group(i), matcher.group(i + 1));
				i++;
			}
		}
		return contenMap;
	}

	/**
	 * 通过反射将map中的值设置到对象
	 */
	private static ZigbeeResponse setDeclaredFields(ZigbeeResponse response, Map<String, String> contentMap) {
		try {
			Field[] field = response.getClass().getDeclaredFields();
			for (int i = 0; i < field.length; i++) {
				String fieldName = field[i].getName();
				if (contentMap.containsKey(fieldName)) {
					String methodName = "set" + fieldName.substring(0,1).toUpperCase(Locale.US) 
							+ fieldName.substring(1);
					Method method = response.getClass().getMethod(methodName, String.class);
					method.invoke(response, contentMap.get(fieldName));
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return response;
	}
}
