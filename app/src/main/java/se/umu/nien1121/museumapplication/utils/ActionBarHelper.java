package se.umu.nien1121.museumapplication.utils;

import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import se.umu.nien1121.museumapplication.R;

public class ActionBarHelper {
    public static void setActionBar(AppCompatActivity activity, String title){
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.action_bar_custom);
        TextView title_textView = activity.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        title_textView.setText(title);
    }
}
