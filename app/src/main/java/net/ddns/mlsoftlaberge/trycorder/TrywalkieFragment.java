package net.ddns.mlsoftlaberge.trycorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.trycorder.R;
import net.ddns.mlsoftlaberge.trycorder.settings.SettingsActivity;
import net.ddns.mlsoftlaberge.trycorder.trycorder.Fetcher;
import net.ddns.mlsoftlaberge.trycorder.trycorder.Listen;
import net.ddns.mlsoftlaberge.trycorder.trycorder.Speak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
public class TrywalkieFragment extends Fragment implements RecognitionListener {

    public TrywalkieFragment() {
    }

    // ======================================================================================
    public interface OnTrywalkieInteractionListener {
        public void onTrywalkieModeChange(int mode);
    }

    private OnTrywalkieInteractionListener mOnTrywalkieInteractionListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Assign callback listener which the holding activity must implement.
            mOnTrywalkieInteractionListener = (OnTrywalkieInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrywalkieInteractionListener");
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
    private Button mSpeakButton;

    // the button for settings
    private Button mSendButton;

    // the main contents layout in the center
    private LinearLayout mCenterLayout;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;

    // in the top layout
    private TextView mMyip;
    private TextView mDestlist;
    private Button mConnectButton;
    private TextView mSendedtext;
    private TextView mReceivedtext;

    // in the bottom layout
    private TextView mLogsConsole;

    // the preferences holder
    private SharedPreferences sharedPref;

    // the preferences values
    private boolean autoListen;
    private boolean isChatty;
    private String speakLanguage;
    private String listenLanguage;
    private String displayLanguage;
    private String deviceName;
    private boolean replaySent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trywalkie_fragment, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        autoListen = sharedPref.getBoolean("pref_key_auto_listen", false);
        isChatty = sharedPref.getBoolean("pref_key_ischatty", false);
        speakLanguage = sharedPref.getString("pref_key_speak_language", "");
        listenLanguage = sharedPref.getString("pref_key_listen_language", "");
        displayLanguage = sharedPref.getString("pref_key_display_language", "");
        deviceName = sharedPref.getString("pref_key_device_name", "");
        replaySent = sharedPref.getBoolean("pref_key_replay_sent", true);

        // ===================== top horizontal button grid ==========================
        // the start button
        mBacktopButton = (ImageButton) view.findViewById(R.id.backtop_button);
        mBacktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtryviewermode(1);
            }
        });

        // the sound-effect button
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                switchtryviewermode(1);
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
                switchtryviewermode(1);
            }
        });

        // the stop button
        mSpeakButton = (Button) view.findViewById(R.id.speak_button);
        mSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
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

        // =========================== center content area ============================

        // the center layout to show contents
        mCenterLayout = (LinearLayout) view.findViewById(R.id.center_layout);
        mTopLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        mBottomLayout = (LinearLayout) view.findViewById(R.id.bottom_layout);

        // in the top layout
        mMyip = (TextView) view.findViewById(R.id.myip_text);
        mDestlist = (TextView) view.findViewById(R.id.destlist_text);
        mConnectButton = (Button) view.findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
            }
        });
        mSendedtext = (TextView) view.findViewById(R.id.sended_msg);
        mReceivedtext = (TextView) view.findViewById(R.id.received_msg);

        // in the bottom layout
        mLogsConsole = (TextView) view.findViewById(R.id.logs_console);

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
        mSpeakButton.setTypeface(face2);
        mSendButton.setTypeface(face2);
        mTextstatus_bottom.setTypeface(face3);
        // top layout
        mMyip.setTypeface(face2);
        mDestlist.setTypeface(face3);
        mConnectButton.setTypeface(face2);
        mSendedtext.setTypeface(face3);
        mReceivedtext.setTypeface(face3);
        // bottom layout
        mLogsConsole.setTypeface(face2);
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
        deviceName = sharedPref.getString("pref_key_device_name", "");
        replaySent = sharedPref.getBoolean("pref_key_replay_sent", true);
        // dynamic status part
        mRunStatus = sharedPref.getBoolean("pref_key_run_status", false);
        // override the languages for french
        speakLanguage = "FR";
        listenLanguage = "FR";
        // fill my fields
        Fetcher fetcher = new Fetcher(getContext());
        mMyip.setText(fetcher.fetch_ip_address());
        // start the speak server
        initspeak();
        // start the network listener server
        initserver();
        registerService();
    }

    @Override
    public void onPause() {
        // save the current status
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("pref_key_run_status", mRunStatus);
        editor.commit();
        // stop the listener server
        unregisterService();
        stopserver();
        super.onPause();
    }

    // ask the activity to switch to another fragment
    private void switchtryviewermode(int mode) {
        mOnTrywalkieInteractionListener.onTrywalkieModeChange(mode);
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

    private void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0, texte + "\n");
        mLogsConsole.setText(logbuffer);
    }

    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private Speak mSpeak = null;

    private void initspeak() {
        if (mSpeak == null) {
            mSpeak = new Speak(getContext());
        }
    }

    private void speak(String texte) {
        initspeak();
        mSpeak.speak(texte, speakLanguage);
        say("Speaked: " + texte);
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
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500);

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
                    say("Said: " + mSentence);
                    return;
                }
            }
            say("Understood: " + dutexte.get(0));
            mSendedtext.setText(dutexte.get(0));
            sendtext();
        }
    }

    private boolean matchvoice(String textein) {
        String texte = textein.toLowerCase();
        if (texte.contains("french") || texte.contains("français")) {
            listenLanguage = "FR";
            speakLanguage = "FR";
            speak("français");
            return (true);
        }
        if (texte.contains("english") || texte.contains("anglais")) {
            listenLanguage = "EN";
            speakLanguage = "EN";
            speak("english");
            return (true);
        }
        return (false);
    }

    // =====================================================================================
    // network operations

    public static final int SERVERPORT = 1701;  // NCC-1701

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
        private String msg;

        public updateUIThread(String str) {
            msg = str;
            Log.d("uithread", str);
        }

        @Override
        public void run() {
            if (msg != null) {
                mReceivedtext.setText(msg);
                say("Received: " + msg);
                speak(msg);
            }
        }
    }

    // ====================================================================================
    // client part

    private Socket clientSocket = null;

    Thread clientThread = null;

    // send a message to the other
    private void sendtext() {
        say("Send [" + mSendedtext.getText() + "] to destlist");
        String str = mSendedtext.getText().toString();
        // start the client thread
        clientThread = new Thread(new ClientThread(str));
        clientThread.start();
    }

    class ClientThread implements Runnable {

        private String mesg;

        public ClientThread(String str) {
            mesg = str;
        }

        @Override
        public void run() {
            for (int i = 0; i < mIpList.size(); ++i) {
                StringBuffer str = new StringBuffer(mIpList.get(i));
                String myip = mMyip.getText().toString();
                if(replaySent==false && myip.equals(str.substring(1))) {
                    Log.d("clientthread","do not replay locally");
                    continue;
                }
                clientsend(str.substring(1));
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

    private String mServiceName;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mService;
    private String SERVICE_TYPE = "_http._tcp.";
    private String SERVICE_NAME = "Trycorder";

    private List<String> mIpList = new ArrayList<String>();

    public void registerService() {
        mIpList.clear();

        mNsdManager = (NsdManager) getContext().getSystemService(Context.NSD_SERVICE);

        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(deviceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(SERVERPORT);

        say("Register service");
        initializeRegistrationListener();

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        say("Discover services");
        initializeResolveListener();

        initializeDiscoveryListener();

        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
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

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e("discovery", "Resolve failed: " + errorCode);
                saypost("Resolve failed: "+serviceInfo.getServiceName()+" Err:"+errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e("discovery", "Resolve Succeeded: " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d("discovery", "Same IP.");
                    saypost("Local machine "+mServiceName);
                    //return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
                Log.d("discovery", "Host: " + host.toString() + " Port: " + port);
                saypost("Resolved " + mService.getServiceName() +
                        " Host: " + host.toString() + " Port: " + port);
                addiplist(host.toString());
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
                saypost("Service discovered: "+service.getServiceName());
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("discovery", "Unknown Service Type: " + service.getServiceType());
                } else {
                    if (service.getServiceName().equals(mServiceName)) {
                        // The name of the service tells the user what they'd be
                        // connecting to. It could be "Bob's Chat App".
                        Log.d("discovery", "Same machine: " + mServiceName);
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
            Log.d("saythread",str);
        }

        @Override
        public void run() {
            if(msg!=null) {
                say(msg);
            }
        }
    }

    private void addiplist(String ip) {
        for(int i=0;i<mIpList.size();++i) {
            if(ip.equals(mIpList.get(i))) {
                return;
            }
        }
        mIpList.add(ip);
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
            StringBuffer str=new StringBuffer("");
            for(int i=0;i<mIpList.size();++i) {
                str.append(mIpList.get(i)+"\n");
            }
            mDestlist.setText(str.toString());
        }
    }



}
