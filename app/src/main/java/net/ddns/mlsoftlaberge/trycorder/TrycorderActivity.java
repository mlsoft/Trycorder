package net.ddns.mlsoftlaberge.trycorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by mlsoft on 16-05-13.
 */
public class TrycorderActivity extends FragmentActivity implements
        TrycorderFragment.OnTrycorderInteractionListener,
        TryviewerFragment.OnTryviewerInteractionListener,
        TrygalleryFragment.OnTrygalleryInteractionListener,
        TrywalkieFragment.OnTrywalkieInteractionListener {

    private static String TAG = "Trycorder";

    private TrycorderFragment mTrycorderFragment=null;
    private TrygalleryFragment mTrygalleryFragment=null;
    private TryviewerFragment mTryviewerFragment=null;
    private TrywalkieFragment mTrywalkieFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ask the permissions
        askpermissions();
        // create the 1 initial fragment
        mTrycorderFragment=new TrycorderFragment();
        // start the fragment full screen
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, mTrycorderFragment, TAG);
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

    @Override
    public void onTrycorderModeChange(int mode) {
        switchfragment(mode);
    }

    @Override
    public void onTrygalleryModeChange(int mode) {
        switchfragment(mode);
    }

    @Override
    public void onTryviewerModeChange(int mode) {
        switchfragment(mode);
    }

    @Override
    public void onTrywalkieModeChange(int mode) {
        switchfragment(mode);
    }

    private void switchfragment(int mode) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch(mode) {
            case 1:
                if(mTrycorderFragment==null) mTrycorderFragment=new TrycorderFragment();
                ft.replace(android.R.id.content, mTrycorderFragment, TAG);
                ft.commit();
                break;
            case 2:
                if(mTrygalleryFragment==null) mTrygalleryFragment=new TrygalleryFragment();
                ft.replace(android.R.id.content, mTrygalleryFragment, TAG);
                ft.commit();
                break;
            case 3:
                if(mTryviewerFragment==null) mTryviewerFragment=new TryviewerFragment();
                ft.replace(android.R.id.content, mTryviewerFragment, TAG);
                ft.commit();
                break;
            case 4:
                if(mTrywalkieFragment==null) mTrywalkieFragment=new TrywalkieFragment();
                ft.replace(android.R.id.content, mTrywalkieFragment, TAG);
                ft.commit();
                break;
        }
    }



    // ==========================================================================

    private void askpermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_TASKS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS
                    }, 1);
        }
    }

}
