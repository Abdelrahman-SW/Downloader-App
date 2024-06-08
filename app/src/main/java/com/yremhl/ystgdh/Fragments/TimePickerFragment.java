package com.yremhl.ystgdh.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;

import java.util.Calendar;
import java.util.Objects;

public class TimePickerFragment extends DialogFragment {
    private Calendar calendar ;
    private static TimePickerFragment timePickerFragment ;

    private TimePickerFragment() {

    }
    public static TimePickerFragment getInstance(Calendar calendar) {
        Bundle bundle = new Bundle() ;
        bundle.putSerializable(Constant.CALENDER , calendar);
        if (timePickerFragment==null)
            timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment ;
    }
    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Utilities.PrintLog("OnCreateTimeDialog");
        Bundle bundle = getArguments() ;
        if (bundle!=null)
        calendar = (Calendar) ((Calendar) Objects.requireNonNull(bundle.getSerializable(Constant.CALENDER))).clone();
        //(context - listener - hour - min - HourSystem )
        //Calendar calendar = Calendar.getInstance();
        boolean is24Hour = DateFormat.is24HourFormat(getActivity());
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity()
                , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24Hour);
    }
}
