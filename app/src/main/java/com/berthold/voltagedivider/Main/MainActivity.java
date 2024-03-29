package com.berthold.voltagedivider.Main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.berthold.voltagedivider.FragmentDivider.FragmentDivider;
import com.berthold.voltagedivider.FragmentInfo;
import com.berthold.voltagedivider.FragmentResistor.FragmentFindResistor;
import com.berthold.voltagedivider.FragmentYesNoDialog;
import com.berthold.voltagedivider.Locale;
import com.berthold.voltagedivider.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.ArrayList;

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

    // Protocoll list...
    RecyclerView protocolListView;
    ProtocolListAdapter protocolListAdapter;

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
        Locale loc = new Locale();
        loc.setProtocolStartText(getApplicationContext().getResources().getString(R.string.protocol_start_text));
        mainViewModel.loc = loc;

        //
        // Check if there is a newer version of this app available at the play store
        //
        if (CheckForNetwork.isNetworkAvailable(getApplicationContext())) {
            final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
            // Returns an intent object that you use to check for an update.
            final Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo result) {

                    if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        // If an update was not installed for some time, check when the last
                        // update info was shown. We so not want to tire the user :-)
                        Log.v("UPDATEUPDATE","Hey, Update");
                        if (showUpdateInfo()) {
                            saveTimeUpdateInfoLastOpened();
                            String dialogText = getResources().getString(R.string.dialog_new_version_available) + " ";
                            String ok = getResources().getString(R.string.do_update_confirm_button);
                            String cancel = getResources().getString(R.string.no_udate_button);
                            showConfirmDialog(CONFIRM_DIALOG_CALLS_BACK_FOR_UPDATE, FragmentYesNoDialog.SHOW_AS_YES_NO_DIALOG, dialogText.toString(), ok, cancel);
                        }
                    } else {
                        // No update available, user does not need to know....
                    }
                }
            });
        } else {
            // No Network, user does not need to know....
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

        //
        // Prepare list, containing the protocol for all calculations done
        //
        ArrayList<ProtocolData> protocolData=new ArrayList<>();
         protocolListView = findViewById(R.id.protocolListView);

        protocolListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));

        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(protocolListView.getContext(),
               manager.getOrientation());
        protocolListView.addItemDecoration(mDividerItemDecoration);

        protocolListAdapter = new ProtocolListAdapter(protocolData);
        protocolListView.setAdapter(protocolListAdapter);

        enableSwipeToDeleteAndUndo();

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
        mainViewModel.getProtocol().observe(this, new Observer<ProtocolData>() {
            @Override
            public void onChanged(ProtocolData s) {

                protocolData.add(s);
                protocolListAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Once a protocol row has been deleted, this snackbar is show enabling the use to
     * make this step undone.....
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final ProtocolData item = protocolListAdapter.getProtocolListData().get(position);

                protocolListAdapter.removeItem(position);

                //@rem Shows how to create a snackbar with an action button@@
                MotionLayout coordinatorLayout=findViewById(R.id.mainLayout);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getResources().getString(R.string.ptotocol_entry_deleted), Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        protocolListAdapter.restoreItem(item, position);
                        protocolListView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.GRAY);
                snackbar.show();
                //@@
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(protocolListView);
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
                Intent openPlayStoreForUpdate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.berthold.voltagedivider&hl=de"));
                startActivity(openPlayStoreForUpdate);
            }
        }
    }
}