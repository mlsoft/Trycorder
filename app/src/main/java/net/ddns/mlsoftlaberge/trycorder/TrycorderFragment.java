package net.ddns.mlsoftlaberge.trycorder;

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
import android.media.AudioManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


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

    // handles to camera and textureview
    private Camera mCamera;
    private TextureView mViewerWindow;
    private TextView mLogsConsole;

    // handles for the conversation functions
    private TextToSpeech tts;
    private AudioManager mAudioManager;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private String mSentence;

    // the handle to the sensors
    private SensorManager mSensorManager;

    // the new scope class
    private MagSensorView mMagSensorView;

    // the new scope class
    private OriSensorView mOriSensorView;

    // the new scope class
    private GraSensorView mGraSensorView;

    // the button to talk to computer
    private ImageButton mTalkButton;

    // the button to start it all
    private Button mStartButton;

    // the button to stop it all
    private Button mStopButton;
    private boolean mRunStatus=false;

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
    private Button mSensoroffButton;
    private int mSensormode=0;

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
    private Button mViewerOffButton;
    private Button mViewerLogsButton;
    private boolean mVieweron;
    private int mViewermode=0;

    // the button to control sound-effects
    private Button mSoundButton;
    private boolean mSoundStatus=false;

    // the layout to put sensorview in
    private LinearLayout mSensorLayout;

    private LinearLayout mSensor2Layout;
    private LinearLayout mSensor2sensorLayout;
    private LinearLayout mSensor2commLayout;
    private LinearLayout mSensor2shieldLayout;
    private LinearLayout mSensor2fireLayout;
    private LinearLayout mSensor2transporterLayout;
    private LinearLayout mSensor2viewerLayout;

    private LinearLayout mSensor3Layout;
    private ImageView mFederationlogo;

    // the player for sound background
    private MediaPlayer mMediaPlayer=null;

    // the preferences values
    boolean autoListen;
    String speakLanguage;
    String listenLanguage;
    String displayLanguage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trycorder_fragment, container, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen",false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        displayLanguage = sharedPref.getString("pref_key_display_language", "");

        // ===================== top horizontal button grid ==========================
        // the start button
        mTalkButton = (ImageButton) view.findViewById(R.id.talk_button);
        mTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenandtalk();
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
                listenandtalk();
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
                longrangesensor();
                switchsensorlayout(2);
                startsensors(2);
            }
        });

        // the gravity button
        mGravityButton = (Button) view.findViewById(R.id.gravity_button);
        mGravityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tractorbeam();
                switchsensorlayout(3);
                startsensors(3);
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
                switchviewer(1);
            }
        });

        mViewerOffButton = (Button) view.findViewById(R.id.vieweroff_button);
        mViewerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(0);
            }
        });

        mViewerLogsButton = (Button) view.findViewById(R.id.viewerlogs_button);
        mViewerLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchviewer(2);
            }
        });

        // ===================== sound buttons group ============================
        // the sound-effect button
        mSoundButton = (Button) view.findViewById(R.id.sound_button);
        mSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchsound();
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

        // the sensor layout, to contain my surfaceview
        mSensor3Layout = (LinearLayout) view.findViewById(R.id.sensor3_layout);
        mFederationlogo = (ImageView) view.findViewById(R.id.federation_logo);
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);
        // create and activate a textureview to contain camera display
        mViewerWindow = (TextureView) view.findViewById(R.id.viewer_window);
        mViewerWindow.setSurfaceTextureListener(this);

        mFederationlogo.setVisibility(View.VISIBLE);
        mViewerWindow.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        mVieweron=false;
        mViewermode=0;

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
        mSensorLayout.addView(mMagSensorView,tlayoutParams);

        // my sensorview that display the sensors data
        mOriSensorView = new OriSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mOriSensorView,tlayoutParams);

        // my sensorview that display the sensors data
        mGraSensorView = new GraSensorView(getContext());

        // add my sensorview to the layout 1
        mSensorLayout.addView(mGraSensorView,tlayoutParams);

        // set the sensors invisible
        switchsensorlayout(0);

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
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.mlsoft");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,false);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,5000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,500);
        //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"sonysketchef.ttf");
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),"finalold.ttf");
        Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(),"finalnew.ttf");
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
        mSettingsButton.setTypeface(face2);
        // left column buttons
        mSensorButton.setTypeface(face2);
        mCommButton.setTypeface(face2);
        mShieldButton.setTypeface(face2);
        mFireButton.setTypeface(face2);
        mTransporterButton.setTypeface(face2);
        mViewerButton.setTypeface(face2);
        mSoundButton.setTypeface(face2);
        // center buttons
        mMagneticButton.setTypeface(face3);
        mOrientationButton.setTypeface(face3);
        mGravityButton.setTypeface(face3);
        mSensoroffButton.setTypeface(face3);
        mOpenCommButton.setTypeface(face3);
        mCloseCommButton.setTypeface(face3);
        mShieldUpButton.setTypeface(face3);
        mShieldDownButton.setTypeface(face3);
        mPhaserButton.setTypeface(face3);
        mTorpedoButton.setTypeface(face3);
        mTransportOutButton.setTypeface(face);
        mTransportInButton.setTypeface(face);
        mViewerOnButton.setTypeface(face3);
        mViewerOffButton.setTypeface(face3);
        mViewerLogsButton.setTypeface(face3);
    }

    @Override
    public void onResume() {
        super.onResume();
        switchbuttonlayout(1);
        startsensors(mSensormode);
    }

    @Override
    public void onPause() {
        stopsensors();
        super.onPause();
    }


    private void startsensors(int mode) {
        stopsensors();
        mSensormode=mode;
        switch(mode) {
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
            default:
                say("Sensors OFF");
                break;
        }
    }

    private void stopsensors() {
        stopmagsensors();
        stoporisensors();
        stopgrasensors();
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
        switch(no) {
            case 1:
                mMagSensorView.setVisibility(View.VISIBLE);
                break;
            case 2:
                mOriSensorView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mGraSensorView.setVisibility(View.VISIBLE);
                break;
        }
    }

    // =====================================================================================

    private void switchbuttonlayout(int no) {
        mSensor2sensorLayout.setVisibility(View.GONE);
        mSensor2commLayout.setVisibility(View.GONE);
        mSensor2shieldLayout.setVisibility(View.GONE);
        mSensor2fireLayout.setVisibility(View.GONE);
        mSensor2transporterLayout.setVisibility(View.GONE);
        mSensor2viewerLayout.setVisibility(View.GONE);
        switch(no) {
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
        }
    }

    // =====================================================================================

    private void listenandtalk() {
        listen();
    }

    // =====================================================================================

    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void opencomm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commopen);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Healing frequency open");
    }

    private void closecomm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.commclose);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        say("Healing frequency closed");
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
        if(mMediaPlayer==null) {
            mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.tricscan2);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    // stop the background sound
    private void stopmusic() {
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // switch background sound on/off
    private void switchsound() {
        if(mSoundStatus) {
            mSoundStatus=false;
            stopmusic();
            mSoundButton.setBackgroundResource(R.drawable.trekbutton);
            //mSoundButton.setBackgroundColor(Color.GRAY);
        } else {
            mSoundStatus=true;
            if(mRunStatus) startmusic();
            mSoundButton.setBackgroundResource(R.drawable.trekbutton_yellow);
            //mSoundButton.setBackgroundColor(Color.YELLOW);
        }
    }

    // ============================================================================
    // stop the sensor updates
    private void stoporisensors() {
        stopmusic();
        mSensorManager.unregisterListener(mOriSensorView);
        mRunStatus=false;
    }

    // here we start the sensor reading
    private void startorisensors() {
        mRunStatus=true;
        // link a sensor to the sensorview
        mSensorManager.registerListener(mOriSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        if(mSoundStatus) startmusic();
    }

    // ==========================================================================================

    public class OriSensorView extends TextView implements SensorEventListener {

        private Paint paint;
        private float position = 0;

        public OriSensorView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setTextSize(25);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int xPoint = getMeasuredWidth() / 2;
            int yPoint = getMeasuredHeight() / 2;

            float radius = (float) (Math.max(xPoint, yPoint) * 0.6);
            canvas.drawCircle(xPoint, yPoint, radius, paint);
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

            // 3.1416 is a good approximation for the circle
            canvas.drawLine(xPoint,
                    yPoint,
                    (float) (xPoint + radius
                            * Math.sin((double) (-position) / 180 * 3.1416)),
                    (float) (yPoint - radius
                            * Math.cos((double) (-position) / 180 * 3.1416)), paint);

            canvas.drawText(String.valueOf(position), xPoint, yPoint, paint);
        }

        public void updateData(float position) {
            this.position = position;
            invalidate();
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

    }

    // ============================================================================
    // stop the sensor updates
    private void stopmagsensors() {
        stopmusic();
        mSensorManager.unregisterListener(mMagSensorView);
        mRunStatus=false;
    }

    // here we start the sensor reading
    private void startmagsensors() {
        mRunStatus=true;
        // link a sensor to the sensorview
        mMagSensorView.resetcount();
        mSensorManager.registerListener(mMagSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        if(mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class MagSensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[]=new int[3];
        private float mWidth;
        private float mHeight;
        private float mYOffset;
        private float mScale;
        private float mSpeed=0.5f;

        // table of values for the trace
        private int MAXVALUES = 300;
        private float mValues[] = new float[MAXVALUES * 3];
        private int nbValues=0;

        // initialize the 3 colors, and setup painter
        public MagSensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            for(int i=0; i<(MAXVALUES * 3); ++i) {
                mValues[i]=0.0f;
            }
            nbValues=0;
        }

        public void resetcount() {
            nbValues=0;
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
            mSpeed = mWidth/MAXVALUES;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if(mBitmap!=null) {
                    // clear the surface
                    mCanvas.drawColor(Color.BLACK);
                    // draw middle line horizontal
                    mPaint.setColor(0xffaaaaaa);
                    mPaint.setStrokeWidth(1.0f);
                    mCanvas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                    // draw the 100 values x 3 rows
                    for(int i=0; i<nbValues-1;++i) {
                        for(int j=0; j<3;++j) {
                            int k=(j*MAXVALUES)+i;
                            float oldx=i*mSpeed;
                            float newx=(i+1)*mSpeed;
                            mPaint.setColor(mColor[j]);
                            mPaint.setStrokeWidth(3.0f);
                            mCanvas.drawLine(oldx, mValues[k], newx, mValues[k+1], mPaint);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap,0,0,null);
                }
            }
            super.onDraw(viewcanvas);
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD) {
                        // scroll left when full
                        if(nbValues>=MAXVALUES) {
                            for (int i = 0; i < (MAXVALUES * 3)-1; ++i) {
                                mValues[i] = mValues[i+1];
                            }
                            nbValues--;
                        }
                        // fill the 3 elements in the table
                        for(int i=0; i<3; ++i) {
                            final float v = mYOffset +event.values[i] * mScale;
                            mValues[nbValues+(i*MAXVALUES)]=v;
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
        mRunStatus=false;
    }

    // here we start the sensor reading
    private void startgrasensors() {
        mRunStatus=true;
        // link a sensor to the sensorview
        mGraSensorView.resetcount();
        mSensorManager.registerListener(mGraSensorView,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        if(mSoundStatus) startmusic();
    }

    // ============================================================================
    // class defining the sensor display widget
    private class GraSensorView extends TextView implements SensorEventListener {

        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Canvas mCanvas = new Canvas();
        private int mColor[]=new int[3];
        private float mWidth;
        private float mHeight;
        private float mYOffset;
        private float mScale;
        private float mSpeed=0.5f;

        // table of values for the trace
        private int MAXVALUES = 300;
        private float mValues[] = new float[MAXVALUES * 3];
        private int nbValues=0;

        // initialize the 3 colors, and setup painter
        public GraSensorView(Context context) {
            super(context);
            mColor[0] = Color.argb(192, 255, 64, 64);
            mColor[1] = Color.argb(192, 64, 64, 255);
            mColor[2] = Color.argb(192, 64, 255, 64);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            for(int i=0; i<(MAXVALUES * 3); ++i) {
                mValues[i]=0.0f;
            }
            nbValues=0;
        }

        public void resetcount() {
            nbValues=0;
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
            mSpeed = mWidth/MAXVALUES;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        // draw
        @Override
        public void onDraw(Canvas viewcanvas) {
            synchronized (this) {
                if(mBitmap!=null) {
                    // clear the surface
                    mCanvas.drawColor(Color.BLACK);
                    // draw middle line horizontal
                    mPaint.setColor(0xffaaaaaa);
                    mPaint.setStrokeWidth(1.0f);
                    mCanvas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                    // draw the 100 values x 3 rows
                    for(int i=0; i<nbValues-1;++i) {
                        for(int j=0; j<3;++j) {
                            int k=(j*MAXVALUES)+i;
                            float oldx=i*mSpeed;
                            float newx=(i+1)*mSpeed;
                            mPaint.setColor(mColor[j]);
                            mPaint.setStrokeWidth(3.0f);
                            mCanvas.drawLine(oldx, mValues[k], newx, mValues[k+1], mPaint);
                        }
                    }
                    // transfer the bitmap to the view
                    viewcanvas.drawBitmap(mBitmap,0,0,null);
                }
            }
            super.onDraw(viewcanvas);
        }

        // extract sensor data and plot them on view
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                if (mBitmap != null) {
                    if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER) {
                        // scroll left when full
                        if(nbValues>=MAXVALUES) {
                            for (int i = 0; i < (MAXVALUES * 3)-1; ++i) {
                                mValues[i] = mValues[i+1];
                            }
                            nbValues--;
                        }
                        // fill the 3 elements in the table
                        for(int i=0; i<3; ++i) {
                            final float v = mYOffset +event.values[i] * mScale;
                            mValues[nbValues+(i*MAXVALUES)]=v;
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

        try {
            mCamera = Camera.open();
            mCamera.setPreviewTexture(surface);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    // ==========================================================================================
    // switch the viewer on/off
    private void switchviewer(int no) {
        mViewerWindow.setVisibility(View.GONE);
        mFederationlogo.setVisibility(View.GONE);
        mLogsConsole.setVisibility(View.GONE);
        switch(no) {
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
        }
        mViewermode=no;
    }

    // =====================================================================================
    // ask the camera to take a photo, and pass it to onPictureTaken


    private void snapphoto() {
        if(mVieweron) {
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

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Trycorder", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
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
                say(fileUri.toString());
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
                say("" + data.getData());
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
            for(int i=0;i<dutexte.size();++i) {
                mSentence = dutexte.get(i);
                if(matchvoice(mSentence)) {
                    mTextstatus_top.setText(mSentence);
                    return;
                }
            }
            mTextstatus_top.setText(dutexte.get(0));
            speak("Unknown command.");
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    // ========================================================================================
    // functions to control the speech process

    private void listen() {
        if(listenLanguage.equals("FR")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        } else if(listenLanguage.equals("EN")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        } else {
            // automatic
        }
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        mTextstatus_top.setText("");
        say("Speak");
    }

    private void speak(String texte) {
        if(speakLanguage.equals("FR")) {
            tts.setLanguage(Locale.FRENCH);
        } else if(speakLanguage.equals("EN")) {
            tts.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say(texte);
    }

    private StringBuffer logbuffer=new StringBuffer(500);

    private void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0,texte + "\n");
        mLogsConsole.setText(logbuffer);
    }

    private boolean matchvoice(String texte) {
        if(texte.contains("Martin")) {
            switchbuttonlayout(0);
            speak("Martin is my Master.");
            return(true);
        }
        if(texte.contains("phaser")) {
            switchbuttonlayout(4);
            firephaser();
            return(true);
        }
        if(texte.contains("fire") || texte.contains("torpedo")) {
            switchbuttonlayout(4);
            firemissiles();
            return(true);
        }
        if(texte.contains("shield") || texte.contains("raise")) {
            switchbuttonlayout(3);
            raiseshields();
            return(true);
        }
        if(texte.contains("sensor off")) {
            switchbuttonlayout(0);
            switchsensorlayout(0);
            stopsensors();
            return(true);
        }
        if(texte.contains("sensor") || texte.contains("magnetic")) {
            switchbuttonlayout(1);
            switchsensorlayout(1);
            startsensors(1);
            return(true);
        }
        if(texte.contains("orientation") || texte.contains("direction")) {
            switchbuttonlayout(1);
            switchsensorlayout(2);
            startsensors(2);
            return(true);
        }
        if(texte.contains("gravity") || texte.contains("vibration")) {
            switchbuttonlayout(1);
            switchsensorlayout(3);
            startsensors(3);
            return(true);
        }
        if(texte.contains("hailing") && texte.contains("close")) {
            switchbuttonlayout(2);
            closecomm();
            return(true);
        }
        if(texte.contains("hailing") && texte.contains("open")) {
            switchbuttonlayout(2);
            opencomm();
            return(true);
        }
        if(texte.contains("beam me up") || texte.contains("scotty")) {
            switchbuttonlayout(5);
            transporterin();
            return(true);
        }
        if(texte.contains("beam me down")) {
            switchbuttonlayout(5);
            transporterout();
            return(true);
        }
        if(texte.contains("viewer")) {
            switchbuttonlayout(6);
            switchviewer(1);
            return(true);
        }
        return(false);
    }
}
