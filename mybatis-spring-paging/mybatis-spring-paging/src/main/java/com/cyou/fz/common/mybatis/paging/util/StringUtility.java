/**
 * ===========================================
 * Project: cyou-framework
 * ===========================================
 * Package: com.cyou.framework.baseutil
 * 
 * Copyright (c) 2012, CYOU All Rights Reserved.
 * ===========================================
 */
package com.cyou.fz.common.mybatis.paging.util;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>StringUtility</p>
 * <p>
 * Description:
 * extends the StringsUtils of commons-lang3
 * </p>
 *
 * @since 1.0
 * @version 1.0, 2012-11-29
 * @author zhufu
 */
public class StringUtility extends StringUtils {
	
	/**
     * <p>{@code StringUtility} instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * {@code StringUtility.trim(" foo ");}.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
	public StringUtility() {
		super();
	}
	
	/**
	 * <p>Removes control characters, including whitespace, from both ends 
	 * of this string, handling null by returning an empty string.</p>
	 * 
	 * @deprecated please Use {@code StringUtility.trimToEmpty()} for instead.
	 * @param str the String to be trimmed, may be {@code null}
	 * @return the trimmed String, or an empty String if {@code null} input
	 * @since 1.0
	 */
	@Deprecated
	public static String clean(String str) {
		// TDOO need delete
		return trimToEmpty(str);
	}
	
	/**
	 * <p>Concatenates elements of an array into a single string. The difference
	 * from join is that concatenate has no delimiter.</p>
	 * 
	 * @deprecated please Use {@code StringUtility.join(T... elements)} for instead.
	 * @param <T> the specific type of values to join together
	 * @return the joined concatenated, {@code null} if null array input
	 * @since 1.0
	 */
	@Deprecated
	public static String concatenate(Object[] array) {
		return join(array, "");
	}
	
	/**
	 * <p>Checks if the email string is a legal Email.</p>
	 * 
	 * @param email the email string to check
	 * @return true or false
	 */
	public static boolean isEmail(String email) {
		if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
			return false;
		}
		return true;
	}
	
	/**
	 * <p>Checks if the CharSequence is an IPv4 Address.</p>
	 * 
	 * @param cs the IPv4 Address CharSequence to check
	 * @return true or false
	 */
	public static boolean isIPAddr(String ipAddr) {
		if (isBlank(ipAddr)) {
			return false;
		}
		
		String[] ipValue = split(ipAddr, ".");
		int length = ipValue.length;
		if (length != 4) {
			return false;
		}
		
		for (int i = 0; i < length; i++) {
			String tempStr = ipValue[i];
			if (!isNumeric(tempStr)) {
				return false;
			}
			int tempInt = Integer.parseInt(tempStr);
			if (tempInt < 0 || tempInt > 255) {
				return false;
			}
		}
		return true;
	}
	
	// TODO convertHTML, escape, parseStringToMoney
	// TODO escapeSQLike, getStrByteLen, formatExcelString

}
