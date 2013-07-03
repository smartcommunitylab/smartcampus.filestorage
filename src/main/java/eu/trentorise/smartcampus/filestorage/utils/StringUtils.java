package eu.trentorise.smartcampus.filestorage.utils;

public class StringUtils {

	public static boolean isNullOrEmpty(String input, boolean emptyIfBlank) {
		return input == null
				|| ((emptyIfBlank) ? input.trim().length() == 0 : input
						.length() == 0);
	}

}
