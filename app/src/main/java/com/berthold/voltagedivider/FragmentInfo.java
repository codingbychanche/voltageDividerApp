package com.berthold.voltagedivider;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.berthold.voltagedivider.Main.CheckForNetwork;
import com.berthold.voltagedivider.Main.MainViewModel;

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

        Context context = getActivity().getApplicationContext();

        //
        // UI
        //
        final Handler handler = new Handler();
        webView = (WebView) view.findViewById(R.id.browser);
        updateInfoView = view.findViewById(R.id.info_new_version_available);
        progress = view.findViewById(R.id.html_load_progress);

        //
        // View Model
        //
        MainViewModel mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        //
        // Load html from assets- folder
        //
        progress.setVisibility(View.VISIBLE);

        // Get current App- version from Google play
        // Check if there is an update available
        currentVersion = GetThisAppsVersion.thisVersion(context);

        // @rem:Get current locale (determine language from Androids settings@@
        //final Locale current=getResources().getConfiguration().locale;
        final String current = getResources().getConfiguration().locale.getLanguage();

        //
        // Load according to locale
        //
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //
                // Active network available?
                //
                //
                // The following statement is true, when network is available, no ,matter if it is switched on or off!!!
                // This statement is noo good if one wants to check if a network connection is possible....
                if (CheckForNetwork.isNetworkAvailable(requireActivity().getApplicationContext())) {
                    final String latestVersionInGooglePlay = getAppVersionfromGooglePlay(requireActivity().getApplicationContext());

                    if (latestVersionInGooglePlay != "-") {
                        if (latestVersionInGooglePlay.equals(currentVersion)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //updateInfoView.setText(getResources().getText(R.string.version_info_is_latest_version));
                                    updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.version_info_ok) + "", 0));
                                }
                            });

                            // OK, could connect to network, could connect to google plays store listing of the app, get version.
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.version_info_update_available) + latestVersionInGooglePlay, 0));
                                }
                            });
                        }

                    } else
                        // Network was available but, could not retrieve version info from google plays store listing of this app.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.no_version_info_available) + "", 0));
                            }
                        });
                } else
                    // No network connection available or network disabled on device.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.no_network) + "", 0));
                        }
                    });
            }
        });
        t.start();

        //
        // Show info text according to the current locale...
        //
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mainViewModel.currentGFragmentShown.getValue().equals("Info")) { // Only if this fragment is visible to the user!
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


                    //
                    // Show info text.
                    //
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadData(htmlSite.toString(), "text/html", null);
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        t2.start();
    }

    /**
     * Returns the version from the app's Google Play store listing...
     *
     * @param c
     * @return A String containing the version tag.
     */
    public String getAppVersionfromGooglePlay(Context c) {
        String latest;


        VersionChecker vc = new VersionChecker();

        try {
            latest = vc.execute().get();
        } catch (Exception e) {
            latest = "-";
        }
        return latest;

    }
}