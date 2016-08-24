package net.ddns.mlsoftlaberge.trycorder.tryclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.trycorder.R;
import net.ddns.mlsoftlaberge.trycorder.TrycorderService;
import net.ddns.mlsoftlaberge.trycorder.settings.SettingsActivity;
import net.ddns.mlsoftlaberge.trycorder.utils.Fetcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mlsoft on 16-06-26.
 */
public class TryclientFragment extends Fragment implements RecognitionListener {

    public TryclientFragment() {
    }

    // ======================================================================================
    public interface OnTryclientInteractionListener {
        public void onTryclientModeChange(int mode);
    }

    private OnTryclientInteractionListener mOnTryclientInteractionListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Assign callback listener which the holding activity must implement.
            mOnTryclientInteractionListener = (OnTryclientInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTryclientInteractionListener");
        }
    }

    // ======================================================================================

    // the button to talk to computer
    private ImageButton mBacktopButton;

    // the button for sound activation
    private Button mBackButton;

    private boolean mRunStatus = false;

    // the button for settings
    private Button mSettingsButton;

    // the one status line
    private TextView mTextstatus_top;
    private TextView mTextstatus_bottom;

    // the button to talk to computer
    private ImageButton mBackbottomButton;

    // the button to stop it all
    private Button mViewButton;

    // the button for settings
    private Button mSendButton;

    // in the bottom layout
    private TextView mLogsConsole;

    // utility class to fetch system infos
    private Fetcher mFetcher;

    // the walkie layout on sensor screen
    private LinearLayout mWalkieLayout;
    private Button mWalkieSpeakButton;
    private Button mWalkieTalkButton;
    private Button mWalkieScanButton;
    private Button mWalkieServeronButton;
    private Button mWalkieServeroffButton;
    private TextView mWalkieIpList;

    // the preferences holder
    private SharedPreferences sharedPref;

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

    // list obtained from the trycorder server
    private List<String> mIpList = new ArrayList<String>();
    private List<String> mNameList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tryclient_fragment, container, false);

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

        // ===================== top horizontal button grid ==========================
        // the start button
        mBacktopButton = (ImageButton) view.findViewById(R.id.backtop_button);
        mBacktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtryclientmode(1);
            }
        });

        // the sound-effect button
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtryclientmode(1);
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

        mTextstatus_top = (TextView) view.findViewById(R.id.textstatus_top);

        // ===================== bottom horizontal button grid ==========================
        // the ask button
        mBackbottomButton = (ImageButton) view.findViewById(R.id.backbottom_button);
        mBackbottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtryclientmode(1);
            }
        });

        // the stop button
        mViewButton = (Button) view.findViewById(R.id.view_button);
        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
            }
        });

        // the settings button
        mSendButton = (Button) view.findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
            }
        });

        mTextstatus_bottom = (TextView) view.findViewById(R.id.textstatus_bottom);
        mTextstatus_bottom.setText("Ready");

        // in the bottom layout
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);

        // utility class to fetch infos from the system
        mFetcher = new Fetcher(getContext());

        // ==================== top main action buttons ========================

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

        mWalkieScanButton = (Button) view.findViewById(R.id.walkie_scan_button);
        mWalkieScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                askscanlist();
            }
        });

        mWalkieServeronButton = (Button) view.findViewById(R.id.walkie_serveron_button);
        mWalkieServeronButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                startTrycorderService();
                bindTrycorderService();
            }
        });

        mWalkieServeroffButton = (Button) view.findViewById(R.id.walkie_serveroff_button);
        mWalkieServeroffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                unbindTrycorderService();
                stopTrycorderService();
            }
        });

        // the list of ip - machine that we discover
        mWalkieIpList = (TextView) view.findViewById(R.id.walkie_iplist);
        mWalkieIpList.setHorizontallyScrolling(true);
        mWalkieIpList.setMovementMethod(new ScrollingMovementMethod());

        // fill the list with at least our private IP until some events fill it more
        mWalkieIpList.setText(mFetcher.fetch_ip_address());

        return view;

    }

    // setup the fonts on every text-containing widgets
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "sonysketchef.ttf");
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "finalold.ttf");
        Typeface face3 = Typeface.createFromAsset(getActivity().getAssets(), "finalnew.ttf");
        // top buttons
        mBackButton.setTypeface(face2);
        mSettingsButton.setTypeface(face2);
        mTextstatus_top.setTypeface(face);
        // bottom buttons
        mViewButton.setTypeface(face2);
        mSendButton.setTypeface(face2);
        mTextstatus_bottom.setTypeface(face3);
        // top layout
        mWalkieSpeakButton.setTypeface(face2);
        mWalkieTalkButton.setTypeface(face2);
        mWalkieScanButton.setTypeface(face2);
        mWalkieServeronButton.setTypeface(face2);
        mWalkieServeroffButton.setTypeface(face2);
        mWalkieIpList.setTypeface(face3);
        // bottom layout
        mLogsConsole.setTypeface(face2);
    }
    // =====================================================================================

    private boolean mBound=false;
    private TrycorderService mTrycorderService;
    private TrycorderService.TryBinder mTryBinder;

    @Override
    public void onStart() {
        super.onStart();
        if (!autoBoot) startTrycorderService();
        bindTrycorderService();
    }

    public void bindTrycorderService() {
        // Bind to Service
        Intent intent = new Intent(getActivity(), TrycorderService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindTrycorderService();
        if(autoStop) stopTrycorderService();
    }

    public void unbindTrycorderService() {
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mTryBinder = (TrycorderService.TryBinder) service;
            mTrycorderService = mTryBinder.getService();
            mBound = true;
            askscanlist();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    // ask the service to give us the list of ip/names

    public void askscanlist() {
        if(mBound) {
            mIpList = mTrycorderService.getiplist();
            mNameList = mTrycorderService.getnamelist();
            saylist();
        }
    }

    public void saylist() {
        StringBuffer str = new StringBuffer("");
        for (int i = 0; i < mIpList.size(); ++i) {
            str.append(mIpList.get(i) + " - " + mNameList.get(i) + "\n");
        }
        mWalkieIpList.setText(str.toString());
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

    // =====================================================================================

    @Override
    public void onResume() {
        super.onResume();
        // settings part of the preferences
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
        // dynamic status part
        mRunStatus = sharedPref.getBoolean("pref_key_run_status", false);
        // start the speak server
        initspeak();
        // start the network listener server
        //initserver();
        // start the service if not started
        if(!autoBoot) startTrycorderService();
    }

    @Override
    public void onPause() {
        // save the current status
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("pref_key_run_status", mRunStatus);
        editor.commit();
        // stop the listener server
        //stopserver();
        // stop the service when needed
        if(autoStop) stopTrycorderService();
        super.onPause();
    }

    // ask the activity to switch to another fragment
    private void switchtryclientmode(int mode) {
        mOnTryclientInteractionListener.onTryclientModeChange(mode);
    }

    // beep
    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    // booooop
    private void buttonbad() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.denybeep1);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    // settings activity incorporation in the display
    public void settingsactivity() {
        say("Settings");
        if (isChatty) speak("Settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    // =========================================================================
    // tell something in the bottom status line
    private StringBuffer logbuffer = new StringBuffer(500);

    public void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0, texte + "\n");
        mLogsConsole.setText(logbuffer);
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
                        //if (mIpList.size() < 2) j = 0;
                        //else j = 1;
                        j=1;
                        // repeat to each other address from list
                        if(mIpList.size()>1) for (int i = j; i < mIpList.size(); ++i) {
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

    // =========================================================================
    // usage of text-to-speech to speak a sensence
    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private TextToSpeech tts=null;

    private void initspeak() {
        if(tts==null) {
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
        say("Speaked: "+texte);
    }

    public void speak(String texte,String lng) {
        initspeak();
        setspeaklang(lng);
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
        say("Speaked: "+texte);
    }


    // ========================================================================================
    // functions to control the speech process

    // handles for the conversation functions
    private SpeechRecognizer mSpeechRecognizer=null;
    private Intent mSpeechRecognizerIntent=null;

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
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200);

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
                    return;
                }
            }
            mTextstatus_top.setText(dutexte.get(0));
            say("Understood: " + dutexte.get(0));
            sendtext(dutexte.get(0));
        }
    }

    private boolean matchvoice(String textein) {
        String texte = textein.toLowerCase();
        if (texte.contains("french") || texte.contains("français")) {
            listenLanguage = "FR";
            speakLanguage = "FR";
            speak("français",speakLanguage);
            return (true);
        }
        if (texte.contains("english") || texte.contains("anglais")) {
            listenLanguage = "EN";
            speakLanguage = "EN";
            speak("english",speakLanguage);
            return (true);
        }
        return (false);
    }

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
        private String msg=null;

        public updateUIThread(String str) {
            msg = str;
            Log.d("uithread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                displaytext(msg);
            }
        }
    }

    public void displaytext(String msg) {
        mTextstatus_top.setText(msg);
        say("Received: " + msg);
        if(matchvoice(msg)==false) {
            speak(msg);
        }
    }


    // ====================================================================================
    // client part

    private Socket clientSocket = null;

    Thread clientThread = null;

    // send a message to the other
    private void sendtext(String text) {
        // start the client thread
        say("Send: "+text);
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
                if(replaySent==false && myip.equals(mIpList.get(i))) {
                    Log.d("clientthread","do not replay locally");
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



}
