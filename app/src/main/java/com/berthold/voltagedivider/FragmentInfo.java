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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;

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
        // Play core library
        // Checks if there is a newer version of this app available.
        //
        // todo: test of play core library
        // This is a test....
        //
        if (CheckForNetwork.isNetworkAvailable(getActivity().getApplicationContext())) {

            final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(requireActivity().getApplicationContext());
            // Returns an intent object that you use to check for an update.
            final Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo result) {
                    Log.v("UPDATEUPDATE","Invoked");
                    if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        updateInfoView.setText(HtmlCompat.fromHtml(getResources().getText(R.string.version_info_update_available_short) +"", 0));

                    } else {
                        updateInfoView.setText(getResources().getText(R.string.version_info_ok));
                    }
                }
            });
        } else {
            updateInfoView.setText(getResources().getText(R.string.no_network));
        }

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