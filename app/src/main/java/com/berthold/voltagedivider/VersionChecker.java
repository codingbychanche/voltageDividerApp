package com.berthold.voltagedivider;

import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Version checker.
 * <p>
 * Retrieves the version of this app from the Google Play- Store.
 * <p>
 * Source: https://stackoverflow.com/questions/34309564/how-to-get-app-market-version-information-from-google-play-store
 */
public class VersionChecker extends AsyncTask<String, String, String> {

    String newVersion;

    @Override
    protected String doInBackground(String... params) {

        // ToDO Change url when this app is available at the play store....
        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.berthold.voltagedivider" + "&hl=de")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (newVersion == null)
            return "-";
        else
            return newVersion;
    }
}
