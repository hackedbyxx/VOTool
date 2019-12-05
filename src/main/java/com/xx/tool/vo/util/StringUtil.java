package com.xx.tool.vo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串处理工具类
 */
public class StringUtil {
	
	//手机号码正则表达式
	public static Pattern mobilePhone = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    public static final String SPACE = " ";
    
	public static final String EMPTY = "";
	
    public static final int INDEX_NOT_FOUND = -1;
    
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    public static final int PAD_LIMIT = 8192;
	
    /**
	 * 	下划线转驼峰法
	 * @param line       源字符串
	 * @param smallCamel 大小驼峰,是否为小驼峰
	 * @return 转换后的字符串
	 */
	public static String underline2Camel(String line, boolean smallCamel) {
		if (line == null || "".equals(line)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
					: Character.toUpperCase(word.charAt(0)));
			int index = word.lastIndexOf('_');
			if (index > 0) {
				sb.append(word.substring(1, index).toLowerCase());
			} else {
				sb.append(word.substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * 	驼峰法转下划线
	 * @param line 源字符串
	 * @return 转换后的字符串
	 */
	public static String camel2Underline(String line) {
		if (line == null || "".equals(line)) {
			return "";
		}
		line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(word.toUpperCase());
			sb.append(matcher.end() == line.length() ? "" : "_");
		}
		return sb.toString();
	}
    
	/**
	 * 清除第一个换行,制表符
	 * @param panelNum
	 * @return
	 */
	public static String processPanelNum(String panelNum) {
		
		if (panelNum == null || panelNum.trim().length() == 0) {
			return panelNum;
		}
		
		StringBuffer buf = new StringBuffer(panelNum);
		int i = 0;
		if ((i = buf.indexOf("\r")) != -1) {
			buf.delete(i, buf.length());
		}
		if ((i = buf.indexOf("\n")) != -1) {
			buf.delete(i, buf.length());
		}
		if ((i = buf.indexOf("\t")) != -1) {
			buf.delete(i, buf.length());
		}
		return buf.toString();
	}
	
	/**
	 * 判断字符串是否全为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();   
	 }
	
	/**
	 * 判断字符串是否为浮点数
	 * @param str
	 * @return
	 */
	public static boolean isFloat(String str) {
		Pattern pattern = Pattern.compile("^[+-]?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$");
		return pattern.matcher(str).matches();   
	}
	
	/**
	 * 判断字符串是否为整数
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[+-]?[0-9]\\d*|0$");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 判断字符串是否为空，为空是设置默认值
	 * @param str
	 * @param defaultStr
	 * @return
	 */
	public static String defaultIfBlank(String str, String defaultStr){
		return isBlank(str) ? defaultStr : str;
	}
	
	/**
	 * 判断字符串是否为空(包含空格)
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
	    int strLen;
	    if ((str == null) || ((strLen = str.length()) == 0))
	      return true;
	    strLen = str.length();
	    for (int i = 0; i < strLen; i++) {
	     if (!Character.isWhitespace(str.charAt(i))) {
	        return false;
	      }
	    }
	    return true;
	 }
	
	/**
	 * 判断字符串是否非空(包含空格)
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str){
	    return !isBlank(str);
	 }
	
	public static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	 
	/**
	 * 判断是不是一个合法的电子邮件地址
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
	    if(isBlank(email)) {
	    	return false;
	    }
	    return emailer.matcher(email).matches();
	}
	
	
	
	/**
	 * 判断是不是一个合法的手机号码
	 * @param email
	 * @return
	 */
	public static boolean isMobilePhone(String phoneNumber){
	    if(isBlank(phoneNumber)) {
	    	return false;
	    }
	    return mobilePhone.matcher(phoneNumber).matches();
	}

	
	public static String toString(Object obj){
		return obj == null ? null : obj.toString().trim();
	}
	
	public static String replaceStartStrToBlank(String sourceStr, String start){
		if(isBlank(sourceStr) || isBlank(start)){
			return sourceStr;
		}
		if(sourceStr.startsWith(start)){
			return sourceStr.substring(sourceStr.indexOf(start)+start.length());
		}
		return sourceStr;
	}
	
	public static String replaceEndStrToBlank(String sourceStr, String end){
		if(isBlank(sourceStr) || isBlank(end)){
			return sourceStr;
		}
		if(sourceStr.endsWith(end)){
			return sourceStr.substring(0,sourceStr.indexOf(end));
		}
		return sourceStr;
	}
	
	public static String captureStr(String str) {
        char[] cs=str.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
		        
	}
	
	/**
	 * 首字母大写其他字母小写 
	 * @param str
	 * @return
	 */
	public static String upperFirstCase(String str) {  
		if(isBlank(str)){
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase(); 
	}  
	
	/**
	 * 首字母及下划线后的首字母大写其他字母小写
	 * @param str
	 * @return
	 */
	public static String upperFirstSeqenceCase(String str) { 
		if(isBlank(str)){
			return str;
		}
		String[] subStrs = str.split("_");
		StringBuilder result = new StringBuilder();
		for(String subStr : subStrs){
			result.append(upperFirstCase(subStr));
		}
		return result.toString(); 
	}  

	/**
	 * 判断字符串是否为空(不包含空格)
	 * @param cs
	 * @return
	 */
	public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
	
	/**
	 * 判断字符串是否为空(不包含空格)
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		return (str == null || EMPTY.equals(str));
	}
	
	/**
	 *: 判断字符串是否为空(包含空格)
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null || EMPTY.equals(str.trim()));
	}
	
	
	/**
	 * 判断字符串是否非空(不包含空格)
	 * @param cs
	 * @return
	 */
	public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
	
	/**
	 * 判断字符串是否非空(不包含空格)
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}
	
	/**
	 * 连接数组中的元素字符串
	 * @param array 连接元素字符串的数组, 可以为空
	 * @return
	 */
	public static String join(Object[] array) {
        return join(array, null);
    }
	
	/**
	 * 连接数组中的元素字符串,以连接符号间隔
	 * @param array 连接元素字符串的数组, 可以为空
	 * @param separator 连接符号，null处理为""
	 * @return
	 */
	public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }
	
	/**
	 * 连接数组中指定范围的元素字符串,以连接符号间隔
	 * @param array 连接元素字符串的数组, 可以为空
	 * @param separator 连接符号，null处理为""
	 * @param startIndex 数组开始下标 
	 * @param endIndex 数组结束下标
	 * @return
	 */
	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                        + separator.length());

        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
	
	/**
	 * 字符串不为空时返回原值，为空时返回"" 
	 * @param str 字符串
	 * @return
	 */
	public static String defaultString(String str) {
        return str == null ? EMPTY : str;
    }

	/**
	 * 字符串不为空时返回原值，为空时返回指定默认字符串
	 * @param str 字符串
	 * @param defaultStr 指定默认字符串
	 * @return
	 */
	public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }
	
	/**
	 * 字符串不为空或""时返回原值，为空或""时返回指定默认字符串
	 * @param str 字符串
	 * @param defaultStr 指定默认字符串
	 * @return
	 */
	public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
	
	
	/**
	 * object转换字符串
	 * @param obj 转换字符串的对象 
	 * @return
	 */
	public static String objToString(Object obj) {
        return obj == null ? EMPTY : obj.toString();
    }
	
	/**
	 * object转换字符串
	 * @param obj 转换字符串的对象 
	 * @param nullStr 为空时默认字符串
	 * @return
	 */
	public static String objToString(Object obj, String nullStr) {
        return obj == null ? nullStr : obj.toString();
    }
	
	/**
	 * 移除字符串末尾空格，为null时返回null 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
        return str == null ? null : str.trim();
    }
	
	/**
	 * 移除字符串末尾空格，为null或者""返回null 
	 * @param str
	 * @return
	 */
	public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }
	
	/**
	 * 移除字符串末尾空格， 为null时返回""
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }
	
	/**
	 * 移除字符串的所有空格
	 * @param str
	 * @return
	 */
	public static String trimAllWhitespace(String str) {
		if (str == null || str.length()<=0) {
			return str;
		}

		int len = str.length();
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 截取指定符号前的字符串
	 * @param str 字符串
	 * @param separator 指定符号
	 * @return
	 */
	public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }
	
	/**
	 * 截取指定起止字符串范围中的子字符串  
	 * @param str 字符串
	 * @param open 起始字符串
	 * @param close  截止字符串
	 * @return
	 */
	public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
	
	/**
	 * 截取指定起止字符串范围中的子字符串数组  
	 * @param str 字符串
	 * @param open 起始字符串
	 * @param close 截止字符串
	 * @return
	 */
	public static String[] substringsBetween(final String str, final String open, final String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<String>();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.toArray(new String [list.size()]);
    }
	
	/**
	 * 字符串左边补充指定长度的字符串
	 * @param str  字符串
	 * @param size 填充字符的次数
	 * @param padStr 填充字符串
	 * @return
	 */
	public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (StringUtil.isEmpty(padStr)) {
            padStr = StringUtil.SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= StringUtil.PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }
	
	/**
	 * 字符串左边补充指定长度的字符
	 * @param str 字符串
	 * @param size 填充字符的次数
	 * @param padChar 填充字符
	 * @return
	 */
	public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > StringUtil.PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }
	
	/**
	 * 返回指定重复次数字符的字符串  
	 * @param ch 字符
	 * @param repeat 重复次数
	 * @return
	 */
	public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return StringUtil.EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
	
	/**
	 * 两个字符串比较
	 * @param str1
	 * @param str2
	 * @return
	 * @author SN34931
	 */
	public static boolean equals(String str1, String str2){
		if (str1 != null && str1.equals(str2)) {
			return true;
		} else if (str2 != null && str2.equals(str1)) {
			return true;
		} else if (str1 == null && str2 == null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取字符串长度
	 * @param cs
	 * @return
	 */
	public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
	
	/**
	 * 截取指定字符串之后的字符串
	 * @param str
	 * @param separator
	 * @return
	 */
	public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }
	
	/**
	 * 所有反斜杠装换为正斜杠
	 * @param str
	 * @return
	 */
	public static String replaceAllBackslant2Slant(String str){
	    if(isEmpty(str)){
	        return "";
	    }
	    String temp = str.replaceAll("\\\\\\\\", "").replaceAll("\\\\", "/").trim();
	    if(temp.endsWith("//")){
	        temp = temp.substring(0, temp.length()-1);
	    }
	    
	    return temp.endsWith("/")?temp:temp+"/";
	}
	
	/**
	 * 获取最后一个指定分割符前的子字符串
	 * @param str
	 * @param separator
	 * @return
	 */
	public static String substringBeforeLast(final String str, final String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }
	
	/**
	 * 获取随机唯一uuid
	 * @return
	 */
	public static String getRandomUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	
	public static void main(String[] args) {
//		String s = ",dffd,";
//		System.out.println(replaceStartStrToBlank(s,null));
//		System.out.println(replaceEndStrToBlank(s,"dffd"));
		
//		System.out.println(upperFirstSeqenceCase("m_mm"));
//		String aa = null;
//		System.out.println(StringUtil.objToString(aa));
//		System.out.println(aa);
		
			
	}
}
