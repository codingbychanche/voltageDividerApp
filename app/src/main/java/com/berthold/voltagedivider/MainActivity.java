package com.berthold.voltagedivider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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

public class MainActivity extends AppCompatActivity {

    public MainViewModel mainViewModel;
    // Debug
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Debug
        tag = getClass().getSimpleName();
        Long time = System.currentTimeMillis();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
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
        Button clearProtocol=findViewById(R.id.clear_protocol);
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
        ImageButton toClipBoard=findViewById(R.id.protocol_to_clipboard);
        toClipBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", protocolView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.to_clipboard),Toast.LENGTH_LONG).show();
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
                protocolView.append(HtmlCompat.fromHtml(s,0));
            }
        });
    }
}