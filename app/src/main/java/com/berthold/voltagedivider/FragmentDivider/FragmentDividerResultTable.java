package com.berthold.voltagedivider.FragmentDivider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.berthold.voltagedivider.Locale;
import com.berthold.voltagedivider.Main.MainViewModel;
import com.berthold.voltagedivider.R;

public class FragmentDividerResultTable extends Fragment {

    // Debug
    private String tag;

    // View model
    private MainViewModel mainViewModel;
    private FragmentDividerModel fragmentDividerModel;

    public static FragmentDividerResultTable newInstance() {
        return new FragmentDividerResultTable();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_divider_result_table, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View Model
        MainViewModel mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        FragmentDividerModel fragmentDividerModel = ViewModelProviders.of(requireActivity()).get(FragmentDividerModel.class);

        // Locale
        Locale loc = new Locale();
        loc.setSearchingText(requireActivity().getResources().getString(R.string.searching));
        loc.setShowingText(requireActivity().getResources().getString(R.string.showing));
        loc.setNoSolutionFoundText(requireActivity().getResources().getString(R.string.no_solution));
        fragmentDividerModel.loc = loc;

        //
        // restore input fields last values
        //
        /*
        if (fragmentDividerModel.vIn != null)
            vInView.setText(fragmentDividerModel.vIn);
        if (fragmentDividerModel.vOut != null)
            vOutView.setText(fragmentDividerModel.vOut);
        */

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        // Share the result.....
        //

        ImageButton share = view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //
        // Back to divider, find best solution...
        //
        ImageButton closeTableView = view.findViewById(R.id.close_table_view);
        closeTableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_for_current, FragmentDivider.newInstance())
                        .commitNow();
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // View Model Observers.
        //
        // Displays the result.
        //
        fragmentDividerModel.getAllSolutionsFound().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                WebView w=getActivity().findViewById(R.id.webview_table_of_all_solutions);
                WebSettings webSettings=w.getSettings();

                //@rem:webView fontSize Show how to set the font size inside a web view globally@@
                //webSettings.setDefaultFontSize(8);

                //@rem:webView zoom Shows how to enable build in zoom feature@@
                webSettings.setBuiltInZoomControls(true); // This enable the zoom controls
                webSettings.setDisplayZoomControls(false); // This enables or disables overlaying the zoom controls....
                
                w.loadData(s,"text/html; charset=UTF-8", null);
            }
        });
    }
}