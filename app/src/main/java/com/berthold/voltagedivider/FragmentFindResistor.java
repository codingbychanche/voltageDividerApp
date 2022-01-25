package com.berthold.voltagedivider;

import android.os.Bundle;
import android.util.Log;
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

//import VoltageDiv.GetResistors;
//import VoltageDiv.ResistorResult;


public class FragmentFindResistor extends Fragment {

    // Debug
    private String tag;

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

        // View Model's
        MainViewModel mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        FragmentFindResistorModel fragmentFindResistorModel=ViewModelProviders.of(requireActivity()).get(FragmentFindResistorModel.class);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UI
        //
        EditText resitorValueInputView= view.findViewById(R.id.resistor_value);
        EditText errorInPercentView=view.findViewById(R.id.tolerable_error_in_p);
        TextView standardValueAndSeriesView=view.findViewById(R.id.standard_value_and_series);

        //
        // Restore the last input value in any of the edit text fields....
        //
        if(fragmentFindResistorModel.resistorToBeFoundInput!=null)
            resitorValueInputView.setText(fragmentFindResistorModel.resistorToBeFoundInput);

        if(fragmentFindResistorModel.errorInput!=null)
           errorInPercentView.setText(fragmentFindResistorModel.errorInput);

        //
        // Search resistor.
        //
        Button findResistorView=view.findViewById(R.id.calc);
        findResistorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save input value's
                fragmentFindResistorModel.resistorToBeFoundInput=resitorValueInputView.getText().toString();
                fragmentFindResistorModel.errorInput=errorInPercentView.getText().toString();

                // Find and show result
                fragmentFindResistorModel.findRsistor(resitorValueInputView.getText().toString(),errorInPercentView.getText().toString());
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // View Model Observers.
        //
        // Displays the result, the standard value of the resistor found...
        //
       fragmentFindResistorModel.getResistorValuefoundInAnyOfTheESeries_Ohm().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                standardValueAndSeriesView.setText(HtmlCompat.fromHtml(s,0));

                // Protocol output
                String sol="<b><u>Gesucht:"+resitorValueInputView.getText().toString()+" Ohm. zul. abw.:"+errorInPercentView.getText()+"%</b></u><br>";
                mainViewModel.protokollOutput.setValue(sol+"="+s+"<p>");
            }
        });
    }
}