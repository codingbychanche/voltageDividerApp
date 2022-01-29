package com.berthold.voltagedivider;

import android.content.res.Resources;
import android.text.Spanned;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

public class HTMLTools extends AppCompatActivity {

    public static String makeHeader(String t){ return "<b>"+t+"</b><p>";
    }

    public static String makeSolutionBlockSolutionFound(String t){
        return "<p>"+t+"</p>";
    }
}
