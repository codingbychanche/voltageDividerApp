package com.berthold.voltagedivider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements FragmentYesNoDialog.getDataFromFragment {

    // Debug
    private String tag;

    // View model
    public MainViewModel mainViewModel;

    // Shared
    SharedPreferences sharedPreferences;

    // Yes No Dialog
    private static final int CONFIRM_DIALOG_CALLS_BACK_FOR_PERMISSIONS = 1;
    private static final int CONFIRM_DIALOG_CALLS_BACK_FOR_UPDATE = 2;

    /**
     * Let there be light....
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Debug
        tag = getClass().getSimpleName();
        Long time = System.currentTimeMillis();

        //
        // View model
        //
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //
        // Locale for use in view models
        //
        Locale loc=new Locale();
        loc.setProtocolStartText(getApplicationContext().getResources().getString(R.string.protocol_start_text));
        mainViewModel.loc=loc;

        //
        // Check if there is a newer version of this app available at the play store.
        //
        if (showUpdateInfo()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                    }

                    String currentVersion = GetThisAppsVersion.thisVersion(getApplicationContext());
                    String latestVersionInGooglePlay = mainViewModel.getAppVersionfromGooglePlay(getApplicationContext());

                    if (!latestVersionInGooglePlay.equals(currentVersion)) {
                        saveTimeUpdateInfoLastOpened();
                        String dialogText = getResources().getString(R.string.dialog_new_version_available) + " " + latestVersionInGooglePlay;
                        String ok = getResources().getString(R.string.do_update_confirm_button);
                        String cancel = getResources().getString(R.string.no_udate_button);
                        showConfirmDialog(CONFIRM_DIALOG_CALLS_BACK_FOR_UPDATE, FragmentYesNoDialog.SHOW_AS_YES_NO_DIALOG, dialogText.toString(), ok, cancel);
                    }
                }
            });
            t.start();
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        Button findResistorView = findViewById(R.id.findResistor);
        findResistorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainViewModel.currentGFragmentShown.getValue().equals("FindResistor"))
                    mainViewModel.currentGFragmentShown.setValue("FindResistor");
            }
        });

        Button calcDividerView = findViewById(R.id.divider);
        calcDividerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainViewModel.currentGFragmentShown.getValue().equals("Divider"))
                    mainViewModel.currentGFragmentShown.setValue("Divider");
            }
        });

        ImageView showInfo = findViewById(R.id.info_fragment_show_info);
        showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainViewModel.currentGFragmentShown.getValue().equals("Info"))
                    mainViewModel.currentGFragmentShown.setValue("Info");
            }
        });

        TextView protocolView = findViewById(R.id.protocol_view);
        //
        // clear the protocol view....
        //
        Button clearProtocol = findViewById(R.id.clear_protocol);
        clearProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                protocolView.setText("");
            }
        });

        /*
        //
        // Share the protocol...
        //
        Button shareProtocol=findViewById(R.id.share_protocol);
        shareProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"<b>"+protocolView.getText().toString()+"\n");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        */

        //
        // Put protocol to clipboard
        //
        ImageButton toClipBoard = findViewById(R.id.protocol_to_clipboard);
        toClipBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", protocolView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "..", Toast.LENGTH_LONG).show();
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Observers
        //
        // Gets and displays the current fragment (e.g. calculate voltage divider, find resistor)
        // inside the appropriate fragment container.
        //
        final Observer<String> fragmentCurtlyShownObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String fragmentCurrentlyShown) {

                // Shows the fragment which allows one to find a resistor closest to the
                // selected value.
                if (fragmentCurrentlyShown.equals("FindResistor")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_for_current, FragmentFindResistor.newInstance())
                            .commitNow();
                }

                // Show the fragment which allows one to calculate a voltage divider....
                if (fragmentCurrentlyShown.equals("Divider")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_for_current, FragmentDivider.newInstance())
                            .commitNow();
                }

                // Show the info fragment
                if (fragmentCurrentlyShown.equals("Info")) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_for_current, FragmentInfo.newInstance())
                            .commitNow();
                }
            }
        };
        mainViewModel.getCurrentGFragmentShown().observe(this, fragmentCurtlyShownObserver);

        //
        //  Updates the protocol view
        //
        mainViewModel.getProtokollOutput().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                protocolView.append(HtmlCompat.fromHtml(s, 0));
                Log.v("CONTEXT_",s+"=");
            }
        });
    }

    /**
     * Saves timestamp when update info was shown and
     * a boolean set to true which tells us that the
     * info has been shown at least once.
     */
    private void saveTimeUpdateInfoLastOpened() {
        Long currentTime = System.currentTimeMillis();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastUpdateInfo", currentTime);
        editor.putBoolean("hasBeenShownAtLastOnce", true);
        editor.commit();
    }

    /**
     * Checks if update info is allowed to be shown again.
     *
     * @return true if allowed, false if not.
     */
    private boolean showUpdateInfo() {
        sharedPreferences = getPreferences(MODE_PRIVATE);

        // If the update info has not been shown at least once, then
        // show it now, for the first time and as a result, also save the
        // time last shown for the first time.
        Boolean hasBeenShowedAtLeastOnce = sharedPreferences.getBoolean("hasBeenShownAtLastOnce", false);
        if (!hasBeenShowedAtLeastOnce)
            return true;    // Don't show

        // Update info has been shown at least for one time. So check
        // when this was and if enough time has passed to allow it
        // to be shown again....
        Long currentTime = System.currentTimeMillis();

        // Time in Millisec. which has to be passed until update info is
        // allowed to be shown again since the  last time it
        // appeared on the screen;
        int timeDiffUntilNextUpdateInfo = 8 * 60 * 60 * 1000; // Show once every eight hours.....

        Long lastTimeOpened = sharedPreferences.getLong("lastUpdateInfo", currentTime);

        Log.v("TIMETIME", " Last:" + lastTimeOpened + "   current:" + currentTime + "  Diff:" + (currentTime - lastTimeOpened));

        if ((currentTime - lastTimeOpened) > timeDiffUntilNextUpdateInfo)
            return true;    // Show
        else
            return false;   // Don't show
    }

    /**
     * Shows a confirm dialog.
     *
     * @param reqCode
     * @param kindOfDialog
     * @param dialogText
     * @param confirmButton
     * @param cancelButton
     */
    private void showConfirmDialog(int reqCode, int kindOfDialog, String dialogText, String confirmButton, String cancelButton) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentYesNoDialog fragmentShowYesNo =
                FragmentYesNoDialog.newInstance(reqCode, kindOfDialog, null, dialogText, confirmButton, cancelButton);
        fragmentShowYesNo.show(fm, "fragment_dialog");
    }

    /**
     * Callback for {@link FragmentYesNoDialog} events.
     *
     * @param reqCode
     * @param dialogTextEntered
     * @param buttonPressed
     */
    @Override
    public void getDialogInput(int reqCode, String dialogTextEntered, String buttonPressed) {
        //
        // Update this app?
        //
        if (reqCode == CONFIRM_DIALOG_CALLS_BACK_FOR_UPDATE) {
            if (buttonPressed.equals(FragmentYesNoDialog.BUTTON_OK_PRESSED)) {
                Intent openPlayStoreForUpdate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.berthold.convertjobscheduletocalendar&hl=de"));
                startActivity(openPlayStoreForUpdate);
            }
        }
    }
}