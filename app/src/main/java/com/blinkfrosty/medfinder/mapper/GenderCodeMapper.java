package com.blinkfrosty.medfinder.mapper;

import java.util.HashMap;
import java.util.Map;

public class GenderCodeMapper {

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String OTHER = "Other";
    public static final String MALE_CODE = "m";
    public static final String FEMALE_CODE = "f";
    public static final String OTHER_CODE = "o";

    private static final Map<String, String> stringToCodeMap = new HashMap<>();
    private static final Map<String, String> codeToStringMap = new HashMap<>();

    static {
        stringToCodeMap.put(MALE, MALE_CODE);
        stringToCodeMap.put(FEMALE, FEMALE_CODE);
        stringToCodeMap.put(OTHER, OTHER_CODE);

        codeToStringMap.put(MALE_CODE, MALE);
        codeToStringMap.put(FEMALE_CODE, FEMALE);
        codeToStringMap.put(OTHER_CODE, OTHER);
    }

    public static String getCode(String genderString) {
        return stringToCodeMap.get(genderString);
    }

    public static String getString(String genderCode) {
        return codeToStringMap.get(genderCode);
    }
}