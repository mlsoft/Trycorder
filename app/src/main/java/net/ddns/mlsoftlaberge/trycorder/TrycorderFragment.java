package net.ddns.mlsoftlaberge.trycorder;

/*
*  By Martin Laberge, From March 2016 to July 2016.
*  Licence: Can be shared with anyone, for non profit, provided my name stays in the comments.
*/

import android.app.Activity;
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
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import net.ddns.mlsoftlaberge.trycorder.contacts.ContactsListActivity;
import net.ddns.mlsoftlaberge.trycorder.gallery.GalleryActivity;
import net.ddns.mlsoftlaberge.trycorder.products.ProductsListActivity;
import net.ddns.mlsoftlaberge.trycorder.settings.SettingsActivity;
import net.ddns.mlsoftlaberge.trycorder.trycorder.AudSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.LogsStatView;
import net.ddns.mlsoftlaberge.trycorder.utils.Fetcher;
import net.ddns.mlsoftlaberge.trycorder.trycorder.FirSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.GIFView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.GraSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.MagSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.MotSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.OriSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.ShiSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.TemSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.TraSensorView;
import net.ddns.mlsoftlaberge.trycorder.trycorder.TrbSensorView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A Fantastic fragment, containing a lot of views
 */
public class TrycorderFragment extends Fragment
        implements TextureView.SurfaceTextureListener,
        RecognitionListener,
        Camera.PictureCallback {

    public TrycorderFragment() {
    }

    // ======================================================================================
    public interface OnTrycorderInteractionListener {
        void onTrycorderModeChange(int mode);
    }

    private OnTrycorderInteractionListener mOnTrycorderInteractionListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Assign callback listener which the holding activity must implement.
            mOnTrycorderInteractionListener = (OnTrycorderInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrycorderInteractionListener");
        }
    }

    // ======================================================================================

    // handles to camera and textureview
    private Camera mCamera = null;
    private TextureView mViewerWindow;

    // handle for the gps
    private LocationManager mLocationManager = null;

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

    // the new scope class
    private MotSensorView mMotSensorView;

    // the Earth-Still Logo on sensor screen
    private ImageView mEarthStill;

    // the startrek logo on sensor screen
    private ImageView mStartrekLogo;

    // the walkie layout on sensor screen
    private LinearLayout mWalkieLayout;
    private Button mWalkieSpeakButton;
    private Button mWalkieTalkButton;
    private Button mWalkieConnectButton;
    private Button mWalkieSpeaklistButton;
    private Button mWalkieServeronButton;
    private Button mWalkieServeroffButton;
    private Button mWalkieLogslistButton;
    private TextView mWalkieIpList;

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
    private Button mSnapButton;

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
    private Button mInterCommButton;
    private int mCommStatus = 0;

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

    // the button to fire at ennemys
    private Button mMotorButton;
    private Button mMotorImpulseButton;
    private Button mMotorOffButton;
    private Button mMotorWarpButton;

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
    private Button mLogsSysButton;
    private Button mLogsStatButton;

    private Button mModeButton;
    private Button mModePhotogalButton;
    private Button mModeVideogalButton;
    private Button mModeWalkieButton;
    private Button mModeCrewButton;
    private Button mModeInvButton;

    // the button to control sound-effects
    private Button mSoundButton;
    private boolean mSoundStatus = false;

    // the layout to put sensorview in
    private LinearLayout mSensorLayout;

    private LinearLayout mButtonsLayout;
    private Animation mAnimation;
    private LinearLayout mButtonssensorLayout;
    private LinearLayout mButtonscommLayout;
    private LinearLayout mButtonsshieldLayout;
    private LinearLayout mButtonsfireLayout;
    private LinearLayout mButtonstransporterLayout;
    private LinearLayout mButtonstractorLayout;
    private LinearLayout mButtonsmotorLayout;
    private LinearLayout mButtonsviewerLayout;
    private LinearLayout mButtonslogsLayout;
    private LinearLayout mButtonsmodeLayout;
    private int mButtonsmode = 0;

    // the bottom right layout for viewing media
    private LinearLayout mViewerLayout;

    // the 2 statics modes from the viewer buttons layout
    private ImageView mFederationlogo;
    private ImageView mViewerPhoto;

    // the 4 modes from the logs buttons layout
    private TextView mLogsConsole;
    private TextView mLogsInfo;
    private ImageView mStarshipPlans;
    private TextView mLogsSys;
    private LogsStatView mLogsStat;

    // the mode animation for motor layout
    private FrameLayout mViewerAnimate;
    private ImageView mImageEarthStill;  // image of warp core
    private GIFView mGIFView;
    private GIFView mGIFView1;

    // utility class to fetch system infos
    private Fetcher mFetcher;

    // the player for sound background
    private MediaPlayer mMediaPlayer = null;

    // the preferences values
    private boolean autoListen;
    private boolean isChatty;
    private String speakLanguage;
    private String listenLanguage;
    private String displayLanguage;
    private String deviceName;
    private boolean isMaster;
    private boolean replaySent;
    private boolean autoBoot;
    private boolean autoStop;

    // the preferences holder
    private SharedPreferences sharedPref;

    // ==========================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trycorder_fragment, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen", false);
        isChatty = sharedPref.getBoolean("pref_key_ischatty", false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        displayLanguage = sharedPref.getString("pref_key_display_language", "");
        deviceName = sharedPref.getString("pref_key_device_name", "");
        isMaster = sharedPref.getBoolean("pref_key_ismaster", true);
        replaySent = sharedPref.getBoolean("pref_key_replay_sent", false);
        autoBoot = sharedPref.getBoolean("pref_key_auto_boot", true);
        autoStop = sharedPref.getBoolean("pref_key_auto_stop", false);

        // ==============================================================================
        // create layout params for the created views
        final LinearLayout.LayoutParams tlayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

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
                snapphoto();
            }
        });

        // the snap button do the same than the ask button
        mSnapButton = (Button) view.findViewById(R.id.snap_button);
        mSnapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                //opengallery();
                switchtrycordermode(2);
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
                sendcommand("magnetic");
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
                sendcommand("orientation");
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
                sendcommand("gravity");
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
                sendcommand("temperature");
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
                sendcommand("sensor off");
            }
        });

        // ===================== comm buttons group ============================
        // the comm button
        mCommButton = (Button) view.findViewById(R.id.comm_button);
        mCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchbuttonlayout(2);
                if (mCommStatus == 0) {
                    switchsensorlayout(11);
                }
                if (mCommStatus == 1) {
                    switchsensorlayout(5);
                }
                if (mCommStatus == 2) {
                    switchsensorlayout(12);
                }
                buttonsound();
            }
        });
        // the open comm button
        mOpenCommButton = (Button) view.findViewById(R.id.opencomm_button);
        mOpenCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommStatus = 1;
                opencomm();
                sendcommand("healing");
            }
        });
        // the close comm button
        mCloseCommButton = (Button) view.findViewById(R.id.closecomm_button);
        mCloseCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommStatus = 0;
                closecomm();
                sendcommand("healing close");
            }
        });
        // the close comm button
        mInterCommButton = (Button) view.findViewById(R.id.intercomm_button);
        mInterCommButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommStatus = 2;
                intercomm();
                sendcommand("intercom");
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
                sendcommand("raise shield");
            }
        });
        // the shield down button
        mShieldDownButton = (Button) view.findViewById(R.id.shield_down_button);
        mShieldDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowershields();
                sendcommand("lower shield");
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
                sendcommand("phaser");
            }
        });

        // the torpedo button
        mTorpedoButton = (Button) view.findViewById(R.id.torpedo_button);
        mTorpedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firemissiles();
                sendcommand("fire");
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
                sendcommand("beam me up");
            }
        });

        // the transporter out button
        mTransportOutButton = (Button) view.findViewById(R.id.transport_out_button);
        mTransportOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transporterout();
                sendcommand("beam me down");
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
                sendcommand("tractor push");
            }
        });

        // the tractor off button
        mTractorOffButton = (Button) view.findViewById(R.id.tractor_off_button);
        mTractorOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractoroff();
                sendcommand("tractor off");
            }
        });

        // the tractor pull button
        mTractorPullButton = (Button) view.findViewById(R.id.tractor_pull_button);
        mTractorPullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractorpull();
                sendcommand("tractor pull");
            }
        });

        // ===================== transporter buttons group ============================
        // the tractor button
        mMotorButton = (Button) view.findViewById(R.id.motor_button);
        mMotorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(7);
                switchsensorlayout(10);
                switchviewer(7);
            }
        });

        // the tractor push button
        mMotorImpulseButton = (Button) view.findViewById(R.id.motor_impulse_button);
        mMotorImpulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motorimpulse();
                sendcommand("impulse power");
            }
        });

        // the tractor off button
        mMotorOffButton = (Button) view.findViewById(R.id.motor_off_button);
        mMotorOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motoroff();
                sendcommand("stay here");
            }
        });

        // the tractor pull button
        mMotorWarpButton = (Button) view.findViewById(R.id.motor_warp_button);
        mMotorWarpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motorwarp();
                sendcommand("warp drive");
            }
        });

        // ===================== viewer buttons group ============================
        // the viewer button
        mViewerButton = (Button) view.findViewById(R.id.viewer_button);
        mViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(8);
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
                sendcommand("viewer on");
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
                sendcommand("local viewer");
            }
        });

        mViewerOffButton = (Button) view.findViewById(R.id.vieweroff_button);
        mViewerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(0);
                sendcommand("viewer off");
            }
        });

        mViewerPhotoButton = (Button) view.findViewById(R.id.viewerphoto_button);
        mViewerPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(5);
            }
        });

        // ===================== logs buttons group ============================
        // the viewer button
        mLogsButton = (Button) view.findViewById(R.id.logs_button);
        mLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(9);
            }
        });

        mLogsConsoleButton = (Button) view.findViewById(R.id.logsconsole_button);
        mLogsConsoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(2);
                sendcommand("logs");
            }
        });

        mLogsInfoButton = (Button) view.findViewById(R.id.logsinfo_button);
        mLogsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(3);
                sendcommand("system info");
            }
        });

        mLogsPlansButton = (Button) view.findViewById(R.id.logsplans_button);
        mLogsPlansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(4);
                sendcommand("plans");
            }
        });

        mLogsSysButton = (Button) view.findViewById(R.id.logssys_button);
        mLogsSysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(6);
            }
        });

        mLogsStatButton = (Button) view.findViewById(R.id.logsstat_button);
        mLogsStatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(9);
            }
        });

        // ===================== switch mode button ============================
        // the viewer button
        mModeButton = (Button) view.findViewById(R.id.mode_button);
        mModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchbuttonlayout(10);
            }
        });

        mModePhotogalButton = (Button) view.findViewById(R.id.mode_photogal_button);
        mModePhotogalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtrycordermode(2);
            }
        });

        mModeVideogalButton = (Button) view.findViewById(R.id.mode_videogal_button);
        mModeVideogalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtrycordermode(3);
            }
        });

        mModeWalkieButton = (Button) view.findViewById(R.id.mode_walkie_button);
        mModeWalkieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtrycordermode(4);
            }
        });

        mModeCrewButton = (Button) view.findViewById(R.id.mode_contact_button);
        mModeCrewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                accesscrew();
            }
        });

        mModeInvButton = (Button) view.findViewById(R.id.mode_product_button);
        mModeInvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                accessinventory();
            }
        });

        // ================== get handles on the 3 layout containers ===================
        // the sensor layout, to contain my sensorview
        mSensorLayout = (LinearLayout) view.findViewById(R.id.sensor_layout);

        // the buttons layout, to contain my buttons groups
        mButtonsLayout = (LinearLayout) view.findViewById(R.id.buttons_layout);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slidein);

        // the buttons group (one visible at a time)
        mButtonssensorLayout = (LinearLayout) view.findViewById(R.id.buttons_sensor_layout);
        mButtonscommLayout = (LinearLayout) view.findViewById(R.id.buttons_comm_layout);
        mButtonsshieldLayout = (LinearLayout) view.findViewById(R.id.buttons_shield_layout);
        mButtonsfireLayout = (LinearLayout) view.findViewById(R.id.buttons_fire_layout);
        mButtonstransporterLayout = (LinearLayout) view.findViewById(R.id.buttons_transporter_layout);
        mButtonstractorLayout = (LinearLayout) view.findViewById(R.id.buttons_tractor_layout);
        mButtonsmotorLayout = (LinearLayout) view.findViewById(R.id.buttons_motor_layout);
        mButtonsviewerLayout = (LinearLayout) view.findViewById(R.id.buttons_viewer_layout);
        mButtonslogsLayout = (LinearLayout) view.findViewById(R.id.buttons_logs_layout);
        mButtonsmodeLayout = (LinearLayout) view.findViewById(R.id.buttons_mode_layout);

        // the viewer layout, to contain my surfaceview and some logs and infos
        mViewerLayout = (LinearLayout) view.findViewById(R.id.viewer_layout);
        mFederationlogo = (ImageView) view.findViewById(R.id.federation_logo);
        mFederationlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                //accesscrew();
            }
        });
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);
        mLogsInfo = (TextView) view.findViewById(R.id.logs_info);
        mStarshipPlans = (ImageView) view.findViewById(R.id.starship_plans);
        mStarshipPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchplans();
            }
        });
        mViewerPhoto = (ImageView) view.findViewById(R.id.photo_view);
        mLogsSys = (TextView) view.findViewById(R.id.logs_sys);

        mLogsStat = new LogsStatView(getContext());
        mViewerLayout.addView(mLogsStat,tlayoutParams);

        // frame for motor animations
        mViewerAnimate = (FrameLayout) view.findViewById(R.id.viewer_animate);
        // image inside the vieweranimate layout
        mImageEarthStill = (ImageView) view.findViewById(R.id.image_earthstill);

        // warp effect animation gif
        mGIFView = new GIFView(getContext(), R.raw.warp_animation);
        mViewerAnimate.addView(mGIFView);
        // impulse effect animation gif
        mGIFView1 = new GIFView(getContext(), R.raw.earth_rotating);
        mViewerAnimate.addView(mGIFView1);

        // set all visibilitys of Vieweranimate frame
        mImageEarthStill.setVisibility(View.VISIBLE);
        mGIFView.setVisibility(View.GONE);
        mGIFView1.setVisibility(View.GONE);

        // create and activate a textureview to contain camera display
        mViewerWindow = (TextureView) view.findViewById(R.id.viewer_window);
        mViewerWindow.setSurfaceTextureListener(this);

        //mFederationlogo.setVisibility(View.VISIBLE);
        //mViewerWindow.setVisibility(View.GONE);
        //mLogsConsole.setVisibility(View.GONE);
        //mLogsInfo.setVisibility(View.GONE);
        //mStarshipPlans.setVisibility(View.GONE);
        //mViewerPhoto.setVisibility(View.GONE);
        //mLogsSys.setVisibility(View.GONE);
        //mLogsStat.setVisibility(View.GONE);
        //mViewerAnimate.setVisibility(View.GONE);
        mVieweron = false;

        // ============== create a sensor display and incorporate in layout ==============

        // a sensor manager to obtain sensors data
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        // a gps manager to obtain gps data
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // my sensorview that display the sensors data
        mMagSensorView = new MagSensorView(getContext(), mSensorManager);
        // add my sensorview to the layout 1
        mSensorLayout.addView(mMagSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mOriSensorView = new OriSensorView(getContext(), mSensorManager, mLocationManager);
        mOriSensorView.setClickable(true);
        mOriSensorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googlemapactivity();
                buttonsound();
            }
        });
        // add my sensorview to the layout 1
        mSensorLayout.addView(mOriSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mGraSensorView = new GraSensorView(getContext(), mSensorManager);
        // add my sensorview to the layout 1
        mSensorLayout.addView(mGraSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mTemSensorView = new TemSensorView(getContext(), mSensorManager);
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

        // my sensorview that display the sensors data
        mMotSensorView = new MotSensorView(getContext());
        // add my sensorview to the layout 1
        mSensorLayout.addView(mMotSensorView, tlayoutParams);

        // position 0 of sensor layout
        mEarthStill = (ImageView) view.findViewById(R.id.earth_still);

        // position 11 of sensor layout
        mStartrekLogo = (ImageView) view.findViewById(R.id.startrek_logo);

        // position 12 of sensor layout
        mWalkieLayout = (LinearLayout) view.findViewById(R.id.walkie_layout);

        mWalkieSpeakButton = (Button) view.findViewById(R.id.walkie_speak_button);
        mWalkieSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });

        mWalkieTalkButton = (Button) view.findViewById(R.id.walkie_talk_button);
        mWalkieTalkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startstreamingaudio();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopstreamingaudio();
                        break;
                }
                return false;
            }
        });

        mWalkieConnectButton = (Button) view.findViewById(R.id.walkie_connect_button);
        mWalkieConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                // stop all servers
                //stoptalkserver();
                //unregisterService();
                stopdiscoverService();
                //stopserver();
                // restart all servers
                //initserver();
                //registerService();
                startdiscoverService();
                //inittalkserver();
                //scantrycorders();
            }
        });

        mWalkieSpeaklistButton = (Button) view.findViewById(R.id.walkie_speaklist_button);
        mWalkieSpeaklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(8);
            }
        });

        mWalkieServeronButton = (Button) view.findViewById(R.id.walkie_serveron_button);
        mWalkieServeronButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                startTrycorderService();
            }
        });

        mWalkieServeroffButton = (Button) view.findViewById(R.id.walkie_serveroff_button);
        mWalkieServeroffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                stopTrycorderService();
            }
        });

        mWalkieLogslistButton = (Button) view.findViewById(R.id.walkie_logslist_button);
        mWalkieLogslistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(2);
            }
        });

        // the list of ip - machine that we discover
        mWalkieIpList = (TextView) view.findViewById(R.id.walkie_iplist);

        // utility class to fetch infos from the system
        mFetcher = new Fetcher(getContext());

        // fill the list with at least our private IP until some events fill it more
        mWalkieIpList.setText(mFetcher.fetch_ip_address());

        // start the talk network listener server player (lol)
        //startTrycorderService();

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
        mSnapButton.setTypeface(face2);
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
        mMotorButton.setTypeface(face2);
        mViewerButton.setTypeface(face2);
        mLogsButton.setTypeface(face2);
        mModeButton.setTypeface(face2);

        // center buttons
        mMagneticButton.setTypeface(face2);
        mOrientationButton.setTypeface(face2);
        mGravityButton.setTypeface(face2);
        mTemperatureButton.setTypeface(face2);
        mSensoroffButton.setTypeface(face2);

        mOpenCommButton.setTypeface(face3);
        mCloseCommButton.setTypeface(face3);
        mInterCommButton.setTypeface(face3);
        mWalkieSpeakButton.setTypeface(face2);
        mWalkieTalkButton.setTypeface(face2);
        mWalkieConnectButton.setTypeface(face2);
        mWalkieSpeaklistButton.setTypeface(face2);
        mWalkieServeronButton.setTypeface(face2);
        mWalkieServeroffButton.setTypeface(face2);
        mWalkieLogslistButton.setTypeface(face2);
        mWalkieIpList.setTypeface(face3);

        mShieldUpButton.setTypeface(face3);
        mShieldDownButton.setTypeface(face3);

        mPhaserButton.setTypeface(face3);
        mTorpedoButton.setTypeface(face3);

        mTransportOutButton.setTypeface(face3);
        mTransportInButton.setTypeface(face3);

        mTractorPushButton.setTypeface(face3);
        mTractorOffButton.setTypeface(face3);
        mTractorPullButton.setTypeface(face3);

        mMotorImpulseButton.setTypeface(face3);
        mMotorOffButton.setTypeface(face3);
        mMotorWarpButton.setTypeface(face3);

        mViewerOnButton.setTypeface(face2);
        mViewerFrontButton.setTypeface(face2);
        mViewerOffButton.setTypeface(face2);
        mViewerPhotoButton.setTypeface(face2);

        mLogsConsoleButton.setTypeface(face2);
        mLogsInfoButton.setTypeface(face2);
        mLogsPlansButton.setTypeface(face2);
        mLogsSysButton.setTypeface(face2);
        mLogsStatButton.setTypeface(face2);

        mLogsConsole.setTypeface(face2);
        mLogsInfo.setTypeface(face2);
        //mLogsSys.setTypeface(face);

        mModePhotogalButton.setTypeface(face2);
        mModeVideogalButton.setTypeface(face2);
        mModeWalkieButton.setTypeface(face2);
        mModeCrewButton.setTypeface(face2);
        mModeInvButton.setTypeface(face2);
    }

    @Override
    public void onResume() {
        super.onResume();
        // settings part of the preferences
        autoListen = sharedPref.getBoolean("pref_key_auto_listen", false);
        isChatty = sharedPref.getBoolean("pref_key_ischatty", false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        displayLanguage = sharedPref.getString("pref_key_display_language", "");
        deviceName = sharedPref.getString("pref_key_device_name", "Trycorder");
        isMaster = sharedPref.getBoolean("pref_key_ismaster", true);
        replaySent = sharedPref.getBoolean("pref_key_replay_sent", false);
        autoBoot = sharedPref.getBoolean("pref_key_auto_boot", true);
        autoStop = sharedPref.getBoolean("pref_key_auto_stop", false);
        // update device name for a device name from system
        if(deviceName.equals("Trycorder")) {
            deviceName=mFetcher.fetch_device_name();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("pref_key_device_name", deviceName);
            editor.commit();
        }
        // dynamic status part
        mSensormode = sharedPref.getInt("pref_key_sensor_mode", 0);
        mSensorpage = sharedPref.getInt("pref_key_sensor_page", 0);
        mButtonsmode = sharedPref.getInt("pref_key_buttons_mode", 0);
        mViewermode = sharedPref.getInt("pref_key_viewer_mode", 0);
        mSoundStatus = sharedPref.getBoolean("pref_key_audio_mode", false);
        mCommStatus = sharedPref.getInt("pref_key_comm_status", 0);
        mViewerfront = sharedPref.getBoolean("pref_key_viewer_front", false);
        // resurect the application to last settings
        switchbuttonlayout(mButtonsmode);
        switchsensorlayout(mSensormode);
        switchviewer(mViewermode);
        if (mSensormode <= 4) startsensors(mSensormode);
        initspeak();
        initserver();
        startTrycorderService();
        //registerService();
        //inittalkserver();
        startdiscoverService();
        //scantrycorders();
    }

    @Override
    public void onPause() {
        // save the current status
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("pref_key_sensor_mode", mSensormode);
        editor.putInt("pref_key_sensor_page", mSensorpage);
        editor.putInt("pref_key_buttons_mode", mButtonsmode);
        editor.putInt("pref_key_viewer_mode", mViewermode);
        editor.putBoolean("pref_key_audio_mode", mSoundStatus);
        editor.putInt("pref_key_comm_status", mCommStatus);
        editor.putBoolean("pref_key_viewer_front", mViewerfront);
        editor.commit();
        stopsensors();
        switchcam(0);
        mLogsStat.stop();
        if(autoStop) {
            stopTrycorderService();
        }
        //stoptalkserver();
        //unregisterService();
        stopdiscoverService();
        stopserver();
        super.onPause();
    }

    private int planno = 0;

    private void switchplans() {
        planno++;
        if (planno >= 6) planno = 0;
        Animation animation;
        switch (planno) {
            case 0:
                mStarshipPlans.setImageResource(R.drawable.starship_view);
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.clockwise);
                mStarshipPlans.startAnimation(animation);
                break;
            case 1:
                mStarshipPlans.setImageResource(R.drawable.starship_build);
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
                mStarshipPlans.startAnimation(animation);
                break;
            case 2:
                mStarshipPlans.setImageResource(R.drawable.starship_plan);
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.slidein);
                mStarshipPlans.startAnimation(animation);
                break;
            case 3:
                mStarshipPlans.setImageResource(R.drawable.starship_sideview);
                //animation = AnimationUtils.loadAnimation(getContext(),R.anim.move);
                //mStarshipPlans.startAnimation(animation);
                break;
            case 4:
                mStarshipPlans.setImageResource(R.drawable.starship_topview);
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.blink);
                mStarshipPlans.startAnimation(animation);
                break;
            case 5:
                mStarshipPlans.setImageResource(R.drawable.earth_still);
                animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotateback);
                mStarshipPlans.startAnimation(animation);
                break;

        }
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
            case 10:
                say("Sensors Motor Animation");
                startmotsensors();
                break;
            case 11:
                say("Sensors Startrek Logo");
                break;
            case 12:
                say("Sensors Walkie Panel");
                break;
            default:
                say("Sensors OFF");
                break;
        }
        if (mode <= 4) mSensorpage = mode;
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
        stopmotsensors();
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        say("Settings");
        if (isChatty) speak("Settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    // settings activity incorporation in the display
    public void accesscrew() {
        say("Access Starship Crew");
        if (isChatty) speak("Crew information and evaluation");
        Intent i = new Intent(getActivity(), ContactsListActivity.class);
        startActivity(i);
    }

    // settings activity incorporation in the display
    public void accessinventory() {
        say("Access Starship Inventory");
        if (isChatty) speak("Inventory");
        Intent i = new Intent(getActivity(), ProductsListActivity.class);
        startActivity(i);
    }

    private void opengallery() {
        say("Open Gallery Class");
        if (isChatty) speak("Gallery");
        Intent i = new Intent(getActivity(), GalleryActivity.class);
        startActivity(i);
    }

    private void switchtrycordermode(int mode) {
        mOnTrycorderInteractionListener.onTrycorderModeChange(mode);
    }

    // =========================================================================================
    // map activity to see where we are on the map of this planet

    public void googlemapactivity() {
        float longitude = mOriSensorView.getLongitude();
        float latitude = mOriSensorView.getLatitude();

        //final Intent viewIntent = new Intent(Intent.ACTION_VIEW, constructGeoUri(view.getContentDescription().toString()));
        String geopath = "geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude);
        Uri geouri = Uri.parse(geopath);
        say("Open planetary mapping");
        say(geopath);
        final Intent viewIntent = new Intent(Intent.ACTION_VIEW, geouri);
        // A PackageManager instance is needed to verify that there's a default app
        // that handles ACTION_VIEW and a geo Uri.
        final PackageManager packageManager = getActivity().getPackageManager();
        // Checks for an activity that can handle this intent. Preferred in this
        // case over Intent.createChooser() as it will still let the user choose
        // a default (or use a previously set default) for geo Uris.
        if (packageManager.resolveActivity(
                viewIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            // Toast.makeText(getActivity(),
            //        R.string.yes_intent_found, Toast.LENGTH_SHORT).show();
            startActivity(viewIntent);
        } else {
            // If no default is found, displays a message that no activity can handle
            // the view button.
            Toast.makeText(getActivity(), "No application for mapping.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Constructs a geo scheme Uri from a postal address.
     *
     * @param postalAddress A postal address.
     * @return the geo:// Uri for the postal address.
     */
    private static final String GEO_URI_SCHEME_PREFIX = "geo:0,0?q=";

    private Uri constructGeoUri(String postalAddress) {
        // Concatenates the geo:// prefix to the postal address. The postal address must be
        // converted to Uri format and encoded for special characters.
        return Uri.parse(GEO_URI_SCHEME_PREFIX + Uri.encode(postalAddress));
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
        mMotSensorView.setVisibility(View.GONE);
        mEarthStill.setVisibility(View.GONE);
        mStartrekLogo.setVisibility(View.GONE);
        mWalkieLayout.setVisibility(View.GONE);
        switch (no) {
            case 0:
                mEarthStill.setVisibility(View.VISIBLE);
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
            case 10:
                mMotSensorView.setVisibility(View.VISIBLE);
                break;
            case 11:
                mStartrekLogo.setVisibility(View.VISIBLE);
                break;
            case 12:
                mWalkieLayout.setVisibility(View.VISIBLE);
                break;
        }
        if (no <= 4) mSensorpage = no;
        mSensormode = no;
    }

    // =====================================================================================


    private void switchbuttonlayout(int no) {
        mButtonssensorLayout.setVisibility(View.GONE);
        mButtonscommLayout.setVisibility(View.GONE);
        mButtonsshieldLayout.setVisibility(View.GONE);
        mButtonsfireLayout.setVisibility(View.GONE);
        mButtonstransporterLayout.setVisibility(View.GONE);
        mButtonstractorLayout.setVisibility(View.GONE);
        mButtonsmotorLayout.setVisibility(View.GONE);
        mButtonsviewerLayout.setVisibility(View.GONE);
        mButtonslogsLayout.setVisibility(View.GONE);
        mButtonsmodeLayout.setVisibility(View.GONE);
        //Animation animation;
        switch (no) {
            case 1:
                say("Sensors Mode");
                mButtonssensorLayout.setVisibility(View.VISIBLE);
                //animation = AnimationUtils.loadAnimation(getContext(),R.anim.clockwise);
                //mButtonssensorLayout.startAnimation(animation);
                break;
            case 2:
                say("Communication Mode");
                mButtonscommLayout.setVisibility(View.VISIBLE);
                //animation = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
                //mButtonscommLayout.startAnimation(animation);
                break;
            case 3:
                say("Shield Mode");
                mButtonsshieldLayout.setVisibility(View.VISIBLE);
                //animation = AnimationUtils.loadAnimation(getContext(),R.anim.slidein);
                //mButtonsshieldLayout.startAnimation(animation);
                break;
            case 4:
                say("Fire Mode");
                mButtonsfireLayout.setVisibility(View.VISIBLE);
                break;
            case 5:
                say("Transporter Mode");
                mButtonstransporterLayout.setVisibility(View.VISIBLE);
                break;
            case 6:
                say("Tractor Mode");
                mButtonstractorLayout.setVisibility(View.VISIBLE);
                break;
            case 7:
                say("Motor Mode");
                mButtonsmotorLayout.setVisibility(View.VISIBLE);
                break;
            case 8:
                say("Viewer Mode");
                mButtonsviewerLayout.setVisibility(View.VISIBLE);
                break;
            case 9:
                say("Logs Mode");
                mButtonslogsLayout.setVisibility(View.VISIBLE);
                break;
            case 10:
                say("Mode Mode");
                mButtonsmodeLayout.setVisibility(View.VISIBLE);
                break;
        }
        mButtonsmode = no;
    }

    // =====================================================================================

    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void buttonbad() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.denybeep1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void opencomm() {
        if (isChatty) speak("Hailing frequency opened");
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commopen);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
        switchsensorlayout(5);
        startsensors(5);
        say("Hailing frequency open.");
    }

    private void closecomm() {
        if (isChatty) speak("Hailing frequency closed");
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commclose);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
        stopsensors();
        switchsensorlayout(11);
        say("Hailing frequency closed.");
    }

    private void intercomm() {
        if (isChatty) speak("Intercom mode ready");
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
        stopsensors();
        switchsensorlayout(12);
        say("Intercom mode ready.");
    }

    private void transporterout() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1a);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport Out");
        switchsensorlayout(8);
        mTraSensorView.setmode(2);
        startsensors(8);
        if (isChatty) speak("Transport In Progress. . . Transport Complete");
    }

    private void transporterin() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1b);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport In");
        switchsensorlayout(8);
        mTraSensorView.setmode(1);
        startsensors(8);
        if (isChatty) speak("Transport In Progress. . . Transport Complete");
    }

    private void raiseshields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shieldup);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Raise Shields");
        switchsensorlayout(6);
        mShiSensorView.setmode(1);
        startsensors(6);
        if (isChatty) speak("Shields Up");
    }

    private void lowershields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shielddown);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Lower Shields");
        switchsensorlayout(6);
        mShiSensorView.setmode(2);
        startsensors(6);
        if (isChatty) speak("Shields Down");
    }

    private void tractorpush() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Push");
        switchsensorlayout(9);
        mTrbSensorView.setmode(1);
        startsensors(9);
        if (isChatty) speak("Repulser Beam Engaged");
    }

    private void tractoroff() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Tractor Beam Off");
        switchsensorlayout(9);
        mTrbSensorView.setmode(0);
        stopsensors();
        if (isChatty) speak("Beam Off");
    }

    private void tractorpull() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Pull");
        switchsensorlayout(9);
        mTrbSensorView.setmode(2);
        startsensors(9);
        if (isChatty) speak("Tractor Beam Engaged");
    }

    private void motorimpulse() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.voy_core_2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Impulse Engine");
        switchsensorlayout(10);
        mMotSensorView.setmode(1);
        startsensors(10);
        switchviewer(7);
        switchanimate(1);
        if (isChatty) speak("Impulse Engine Engaged");
    }

    private void motoroff() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_slowwarp_clean2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Motor Off");
        switchsensorlayout(10);
        mMotSensorView.setmode(0);
        stopsensors();
        switchviewer(7);
        switchanimate(0);
        if (isChatty) speak("All engines down");
    }

    private void motorwarp() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_warp5_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Warp Drive");
        switchsensorlayout(10);
        mMotSensorView.setmode(2);
        startsensors(10);
        switchviewer(7);
        switchanimate(2);
        if (isChatty) speak("Warp Drive Engaged");
    }

    private void firephaser() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.phasertype2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Phaser");
        switchsensorlayout(7);
        mFirSensorView.setmode(2);
        startsensors(7);
        if (isChatty) speak("The target is disabled");
    }

    private void firemissiles() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.photorp1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Torpedo");
        switchsensorlayout(7);
        mFirSensorView.setmode(1);
        startsensors(7);
        if (isChatty) speak("The ship is destroyed");
    }

    private void switchanimate(int no) {
        mImageEarthStill.setVisibility(View.GONE);
        mGIFView.setVisibility(View.GONE);
        mGIFView1.setVisibility(View.GONE);
        switch (no) {
            case 0:
                mImageEarthStill.setVisibility(View.VISIBLE);
                break;
            case 1:
                mGIFView1.setVisibility(View.VISIBLE);
                mGIFView1.start();
                break;
            case 2:
                mGIFView.setVisibility(View.VISIBLE);
                mGIFView.start();
                break;

        }
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

    private void stopmotsensors() {
        stopmusic();
        mMotSensorView.stop();
    }

    private void startmotsensors() {
        mMotSensorView.start();
        if (mSoundStatus) startmusic();
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

    // ==============================================================================
    // shield sensor, display shields effects

    private void stopshisensors() {
        mShiSensorView.stop();
    }

    private void startshisensors() {
        mShiSensorView.start();
    }

    // ==============================================================================
    // transporter sensor, display person disappearing

    private void stoptrasensors() {
        mTraSensorView.stop();
    }

    private void starttrasensors() {
        mTraSensorView.start();
    }

    // ==============================================================================
    // audio sensor, display waveform of ambient sound

    private void stopaudsensors() {
        mAudSensorView.stop();
    }

    private void startaudsensors() {
        mAudSensorView.start();
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
    // stop the sensor updates
    private void stoptemsensors() {
        stopmusic();
        mTemSensorView.stop();
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void starttemsensors() {
        mRunStatus = true;
        mTemSensorView.start();
        if (mSoundStatus) startmusic();
    }


    // ============================================================================
    // stop the sensor updates
    private void stoporisensors() {
        stopmusic();
        mOriSensorView.stop();
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void startorisensors() {
        mRunStatus = true;
        mOriSensorView.start();
        if (mSoundStatus) startmusic();
    }


    // ============================================================================
    // stop the sensor updates
    private void stopmagsensors() {
        stopmusic();
        mMagSensorView.stop();
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void startmagsensors() {
        mRunStatus = true;
        mMagSensorView.start();
        if (mSoundStatus) startmusic();
    }

    // ============================================================================
    // stop the sensor updates
    private void stopgrasensors() {
        stopmusic();
        mGraSensorView.stop();
        mRunStatus = false;
    }

    // here we start the sensor reading
    private void startgrasensors() {
        mRunStatus = true;
        mGraSensorView.start();
        if (mSoundStatus) startmusic();
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
        mLogsSys.setVisibility(View.GONE);
        mLogsStat.setVisibility(View.GONE);
        mViewerAnimate.setVisibility(View.GONE);
        mLogsStat.stop();
        switchcam(0);
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
                mLogsInfo.append(mFetcher.fetch_connectivity());
                mLogsInfo.append("--------------------\nTelephony\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_tel_status());
                mLogsInfo.append("--------------------\nNetwork\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_network_info());
                mLogsInfo.append("--------------------\nSystem\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_system_info());
                mLogsInfo.append("--------------------\nOperSys\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_os_info());
                mLogsInfo.append("--------------------\nPackage\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_packinfo());
                mLogsInfo.append("--------------------\nWifi-Dhcp\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_dhcpinfo());
                //mLogsInfo.append("--------------------\nDmesg\n--------------------\n");
                //mLogsInfo.append(mFetcher.fetch_dmesg_info());
                //mLogsInfo.append("--------------------\nProcess\n--------------------\n");
                //mLogsInfo.append(mFetcher.fetch_process_info());
                break;
            case 4:
                say("Plans");
                mStarshipPlans.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 5:
                say("View Snap Photo");
                mViewerPhoto.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 6:
                say("System Info");
                mLogsInfo.setVisibility(View.VISIBLE);
                mVieweron = false;
                mLogsInfo.setText("");
                mLogsInfo.append("--------------------\nConnectivity\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_connectivity());
                mLogsInfo.append("--------------------\nSensors List\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_sensors_list());
                mLogsInfo.append("--------------------\nCPU Info\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_cpu_info());
                mLogsInfo.append("--------------------\nMemory Info\n--------------------\n");
                mLogsInfo.append(mFetcher.fetch_memory_info());
                break;
            case 7:
                say("Animate Viewer");
                mViewerAnimate.setVisibility(View.VISIBLE);
                mVieweron = false;
                break;
            case 8:
                say("Speak List");
                mLogsSys.setVisibility(View.VISIBLE);
                mVieweron = false;
                mLogsSys.setText(mListenText);
                break;
            case 9:
                say("Logs Stat");
                mLogsStat.setVisibility(View.VISIBLE);
                mLogsStat.start();
                mVieweron = false;
                break;
        }
        mViewermode = no;
    }

    // =====================================================================================
    // ask the camera to take a photo, and pass it to onPictureTaken


    private void snapphoto() {
        if (mVieweron) {
            buttonsound();
            mCamera.takePicture(null, null, this);
        } else {
            buttonbad();
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
        if (image != null) processbitmap(image);
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

    // =========================================================================
    // system log talker
    private StringBuffer logbuffer = new StringBuffer(500);

    private void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0, texte + "\n");
        mLogsConsole.setText(logbuffer);
    }

    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private TextToSpeech tts = null;

    private void initspeak() {
        if (tts == null) {
            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        setspeaklang(speakLanguage);
                    }
                }
            });
        }
    }

    public void setspeaklang(String lng) {
        if (lng.equals("FR")) {
            tts.setLanguage(Locale.FRENCH);
        } else if (lng.equals("EN")) {
            tts.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
    }

    public void speak(String texte) {
        initspeak();
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say("Speaked: " + texte);
    }

    public void speak(String texte, String lng) {
        initspeak();
        setspeaklang(lng);
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say("Speaked: " + texte);
    }

    // ========================================================================================
    // functions to control the speech process

    // handles for the conversation functions
    private SpeechRecognizer mSpeechRecognizer = null;
    private Intent mSpeechRecognizerIntent = null;

    private void listen() {
        if (mSpeechRecognizer == null) {
            // ============== initialize the audio listener and talker ==============

            //AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.trycorder");
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 20);

            // produce a FC on android 4.0.3
            //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
        }

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

    // =================================================================================
    // listener for the speech recognition service
    // ========================================================================================
    // functions to listen to the voice recognition callbacks

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
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> dutexte = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (dutexte != null && dutexte.size() > 0) {
            for (int i = 0; i < dutexte.size(); ++i) {
                String mSentence = dutexte.get(i);
                if (matchvoice(mSentence)) {
                    mTextstatus_top.setText(mSentence);
                    say("Said: " + mSentence);
                    sendtext(mSentence);
                    if(autoListen) listen();
                    return;
                }
            }
            mTextstatus_top.setText(dutexte.get(0));
            say("Understood: " + dutexte.get(0));
            sendtext(dutexte.get(0));
            if(autoListen) listen();
            //if(speakLanguage.equals("FR")) speak("Commande inconnue.");
            //else speak("Unknown command.");
        }
    }


    // ==============================================================================
    // accents resource : éèêë áàâä íìîï óòôö úùûü çÇ
    // ==============================================================================
    private boolean matchvoice(String textein) {
        String texte = textein.toLowerCase();
        if (texte.contains("french") || texte.contains("français")) {
            listenLanguage = "FR";
            speakLanguage = "FR";
            speak("français", speakLanguage);
            return (true);
        }
        if (texte.contains("english") || texte.contains("anglais")) {
            listenLanguage = "EN";
            speakLanguage = "EN";
            speak("english", speakLanguage);
            return (true);
        }
        if (texte.contains("martin") || texte.contains("master")) {
            if (speakLanguage.equals("FR")) speak("Martin est mon maître.");
            else speak("Martin is my Master.");
            return (true);
        }
        if (texte.contains("computer") || texte.contains("ordinateur")) {
            if (speakLanguage.equals("FR")) speak("Faites votre requète");
            else speak("State your question");
            return (true);
        }
        if (texte.contains("fuck") || texte.contains("shit")) {
            if (speakLanguage.equals("FR")) speak("Ce n'est pas très poli");
            else speak("This is not very polite.");
            switchviewer(0);
            switchsensorlayout(0);
            switchbuttonlayout(0);
            return (true);
        }
        // actions on the trycorder
        if (texte.contains("sensor off")) {
            switchbuttonlayout(1);
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
            switchsensorlayout(11);
            closecomm();
            return (true);
        }
        if (texte.contains("hailing") || texte.contains("frequency")) {
            switchbuttonlayout(2);
            switchsensorlayout(5);
            opencomm();
            return (true);
        }
        if (texte.contains("intercom")) {
            switchbuttonlayout(2);
            switchsensorlayout(12);
            intercomm();
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
        if (texte.contains("beam me up") || texte.contains("scotty") || texte.contains("transporteur")) {
            switchbuttonlayout(5);
            transporterin();
            return (true);
        }
        if (texte.contains("beam me down")) {
            switchbuttonlayout(5);
            transporterout();
            return (true);
        }
        if (texte.contains("tractor push")) {
            switchbuttonlayout(6);
            tractorpush();
            return (true);
        }
        if (texte.contains("tractor off")) {
            switchbuttonlayout(6);
            tractoroff();
            return (true);
        }
        if (texte.contains("tractor pull")) {
            switchbuttonlayout(6);
            tractorpull();
            return (true);
        }
        if (texte.contains("impulse power")) {
            switchbuttonlayout(7);
            motorimpulse();
            return (true);
        }
        if (texte.contains("stay here")) {
            switchbuttonlayout(7);
            motoroff();
            return (true);
        }
        if (texte.contains("warp drive")) {
            switchbuttonlayout(7);
            motorwarp();
            return (true);
        }
        if (texte.contains("viewer on")) {
            switchbuttonlayout(8);
            mViewerfront = false;
            switchviewer(1);
            switchcam(1);
            return (true);
        }
        if (texte.contains("local viewer")) {
            switchbuttonlayout(8);
            mViewerfront = true;
            switchviewer(1);
            switchcam(2);
            return (true);
        }
        if (texte.contains("viewer off")) {
            switchbuttonlayout(8);
            switchviewer(0);
            return (true);
        }
        if (texte.contains("logs")) {
            switchbuttonlayout(9);
            switchviewer(2);
            return (true);
        }
        if (texte.contains("system info")) {
            switchbuttonlayout(9);
            switchviewer(3);
            return (true);
        }
        if (texte.contains("plans")) {
            switchbuttonlayout(9);
            switchviewer(4);
            return (true);
        }
        return (false);
    }

    public String mListenText = "The SPEAK will respond to:\n" +
            "francais | french\n" +
            "anglais | english\n" +
            "computer | ordinateur\n" +
            "intercom\n" +
            "phaser\n" +
            "fire | torpedo\n" +
            "shield down\n" +
            "raise shield\n" +
            "sensor off\n" +
            "sensor\n" +
            "magnetic\n" +
            "orientation | direction\n" +
            "gravity | vibration\n" +
            "temperature | pressure | light\n" +
            "hailing close\n" +
            "hailing | frequency\n" +
            "beam me up | scotty | transporteur\n" +
            "beam me down\n" +
            "viewer\n" +
            "logs\n" +
            "fuck | shit\n";

    // =====================================================================================
    // network operations.   ===   Hi Elvis!
    // =====================================================================================

    public static final int SERVERPORT = 1701;  // Network Common Channel - NCC-1701

    Handler updateConversationHandler;

    private ServerSocket serverSocket = null;

    Thread serverThread = null;

    // initialize the servers
    private void initserver() {
        say("Initialize the network server");
        // create the handler to receive events from communication thread
        updateConversationHandler = new Handler();
        // start the server thread
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }

    // stop the servers
    private void stopserver() {
        say("Stop the network");
        // stop the server thread
        serverThread.interrupt();
        // close the socket of the server
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ====================================================================================
    // server part

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Log.d("serverthread", "accepting server socket");
                    socket = serverSocket.accept();
                    Log.d("serverthread", "accepted server socket");

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    Log.d("serverthread", "exception " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket commSocket;

        private BufferedReader bufinput;

        public CommunicationThread(Socket socket) {

            commSocket = socket;

            try {

                bufinput = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));

            } catch (IOException e) {
                Log.d("commthreadinit", "exception " + e.toString());
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = bufinput.readLine();
                    if (read == null) break;
                    Log.d("commthreadrun", "update conversation");
                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    Log.d("commthreadrun", "exception " + e.toString());
                    e.printStackTrace();
                }
            }
        }

    }

    // tread to update the ui
    class updateUIThread implements Runnable {
        private String msg = null;

        public updateUIThread(String str) {
            msg = str;
            Log.d("uithread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                mTextstatus_top.setText(msg);
                say("Received: " + msg);
                if (matchvoice(msg) == false) {
                    speak(msg);
                }
            }
        }
    }

    // ====================================================================================
    // client part

    private Socket clientSocket = null;

    Thread clientThread = null;

    private void sendcommand(String text) {
        if(isMaster) {
            sendtext(text);
        }
    }

    // send a message to the other
    private void sendtext(String text) {
        // start the client thread
        say("Send: " + text);
        clientThread = new Thread(new ClientThread(text));
        clientThread.start();
    }

    class ClientThread implements Runnable {

        private String mesg;

        public ClientThread(String str) {
            mesg = str;
        }

        @Override
        public void run() {
            String myip = mFetcher.fetch_ip_address();
            for (int i = 0; i < mIpList.size(); ++i) {
                if (replaySent == false && myip.equals(mIpList.get(i))) {
                    Log.d("clientthread", "do not replay locally");
                    continue;
                }
                clientsend(mIpList.get(i));
            }
        }

        private void clientsend(String destip) {
            // try to connect to a socket
            try {
                Log.d("clientthread", "try to connect to a server " + destip);
                InetAddress serverAddr = InetAddress.getByName(destip);
                clientSocket = new Socket(serverAddr, SERVERPORT);
                Log.d("clientthread", "server connected " + destip);
            } catch (UnknownHostException e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            }
            // try to send the message
            try {
                Log.d("clientthread", "sending data");
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream())), true);
                out.println(mesg);
                Log.d("clientthread", "data sent");
            } catch (UnknownHostException e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            }
            // try to close the socket of the client
            try {
                Log.d("clientthread", "closing socket");
                clientSocket.close();
                Log.d("clientthread", "socket closed");
            } catch (Exception e) {
                Log.d("clientthread", e.toString());
                e.printStackTrace();
            }
        }

    }

    // =====================================================================================
    // register my service with NSD

    private String mServiceName=null;
    private NsdManager mNsdManager=null;
    private NsdManager.RegistrationListener mRegistrationListener=null;
    private NsdServiceInfo mService=null;
    private String SERVICE_TYPE = "_http._tcp.";
    private String SERVICE_NAME = "Trycorder";

    public void registerService() {
        if (deviceName.isEmpty()) {
            deviceName = SERVICE_NAME;
        }

        mNsdManager = (NsdManager) getContext().getSystemService(Context.NSD_SERVICE);

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(deviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(SERVERPORT);

        say("Register service");
        initializeRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
                Log.d("registration", "Registration done: " + mServiceName);
                saypost("Registration done: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d("registration", "Registration failed");
                saypost("Registration failed: " + mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d("registration", "Unregistration done");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.d("registration", "Unregistration failed");
            }
        };
    }

    // =======================================================================================
    // discovery section

    private NsdManager.DiscoveryListener mDiscoveryListener=null;
    private NsdManager.ResolveListener mResolveListener=null;

    private List<String> mIpList = new ArrayList<String>();
    private List<String> mNameList = new ArrayList<String>();

    public void startdiscoverService() {
        if (deviceName.isEmpty()) deviceName = SERVICE_NAME;

        mIpList.clear();
        mIpList.add(mFetcher.fetch_ip_address());
        mNameList.clear();
        mNameList.add(deviceName);

        mNsdManager = (NsdManager) getContext().getSystemService(Context.NSD_SERVICE);

        say("Discover services");
        initializeResolveListener();

        initializeDiscoveryListener();

        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    public void stopdiscoverService() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e("discovery", "Resolve failed: " + errorCode);
                saypost("Resolve failed: " + serviceInfo.getServiceName() + " Err:" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e("discovery", "Resolve Succeeded: " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d("discovery", "Same IP.");
                    saypost("Local machine " + mServiceName);
                    //return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
                Log.d("discovery", "Host: " + host.toString() + " Port: " + port);
                saypost("Resolved " + mService.getServiceName() +
                        " Host: " + host.toString() + " Port: " + port);
                StringBuffer str = new StringBuffer(host.toString());
                addiplist(str.substring(1), mService.getServiceName());
            }
        };
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("discovery", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d("discovery", "Service discovery success: " + service);
                saypost("Service discovered: " + service.getServiceName());
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("discovery", "Unknown Service Type: " + service.getServiceType());
                } else {
                    if (service.getServiceName().equals(mServiceName)) {
                        // The name of the service tells the user what they'd be
                        // connecting to. It could be "Bob's Chat App".
                        Log.d("discovery", "Same machine: " + mServiceName);
                        return;
                    }
                    Log.d("discovery", "Resolved service: " + service.getServiceName());
                    //Log.d("discovery", "Resolved service: " + service.getHost());  // empty
                    //Log.d("discovery", "Resolved service: " + service.getPort());  // empty
                    try {
                        mNsdManager.resolveService(service, mResolveListener);
                    } catch (Exception e) {
                        Log.d("discovery", "resolve error: " + e.toString());
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("discovery", "service lost: " + service);
                saypost("Lost: " + service.getServiceName());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i("discovery", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("discovery", "Start Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("discovery", "Stop Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    // post something to say on the main thread (from a secondary thread)
    public void saypost(String str) {
        updateConversationHandler.post(new sayThread(str));
    }

    // tread to update the ui
    class sayThread implements Runnable {
        private String msg;

        public sayThread(String str) {
            msg = str;
            Log.d("saythread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                say(msg);
            }
        }
    }

    private void addiplist(String ip, String name) {
        for (int i = 0; i < mIpList.size(); ++i) {
            if (ip.equals(mIpList.get(i))) {
                listpost();
                return;
            }
        }
        String newname = name.replaceFirst("\\032"," ");
        mIpList.add(ip);
        mNameList.add(newname);
        listpost();
    }

    public void listpost() {
        updateConversationHandler.post(new listThread());
    }

    // tread to update the ui
    class listThread implements Runnable {

        public listThread() {
        }

        @Override
        public void run() {
            StringBuffer str = new StringBuffer("");
            for (int i = 0; i < mIpList.size(); ++i) {
                str.append(mIpList.get(i) + " - " + mNameList.get(i) + "\n");
            }
            mWalkieIpList.setText(str.toString());
        }
    }

    // =====================================================================================
    // voice capture and send on udp

    private int RECORDING_RATE = 44100;
    private int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private int BUFFER_SIZE = AudioRecord.getMinBufferSize(RECORDING_RATE, CHANNEL, FORMAT);

    private AudioRecord recorder;

    private boolean currentlySendingAudio = false;

    private void startstreamingaudio() {
        say("start streaming audio");
        currentlySendingAudio = true;
        startStreaming();
    }

    private void stopstreamingaudio() {
        say("stop streaming audio");
        currentlySendingAudio = false;
        try {
            recorder.release();
        } catch (Exception e) {
            Log.d("stop streaming", "stop streaming error");
        }
    }

    private void startStreaming() {

        Log.i("startstreaming", "Starting the background thread to stream the audio data");

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Log.d("streamaudio", "Obtaining server address");
                    String SERVER;
                    int PORT = SERVERPORT;

                    Log.d("streamaudio", "Creating the datagram socket");
                    DatagramSocket socket = new DatagramSocket();

                    Log.d("streamaudio", "Creating the buffer of size " + BUFFER_SIZE);
                    byte[] buffer = new byte[BUFFER_SIZE];

                    List<InetAddress> mServerAddress = new ArrayList<>();
                    mServerAddress.clear();
                    for (int i = 0; i < mIpList.size(); ++i) {
                        SERVER = mIpList.get(i);
                        Log.d("streamaudio", "Connecting to " + SERVER + ":" + PORT);
                        mServerAddress.add(InetAddress.getByName(SERVER));
                        Log.d("streamaudio", "Connected to " + SERVER + ":" + PORT);
                    }

                    Log.d("streamaudio", "Creating the reuseable DatagramPacket");
                    DatagramPacket packet;

                    Log.d("streamaudio", "Creating the AudioRecord");
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);

                    Log.d("streamaudio", "AudioRecord recording...");
                    recorder.startRecording();

                    while (currentlySendingAudio == true) {

                        Log.d("streamloop", "Reading data from recorder");
                        // read the data into the buffer
                        int read = recorder.read(buffer, 0, buffer.length);

                        // repeat to myself if i am alone on the net only
                        int j;
                        if (mIpList.size() < 2) j = 0;
                        else j = 1;
                        // repeat to each other address from list
                        for (int i = j; i < mIpList.size(); ++i) {
                            // place contents of buffer into the packet
                            packet = new DatagramPacket(buffer, read, mServerAddress.get(i), PORT);

                            Log.d("streamloop", "Sending packet : " + read + " to " + mIpList.get(i));
                            // send the packet
                            socket.send(packet);
                        }
                    }

                    Log.d("streamaudio", "AudioRecord finished recording");

                } catch (Exception e) {
                    Log.e("streamaudio", "Exception: " + e);
                }
            }
        });

        // start the thread
        streamThread.start();
    }

    // =====================================================================================
    // voice receive on udp and playback
    private Thread talkServerThread = null;

    private void inittalkserver() {
        say("Start talk server thread");
        talkServerThread = new Thread(new TalkServerThread());
        talkServerThread.start();
    }

    private void stoptalkserver() {
        say("Stop the talk server");
        // stop the server thread
        try {
            talkServerThread.interrupt();
        } catch (Exception e) {
            say("cant stop talk server thread");
        }
    }

    class TalkServerThread implements Runnable {

        private int bufferSize = 10000;

        public void run() {

            DatagramSocket talkServerSocket;
            try {
                // create the server socket
                talkServerSocket = new DatagramSocket(null);
                talkServerSocket.setReuseAddress(true);
                talkServerSocket.bind(new InetSocketAddress(SERVERPORT));
                Log.d("talkserver", "socket created");
            } catch (Exception e) {
                saypost("talkserverthread Cant create socket");
                return;
            }

            byte[] receiveData = new byte[bufferSize];

            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    RECORDING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);
            track.play();

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Log.d("talkloop", "ready to receive " + bufferSize);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    talkServerSocket.receive(receivePacket);
                    byte[] buffer = receivePacket.getData();
                    int offset = receivePacket.getOffset();
                    int len = receivePacket.getLength();
                    Log.d("talkloop", "received bytes : " + offset + " - " + len);
                    track.write(buffer, offset, len);
                } catch (Exception e) {
                    Log.d("talkloop", "exception: " + e);
                }

            }

            track.flush();
            track.release();
            talkServerSocket.close();

        }

    }

    // ===================================================================================

    private void startTrycorderService() {
        say("Start Trycorder Service");
        try {
            getActivity().startService(new Intent(getContext(), TrycorderService.class));
        } catch (Exception e) {
            say("Cant start trycorder service");
        }
    }

    private void stopTrycorderService() {
        say("Stop Trycorder Service");
        try {
            getActivity().stopService(new Intent(getContext(), TrycorderService.class));
        } catch (Exception e) {
            say("Cant stop trycorder service");
        }
    }

    // ====================================================================================
    // port scanner part

    private Socket scanSocket = null;

    Thread scanThread = null;

    private List<String> listIp = new ArrayList<>();

    // scan all addresses and fill the list
    private void scantrycorders() {
        listIp.clear();
        scanThread = new Thread(new ScanThread());
        scanThread.start();
    }

    class ScanThread implements Runnable {

        public ScanThread() {
            // constructor
        }

        @Override
        public void run() {
            String myip = mFetcher.fetch_ip_address();
            String mynet=myip.substring(0,myip.lastIndexOf('.'));
            for(int i=1;i<255;++i) {
                String destip = mynet+"."+String.valueOf(i);
                if(destip.equals(myip)) {
                    listIp.add(destip);
                } else {
                    clientcheck(destip);
                }
            }
            mIpList=listIp;
            mNameList=listIp;
            listpost();
        }

        private void clientcheck(String destip) {
            // try to connect to a socket
            try {
                Log.d("scanthread", "try to connect to a server " + destip);
                InetAddress serverAddr = InetAddress.getByName(destip);
                clientSocket = new Socket(serverAddr, SERVERPORT);
                Log.d("scanthread", "server connected " + destip);
            } catch (UnknownHostException e) {
                Log.d("scanthread", e.toString());
                e.printStackTrace();
                return;
            } catch (IOException e) {
                Log.d("scanthread", e.toString());
                e.printStackTrace();
                return;
            }
            // add the ip found in the list
            listIp.add(destip);
            // try to close the socket of the client
            try {
                Log.d("scanthread", "closing socket");
                clientSocket.close();
                Log.d("scanthread", "socket closed");
            } catch (Exception e) {
                Log.d("scanthread", e.toString());
                e.printStackTrace();
                return;
            }
        }

    }


}
