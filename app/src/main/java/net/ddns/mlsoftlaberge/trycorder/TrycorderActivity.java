package net.ddns.mlsoftlaberge.trycorder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by mlsoft on 16-05-13.
 */
public class TrycorderActivity extends FragmentActivity {

    private static String TAG="Trycorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // start the fragment full screen
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, new TrycorderFragment(), TAG);
        ft.commit();
    }

    // permits this activity to hide status and action bars, and proceed full screen
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
