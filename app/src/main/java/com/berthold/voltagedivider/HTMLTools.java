package com.berthold.voltagedivider;

import android.text.Spanned;

import androidx.core.text.HtmlCompat;

public class HTMLTools {
    public static String makeHeader(String t){
        return "<b>"+t+"</b>";
    }

    public static String makeSolutionBlockSolutionFound(String t){
        return "<p>"+t+"</p>";
    }
}
