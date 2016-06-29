package net.ddns.mlsoftlaberge.trycorder.trycorder;

import android.app.Activity;
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
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
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
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import net.ddns.mlsoftlaberge.trycorder.R;
import net.ddns.mlsoftlaberge.trycorder.contacts.ContactsListActivity;
import net.ddns.mlsoftlaberge.trycorder.gallery.GalleryActivity;
import net.ddns.mlsoftlaberge.trycorder.products.ProductsListActivity;
import net.ddns.mlsoftlaberge.trycorder.settings.SettingsActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    // handles for the conversation functions
    private TextToSpeech tts=null;
    private SpeechRecognizer mSpeechRecognizer=null;
    private Intent mSpeechRecognizerIntent=null;

    // handle for the gps
    private LocationManager mLocationManager=null;

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

    // the 3 modes from the viewer buttons layout
    private ImageView mFederationlogo;
    private ImageView mStarshipPlans;
    private ImageView mViewerPhoto;

    // the 3 modes from the logs buttons layout
    private TextView mLogsConsole;
    private TextView mLogsInfo;
    private TextView mLogsSys;

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
            }
        });

        // the tractor off button
        mMotorOffButton = (Button) view.findViewById(R.id.motor_off_button);
        mMotorOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motoroff();
            }
        });

        // the tractor pull button
        mMotorWarpButton = (Button) view.findViewById(R.id.motor_warp_button);
        mMotorWarpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motorwarp();
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
                switchcam(0);
                switchviewer(0);
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
                switchcam(0);
                switchviewer(2);
            }
        });

        mLogsInfoButton = (Button) view.findViewById(R.id.logsinfo_button);
        mLogsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchcam(0);
                switchviewer(3);
            }
        });

        mLogsPlansButton = (Button) view.findViewById(R.id.logsplans_button);
        mLogsPlansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchcam(0);
                switchviewer(4);
            }
        });

        mLogsSysButton = (Button) view.findViewById(R.id.logssys_button);
        mLogsSysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchcam(0);
                switchviewer(6);
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
        mViewerPhoto =  (ImageView) view.findViewById(R.id.photo_view);
        mLogsSys = (TextView) view.findViewById(R.id.logs_sys);

        // frame for motor animations
        mViewerAnimate = (FrameLayout) view.findViewById(R.id.viewer_animate);
        // image inside the vieweranimate layout
        mImageEarthStill = (ImageView) view.findViewById(R.id.image_earthstill);

        // warp effect animation gif
        mGIFView= new GIFView(getContext(),R.raw.warp_animation);
        mViewerAnimate.addView(mGIFView);
        // impulse effect animation gif
        mGIFView1= new GIFView(getContext(),R.raw.earth_rotating);
        mViewerAnimate.addView(mGIFView1);

        // set all visibilitys of Vieweranimate frame
        mImageEarthStill.setVisibility(View.VISIBLE);
        mGIFView.setVisibility(View.GONE);
        mGIFView1.setVisibility(View.GONE);

        // create and activate a textureview to contain camera display
        mViewerWindow = (TextureView) view.findViewById(R.id.viewer_window);
        mViewerWindow.setSurfaceTextureListener(this);

        mFederationlogo.setVisibility(View.VISIBLE);
        mViewerWindow.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        mLogsInfo.setVisibility(View.GONE);
        mStarshipPlans.setVisibility(View.GONE);
        mViewerPhoto.setVisibility(View.GONE);
        mLogsSys.setVisibility(View.GONE);
        mViewerAnimate.setVisibility(View.GONE);
        mVieweron = false;

        // ==============================================================================
        // create layout params for the created views
        final LinearLayout.LayoutParams tlayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        // ============== create a sensor display and incorporate in layout ==============

        // a sensor manager to obtain sensors data
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        // a gps manager to obtain gps data
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // my sensorview that display the sensors data
        mMagSensorView = new MagSensorView(getContext(),mSensorManager);
        // add my sensorview to the layout 1
        mSensorLayout.addView(mMagSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mOriSensorView = new OriSensorView(getContext(),mSensorManager,mLocationManager);
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
        mGraSensorView = new GraSensorView(getContext(),mSensorManager);
        // add my sensorview to the layout 1
        mSensorLayout.addView(mGraSensorView, tlayoutParams);

        // my sensorview that display the sensors data
        mTemSensorView = new TemSensorView(getContext(),mSensorManager);
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

        mFetcher = new Fetcher(getContext());

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
        // dynamic status part
        mSensormode = sharedPref.getInt("pref_key_sensor_mode", 0);
        mSensorpage = sharedPref.getInt("pref_key_sensor_page", 0);
        mButtonsmode = sharedPref.getInt("pref_key_buttons_mode", 0);
        mViewermode = sharedPref.getInt("pref_key_viewer_mode", 0);
        mSoundStatus = sharedPref.getBoolean("pref_key_audio_mode", false);
        mViewerfront = sharedPref.getBoolean("pref_key_viewer_front", false);
        // resurect the application to last settings
        switchbuttonlayout(mButtonsmode);
        switchsensorlayout(mSensormode);
        switchviewer(mViewermode);
        if(mSensormode<=4) startsensors(mSensormode);
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
        editor.putBoolean("pref_key_viewer_front", mViewerfront);
        editor.commit();
        stopsensors();
        switchcam(0);
        super.onPause();
    }

    private int planno=0;

    private void switchplans() {
        planno++;
        if (planno>=6) planno=0;
        switch(planno) {
            case 0:
                mStarshipPlans.setImageResource(R.drawable.starship_view);
                break;
            case 1:
                mStarshipPlans.setImageResource(R.drawable.starship_build);
                break;
            case 2:
                mStarshipPlans.setImageResource(R.drawable.starship_plan);
                break;
            case 3:
                mStarshipPlans.setImageResource(R.drawable.starship_sideview);
                break;
            case 4:
                mStarshipPlans.setImageResource(R.drawable.starship_topview);
                break;
            case 5:
                mStarshipPlans.setImageResource(R.drawable.earth_still);
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
        stopmotsensors();
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        say("Settings");
        if(isChatty) speak("Settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    // settings activity incorporation in the display
    public void accesscrew() {
        say("Access Starship Crew");
        if(isChatty) speak("Crew information and evaluation");
        Intent i = new Intent(getActivity(), ContactsListActivity.class);
        startActivity(i);
    }

    // settings activity incorporation in the display
    public void accessinventory() {
        say("Access Starship Inventory");
        if(isChatty) speak("Inventory");
        Intent i = new Intent(getActivity(), ProductsListActivity.class);
        startActivity(i);
    }

    private void opengallery() {
        say("Open Gallery Class");
        if(isChatty) speak("Gallery");
        Intent i = new Intent(getActivity(), GalleryActivity.class);
        startActivity(i);
    }

    private void switchtrycordermode(int mode) {
        mOnTrycorderInteractionListener.onTrycorderModeChange(mode);
    }

    // =========================================================================================
    // map activity to see where we are on the map of this planet

    public void googlemapactivity() {
        float longitude=mOriSensorView.getLongitude();
        float latitude=mOriSensorView.getLatitude();

        //final Intent viewIntent = new Intent(Intent.ACTION_VIEW, constructGeoUri(view.getContentDescription().toString()));
        String geopath = "geo:"+String.valueOf(latitude)+","+String.valueOf(longitude);
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
        }
        if(no<=4) mSensorpage = no;
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
        switch (no) {
            case 1:
                say("Sensors Mode");
                mButtonssensorLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                say("Communication Mode");
                mButtonscommLayout.setVisibility(View.VISIBLE);
                break;
            case 3:
                say("Shield Mode");
                mButtonsshieldLayout.setVisibility(View.VISIBLE);
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
        if(isChatty) speak("Hailing frequency opened");
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commopen);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
        switchsensorlayout(5);
        startsensors(5);
        say("Hailing frequency open.");
    }

    private void closecomm() {
        if(isChatty) speak("Hailing frequency closed");
        else {
            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commclose);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
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
        if(isChatty) speak("Transport In Progress. . . Transport Complete");
    }

    private void transporterin() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.beam1b);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Transport In");
        switchsensorlayout(8);
        mTraSensorView.setmode(1);
        startsensors(8);
        if(isChatty) speak("Transport In Progress. . . Transport Complete");
    }

    private void raiseshields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shieldup);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Raise Shields");
        switchsensorlayout(6);
        mShiSensorView.setmode(1);
        startsensors(6);
        if(isChatty) speak("Shields Up");
    }

    private void lowershields() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.shielddown);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Lower Shields");
        switchsensorlayout(6);
        mShiSensorView.setmode(2);
        startsensors(6);
        if(isChatty) speak("Shields Down");
    }

    private void tractorpush() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Push");
        switchsensorlayout(9);
        mTrbSensorView.setmode(1);
        startsensors(9);
        if(isChatty) speak("Repulser Beam Engaged");
    }

    private void tractoroff() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Tractor Beam Off");
        switchsensorlayout(9);
        mTrbSensorView.setmode(0);
        stopsensors();
        if(isChatty) speak("Beam Off");
    }

    private void tractorpull() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.tng_tractor_clean);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Engage Tractor Beam Pull");
        switchsensorlayout(9);
        mTrbSensorView.setmode(2);
        startsensors(9);
        if(isChatty) speak("Tractor Beam Engaged");
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
        if(isChatty) speak("Impulse Engine Engaged");
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
        if(isChatty) speak("All engines down");
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
        if(isChatty) speak("Warp Drive Engaged");
    }

    private void firephaser() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.phasertype2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Phaser");
        switchsensorlayout(7);
        mFirSensorView.setmode(2);
        startsensors(7);
        if(isChatty) speak("The target is disabled");
    }

    private void firemissiles() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.photorp1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Fire Torpedo");
        switchsensorlayout(7);
        mFirSensorView.setmode(1);
        startsensors(7);
        if(isChatty) speak("The ship is destroyed");
    }

    private void switchanimate(int no) {
        mImageEarthStill.setVisibility(View.GONE);
        mGIFView.setVisibility(View.GONE);
        mGIFView1.setVisibility(View.GONE);
        switch(no) {
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
        mViewerAnimate.setVisibility(View.GONE);
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
                mLogsSys.setVisibility(View.VISIBLE);
                mVieweron = false;
                mLogsSys.setText("");
                mLogsSys.append("--------------------\nConnectivity\n--------------------\n");
                mLogsSys.append(mFetcher.fetch_connectivity());
                mLogsSys.append("--------------------\nSensors List\n--------------------\n");
                mLogsSys.append(mFetcher.fetch_sensors_list());
                mLogsSys.append("--------------------\nCPU Info\n--------------------\n");
                mLogsSys.append(mFetcher.fetch_cpu_info());
                mLogsSys.append("--------------------\nMemory Info\n--------------------\n");
                mLogsSys.append(mFetcher.fetch_memory_info());
                break;
            case 7:
                say("Animate Viewer");
                mViewerAnimate.setVisibility(View.VISIBLE);
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
                String mSentence = dutexte.get(i);
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
        if(mSpeechRecognizer==null) {
            // ============== initialize the audio listener and talker ==============

            //AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

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

    private void speak(String texte) {
        if(tts==null) {
            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                    }
                }
            });
        }
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
            switchbuttonlayout(9);
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


}
