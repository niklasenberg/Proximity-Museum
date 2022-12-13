package se.umu.nien1121.museumapplication.utils;

import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import se.umu.nien1121.museumapplication.R;

public class ActionBarHelper {
    public static void setActionBar(AppCompatActivity activity, String title){
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.action_bar_custom);
        activity.getSupportActionBar().setTitle(title);
    }
}
