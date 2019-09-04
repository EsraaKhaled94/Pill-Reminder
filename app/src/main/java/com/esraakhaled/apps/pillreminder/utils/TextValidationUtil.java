package com.esraakhaled.apps.pillreminder.utils;

import android.widget.EditText;

public class TextValidationUtil {
    public static boolean isEmptyText(EditText editText) {
        if (editText.getText().toString().trim().length() == 0) {
            return true;
        }
        return false;
    }
}
