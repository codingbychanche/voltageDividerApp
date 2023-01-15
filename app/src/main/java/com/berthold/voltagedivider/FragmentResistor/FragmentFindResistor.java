package com.berthold.voltagedivider.FragmentResistor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.berthold.voltagedivider.Locale;
import com.berthold.voltagedivider.Main.MainViewModel;
import com.berthold.voltagedivider.Main.ProtocolData;
import com.berthold.voltagedivider.R;

//import VoltageDiv.GetResistors;
//import VoltageDiv.ResistorResult;


public class FragmentFindResistor extends Fragment {

    // Debug
    private String tag;

    // View model
    private boolean isNewSolution; // Is true, if a new solution was found. This helps to preent the protocol view from beeing updated whn fragment is restarted....

    public static FragmentFindResistor newInstance() {
        return new FragmentFindResistor();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_resistor, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        // View Model's
        //
        MainViewModel mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        FragmentFindResistorModel fragmentFindResistorModel = ViewModelProviders.of(requireActivity()).get(FragmentFindResistorModel.class);

        //
        // Locale for use in view models
        //
        Locale loc = new Locale();
        loc.setNoSolutionFoundText(requireActivity().getResources().getString(R.string.no_solution));
        loc.seteSeriesErrorMarginText(requireActivity().getResources().getString(R.string.e_series_error_margin));
        loc.setResistanceMinText(requireActivity().getResources().getString(R.string.resistance_min));
        loc.setResistanceMaxText(requireActivity().getResources().getString(R.string.resistance_max));
        loc.setResistorNominalText(requireActivity().getResources().getString(R.string.resistor_nominel_text));

        fragmentFindResistorModel.loc = loc;

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        EditText resitorValueInputView = view.findViewById(R.id.resistor_value);
        EditText errorInPercentView = view.findViewById(R.id.tolerable_error_in_p);
        //TextView standardValueAndSeriesView = view.findViewById(R.id.standard_value_and_series);

        //
        // Restore the last input value in any of the edit text fields....
        //
        if (fragmentFindResistorModel.resistorToBeFoundInput != null)
            resitorValueInputView.setText(fragmentFindResistorModel.resistorToBeFoundInput);

        if (fragmentFindResistorModel.errorInput != null)
            errorInPercentView.setText(fragmentFindResistorModel.errorInput);

        //
        // Search resistor.
        //
        Button findResistorView = view.findViewById(R.id.calc);
        findResistorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save input value's
                fragmentFindResistorModel.resistorToBeFoundInput = resitorValueInputView.getText().toString();
                fragmentFindResistorModel.errorInput = errorInPercentView.getText().toString();

                // Find and show result
                isNewSolution = true;
                fragmentFindResistorModel.findResistor(resitorValueInputView.getText().toString(), errorInPercentView.getText().toString());
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // View Model Observers.
        //
        // Displays the result, the standard value of the resistor found...
        //
        fragmentFindResistorModel.getResistorValuefoundInAnyOfTheESeries_Ohm().observe(getViewLifecycleOwner(), new Observer<ProtocolData>() {
            @Override
            public void onChanged(ProtocolData s) {

                //
                // The result ist written to the protocol directly....
                //

                if (isNewSolution) {
                    String sol = "<b>" + getResources().getString(R.string.searched_was_text)+" "
                            + resitorValueInputView.getText().toString() + "&Omega;<br>" +
                            getResources().getString(R.string.allowed_deviation)+":"
                            + errorInPercentView.getText() + "%</b><br><hr>";

                    mainViewModel.getProtocol().setValue(new ProtocolData(sol + s.getSolutionString() + "<p>",ProtocolData.IS_RESISTOR_RESULT));
                    isNewSolution = false;
                }
            }
        });
    }
}