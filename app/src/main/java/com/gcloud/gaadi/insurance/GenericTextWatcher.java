package com.gcloud.gaadi.insurance;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;

/**
 * Created by Priya on 29-05-2015.
 */
class GenericTextWatcher implements TextWatcher {

    private View view;

    GenericTextWatcher(View view) {
        this.view = view;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        insertCommaIntoNumber((EditText) view, charSequence, "##,##,###");

    }

    public void afterTextChanged(Editable editable) {

    }

    protected void insertCommaIntoNumber(EditText etText, CharSequence s, String format) {
        try {
            if (s.toString().length() > 0) {
                String convertedStr = s.toString();
                if (s.toString().contains(".")) {
                    if (chkConvert(s.toString()))
                        convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                } else {
                    convertedStr = customFormat(format, Double.parseDouble(s.toString().replace(",", "")));
                }


                if (!etText.getText().toString().equals(convertedStr) && convertedStr.length() > 0) {
                    etText.setText(convertedStr);
                    etText.setSelection(etText.getText().length());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        return myFormatter.format(value);

    }

    private boolean chkConvert(String s) {
        String tempArray[] = s.split("\\.");
        if (tempArray.length > 1) {
            if (Integer.parseInt(tempArray[1]) > 0) {
                return true;
            } else
                return false;
        } else
            return false;
    }

}
