package com.yremhl.ystgdh.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {
    private Calendar calendar ;
    private static DatePickerFragment datePickerFragment ;

    private DatePickerFragment() {

    }

    public static DatePickerFragment getInstance(Calendar calendar) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.CALENDER, calendar);
        if (datePickerFragment == null)
            datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Utilities.PrintLog("OnDateDialog");
        Bundle bundle = getArguments() ;
        if (bundle!=null)
            calendar = (Calendar) ((Calendar) Objects.requireNonNull(bundle.getSerializable(Constant.CALENDER))).clone();
        //(context - listener - Month - year )
        return new DatePickerDialog(requireActivity(),  (DatePickerDialog.OnDateSetListener) getActivity() , calendar.get(Calendar.YEAR) ,
                calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
    }
}
