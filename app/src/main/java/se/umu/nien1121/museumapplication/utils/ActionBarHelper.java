package se.umu.nien1121.museumapplication.utils;

import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import se.umu.nien1121.museumapplication.R;

/**
 * Utility class for modifying {@link AppCompatActivity}s action bar.
 */
public class ActionBarHelper {
    /**
     * Sets a given activities action bar to customized appearance.
     *
     * @param activity Activity which view to be modified
     * @param title    The title to be set on the action bar
     */
    public static void setActionBar(AppCompatActivity activity, String title) {
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.action_bar_custom);
        TextView title_textView = activity.getSupportActionBar().getCustomView().findViewById(R.id.tvTitle);
        title_textView.setText(title);
    }
}
