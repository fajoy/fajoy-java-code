import java.util.*;
import java.util.regex.*;

public class RegexSample {
	public static void main(String[] args) {
		String pattern = "\\b(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\b";
		String input1 = "127.0.0.1 is localhost 192.168.0.1 is LAN";
		RegexHelper.printResult(pattern, input1);
	}

	public static class RegexHelper {
		public static Matcher getMatcher(String pattern, String input) {
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(input);
			return m;
		}

		public static String[] getSubString(String pattern, String input) {
			Matcher m = getMatcher(pattern, input);
			return getSubString(m);
		}

		public static String[] getSubString(Matcher matcher) {
			if (!matcher.find())
				return null;
			String str[] = new String[matcher.groupCount() + 1];
			for (int i = 0; i <= matcher.groupCount(); i++) {
				str[i] = matcher.group(i);
			}
			return str;
		}

		public static List<String[]> getSubStrings(String pattern, String input) {
			List<String[]> result = new ArrayList<String[]>();
			Matcher m = getMatcher(pattern, input);
			String[] str = getSubString(m);
			while (str != null) {
				result.add(str);
				str = getSubString(m);
			}
			return result;
		}

		public static void printResult(String pattern, String input) {
			List<String[]> result = RegexHelper.getSubStrings(pattern, input);
			for (String[] strings : result) {
				System.out.print(String.format("[%s", strings[0]));
				for (int i = 1; i < strings.length; i++) {
					System.out.print(String.format(",%s", strings[i]));
				}
				System.out.println("]");
			}
		}
	}
}
