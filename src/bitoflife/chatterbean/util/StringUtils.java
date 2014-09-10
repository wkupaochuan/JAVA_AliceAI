package bitoflife.chatterbean.util;


public class StringUtils extends org.apache.commons.lang.StringUtils {

	public static String StringAddSpace(String input) {
		// 文加空格处理
		StringBuffer temp = new StringBuffer();
		boolean flag = false;
		for (int i = 0; i < input.length(); i++) {
			java.util.regex.Pattern p = java.util.regex.Pattern
					.compile("^[\u4E00-\u9FA5|\\*]+$");
			java.util.regex.Matcher m = p.matcher(String.valueOf(input
					.charAt(i)));
			if (m.matches()) {
				if (flag) {
					temp.append(" ");
				}
				temp.append(input.charAt(i)).append(" ");
				flag = false;
			} else {
				if ("*".equals(input.charAt(i))) {
					if (flag) {
						temp.append(" ");
					}
					temp.append(input.charAt(i)).append(" ");
					flag = false;
				} else {
					temp.append(input.charAt(i));
					flag = true;
				}
			}
		}
		input = temp.toString();
		return input;
	}
}
