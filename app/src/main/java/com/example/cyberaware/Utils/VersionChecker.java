package com.example.cyberaware.Utils;

import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;
/*
    Creates a jquery to play store site for versions stored within the html
    Only has some versions. so was not used.
 */
public class VersionChecker extends AsyncTask<String, String, String> {

    String latestVersion,packageName;

    protected void setPackageName(String pName) {
        packageName = pName;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en")
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
        return latestVersion;
    }
}
