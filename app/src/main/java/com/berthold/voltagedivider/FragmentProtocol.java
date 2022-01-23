package com.berthold.voltagedivider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class FragmentProtocol extends Fragment {

    // Debug
    private String tag;

    // View model
    private MainViewModel mainViewModel;

    public static FragmentProtocol newInstance() {
        return new FragmentProtocol();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_protocol, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View Model
        MainViewModel mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        TextView protokollWindowView=view.findViewById(R.id.protocol);

        //
        // clear the protocol view....
        //
        Button clearProtocol=view.findViewById(R.id.clear_protocol);
        clearProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                protokollWindowView.setText("");
            }
        });

        //
        // Share the protocol...
        //
        Button shareProtocol=view.findViewById(R.id.share_protocol);
        shareProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>"+protokollWindowView.getText().toString()+"</p>"));
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // View Model Observers.
        //
        // Resistor value to be found.
        //
        final Observer<String> protocolObserver=new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                protokollWindowView.append(s+"\n\n");
            }
        };
        mainViewModel.getProtokollOutput().observe(getActivity(),protocolObserver);
    }
}