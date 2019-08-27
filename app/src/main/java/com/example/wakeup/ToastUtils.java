package com.example.wakeup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

@SuppressLint("ShowToast")
public class ToastUtils {

    public static void shortToast(Context context, String message) {
        Toast toast = makeText(context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        setToastDefaults(toast, view, textView);
    }

    public static void longToast(Context context, String message) {
        Toast toast = makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        setToastDefaults(toast, view, textView);
    }

    private static void setToastDefaults(Toast toast, View view, TextView textView) {
        textView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        textView.setTextColor(Color.WHITE);
        view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
