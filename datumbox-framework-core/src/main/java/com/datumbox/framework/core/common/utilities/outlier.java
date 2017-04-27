package com.datumbox.framework.core.common.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cventdev on 4/20/17.
 */
public class outlier {
    private static List<String> outLiers = Arrays.asList(
            "can",
            "could",
            "may",
            "might",
            "will",
            "would",
            "shall",
            "should",
            "must",
            "have",
            "this",
            "then",
            "that");

    public static List<String> getOutLiers() {
        return outLiers;
    }
}
