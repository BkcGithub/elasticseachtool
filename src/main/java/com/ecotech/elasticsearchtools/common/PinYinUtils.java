package com.ecotech.elasticsearchtools.common;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class PinYinUtils {
    public static String getPinyin(String words) {
        return PinyinHelper.convertToPinyinString(words, "", PinyinFormat.WITHOUT_TONE);
    }

    public static String getPinyinShort(String words) {
        return PinyinHelper.getShortPinyin(words);
    }
}
