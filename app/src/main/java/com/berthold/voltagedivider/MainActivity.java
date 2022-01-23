package com.berthold.voltagedivider;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // Debug
    private String tag;

    // View model
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Debug
        tag = getClass().getSimpleName();
        Long time = System.currentTimeMillis();

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

       ImageView showInfo=findViewById(R.id.info_fragment_show_info);
       showInfo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!mainViewModel.currentGFragmentShown.getValue().equals("Info"))
                   mainViewModel.currentGFragmentShown.setValue("Info");
           }
       });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // View model and it's observers
        //
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

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


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Show protocol fragment
        //
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_protocol, FragmentProtocol.newInstance())
                    .commitNow();
        }
    }
}