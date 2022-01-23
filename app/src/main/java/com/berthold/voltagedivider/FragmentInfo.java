package com.berthold.voltagedivider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FragmentInfo extends Fragment {

    // Debug
    private String tag;

    // Filesystem
    private BufferedReader bufferedReader;

    // Html
    private StringBuilder htmlSite;
    private TextView updateInfoView;
    private WebView webView;
    private ProgressBar progress;

    // Version info
    private String currentVersion;

    // View model
    private MainViewModel mainViewModel;

    public static FragmentInfo newInstance() {
        return new FragmentInfo();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        // UI
        //
        final Handler handler = new Handler();
        webView = (WebView) view.findViewById(R.id.browser);
        updateInfoView=view.findViewById(R.id.info_new_version_available);
        progress=view.findViewById(R.id.html_load_progress);

        //
        // Load html from assets- folder
        //
        progress.setVisibility(View.VISIBLE);


        // @rem:Get current locale (determine language from Androids settings@@
        //final Locale current=getResources().getConfiguration().locale;
        final String current = getResources().getConfiguration().locale.getLanguage();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    htmlSite = new StringBuilder();

                    // @rem:Shows how to load data from androids 'assests'- folder@@
                    if (current.equals("de") || current.equals("en")) {
                        if (current.equals("de"))
                            bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("InfoPage-de.html")));
                        if (current.equals("en"))
                            bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("InfoPage-en.html")));
                    } else
                        bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("InfoPage-en.html")));

                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                        htmlSite.append(line);

                } catch (IOException io) {
                    Log.v("Info", io.toString());
                }

                // Show
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        webView.loadData(htmlSite.toString(), "text/html", null);

                        /*
                        String latestVersionInGooglePlay = mainActivityViewModel.getAppVersionfromGooglePlay(getApplicationContext());

                        if (latestVersionInGooglePlay.equals(currentVersion)) {
                            //updateInfoView.setText(getResources().getText(R.string.version_info_is_latest_version));
                            updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.version_info_ok) + "", 0));
                        } else
                            updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.version_info_update_available) + latestVersionInGooglePlay, 0));
                            */

                    }
                });
            }
        });
        t.start();
    }
}