package net.ddns.mlsoftlaberge.trycorder;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.support.v4.app.ActivityCompat;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Timer;
import java.util.TimerTask;

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

    // the new scope class
    private AudSensorView mAudSensorView;

    // the new scope class
    private ShiSensorView mShiSensorView;

    // the new scope class
    private FirSensorView mFirSensorView;

    // the new scope class
    private TraSensorView mTraSensorView;

    // the new scope class
    private TrbSensorView mTrbSensorView;

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
    private int mSensorpage = 0;

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

    // the button to fire at ennemys
    private Button mTractorButton;
    private Button mTractorPullButton;
    private Button mTractorOffButton;
    private Button mTractorPushButton;

    // the button to control the viewer
    private Button mViewerButton;
    private Button mViewerOnButton;
    private Button mViewerFrontButton;
    private Button mViewerOffButton;
    private Button mViewerPhotoButton;
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
    private LinearLayout mSensor2tractorLayout;
    private LinearLayout mSensor2viewerLayout;
    private LinearLayout mSensor2logsLayout;
    private int mSensor2mode = 0;

    private LinearLayout mSensor3Layout;
    private ImageView mFederationlogo;
    private ScrollView mStarshipPlans;
    private ImageView mViewerPhoto;

    // the player for sound background
    private MediaPlayer mMediaPlayer = null;

    // the preferences values
    private boolean autoListen;
    private String speakLanguage;
    private String listenLanguage;
    private String displayLanguage;

    // the preferences holder
    private SharedPreferences sharedPref;

    // ==========================================================================

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
                buttonsound();
                switchbuttonlayout(1);
                switchsensorlayout(mSensorpage);
                startsensors(mSensorpage);
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
                switchsensorlayout(5);
                buttonsound();
            }
        });
        // the open comm button
        mOpenCommButton = (Button) view.findViewById(R.id.opencomm_button);
        mOpenCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchsensorlayout(5);
                opencomm();
            }
        });
        // the close comm button
        mCloseCommButton = (Button) view.findViewById(R.id.closecomm_button);
        mCloseCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchsensorlayout(5);
                closecomm();
            }
        });

        // ===================== shield buttons group ============================
        // the shield button
        mShieldButton = (Button) view.findViewById(R.id.shield_button);
        mShieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(3);
                switchsensorlayout(6);
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
                buttonsound();
                switchbuttonlayout(4);
                switchsensorlayout(7);
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
                buttonsound();
                switchbuttonlayout(5);
                switchsensorlayout(8);
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

        // ===================== transporter buttons group ============================
        // the tractor button
        mTractorButton = (Button) view.findViewById(R.id.tractor_button);
        mTractorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(6);
                switchsensorlayout(9);
            }
        });

        // the tractor push button
        mTractorPushButton = (Button) view.findViewById(R.id.tractor_push_button);
        mTractorPushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractorpush();
            }
        });

        // the tractor off button
        mTractorOffButton = (Button) view.findViewById(R.id.tractor_off_button);
        mTractorOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractoroff();
            }
        });

        // the tractor pull button
        mTractorPullButton = (Button) view.findViewById(R.id.tractor_pull_button);
        mTractorPullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractorpull();
            }
        });

        // ===================== viewer buttons group ============================
        // the viewer button
        mViewerButton = (Button) view.findViewById(R.id.viewer_button);
        mViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(7);
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

        mViewerPhotoButton = (Button) view.findViewById(R.id.viewerphoto_button);
        mViewerPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchcam(0);
                switchviewer(5);
            }
        });

        // ===================== viewer buttons group ============================
        // the viewer button
        mLogsButton = (Button) view.findViewById(R.id.logs_button);
        mLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(8);
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
        mSensor2tractorLayout = (LinearLayout) view.findViewById(R.id.sensor2_tractor_layout);
        mSensor2viewerLayout = (LinearLayout) view.findViewById(R.id.sensor2_viewer_layout);
        mSensor2logsLayout = (LinearLayout) view.findViewById(R.id.sensor2_logs_layout);

        // the sensor layout, to contain my surfaceview
        mSensor3Layout = (LinearLayout) view.findViewById(R.id.sensor3_layout);
        mFederationlogo = (ImageView) view.findViewById(R.id.federation_logo);
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);
        mLogsInfo = (TextView) view.findViewById(R.id.logs_info);
        mStarshipPlans = (ScrollView) view.findViewById(R.id.starship_plans);
        mViewerPhoto =  (ImageView) view.findViewById(R.id.photo_view);

        // create and activate a textureview to contain camera display
        mViewerWindow = (TextureView) view.findViewById(R.id.viewer_window);
        mViewerWindow.setSurfaceTextureListener(this);

        mFederationlogo.setVisibility(View.VISIBLE);
        mViewerWindow.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        mLogsInfo.setVisibility(View.GONE);
        mStarshipPlans.setVisibility(View.GONE);
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

        // my sensorview that display the sensors data
        mAudSensorView = new AudSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mAudSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mShiSensorView = new ShiSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mShiSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mFirSensorView = new FirSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mFirSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mTraSensorView = new TraSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mTraSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mTrbSensorView = new TrbSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mTrbSensorView, tlayoutParams);

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

        // produce a FC on android 4.0.3
        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);

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
        mTractorButton.setTypeface(face2);
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

        mTransportOutButton.setTypeface(face3);
        mTransportInButton.setTypeface(face3);

        mTractorPushButton.setTypeface(face3);
        mTractorOffButton.setTypeface(face3);
        mTractorPullButton.setTypeface(face3);

        mViewerOnButton.setTypeface(face3);
        mViewerFrontButton.setTypeface(face3);
        mViewerOffButton.setTypeface(face3);

        mLogsConsoleButton.setTypeface(face3);
        mLogsInfoButton.setTypeface(face3);
        mLogsPlansButton.setTypeface(face3);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensormode = sharedPref.getInt("pref_key_sensor_mode", 0);
        mSensorpage = sharedPref.getInt("pref_key_sensor_page", 0);
        mSensor2mode = sharedPref.getInt("pref_key_sensor2_mode", 0);
        mViewermode = sharedPref.getInt("pref_key_viewer_mode", 0);
        mSoundStatus = sharedPref.getBoolean("pref_key_audio_mode", false);
        mViewerfront = sharedPref.getBoolean("pref_key_viewer_front", false);
        switchbuttonlayout(mSensor2mode);
        switchsensorlayout(mSensormode);
        switchviewer(mViewermode);
        if(mSensormode<=4) startsensors(mSensormode);
    }

    @Override
    public void onPause() {
        stopsensors();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("pref_key_sensor_mode", mSensormode);
        editor.putInt("pref_key_sensor_page", mSensorpage);
        editor.putInt("pref_key_sensor2_mode", mSensor2mode);
        editor.putInt("pref_key_viewer_mode", mViewermode);
        editor.putBoolean("pref_key_audio_mode", mSoundStatus);
        editor.putBoolean("pref_key_viewer_front", mViewerfront);
        editor.commit();
        super.onPause();
    }


    private void startsensors(int mode) {
        stopsensors();
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
            case 5:
                say("Sensors Audio Wave");
                startaudsensors();
                break;
            case 6:
                say("Sensors Shields");
                startshisensors();
                break;
            case 7:
                say("Sensors Fire Animation");
                startfirsensors();
                break;
            case 8:
                say("Sensors Transport Animation");
                starttrasensors();
                break;
            case 9:
                say("Sensors Tractor Animation");
                starttrbsensors();
                break;
            default:
                say("Sensors OFF");
                break;
        }
        if(mode<=4) mSensorpage = mode;
        mSensormode = mode;
    }

    private void stopsensors() {
        stopmagsensors();
        stoporisensors();
        stopgrasensors();
        stoptemsensors();
        stopaudsensors();
        stopshisensors();
        stopfirsensors();
        stoptrasensors();
        stoptrbsensors();
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
        mAudSensorView.setVisibility(View.GONE);
        mShiSensorView.setVisibility(View.GONE);
        mFirSensorView.setVisibility(View.GONE);
        mTraSensorView.setVisibility(View.GONE);
        mTrbSensorView.setVisibility(View.GONE);
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
            case 5:
                mAudSensorView.setVisibility(View.VISIBLE);
                break;
            case 6:
                mShiSensorView.setVisibility(View.VISIBLE);
                break;
            case 7:
                mFirSensorView.setVisibility(View.VISIBLE);
                break;
            case 8:
                mTraSensorView.setVisibility(View.VISIBLE);
                break;
            case 9:
                mTrbSensorView.setVisibility(View.VISIBLE);
                break;
        }
        if(no<=4) mSensorpage = no;
        mSensormode = no;
    }

    // =====================================================================================

    private void switchbuttonlayout(int no) {
        mSensor2sensorLayout.setVisibility(View.GONE);
        mSensor2commLayout.setVisibility(View.GONE);
        mSensor2shieldLayout.setVisibility(View.GONE);
        mSensor2fireLayout.setVisibility(View.GONE);
        mSensor2transporterLayout.setVisibility(View.GONE);
        mSensor2tractorLayout.setVisibility(View.GONE);
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
                say("Tractor Mode");
                mSensor2tractorLayout.setVisibility(View.VISIBLE);
                break;
            case 7:
                say("Viewer Mode");
                mSensor2viewerLayout.setVisibility(View.VISIBLE);
                break;
            case 8:
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
        switchsensorlayout(5);
        startsensors(5);
        say("Hailing frequency open.");
    }

    private void closecomm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commclose);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        switchsensorlayout(5);
        stopsensors();
        say("Hailing frequency closed.");
    }

    private void transporterout() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1a);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport Out");
        switchsensorlayout(8);
        mTraSensorView.setmode(2);
        startsensors(8);
    }

    private void transporterin() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1b);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport In");
        switchsensorlayout(8);
        mTraSensorView.setmode(1);
        startsensors(8);
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
        switchsensorlayout(6);
        mShiSensorView.setmode(1);
        startsensors(6);
    }

    private void lowershields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shielddown);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Lower Shields");
        switchsensorlayout(6);
        mShiSensorView.setmode(2);
        startsensors(6);
    }

    private void tractorpush() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Push");
        switchsensorlayout(9);
        mTrbSensorView.setmode(1);
        startsensors(9);
    }

    private void tractoroff() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Tractor Beam Off");
        switchsensorlayout(9);
        mTrbSensorView.setmode(0);
        stopsensors();
    }

    private void tractorpull() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Pull");
        switchsensorlayout(9);
        mTrbSensorView.setmode(2);
        startsensors(9);
    }

    private void firephaser() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.phasertype2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Phaser");
        switchsensorlayout(7);
        mFirSensorView.setmode(2);
        startsensors(7);
    }

    private void firemissiles() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.photorp1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Torpedo");
        switchsensorlayout(7);
        mFirSensorView.setmode(1);
        startsensors(7);
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
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.scan_low);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
                case 4:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.scan_high);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start(); // no need to call prepare(); create() does that for you
                    break;
                case 9:
                    mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.tng_tractor_clean);
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

    // ==============================================================================
    // shield sensor, display person disappearing

    private void stoptrbsensors() {
        stopmusic();
        mTrbSensorView.stop();
    }

    private void starttrbsensors() {
        mTrbSensorView.start();
        if (mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class TrbSensorView extends TextView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        private int mode;   // 1=in 2=out

        private int position=0;

        // initialize the 3 colors, and setup painter
        public TrbSensorView(Context context) {
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
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setColor(Color.MAGENTA);
        }

        public void setmode(int no) {
            mode=no;
            if(mode==0) stop();
        }

        // ======= timer section =======
        private Timer timer=null;
        private MyTimer myTimer;

        public void stop() {
            if(timer!=null) {
                timer.cancel();
                timer=null;
            }
            position=0;
            invalidate();
        }

        public void start() {
            // start the timer to eat this stuff and display it
            position=1;
            timer = new Timer("tractor");
            myTimer = new MyTimer();
            timer.schedule(myTimer, 10L, 10L);
        }

        private class MyTimer extends TimerTask {
            public void run() {
                position+=3;
                postInvalidate();
                if(position>=100) {
                    position=1;
                    postInvalidate();
                }
            }
        }

        // =========== textview callbacks =================
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
                    // draw the shield effect
                    if(mode==1) mPaint2.setColor(Color.MAGENTA);
                    else mPaint2.setColor(Color.BLUE);
                    if(position!=0) {
                        // compute positions
                        float px=mWidth/2;
                        float dx=position;
                        mCanvas.drawLine(px,mHeight,px-dx,0,mPaint2);
                        mCanvas.drawLine(px,mHeight,px,0,mPaint2);
                        mCanvas.drawLine(px,mHeight,px+dx,0,mPaint2);
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

    }

    // ==============================================================================
    // shield sensor, display shields effects

    private void stopshisensors() {
        mShiSensorView.stop();
    }

    private void startshisensors() {
        mShiSensorView.start();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class ShiSensorView extends TextView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Paint mPaint3 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        private int mode;   // 1=in 2=out

        private int position=0;

        // initialize the 3 colors, and setup painter
        public ShiSensorView(Context context) {
            super(context);
            // text paint
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(2);
            mPaint.setTextSize(24);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            // circle paint
            mPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint2.setStrokeWidth(2);
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setColor(Color.GREEN);
            // line paint
            mPaint3.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint3.setStrokeWidth(2);
            mPaint3.setStyle(Paint.Style.STROKE);
            mPaint3.setColor(Color.LTGRAY);
        }

        public void setmode(int no) {
            mode=no;
        }

        // ======= timer section =======
        private Timer timer=null;
        private MyTimer myTimer;

        public void stop() {
            if(timer!=null) {
                timer.cancel();
                timer=null;
            }
        }

        public void start() {
            position=1;
            // start the timer to eat this stuff and display it
            timer = new Timer("shield");
            myTimer = new MyTimer();
            timer.schedule(myTimer, 10L, 10L);
        }

        private class MyTimer extends TimerTask {
            public void run() {
                position+=3;
                postInvalidate();
                if(position>=250) {
                    cancel();
                    position=0;
                    postInvalidate();
                }
            }
        }

        // =========== textview callbacks =================
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
                    // ajust alpha of grid
                    if(position==0) {
                        if(mode==1) {
                            mPaint3.setAlpha(255);
                        } else {
                            mPaint3.setAlpha(0);
                        }
                    } else {
                        if(mode==1) {
                            mPaint3.setAlpha(position);
                        } else {
                            mPaint3.setAlpha(255-position);
                        }
                    }
                    // draw the grid
                    for(int i=0;i<10;++i) {
                        mCanvas.drawLine(0,mHeight/10*i,mWidth,mHeight/10*i,mPaint3);
                        mCanvas.drawLine(mWidth/10*i,0,mWidth/10*i,mHeight,mPaint3);
                    }
                    // draw the circle effect
                    if(position!=0) {
                        if (mode == 1) {
                            mCanvas.drawCircle(mWidth / 2, mHeight / 2, position, mPaint2);
                        } else {
                            mCanvas.drawCircle(mWidth / 2, mHeight / 2, 250 - position, mPaint2);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

    }

    // ==============================================================================
    // transporter sensor, display person disappearing

    private void stoptrasensors() {
        mTraSensorView.stop();
    }

    private void starttrasensors() {
        mTraSensorView.start();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class TraSensorView extends TextView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Paint mPaint3 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        private int mode=1;   // 1=in 2=out

        private int position=0;

        // initialize the 3 colors, and setup painter
        public TraSensorView(Context context) {
            super(context);
            // text paint
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(2);
            mPaint.setTextSize(24);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            // circle paint
            mPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint2.setStrokeWidth(2);
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setColor(Color.BLUE);
            // line paint
            mPaint3.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint3.setStrokeWidth(2);
            mPaint3.setStyle(Paint.Style.STROKE);
            mPaint3.setColor(Color.LTGRAY);
        }

        public void setmode(int no) {
            mode=no;
        }

        // ======= timer section =======
        private Timer timer=null;
        private MyTimer myTimer;

        public void stop() {
            if(timer!=null) {
                timer.cancel();
                timer=null;
            }
        }

        public void start() {
            position=1;
            // start the timer to eat this stuff and display it
            timer = new Timer("transporter");
            myTimer = new MyTimer();
            timer.schedule(myTimer, 10L, 10L);
        }

        private class MyTimer extends TimerTask {
            public void run() {
                position++;
                postInvalidate();
                if(position>=250) {
                    cancel();
                    position=0;
                    postInvalidate();
                }
            }
        }

        // =========== textview callbacks =================
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
                    // ajust alpha level of person
                    if(position==0) {
                        if(mode==1) {
                            mPaint3.setAlpha(255);
                        } else {
                            mPaint3.setAlpha(0);
                        }
                    } else {
                        if(mode==1) {
                            mPaint3.setAlpha(position);
                        } else {
                            mPaint3.setAlpha(255-position);
                        }
                    }
                    // draw the vertical person
                    int px=mWidth/2;
                    int py=mHeight/2;
                    int len=mHeight/5;
                    mCanvas.drawLine(px,py-len,px,py+len,mPaint3);
                    mCanvas.drawLine(px-len/2,py,px+len/2,py,mPaint3);
                    mCanvas.drawLine(px,py+len,px-len/2,py+len*2,mPaint3);
                    mCanvas.drawLine(px,py+len,px+len/2,py+len*2,mPaint3);
                    mCanvas.drawCircle(px,py-len-len/2,len/2,mPaint3);
                    // draw the transport circle effect
                    if(position!=0) {
                        if (mode == 1) {
                            mCanvas.drawCircle(mWidth / 2, mHeight / 2, position, mPaint2);
                        } else {
                            mCanvas.drawCircle(mWidth / 2, mHeight / 2, 250 - position, mPaint2);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

    }

    // ==============================================================================
    // audio sensor, display waveform of ambient sound

    private void stopaudsensors() {
        mAudSensorView.stop();
    }

    private void startaudsensors() {
        mAudSensorView.start();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class AudSensorView extends TextView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        // initialize the 3 colors, and setup painter
        public AudSensorView(Context context) {
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
            mPaint2.setStyle(Paint.Style.STROKE);
            mPaint2.setColor(Color.YELLOW);
        }

        // ======= timer section =======
        private Timer timer=null;
        private MyTimer myTimer;

        private static final int RECORDER_SAMPLERATE = 8000;
        private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

        private AudioRecord mAudioRecord=null;
        private int bufferSize=0;

        int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
        int BytesPerElement = 2; // 2 bytes in 16bit format

        public void stop() {
            if(timer!=null) {
                timer.cancel();
                timer=null;
            }
            if(mAudioRecord!=null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord=null;
            }
        }

        public void start() {
            // start the recording
            bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, bufferSize);
            mAudioRecord.startRecording();
            // start the timer to eat this stuff and display it
            timer = new Timer("audiowave");
            myTimer = new MyTimer();
            timer.schedule(myTimer, 10L, 10L);
        }

        private short sData[] = new short[BufferElements2Rec];
        private int nbe=0;

        private class MyTimer extends TimerTask {
            public void run() {
                nbe = BufferElements2Rec>mWidth ? mWidth : BufferElements2Rec;
                mAudioRecord.read(sData,0,nbe);
                postInvalidate();
            }
        }

        // =========== textview callbacks =================
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
                    // draw the horizontal line
                    mCanvas.drawLine(0,mHeight/2,mWidth,mHeight/2,mPaint);
                    // draw the sound wave
                    int posx;
                    int posy;
                    int newx;
                    int newy;
                    int maxy;
                    int yscale;
                    maxy=mHeight/2;
                    yscale=(32768/maxy)/4;    // increase noise level by 4
                    posx=0;
                    posy=maxy;
                    for(int i=1;i<nbe;++i) {
                        newx=i;
                        newy=maxy+(sData[i]/yscale);
                        mCanvas.drawLine((float)posx,(float)posy,(float)newx,(float)newy,mPaint2);
                        posx=newx;
                        posy=newy;
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
        }

    }

    // ==============================================================================
    // firmation sensor, (ex: firing missiles)

    private void stopfirsensors() {
        stopmusic();
    }

    private void startfirsensors() {
        mFirSensorView.resetcount();
        if (mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class FirSensorView extends TextView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Paint mPaint2 = new Paint();
        private Canvas mCanvas = new Canvas();

        private int mWidth;
        private int mHeight;

        private float position = 0.0f;
        private int firMode = 0;

        private Timer timer;
        private MyTimer myTimer;

        // initialize the 3 colors, and setup painter
        public FirSensorView(Context context) {
            super(context);
            // text paint
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(2);
            mPaint.setTextSize(24);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            // line paint
            mPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint2.setStrokeWidth(16);
            mPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint2.setColor(Color.RED);

        }

        public void setmode(int no) {
            firMode = no;
        }

        public void resetcount() {
            position = 0.0f;
            timer = new Timer("fire");
            myTimer = new MyTimer();
            timer.schedule(myTimer, 10L, 10L);
        }

        private class MyTimer extends TimerTask {
            public void run() {
                if(firMode==1) position += 10;
                else position += 5;
                postInvalidate();
                if (position > mHeight) {
                    cancel();
                    position = 0;
                    postInvalidate();
                }
            }
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
                    // draw the grid
                    mCanvas.drawLine(mWidth/3,0,mWidth/3,mHeight,mPaint);
                    mCanvas.drawLine(mWidth/3.0f*2.0f,0,mWidth/3.0f*2.0f,mHeight,mPaint);
                    mCanvas.drawLine(0,mHeight/3,mWidth,mHeight/3,mPaint);
                    mCanvas.drawLine(0,mHeight/3.0f*2.0f,mWidth,mHeight/3.0f*2.0f,mPaint);
                    // draw the shooting line
                    if (position != 0.0f) {
                        switch (firMode) {
                            case 1:
                                mCanvas.drawLine(mWidth / 2.0f, mHeight - position + 32, mWidth / 2.0f, mHeight - position, mPaint2);
                                break;
                            case 2:
                                mCanvas.drawLine(mWidth / 3.0f, mHeight, mWidth / 2.0f, mHeight - position, mPaint2);
                                mCanvas.drawLine(mWidth / 3.0f * 2.0f, mHeight, mWidth / 2.0f, mHeight - position, mPaint2);
                                break;
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap, 0, 0, null);
                }
            }
            super.onDraw(viewcanvas);
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
        // Define the criteria how to select the location provider -> use
        // default
        Location location = null;
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        //locationProvider = "gps";
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
            switchcam(1);
        } else {
            switchcam(2);
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
                    parameters.set("scene-mode", "portrait");
                    parameters.set("rotation", "90");
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
                    parameters.set("scene-mode", "portrait");
                    parameters.set("rotation", "270");
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
        mStarshipPlans.setVisibility(View.GONE);
        mViewerPhoto.setVisibility(View.GONE);
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
                mStarshipPlans.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 5:
                say("Photo");
                mViewerPhoto.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
        }
        mViewermode = no;
    }

    // =====================================================================================
    // ask the camera to take a photo, and pass it to onPictureTaken


    private void snapphoto() {
        if (mVieweron) {
            mCamera.takePicture(null, null, this);
        }
    }

    // photo saving of picture taken callback
    public void onPictureTaken(byte[] data, Camera camera) {
        // Uri imageFileUri = getActivity().getContentResolver().insert(
        //         MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Uri imageFileUri = Uri.fromFile(file);
        try {
            OutputStream imageFileOS = getActivity().getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
            say("Picture taken and saved!");
            // Toast.makeText(getActivity(), "Saved " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // inform the media manager that we have a new photo in the gallery
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageFileUri);
        getActivity().sendBroadcast(intent);
        // transfer the photo in the imageview
        switchviewer(5);
        mViewerPhoto.setImageURI(imageFileUri);
        Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
        if(image!=null) processbitmap(image);
        // do not restart camera if we switch the viewer page before
        // camera.startPreview();
    }

    private void processbitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        //For Exception handling , odd width throws exception .
        if (width % 2 != 0)
            width = width - 1;

        FaceDetector detector = new FaceDetector(width, height, 5);
        FaceDetector.Face[] faces = new FaceDetector.Face[5];

        Bitmap bitmap565 = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Paint ditherPaint = new Paint();
        ditherPaint.setDither(true);

        Paint headPaint = new Paint();
        headPaint.setColor(Color.RED);
        headPaint.setStyle(Paint.Style.STROKE);
        headPaint.setStrokeWidth(3);

        Paint eyePaint = new Paint();
        eyePaint.setColor(Color.BLUE);
        eyePaint.setStyle(Paint.Style.STROKE);
        eyePaint.setStrokeWidth(1);

        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap565);
        canvas.drawBitmap(image, 0, 0, ditherPaint);

        int facesFound = detector.findFaces(bitmap565, faces);
        PointF midPoint = new PointF();
        float eyeDistance = 0.0f;
        float confidence = 0.0f;

        Toast.makeText(getActivity(), "Faces Found " + facesFound, Toast.LENGTH_LONG).show();

        if (facesFound > 0) {
            for (int index = 0; index < facesFound; ++index) {
                // get values from the faces
                faces[index].getMidPoint(midPoint);
                eyeDistance = faces[index].eyesDistance();
                confidence = faces[index].confidence();
                // draw circles around features
                canvas.drawCircle(midPoint.x, midPoint.y, (float) 1.5 * eyeDistance, headPaint);
                canvas.drawCircle((float) (midPoint.x - eyeDistance / 2), (float) (midPoint.y - eyeDistance / 8), (float) eyeDistance / (float) 2.5, eyePaint);
                canvas.drawCircle(midPoint.x + eyeDistance / 2, midPoint.y - eyeDistance / 8, (float) eyeDistance / (float) 2.5, eyePaint);

            }
        }

        mViewerPhoto.setImageBitmap(bitmap565);
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
        switchviewer(5);
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
                // process the bitmap from the photo application (thumbnail data)
                //Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                //switchviewer(5);
                //mViewerPhoto.setImageBitmap(cameraBitmap);
                // Process the file, and view it
                switchviewer(5);
                mViewerPhoto.setImageURI(fileUri);
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
        if (texte.contains("temperature") || texte.contains("pressure") || texte.contains("light")) {
            switchbuttonlayout(1);
            switchsensorlayout(4);
            startsensors(4);
            return (true);
        }
        if (texte.contains("hailing") && texte.contains("close")) {
            switchbuttonlayout(2);
            closecomm();
            return (true);
        }
        if (texte.contains("hailing") || texte.contains("frequency")) {
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
        if (texte.contains("fuck") || texte.contains("shit")) {
            speak("This is not very polite.");
            switchviewer(0);
            switchsensorlayout(0);
            switchbuttonlayout(0);
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
        Context cx = getContext();
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
        if (checkInternet()) {
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


