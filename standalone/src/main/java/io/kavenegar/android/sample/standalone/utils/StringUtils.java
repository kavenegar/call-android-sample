package io.kavenegar.android.sample.standalone.utils;

public class StringUtils {
    public static String getOnlyNumerics(String text) {
        return text.replaceAll("[^0-9]", "");
    }

    public static String getOnlyNumericsWithPlus(String text) {
        return text.replaceAll("[^0-9,+]", "");
    }

    public static String normalizeMobileNumber(String mobileNumber, String defaultCountryCode) {
        mobileNumber = getOnlyNumericsWithPlus(mobileNumber);
        if (mobileNumber.startsWith("0")) {
            mobileNumber = mobileNumber.substring(1);
        }
        if (!mobileNumber.startsWith("+")) {
            mobileNumber = defaultCountryCode + mobileNumber;
        }

        return mobileNumber;
    }

    public static String capitalize(String str){
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
