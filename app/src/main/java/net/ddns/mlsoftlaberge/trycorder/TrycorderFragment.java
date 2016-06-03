package net.ddns.mlsoftlaberge.trycorder;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrycorderFragment extends Fragment
        implements TextureView.SurfaceTextureListener,
        RecognitionListener,
        Camera.PictureCallback {

    public TrycorderFragment() {
    }

    // handles to camera and textureview
    private Camera mCamera = null;
    private TextureView mViewerWindow;

    // handles for the 2 logs pages
    private TextView mLogsConsole;
    private TextView mLogsInfo;

    // handles for the conversation functions
    private TextToSpeech tts;
    private AudioManager mAudioManager;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private String mSentence;

    // handle for the gps
    private LocationManager locationManager;
    private String locationProvider;

    // the handle to the sensors
    private SensorManager mSensorManager;

    // the new scope class
    private MagSensorView mMagSensorView;

    // the new scope class
    private OriSensorView mOriSensorView;

    // the new scope class
    private GraSensorView mGraSensorView;

    // the new scope class
    private TemSensorView mTemSensorView;

    // the Star-Trek Logo on sensor screen
    private ImageView mStartrekLogo;

    // the button to talk to computer
    private ImageButton mTalkButton;

    // the button to start it all
    private Button mStartButton;

    // the button to stop it all
    private Button mStopButton;
    private boolean mRunStatus = false;

    // the button for settings
    private Button mSettingsButton;

    // the two status lines
    private TextView mTextstatus_top;
    private TextView mTextstatus_bottom;

    // the button to talk to computer
    private ImageButton mAskButton;

    // the button to start it all
    private Button mPhotoButton;

    // the button to stop it all
    private Button mRecordButton;

    // the button for settings
    private Button mGalleryButton;

    // the buttons to switch between sensors
    private Button mSensorButton;
    private Button mMagneticButton;
    private Button mOrientationButton;
    private Button mGravityButton;
    private Button mTemperatureButton;
    private Button mSensoroffButton;
    private int mSensormode = 0;

    // the button to open a channel
    private Button mCommButton;
    private Button mOpenCommButton;
    private Button mCloseCommButton;

    // the button to control shields
    private Button mShieldButton;
    private Button mShieldUpButton;
    private Button mShieldDownButton;

    // the button to fire at ennemys
    private Button mFireButton;
    private Button mPhaserButton;
    private Button mTorpedoButton;

    // the button to fire at ennemys
    private Button mTransporterButton;
    private Button mTransportInButton;
    private Button mTransportOutButton;

    // the button to control the viewer
    private Button mViewerButton;
    private Button mViewerOnButton;
    private Button mViewerFrontButton;
    private Button mViewerOffButton;
    private boolean mVieweron = false;
    private boolean mViewerfront = false;
    private int mViewermode = 0;

    private Button mLogsButton;
    private Button mLogsConsoleButton;
    private Button mLogsInfoButton;
    private Button mLogsPlansButton;

    // the button to control sound-effects
    private Button mSoundButton;
    private boolean mSoundStatus = false;

    // the layout to put sensorview in
    private LinearLayout mSensorLayout;

    private LinearLayout mSensor2Layout;
    private LinearLayout mSensor2sensorLayout;
    private LinearLayout mSensor2commLayout;
    private LinearLayout mSensor2shieldLayout;
    private LinearLayout mSensor2fireLayout;
    private LinearLayout mSensor2transporterLayout;
    private LinearLayout mSensor2viewerLayout;
    private LinearLayout mSensor2logsLayout;
    private int mSensor2mode = 0;

    private LinearLayout mSensor3Layout;
    private ImageView mFederationlogo;
    private ImageView mStarshipPlan;

    // the player for sound background
    private MediaPlayer mMediaPlayer = null;

    // the preferences values
    private boolean autoListen;
    private String speakLanguage;
    private String listenLanguage;
    private String displayLanguage;

    // the preferences holder
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trycorder_fragment, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen", false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        displayLanguage = sharedPref.getString("pref_key_display_language", "");

        // ===================== top horizontal button grid ==========================
        // the start button
        mTalkButton = (ImageButton) view.findViewById(R.id.talk_button);
        mTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });

        // the start button
        mStartButton = (Button) view.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                startsensors(mSensormode);
            }
        });

        // the stop button
        mStopButton = (Button) view.findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                stopsensors();
            }
        });

        // the sound-effect button
        mSoundButton = (Button) view.findViewById(R.id.sound_button);
        mSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchsound();
            }
        });

        // the settings button
        mSettingsButton = (Button) view.findViewById(R.id.settings_button);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                settingsactivity();
            }
        });

        // ===================== bottom horizontal button grid ==========================
        // the ask button
        mAskButton = (ImageButton) view.findViewById(R.id.ask_button);
        mAskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                snapphoto();
            }
        });

        // the start button
        mPhotoButton = (Button) view.findViewById(R.id.photo_button);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                takephoto();
            }
        });

        // the stop button
        mRecordButton = (Button) view.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                recordvideo();
            }
        });

        // the settings button
        mGalleryButton = (Button) view.findViewById(R.id.gallery_button);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                opengallery();
            }
        });

        // ==== the two status lines at top and bottom =====

        mTextstatus_top = (TextView) view.findViewById(R.id.textstatus_top);
        mTextstatus_top.setText("");

        mTextstatus_bottom = (TextView) view.findViewById(R.id.textstatus_bottom);
        mTextstatus_bottom.setText("Ready");

        // ===================== left vertical button grid ============================

        // ===================== sensor buttons group ============================
        // the sensor button
        mSensorButton = (Button) view.findViewById(R.id.sensor_button);
        mSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(1);
                buttonsound();
            }
        });
        // the magnetic button
        mMagneticButton = (Button) view.findViewById(R.id.magnetic_button);
        mMagneticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchsensorlayout(1);
                startsensors(1);
            }
        });

        // the orientation button
        mOrientationButton = (Button) view.findViewById(R.id.orientation_button);
        mOrientationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchsensorlayout(2);
                startsensors(2);
            }
        });

        // the gravity button
        mGravityButton = (Button) view.findViewById(R.id.gravity_button);
        mGravityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchsensorlayout(3);
                startsensors(3);
            }
        });

        // the gravity button
        mTemperatureButton = (Button) view.findViewById(R.id.temperature_button);
        mTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchsensorlayout(4);
                startsensors(4);
            }
        });

        // the sensoroff button
        mSensoroffButton = (Button) view.findViewById(R.id.sensoroff_button);
        mSensoroffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchsensorlayout(0);
                stopsensors();
            }
        });

        // ===================== comm buttons group ============================
        // the comm button
        mCommButton = (Button) view.findViewById(R.id.comm_button);
        mCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(2);
                buttonsound();
            }
        });
        // the open comm button
        mOpenCommButton = (Button) view.findViewById(R.id.opencomm_button);
        mOpenCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opencomm();
            }
        });
        // the close comm button
        mCloseCommButton = (Button) view.findViewById(R.id.closecomm_button);
        mCloseCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closecomm();
            }
        });

        // ===================== shield buttons group ============================
        // the shield button
        mShieldButton = (Button) view.findViewById(R.id.shield_button);
        mShieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(3);
                buttonsound();
            }
        });
        // the shield up button
        mShieldUpButton = (Button) view.findViewById(R.id.shield_up_button);
        mShieldUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                raiseshields();
            }
        });
        // the shield down button
        mShieldDownButton = (Button) view.findViewById(R.id.shield_down_button);
        mShieldDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowershields();
            }
        });

        // ===================== fire buttons group ============================
        // the shield button
        mFireButton = (Button) view.findViewById(R.id.fire_button);
        mFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(4);
                buttonsound();
            }
        });

        // the phaser button
        mPhaserButton = (Button) view.findViewById(R.id.phaser_button);
        mPhaserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firephaser();
            }
        });

        // the torpedo button
        mTorpedoButton = (Button) view.findViewById(R.id.torpedo_button);
        mTorpedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firemissiles();
            }
        });

        // ===================== transporter buttons group ============================
        // the transporter button
        mTransporterButton = (Button) view.findViewById(R.id.transporter_button);
        mTransporterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(5);
                buttonsound();
            }
        });

        // the transporter in button
        mTransportInButton = (Button) view.findViewById(R.id.transport_in_button);
        mTransportInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transporterin();
            }
        });

        // the transporter out button
        mTransportOutButton = (Button) view.findViewById(R.id.transport_out_button);
        mTransportOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transporterout();
            }
        });

        // ===================== viewer buttons group ============================
        // the viewer button
        mViewerButton = (Button) view.findViewById(R.id.viewer_button);
        mViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(6);
            }
        });

        mViewerOnButton = (Button) view.findViewById(R.id.vieweron_button);
        mViewerOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                mViewerfront = false;
                switchviewer(1);
                switchcam(1);
            }
        });

        mViewerFrontButton = (Button) view.findViewById(R.id.viewerfront_button);
        mViewerFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                mViewerfront = true;
                switchviewer(1);
                switchcam(2);
            }
        });

        mViewerOffButton = (Button) view.findViewById(R.id.vieweroff_button);
        mViewerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(0);
                switchcam(0);
            }
        });

        // ===================== viewer buttons group ============================
        // the viewer button
        mLogsButton = (Button) view.findViewById(R.id.logs_button);
        mLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(7);
            }
        });

        mLogsConsoleButton = (Button) view.findViewById(R.id.logsconsole_button);
        mLogsConsoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(2);
            }
        });

        mLogsInfoButton = (Button) view.findViewById(R.id.logsinfo_button);
        mLogsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(3);
            }
        });

        mLogsPlansButton = (Button) view.findViewById(R.id.logsplans_button);
        mLogsPlansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(4);
            }
        });

        // ================== get handles on the 3 layout containers ===================
        // the sensor layout, to contain my sensorview
        mSensorLayout = (LinearLayout) view.findViewById(R.id.sensor_layout);

        // the sensor layout, to contain my buttons groups
        mSensor2Layout = (LinearLayout) view.findViewById(R.id.sensor2_layout);
        mSensor2sensorLayout = (LinearLayout) view.findViewById(R.id.sensor2_sensor_layout);
        mSensor2commLayout = (LinearLayout) view.findViewById(R.id.sensor2_comm_layout);
        mSensor2shieldLayout = (LinearLayout) view.findViewById(R.id.sensor2_shield_layout);
        mSensor2fireLayout = (LinearLayout) view.findViewById(R.id.sensor2_fire_layout);
        mSensor2transporterLayout = (LinearLayout) view.findViewById(R.id.sensor2_transporter_layout);
        mSensor2viewerLayout = (LinearLayout) view.findViewById(R.id.sensor2_viewer_layout);
        mSensor2logsLayout = (LinearLayout) view.findViewById(R.id.sensor2_logs_layout);

        // the sensor layout, to contain my surfaceview
        mSensor3Layout = (LinearLayout) view.findViewById(R.id.sensor3_layout);
        mFederationlogo = (ImageView) view.findViewById(R.id.federation_logo);
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);
        mLogsInfo = (TextView) view.findViewById(R.id.logs_info);
        mStarshipPlan = (ImageView) view.findViewById(R.id.starship_plans);

        // create and activate a textureview to contain camera display
        mViewerWindow = (TextureView) view.findViewById(R.id.viewer_window);
        mViewerWindow.setSurfaceTextureListener(this);

        mFederationlogo.setVisibility(View.VISIBLE);
        mViewerWindow.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        mLogsInfo.setVisibility(View.GONE);
        mStarshipPlan.setVisibility(View.GONE);
        mVieweron = false;

        // ==============================================================================
        // create layout params for the created views
        final LinearLayout.LayoutParams tlayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        // ============== create a sensor display and incorporate in layout ==============
        // a sensor manager to obtain sensors data
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        // my sensorview that display the sensors data
        mMagSensorView = new MagSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mMagSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mOriSensorView = new OriSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mOriSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mGraSensorView = new GraSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mGraSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mTemSensorView = new TemSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mTemSensorView, tlayoutParams);

        // position 0 of layout 1
        mStartrekLogo = (ImageView) view.findViewById(R.id.startrek_logo);

        // initialize the gps service
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // Define the criteria how to select the locatioin provider -> use
        // default
        Location location = null;
        //Criteria criteria = new Criteria();
        //locationProvider = locationManager.getBestProvider(criteria, false);
        locationProvider = "gps";
        try {
            location = locationManager.getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {
            say("No GPS available");
        }
        // Initialize the location fields
        if (location != null) {
            say("Provider " + locationProvider + " has been selected.");
            mOriSensorView.setLocation(location);
        } else {
            say("No location available. " + locationProvider);
        }

        // ============== initialize the audio listener and talker ==============

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeechRecognizer.setRecognitionListener(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.trycorder");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500);

        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "sonysketchef.ttf");
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "finalold.ttf");
        Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "finalnew.ttf");
        // status fields
        mTextstatus_top.setTypeface(face);
        mTextstatus_bottom.setTypeface(face);
        // bottom buttons
        mPhotoButton.setTypeface(face2);
        mRecordButton.setTypeface(face2);
        mGalleryButton.setTypeface(face2);
        // top buttons
        mStartButton.setTypeface(face2);
        mStopButton.setTypeface(face2);
        mSoundButton.setTypeface(face2);
        mSettingsButton.setTypeface(face2);
        // left column buttons
        mSensorButton.setTypeface(face2);
        mCommButton.setTypeface(face2);
        mShieldButton.setTypeface(face2);
        mFireButton.setTypeface(face2);
        mTransporterButton.setTypeface(face2);
        mViewerButton.setTypeface(face2);
        mLogsButton.setTypeface(face2);

        // center buttons
        mMagneticButton.setTypeface(face);
        mOrientationButton.setTypeface(face);
        mGravityButton.setTypeface(face);
        mTemperatureButton.setTypeface(face);
        mSensoroffButton.setTypeface(face);

        mOpenCommButton.setTypeface(face3);
        mCloseCommButton.setTypeface(face3);

        mShieldUpButton.setTypeface(face3);
        mShieldDownButton.setTypeface(face3);

        mPhaserButton.setTypeface(face3);
        mTorpedoButton.setTypeface(face3);

        mTransportOutButton.setTypeface(face);
        mTransportInButton.setTypeface(face);

        mViewerOnButton.setTypeface(face);
        mViewerFrontButton.setTypeface(face);
        mViewerOffButton.setTypeface(face);

        mLogsConsoleButton.setTypeface(face);
        mLogsInfoButton.setTypeface(face);
        mLogsPlansButton.setTypeface(face);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensormode = sharedPref.getInt("pref_key_sensor_mode", 0);
        mSensor2mode = sharedPref.getInt("pref_key_sensor2_mode", 0);
        mViewermode = sharedPref.getInt("pref_key_viewer_mode", 0);
        mSoundStatus = sharedPref.getBoolean("pref_key_audio_mode", false);
        mViewerfront = sharedPref.getBoolean("pref_key_viewer_front", false);
        switchbuttonlayout(mSensor2mode);
        switchsensorlayout(mSensormode);
        startsensors(mSensormode);
        switchviewer(mViewermode);
    }

    @Override
    public void onPause() {
        stopsensors();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("pref_key_sensor_mode", mSensormode);
        editor.putInt("pref_key_sensor2_mode", mSensor2mode);
        editor.putInt("pref_key_viewer_mode", mViewermode);
        editor.putBoolean("pref_key_audio_mode", mSoundStatus);
        editor.putBoolean("pref_key_viewer_front", mViewerfront);
        editor.commit();
        super.onPause();
    }


    private void startsensors(int mode) {
        stopsensors();
        mSensormode = mode;
        switch (mode) {
            case 1:
                say("Sensors Magnetic");
                startmagsensors();
                break;
            case 2:
                say("Sensors Orientation");
                startorisensors();
                break;
            case 3:
                say("Sensors Gravity");
                startgrasensors();
                break;
            case 4:
                say("Sensors Temperature");
                starttemsensors();
                break;
            default:
                say("Sensors OFF");
                break;
        }
    }

    private void stopsensors() {
        stopmagsensors();
        stoporisensors();
        stopgrasensors();
        stoptemsensors();
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        say("Settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    // =====================================================================================

    private void switchsensorlayout(int no) {
        mMagSensorView.setVisibility(View.GONE);
        mOriSensorView.setVisibility(View.GONE);
        mGraSensorView.setVisibility(View.GONE);
        mTemSensorView.setVisibility(View.GONE);
        mStartrekLogo.setVisibility(View.GONE);
        switch (no) {
            case 0:
                mStartrekLogo.setVisibility(View.VISIBLE);
                break;
            case 1:
                mMagSensorView.setVisibility(View.VISIBLE);
                break;
            case 2:
                mOriSensorView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mGraSensorView.setVisibility(View.VISIBLE);
                break;
            case 4:
                mTemSensorView.setVisibility(View.VISIBLE);
                break;
        }
        mSensormode = no;
    }

    // =====================================================================================

    private void switchbuttonlayout(int no) {
        mSensor2sensorLayout.setVisibility(View.GONE);
        mSensor2commLayout.setVisibility(View.GONE);
        mSensor2shieldLayout.setVisibility(View.GONE);
        mSensor2fireLayout.setVisibility(View.GONE);
        mSensor2transporterLayout.setVisibility(View.GONE);
        mSensor2viewerLayout.setVisibility(View.GONE);
        mSensor2logsLayout.setVisibility(View.GONE);
        switch (no) {
            case 1:
                say("Sensors Mode");
                mSensor2sensorLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                say("Communication Mode");
                mSensor2commLayout.setVisibility(View.VISIBLE);
                break;
            case 3:
                say("Shield Mode");
                mSensor2shieldLayout.setVisibility(View.VISIBLE);
                break;
            case 4:
                say("Fire Mode");
                mSensor2fireLayout.setVisibility(View.VISIBLE);
                break;
            case 5:
                say("Transporter Mode");
                mSensor2transporterLayout.setVisibility(View.VISIBLE);
                break;
            case 6:
                say("Viewer Mode");
                mSensor2viewerLayout.setVisibility(View.VISIBLE);
                break;
            case 7:
                say("Logs Mode");
                mSensor2logsLayout.setVisibility(View.VISIBLE);
                break;
        }
        mSensor2mode = no;
    }

    // =====================================================================================

    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void opencomm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commopen);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        speak("Hailing frequency open.");
    }

    private void closecomm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commclose);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        speak("Hailing frequency closed.");
    }

    private void transporterout() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1a);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport Out");
    }

    private void transporterin() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1b);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport In");
    }

    private void longrangesensor() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.long_range_scan);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Long range Sensors");
    }

    private void raiseshields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shieldup);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Raise Shields");
    }

    private void lowershields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shielddown);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Lower Shields");
    }

    private void tractorbeam() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam");
    }

    private void firephaser() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.phasertype2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Phaser");
    }

    private void firemissiles() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.photorp1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Torpedo");
    }

    // ==========================================================================================
    // start sensor background sound
    private void startmusic() {
        if (mMediaPlayer == null) {
            switch (mSensormode) {
                case 1:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.tricscan2);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
                case 2:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.long_range_scan);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
                case 3:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.tng_tractor_clean);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
                case 4:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.scan_high);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
            }

        }
        if (mSensormode != 0)
            mSoundButton.setBackgroundResource(R.drawable.trekbutton_yellow_center);
    }

    // stop the background sound
    private void stopmusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mSoundButton.setBackgroundResource(R.drawable.trekbutton_gray_center);
    }

    // switch background sound on/off
    private void switchsound() {
        if (mSoundStatus) {
            mSoundStatus = false;
            stopmusic();
        } else {
            mSoundStatus = true;
            if (mRunStatus) startmusic();
        }
    }

    // ============================================================================
    // stop the sensor updates
    private void stoptemsensors() {
        stopmusic();
        mSensorManager.unregisterListener(mTemSensorView);
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void starttemsensors() {
        mRunStatus = true;
        // link a sensor to the sensorview
        mTemSensorView.resetcount();
        mSensorManager.registerListener(mTemSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mTemSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mTemSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_FASTEST);
        if (mSoundStatus) startmusic();
    }


    // ============================================================================
    // class defining the sensor display widget
    private class TemSensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        private float lastPresValue = 0.0f;
        private float lastAtempValue = 0.0f;
        private float lastLightValue = 0.0f;

        // initialize the 3 colors, and setup painter
        public TemSensorView(Context context) {
            super(context);
            // text paint
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(2);
            mPaint.setTextSize(24);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            // line paint
            mPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint2.setStrokeWidth(2);
            mPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint2.setColor(Color.RED);

        }

        public void resetcount() {
            lastPresValue = 0.0f;
            lastAtempValue = 0.0f;
            lastLightValue = 0.0f;
        }

        // initialize the bitmap to the size of the view, fill it white
        // init the view state variables to initial values
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(Color.BLACK);
            mWidth = w;
            mHeight = h;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if (mBitmap != null) {
                    // clear the surface
                    mCanvas.drawColor(Color.BLACK);
                    // draw the ambient temperature
                    drawSensor("Press(Kpa)", lastPresValue, 0.0f, 1200.0f, 0, 0, mWidth / 3.0f, mHeight);

                    mCanvas.drawLine(mWidth / 3.0f, 0, mWidth / 3.0f, mHeight, mPaint);

                    // draw the ambient temperature
                    drawSensor("ATemp(°C)", lastAtempValue, -20.0f, 100.0f, mWidth / 3.0f, 0, mWidth / 3.0f, mHeight);

                    mCanvas.drawLine(mWidth / 3.0f * 2.0f, 0, mWidth / 3.0f * 2.0f, mHeight, mPaint);

                    // draw the ambient temperature
                    drawSensor("Light(Lux)", lastLightValue, 0.0f, 200.0f, mWidth / 3.0f * 2.0f, 0, mWidth / 3.0f, mHeight);


                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

        private void drawSensor(String label, float value, float minvalue, float maxvalue,
                                float px, float py, float nx, float ny) {
            // draw a proportionnal large red line for the value
            float linelen = (ny - 64.0f) * ((value - minvalue) / (maxvalue - minvalue));
            mCanvas.drawRect(px + (nx / 3.0f), py + ny - 32 - linelen, px + (nx / 3.0f * 2.0f), py + ny - 32, mPaint2);
            // draw the label on top
            mCanvas.drawText(label, px + 32, py + 24, mPaint);
            // draw the value on bottom
            mCanvas.drawText(String.format("%.2f", value), px + (nx / 3.0f), py + ny - 4, mPaint);
            // draw a center white line showing range
            mCanvas.drawLine(px + (nx / 2.0f), py + 32, px + (nx / 2.0f), py + ny - 32, mPaint);
            // draw scale lines
            mCanvas.drawLine(px + (nx / 2) - 32, py + 32, px + (nx / 2) + 32, py + 32, mPaint); // top white line
            mCanvas.drawLine(px + (nx / 2) - 32, py + ny - 32, px + (nx / 2) + 32, py + ny - 32, mPaint); // bottom white line
            float zerolen = (ny - 64.0f) * ((0.0f - minvalue) / (maxvalue - minvalue));
            if (zerolen > 0.0f)
                mCanvas.drawLine(px + (nx / 2) - 32, py + ny - 32 - zerolen, px + (nx / 2) + 32, py + ny - 32 - zerolen, mPaint); // zero white line
            // draw scale texts
            mCanvas.drawText(String.format("%.0f", maxvalue), px + 4, py + 56, mPaint);  // max value indicator
            mCanvas.drawText(String.format("%.0f", minvalue), px + 4, py + ny - 32, mPaint);  // min value indicator
            if (zerolen > 0.0f)
                mCanvas.drawText("0.0", px + 4, py + ny - 32 - zerolen, mPaint);  // zero indicator
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                        lastAtempValue = event.values[0];
                        invalidate();
                    }
                    if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                        lastPresValue = event.values[0];
                        invalidate();
                    }
                    if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                        lastLightValue = event.values[0];
                        invalidate();
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do
        }

    }

    // ============================================================================
    // stop the sensor updates
    private void stoporisensors() {
        stopmusic();
        mSensorManager.unregisterListener(mOriSensorView);
        mRunStatus = false;
        try {
            locationManager.removeUpdates(mOriSensorView);
        } catch (SecurityException e) {
            say("Error closing GPS");
        }
    }

    // here we start the sensor reading
    private void startorisensors() {
        mRunStatus = true;
        // link a sensor to the sensorview
        mSensorManager.registerListener(mOriSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        if (mSoundStatus) startmusic();
        // start gps location updates
        try {
            locationManager.requestLocationUpdates(locationProvider, 400, 1, mOriSensorView);
        } catch (SecurityException e) {
            say("No GPS avalaible.");
        }

    }

    // ==========================================================================================

    public class OriSensorView extends TextView implements SensorEventListener, LocationListener {

        private Paint paint;
        private float position = 0;

        private Location location = null;

        public OriSensorView(Context context) {
            super(context);
            init();
        }

        private void init() {
            // initialize the paint object
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setTextSize(24);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int xPoint = getMeasuredWidth() / 2;
            int yPoint = getMeasuredHeight() / 2;
            if (yPoint > xPoint) {
                yPoint = xPoint;
            } else {
                xPoint = yPoint;
            }

            float radius = (float) (Math.min(xPoint, yPoint) * 0.9);
            canvas.drawCircle(xPoint, yPoint, radius, paint);
            // canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

            // 3.1416 is a good approximation for the circle
            canvas.drawLine(xPoint,
                    yPoint,
                    (float) (xPoint + radius
                            * Math.sin((double) (-position) / 180 * 3.1416)),
                    (float) (yPoint - radius
                            * Math.cos((double) (-position) / 180 * 3.1416)), paint);

            canvas.drawText("ORI: " + String.valueOf(position), xPoint * 2.0f, yPoint * 2.0f - 32.0f, paint);

            // draw the longitude and latitude
            if (location != null) {
                float lat = (float) (location.getLatitude());
                float lng = (float) (location.getLongitude());
                canvas.drawText("LAT: " + String.valueOf(lat), xPoint * 2.0f, 32.0f, paint);
                canvas.drawText("LON: " + String.valueOf(lng), xPoint * 2.0f, 64.0f, paint);
            } else {
                canvas.drawText("LAT: " + "Not avalaible", xPoint * 2.0f, 32.0f, paint);
                canvas.drawText("LON: " + "Not avalaible", xPoint * 2.0f, 64.0f, paint);
            }
        }

        public void updateData(float position) {
            this.position = position;
            invalidate();
        }

        public void setLocation(Location loc) {
            this.location = loc;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
            float azimuth = event.values[0];
            updateData(azimuth);
        }

        // ============ callbacks for the location listener ===============

        @Override
        public void onLocationChanged(Location location) {
            this.location = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            say("Location Status changed. " + String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            say("Enabled new provider " + provider);

        }

        @Override
        public void onProviderDisabled(String provider) {
            say("Disabled new provider " + provider);
        }


    }

    // ============================================================================
    // stop the sensor updates
    private void stopmagsensors() {
        stopmusic();
        mSensorManager.unregisterListener(mMagSensorView);
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void startmagsensors() {
        mRunStatus = true;
        // link a sensor to the sensorview
        mMagSensorView.resetcount();
        mSensorManager.registerListener(mMagSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        if (mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class MagSensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[] = new int[3];
        private float mWidth;
        private float mHeight;
        private float mYOffset;
        private float mScale;
        private float mSpeed = 0.5f;

        // table of values for the trace
        private int MAXVALUES = 300;
        private float mValues[] = new float[MAXVALUES * 3];
        private int nbValues = 0;

        // initialize the 3 colors, and setup painter
        public MagSensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            for (int i = 0; i < (MAXVALUES * 3); ++i) {
                mValues[i] = 0.0f;
            }
            nbValues = 0;
        }

        public void resetcount() {
            nbValues = 0;
        }

        // initialize the bitmap to the size of the view, fill it white
        // init the view state variables to initial values
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(Color.BLACK);
            mYOffset = h * 0.5f;
            mScale = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            mSpeed = mWidth / MAXVALUES;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if (mBitmap != null) {
                    // clear the surface
                    mCanvas.drawColor(Color.BLACK);
                    // draw middle line horizontal
                    mPaint.setColor(0xffaaaaaa);
                    mPaint.setStrokeWidth(1.0f);
                    mCanvas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                    // draw the 100 values x 3 rows
                    for (int i = 0; i < nbValues - 1; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            int k = (j * MAXVALUES) + i;
                            float oldx = i * mSpeed;
                            float newx = (i + 1) * mSpeed;
                            mPaint.setColor(mColor[j]);
                            mPaint.setStrokeWidth(3.0f);
                            mCanvas.drawLine(oldx, mValues[k], newx, mValues[k + 1], mPaint);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        // scroll left when full
                        if (nbValues >= MAXVALUES) {
                            for (int i = 0; i < (MAXVALUES * 3) - 1; ++i) {
                                mValues[i] = mValues[i + 1];
                            }
                            nbValues--;
                        }
                        // fill the 3 elements in the table
                        for (int i = 0; i < 3; ++i) {
                            final float v = mYOffset + event.values[i] * mScale;
                            mValues[nbValues + (i * MAXVALUES)] = v;
                        }
                        nbValues++;
                        invalidate();
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do
        }

    }


    // ============================================================================
    // stop the sensor updates
    private void stopgrasensors() {
        stopmusic();
        mSensorManager.unregisterListener(mGraSensorView);
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void startgrasensors() {
        mRunStatus = true;
        // link a sensor to the sensorview
        mGraSensorView.resetcount();
        mSensorManager.registerListener(mGraSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        if (mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class GraSensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[] = new int[3];
        private float mWidth;
        private float mHeight;
        private float mYOffset;
        private float mScale;
        private float mSpeed = 0.5f;

        // table of values for the trace
        private int MAXVALUES = 300;
        private float mValues[] = new float[MAXVALUES * 3];
        private int nbValues = 0;

        // initialize the 3 colors, and setup painter
        public GraSensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            for (int i = 0; i < (MAXVALUES * 3); ++i) {
                mValues[i] = 0.0f;
            }
            nbValues = 0;
        }

        public void resetcount() {
            nbValues = 0;
        }

        // initialize the bitmap to the size of the view, fill it white
        // init the view state variables to initial values
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            mCanvas.setBitmap(mBitmap);
            mCanvas.drawColor(Color.BLACK);
            mYOffset = h * 0.5f;
            mScale = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            mWidth = w;
            mHeight = h;
            mSpeed = mWidth / MAXVALUES;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if (mBitmap != null) {
                    // clear the surface
                    mCanvas.drawColor(Color.BLACK);
                    // draw middle line horizontal
                    mPaint.setColor(0xffaaaaaa);
                    mPaint.setStrokeWidth(1.0f);
                    mCanvas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                    // draw the 100 values x 3 rows
                    for (int i = 0; i < nbValues - 1; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            int k = (j * MAXVALUES) + i;
                            float oldx = i * mSpeed;
                            float newx = (i + 1) * mSpeed;
                            mPaint.setColor(mColor[j]);
                            mPaint.setStrokeWidth(3.0f);
                            mCanvas.drawLine(oldx, mValues[k], newx, mValues[k + 1], mPaint);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        // scroll left when full
                        if (nbValues >= MAXVALUES) {
                            for (int i = 0; i < (MAXVALUES * 3) - 1; ++i) {
                                mValues[i] = mValues[i + 1];
                            }
                            nbValues--;
                        }
                        // fill the 3 elements in the table
                        for (int i = 0; i < 3; ++i) {
                            final float v = mYOffset + event.values[i] * mScale;
                            mValues[nbValues + (i * MAXVALUES)] = v;
                        }
                        nbValues++;
                        invalidate();
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do
        }

    }


    // ========================================================================================
    // functions to listen to the surface texture view

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = null;
        if (mViewerfront == false) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewTexture(surface);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.set("orientation", "portrait");
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (IOException ioe) {
                say("Something bad happened with camera");
            } catch (Exception sex) {
                say("camera permission refused");
            }
        } else {
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                mCamera.setPreviewTexture(surface);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.set("orientation", "portrait");
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (IOException ioe) {
                say("Something bad happened with camera");
            } catch (Exception sex) {
                say("camera permission refused");
            }
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    // switch between cameras
    private void switchcam(int no) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        switch (no) {
            case 1:
                try {
                    mCamera = Camera.open();
                    mCamera.setPreviewTexture(mViewerWindow.getSurfaceTexture());
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.set("orientation", "portrait");
                    mCamera.setParameters(parameters);
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                } catch (IOException ioe) {
                    say("Something bad happened with camera");
                } catch (Exception sex) {
                    say("camera permission refused");
                }
                break;
            case 2:
                try {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    mCamera.setPreviewTexture(mViewerWindow.getSurfaceTexture());
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.set("orientation", "portrait");
                    mCamera.setParameters(parameters);
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                } catch (IOException ioe) {
                    say("Something bad happened with camera");
                } catch (Exception sex) {
                    say("camera permission refused");
                }
                break;
        }
    }

    // ==========================================================================================
    // switch the viewer on/off
    private void switchviewer(int no) {
        mViewerWindow.setVisibility(View.GONE);
        mFederationlogo.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        mLogsInfo.setVisibility(View.GONE);
        mStarshipPlan.setVisibility(View.GONE);
        switch (no) {
            case 0:
                say("Viewer OFF");
                mFederationlogo.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 1:
                say("Viewer ON");
                mViewerWindow.setVisibility(View.VISIBLE);
                mVieweron = true;
                break;
            case 2:
                say("Logs Console");
                mLogsConsole.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 3:
                say("Logs Info");
                mLogsInfo.setVisibility(View.VISIBLE);
                mVieweron = false;
                mLogsInfo.setText("");
                mLogsInfo.append("--------------------\nConnectivity\n--------------------\n");
                mLogsInfo.append(fetch_connectivity());
                mLogsInfo.append("--------------------\nTelephony\n--------------------\n");
                mLogsInfo.append(fetch_tel_status());
                mLogsInfo.append("--------------------\nNetwork\n--------------------\n");
                mLogsInfo.append(fetch_network_info());
                mLogsInfo.append("--------------------\nSystem\n--------------------\n");
                mLogsInfo.append(fetch_system_info());
                mLogsInfo.append("--------------------\nOperSys\n--------------------\n");
                mLogsInfo.append(fetch_os_info());
                //mLogsInfo.append("--------------------\nDmesg\n--------------------\n");
                //mLogsInfo.append(fetch_dmesg_info());
                //mLogsInfo.append("--------------------\nProcess\n--------------------\n");
                //mLogsInfo.append(fetch_process_info());
                break;
            case 4:
                say("Plans");
                mStarshipPlan.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
        }
        mViewermode = no;
    }

    // =====================================================================================
    // ask the camera to take a photo, and pass it to onPictureTaken


    private void snapphoto() {
        if (mVieweron) {
            say("Picture Taken !");
            mCamera.takePicture(null, null, this);
        }
    }

    // photo saving of picture taken callback
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageFileUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try {
            OutputStream imageFileOS = getActivity().getContentResolver().openOutputStream(
                    imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (Exception e) {
            Toast t = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        }
        camera.startPreview();
        say("Picture taken !");
    }

    // ===================================================================================
    // common functions to obtain a media uri

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Trycorder", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    // ==========================================================================================
    // call camera and gallery application

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUri;

    private void takephoto() {
        say("Open Photo application");
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);  // create a file to save the picture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void recordvideo() {
        say("Open Video application");
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                say("Saved to " + fileUri.toString());
                // inform the media manager to scan our new file
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(fileUri);
                getActivity().sendBroadcast(intent);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the image capture
                say("Cancelled Photo");
            } else {
                // Image capture failed, advise user
                say("Failed Saving Photo");
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                //say("" + data.getData());
                say("Saved to " + fileUri.toString());
                // inform the media manager to scan our new file
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(fileUri);
                getActivity().sendBroadcast(intent);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the video capture
                say("Cancelled Video");
            } else {
                // Video capture failed, advise user
                say("Failed Saving Video");
            }
        }
    }

    private void opengallery() {
        say("Open Gallery application");
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // ========================================================================================
    // functions to listen to the voice recognition callbacks

    // =================================================================================
    // listener for the speech recognition service


    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> dutexte = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (dutexte != null && dutexte.size() > 0) {
            for (int i = 0; i < dutexte.size(); ++i) {
                mSentence = dutexte.get(i);
                if (matchvoice(mSentence)) {
                    mTextstatus_top.setText(mSentence);
                    say("Said: " + mSentence);
                    return;
                }
            }
            mTextstatus_top.setText(dutexte.get(0));
            say("Understood: " + dutexte.get(0));
            speak("Unknown command.");
        }
    }


    @Override
    public void onRmsChanged(float rmsdB) {
    }

    // ========================================================================================
    // functions to control the speech process

    private void listen() {
        if (listenLanguage.equals("FR")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        } else if (listenLanguage.equals("EN")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        } else {
            // automatic
        }
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        mTextstatus_top.setText("");
        say("Speak");
    }

    private void speak(String texte) {
        if (speakLanguage.equals("FR")) {
            tts.setLanguage(Locale.FRENCH);
        } else if (speakLanguage.equals("EN")) {
            tts.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say(texte);
    }

    private StringBuffer logbuffer = new StringBuffer(500);

    private void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0, texte + "\n");
        mLogsConsole.setText(logbuffer);
    }

    private boolean matchvoice(String textein) {
        String texte = textein.toLowerCase();
        if (texte.contains("martin")) {
            switchbuttonlayout(0);
            speak("Martin is my Master.");
            return (true);
        }
        if (texte.contains("phaser")) {
            switchbuttonlayout(4);
            firephaser();
            return (true);
        }
        if (texte.contains("fire") || texte.contains("torpedo")) {
            switchbuttonlayout(4);
            firemissiles();
            return (true);
        }
        if (texte.contains("shield") && texte.contains("down")) {
            switchbuttonlayout(3);
            lowershields();
            return (true);
        }
        if (texte.contains("shield") || texte.contains("raise")) {
            switchbuttonlayout(3);
            raiseshields();
            return (true);
        }
        if (texte.contains("sensor off")) {
            switchbuttonlayout(0);
            switchsensorlayout(0);
            stopsensors();
            return (true);
        }
        if (texte.contains("sensor") || texte.contains("magnetic")) {
            switchbuttonlayout(1);
            switchsensorlayout(1);
            startsensors(1);
            return (true);
        }
        if (texte.contains("orientation") || texte.contains("direction")) {
            switchbuttonlayout(1);
            switchsensorlayout(2);
            startsensors(2);
            return (true);
        }
        if (texte.contains("gravity") || texte.contains("vibration")) {
            switchbuttonlayout(1);
            switchsensorlayout(3);
            startsensors(3);
            return (true);
        }
        if (texte.contains("hailing") && texte.contains("close")) {
            switchbuttonlayout(2);
            closecomm();
            return (true);
        }
        if (texte.contains("hailing")) {
            switchbuttonlayout(2);
            opencomm();
            return (true);
        }
        if (texte.contains("beam me up") || texte.contains("scotty")) {
            switchbuttonlayout(5);
            transporterin();
            return (true);
        }
        if (texte.contains("beam me down")) {
            switchbuttonlayout(5);
            transporterout();
            return (true);
        }
        if (texte.contains("viewer")) {
            switchbuttonlayout(6);
            switchviewer(1);
            return (true);
        }
        if (texte.contains("logs")) {
            switchbuttonlayout(7);
            switchviewer(2);
            return (true);
        }
        return (false);
    }

    // =================================================================================
    // run a command line program with args, return the printed output
    // invoque with run(new String[] { "ls", "-la" },"/data/data");
    public synchronized String run(String[] cmd, String workdirectory)
            throws IOException {
        String result = "";

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            // set working directory
            if (workdirectory != null)
                builder.directory(new File(workdirectory));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    // =================================================================================
    // functions to fetch info from the system

    // =================== fetch telephone status =======================

    public String fetch_tel_status() {
        Context cx=getContext();
        String result = null;
        TelephonyManager tm = (TelephonyManager) cx
                .getSystemService(Context.TELEPHONY_SERVICE);//
        String str = "";
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion()
                + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "PhoneType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";

        int mcc = cx.getResources().getConfiguration().mcc;
        int mnc = cx.getResources().getConfiguration().mnc;
        str += "IMSI MCC (Mobile Country Code):" + String.valueOf(mcc) + "\n";
        str += "IMSI MNC (Mobile Network Code):" + String.valueOf(mnc) + "\n";
        result = str;
        return result;
    }

    // ================ fetch process info ===================

    public String fetch_process_info() {
        String result = "";
        try {
            String[] args = {"/system/bin/top", "-n", "1"};
            result = run(args, "/system/bin/");
        } catch (IOException ex) {
            say("fetch_process_info ex=" + ex.toString());
        }
        return result;
    }

    // ================= fetch network info ===================

    public String fetch_dmesg_info() {
        String result = "";
        try {
            String[] args = {"/system/bin/dmesg"};
            result = run(args, "/system/bin/");
        } catch (IOException ex) {
            say("fetch_dmesg_info ex=" + ex.toString());
        }
        return result;
    }

    // ============ fetch system info ===========
    private StringBuffer buffer;

    public String fetch_system_info() {
        buffer = new StringBuffer();
        initProperty("java.vendor.url", "java.vendor.url");
        initProperty("java.class.path", "java.class.path");
        initProperty("user.home", "user.home");
        initProperty("java.class.version", "java.class.version");
        initProperty("os.version", "os.version");
        initProperty("java.vendor", "java.vendor");
        initProperty("user.dir", "user.dir");
        initProperty("user.timezone", "user.timezone");
        initProperty("path.separator", "path.separator");
        initProperty(" os.name", " os.name");
        initProperty("os.arch", "os.arch");
        initProperty("line.separator", "line.separator");
        initProperty("file.separator", "file.separator");
        initProperty("user.name", "user.name");
        initProperty("java.version", "java.version");
        initProperty("java.home", "java.home");
        return buffer.toString();
    }

    private void initProperty(String description, String propertyStr) {
        buffer.append(description).append(":");
        buffer.append(System.getProperty(propertyStr)).append("\n");
    }

    // ================= fetch os information ===================

    private String fetch_os_info() {
        StringBuffer sInfo = new StringBuffer();
        final ActivityManager activityManager =
                (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(100);
        Iterator<ActivityManager.RunningTaskInfo> l = tasks.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningTaskInfo ti = (ActivityManager.RunningTaskInfo) l.next();
            sInfo.append("id: ").append(ti.id);
            sInfo.append("\nbaseActivity: ").append(
                    ti.baseActivity.flattenToString());
            sInfo.append("\nnumActivities: ").append(ti.numActivities);
            sInfo.append("\nnumRunning: ").append(ti.numRunning);
            sInfo.append("\ndescription: ").append(ti.description);
            sInfo.append("\n\n");
        }
        return sInfo.toString();
    }

    // ================= fetch network info ===================

    public String fetch_network_info() {
        String result = "";
        NetInfo netinfo = new NetInfo(getContext());
        result += String.format("Network type : %d\n", netinfo.getCurrentNetworkType());
        result += String.format("Wifi IP Addr : %s\n", netinfo.getWifiIpAddress());
        result += String.format("Wifi MAC Addr : %s\n", netinfo.getWiFiMACAddress());
        result += String.format("Wifi SSID : %s\n", netinfo.getWiFiSSID());
        result += String.format("IP Address : %s\n", netinfo.getIPAddress());
        return result;
    }

    // ====================================================================================
    // public functions to obtain different infos from network interface

    public class NetInfo {
        private ConnectivityManager connManager = null;
        private WifiManager wifiManager = null;
        private WifiInfo wifiInfo = null;

        public NetInfo(Context context) {
            connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiInfo = wifiManager.getConnectionInfo();
        }

        public int getCurrentNetworkType() {
            if (null == connManager)
                return 0;

            NetworkInfo netinfo = connManager.getActiveNetworkInfo();

            return netinfo.getType();
        }

        public String getWifiIpAddress() {
            if (null == wifiManager || null == wifiInfo)
                return "";

            int ipAddress = wifiInfo.getIpAddress();

            return String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }

        public String getWiFiMACAddress() {
            if (null == wifiManager || null == wifiInfo)
                return "";

            return wifiInfo.getMacAddress();
        }

        public String getWiFiSSID() {
            if (null == wifiManager || null == wifiInfo)
                return "";

            return wifiInfo.getSSID();
        }

        public String getIPAddress() {
            String ipaddress = "";

            try {
                Enumeration<NetworkInterface> enumnet = NetworkInterface.getNetworkInterfaces();
                NetworkInterface netinterface = null;

                while (enumnet.hasMoreElements()) {
                    netinterface = enumnet.nextElement();

                    for (Enumeration<InetAddress> enumip = netinterface.getInetAddresses();
                         enumip.hasMoreElements(); ) {
                        InetAddress inetAddress = enumip.nextElement();

                        if (!inetAddress.isLoopbackAddress()) {
                            ipaddress = inetAddress.getHostAddress();

                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            return ipaddress;
        }
    }

    // =================================================================================
    // fetch the internet connectivity

    private String fetch_connectivity() {
        if(checkInternet()) {
            return "INTERNET is Active\n";
        }
        return "INTERNET is Off\n";
    }

    // check internet connectivity
    public boolean checkInternet() {
        ConnectivityManager connect = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

}


