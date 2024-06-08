package com.yremhl.ystgdh.UI.Settings;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.SortByDialogViewBinding;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsFragment extends PreferenceFragmentCompat implements androidx.preference.Preference.OnPreferenceChangeListener {
    private Dialog dialog ;
    Preference orderFilePreference ;
    ColorPicker colorPicker ;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
        // we must update the summary for each preference from the default shared Preference value
        // as if we did not do that it will take the value that saved in the xml file...
        setPreferenceListeners();
        loadSummary();
    }

    private void setPreferenceListeners() {
        //CheckBoxPreference checkBoxPreference1 = findPreference(getString(R.string.wifi_key));
        CheckBoxPreference checkBoxPreference2 = findPreference(getString(R.string.vibrate_key));
        //CheckBoxPreference checkBoxPreference3 = findPreference(getString(R.string.battery_key));
        CheckBoxPreference checkBoxPreference4 = findPreference(getString(R.string.auto_key));
        SeekBarPreference seekBarPreference1 = findPreference(getString(R.string.concurrent_download_key));
        SeekBarPreference seekBarPreference2 = findPreference(getString(R.string.download_speed_key));
        //Preference languagePreference = findPreference(getString(R.string.language_key));
        //if (checkBoxPreference1 != null) checkBoxPreference1.setOnPreferenceChangeListener(this);
        if (checkBoxPreference2 != null) checkBoxPreference2.setOnPreferenceChangeListener(this);
        //if (checkBoxPreference3 != null) checkBoxPreference3.setOnPreferenceChangeListener(this);
        if (checkBoxPreference4 != null) checkBoxPreference4.setOnPreferenceChangeListener(this);
        if (seekBarPreference1 != null) seekBarPreference1.setOnPreferenceChangeListener(this);
        if (seekBarPreference2 != null) seekBarPreference2.setOnPreferenceChangeListener(this);
        //if (languagePreference != null) languagePreference.setOnPreferenceChangeListener(this);
    }

    private void loadSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Preference languagePreference = findPreference(getString(R.string.language_key));
        orderFilePreference = findPreference(getString(R.string.download_order_key));
        SeekBarPreference speedPreference = findPreference(getString(R.string.download_speed_key));
        if (speedPreference!=null)
        speedPreference.setSummary(Utilities.getSpeedSummary(requireContext() , speedPreference.getValue()));
//        if (languagePreference!=null) {
//            String value = sharedPreferences.getString(getString(R.string.language_key), getString(R.string.default_language));
//            if (value.equals(getString(R.string.default_language)))
//            languagePreference.setSummary(getString(R.string.auto));
//            else
//            languagePreference.setSummary(value);
//        }
        if (orderFilePreference != null)  Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
    }

    @Override
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {

        if(preference.getKey().equals(getString(R.string.download_order_key))) {
            showOrderFilesDialog();
            return true ;
        }

        if(preference.getKey().equals(getString(R.string.feedback))) {
            sendFeedback();
            return true ;
        }


        if(preference.getKey().equals(getString(R.string.rate_key))) {
            rateApp();
            return true ;
        }

       


//        if(preference.getKey().equals(getString(R.string.AppearanceKey))) {
//            showPickerColorDialog();
//            return true ;
//        }

        if(preference.getKey().equals(getString(R.string.sharekey))) {
            shareApp();
            return true ;
        }

        return super.onPreferenceTreeClick(preference);

    }

    private void showOrderFilesDialog() {
        com.yremhl.ystgdh.databinding.SortByDialogViewBinding binding = SortByDialogViewBinding.inflate(getLayoutInflater());
        dialog = new Dialog(requireContext());
        dialog.setContentView(binding.getRoot());
        binding.imgNameAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_NAME_ASC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        binding.imgNameDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_NAME_DESC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        binding.imgDateAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_DATE_ASC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        binding.imgDateDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_DATE_DESC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        binding.imgSizeAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_SIZE_ASC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        binding.imgSizeDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Utilities.updateOrderByValue(requireContext() , Constant.ORDER_BY_SIZE_DESC);
                Utilities.updateOrderBySummary(requireContext() , orderFilePreference);
            }
        });
        updateSelectedImg(binding);
        dialog.show();
    }

    private void updateSelectedImg(SortByDialogViewBinding binding) {
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(requireContext() , R.color.teal_700),
                PorterDuff.Mode.SRC_ATOP);
        ImageView imageView = Utilities.getSelectedImage(requireContext() , binding);
        imageView.setColorFilter(porterDuffColorFilter);
    }

//    private void showPickerColorDialog() {
//        if (colorPicker == null)
//        CreatePickerDialog();
//        if (colorPicker.getDialogViewLayout().getParent() != null) {
//            ((ViewGroup)colorPicker.getDialogViewLayout().getParent()).removeView(colorPicker.getDialogViewLayout());
//        }
//        colorPicker.show();
//    }
//
//    private void CreatePickerDialog() {
//        colorPicker = new ColorPicker(requireActivity());
//        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
//            @Override
//            public void onChooseColor(int position,int color) {
//                Utilities.setThemeColor(requireContext() , color);
//                requireActivity().recreate();
//            }
//
//            @Override
//            public void onCancel(){
//            }
//        })
//        .setRoundColorButton(true)
//        .setColumns(5)
//        .setDefaultColorButton(Utilities.getCurrentColor(requireContext()))
//        .addListenerButton(getString(R.string.ok), Utilities.getDefaultButtonStyle(requireContext() , Color.BLUE) , new ColorPicker.OnButtonListener() {
//            @Override
//            public void onClick(View v, int position, int color) {
//                colorPicker.getPositiveButton().callOnClick();
//            }
//        })
//        .addListenerButton(getString(R.string.cancel_btn), Utilities.getDefaultButtonStyle(requireContext() , Color.BLACK) , new ColorPicker.OnButtonListener() {
//            @Override
//            public void onClick(View v, int position, int color) {
//                colorPicker.getNegativeButton().callOnClick();
//                if (getView() != null)
//                ((ViewGroup)getView()).removeView(v);
//            }
//        })
//       .addListenerButton(getString(R.string.default_btn), Utilities.getDefaultButtonStyle(requireContext() , Color.RED) , new ColorPicker.OnButtonListener() {
//            @Override
//            public void onClick(View v, int position, int color) {
//                   colorPicker.dismissDialog();
//                   if (getView() != null)
//                  ((ViewGroup)getView()).removeView(v);
//                   Utilities.setDefaultColor(requireContext());
//                   requireActivity().recreate();
//            }
//        })
//        .disableDefaultButtons(true)
//        .setTitle(getString(R.string.colorPicker))
//        .setColors(R.array.PickerColorsDialog);
//    }

    private void shareApp() {
        Utilities.shareApp(requireContext());
    }

    private void rateApp() {
        Utilities.rateApp(requireContext());
    }

    private void sendFeedback() {
        // implicit intent
        Utilities.sendFeedback(requireContext());
    }

    @Override
    public boolean onPreferenceChange(androidx.preference.Preference preference, Object newValue) {
        // reset fetch
        Utilities.PrintLog("new value " + newValue.toString());
        if (preference.getKey().equals(getString(R.string.download_speed_key))) {
            int value = Integer.parseInt(newValue.toString());
            preference.setSummary(Utilities.getSpeedSummary(requireContext() , value));
            return true;
        }
//        if (preference.getKey().equals(getString(R.string.language_key))) {
//            preference.setSummary(newValue.toString());
//            requireActivity().recreate();
//            return true;
//        }
        return true;
    }
}

