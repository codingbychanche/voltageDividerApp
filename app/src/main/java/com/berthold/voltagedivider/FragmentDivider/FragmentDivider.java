package com.berthold.voltagedivider.FragmentDivider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.berthold.voltagedivider.HTMLTools;
import com.berthold.voltagedivider.Locale;
import com.berthold.voltagedivider.Main.MainViewModel;
import com.berthold.voltagedivider.R;

public class FragmentDivider extends Fragment {

    // Debug
    private String tag;

    // View model
    private MainViewModel mainViewModel;
    private FragmentDividerModel fragmentDividerModel;
    private boolean isNewSolution;

    public static FragmentDivider newInstance() {
        return new FragmentDivider();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_divider, container, false);
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

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        EditText vInView = view.findViewById(R.id.v_in);
        EditText vOutView = view.findViewById(R.id.v_out);
        ProgressBar searchSolProgressView = view.findViewById(R.id.progress_searching_divider);
        TextView solutionCounterView = view.findViewById(R.id.solution_counter);

        //
        // restore input fields last values
        //
        if (fragmentDividerModel.vIn != null)
            vInView.setText(fragmentDividerModel.vIn);
        if (fragmentDividerModel.vOut != null)
            vOutView.setText(fragmentDividerModel.vOut);

        //
        // Solve the divider and show best solution found.
        //
        Button solve = view.findViewById(R.id.find_solution_for_divider);
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Store input fields
                fragmentDividerModel.vIn = vInView.getText().toString();
                fragmentDividerModel.vOut = vOutView.getText().toString();

                // Find and display solution via the post methods
                // invoke from inside the view model
                isNewSolution = true; // Notifes the observe, that a new solution was calculated and thus, display the result also inside protocol view...
                Long timestamp = System.currentTimeMillis();
                fragmentDividerModel.solveDividerForR1AndR2(vInView.getText().toString(), vOutView.getText().toString(), timestamp);
            }
        });

        //
        // Show all solutions found.....
        //
        Button showAllSolutionsFound = view.findViewById(R.id.show_all_solutions_for_divider);
        showAllSolutionsFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_for_current, FragmentDividerResultTable.newInstance())
                        .commitNow();
            }
        });

        //
        // Marked checkboxes excluding certain E- series
        //
        CheckBox exE3View = view.findViewById(R.id.exclude_e3);
        exE3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE3View.isChecked())
                    fragmentDividerModel.exE3.postValue(true);
                else
                    fragmentDividerModel.exE3.postValue(false);
            }
        });

        CheckBox exE6View = view.findViewById(R.id.exclude_e6);
        exE6View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE6View.isChecked())
                    fragmentDividerModel.exE6.postValue(true);
                else
                    fragmentDividerModel.exE6.postValue(false);
            }
        });

        CheckBox exE12View = view.findViewById(R.id.exclude_e12);
        exE12View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE12View.isChecked())
                    fragmentDividerModel.exE12.postValue(true);
                else
                    fragmentDividerModel.exE12.postValue(false);
            }
        });

        CheckBox exE24View = view.findViewById(R.id.exclude_e24);
        exE24View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE24View.isChecked())
                    fragmentDividerModel.exE24.postValue(true);
                else
                    fragmentDividerModel.exE24.postValue(false);
            }
        });


        CheckBox exE48View = view.findViewById(R.id.exclude_e48);
        exE48View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE48View.isChecked())
                    fragmentDividerModel.exE48.postValue(true);
                else
                    fragmentDividerModel.exE48.postValue(false);
            }
        });

        CheckBox exE96View = view.findViewById(R.id.exclude_e96);
        exE96View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exE96View.isChecked())
                    fragmentDividerModel.exE96.postValue(true);
                else
                    fragmentDividerModel.exE96.postValue(false);
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
                //
                // The reason why this is empty is:
                // The view model fragmentDividerModel is shared by this class and the FragmentDividerResultTable- class
                // If we would not implement this observer, the app would crash when posting  a value because 'getAllsolutionsFound'
                // would be 'null'....
                //
            }
        });




        fragmentDividerModel.getBestSolutionFound().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //dividerResultView.setText(HtmlCompat.fromHtml(s, 0));

                if (isNewSolution) {
                    //todo Change, once protocol list works...   mainViewModel.protokollOutput.setValue(HTMLTools.makeSolutionBlockSolutionFound(s));
                    mainViewModel.getProtocol().setValue(HTMLTools.makeSolutionBlockSolutionFound(s));
                    isNewSolution = false;
                }
            }
        });

        //
        // Displays additional info about the result found...
        //
        fragmentDividerModel.getNumberOfSolAndIndexOfCurrentlyShown().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                solutionCounterView.setText(s);
            }
        });

        //
        // Checkboxes showing whether an E- series is to be excludes or not.
        //
        fragmentDividerModel.doExcludeE3().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE3View.setChecked(true);
                else
                    exE3View.setChecked(false);
            }
        });

        fragmentDividerModel.doExcludeE6().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE6View.setChecked(true);
                else
                    exE6View.setChecked(false);
            }
        });

        fragmentDividerModel.doExcludeE12().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE12View.setChecked(true);
                else
                    exE12View.setChecked(false);
            }
        });

        fragmentDividerModel.doExcludeE24().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE24View.setChecked(true);
                else
                    exE24View.setChecked(false);
            }
        });

        fragmentDividerModel.doExcludeE48().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE48View.setChecked(true);
                else
                    exE48View.setChecked(false);
            }
        });

        fragmentDividerModel.doExcludeE96().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if (s)
                    exE96View.setChecked(true);
                else
                    exE96View.setChecked(false);
            }
        });
    }
}