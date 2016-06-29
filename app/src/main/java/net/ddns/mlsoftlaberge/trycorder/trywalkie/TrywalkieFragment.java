package net.ddns.mlsoftlaberge.trycorder.trywalkie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ddns.mlsoftlaberge.trycorder.R;
import net.ddns.mlsoftlaberge.trycorder.settings.SettingsActivity;

import java.util.ArrayList;
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

    private TextView mLogsConsole;

    // the preferences holder
    private SharedPreferences sharedPref;

    // the preferences values
    private boolean autoListen;
    private boolean isChatty;
    private String speakLanguage;
    private String listenLanguage;
    private String displayLanguage;


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
        mRunStatus = sharedPref.getBoolean("pref_key_run_status", false);
    }

    @Override
    public void onPause() {
        // save the current status
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("pref_key_run_status", mRunStatus);
        editor.commit();
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

    // tell something in the bottom status line
    private StringBuffer logbuffer = new StringBuffer(500);

    private void say(String texte) {
        mTextstatus_bottom.setText(texte);
        logbuffer.insert(0, texte + "\n");
        mLogsConsole.setText(logbuffer);
    }



    // settings activity incorporation in the display
    public void settingsactivity() {
        say("Settings");
        if(isChatty) speak("Settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    // ========================================================================================
    // functions to listen to the voice recognition callbacks

    private SpeechRecognizer mSpeechRecognizer=null;
    private Intent mSpeechRecognizerIntent=null;

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
            if(speakLanguage.equals("FR")) speak("Commande inconnue.");
            else speak("Unknown command.");
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    // ==============================================================================
    // match the understood content with a possible action
    private boolean matchvoice(String textein) {
        String texte = textein.toLowerCase();
        if (texte.contains("french") || texte.contains("français")) {
            listenLanguage="FR";
            speakLanguage="FR";
            speak("français");
            return (true);
        }
        if (texte.contains("english") || texte.contains("anglais")) {
            listenLanguage="EN";
            speakLanguage="EN";
            speak("english");
            return (true);
        }
        if (texte.contains("martin")) {
            speak("Martin is my Master.");
            return (true);
        }
        if (texte.contains("fuck") || texte.contains("shit") || texte.contains("merde")) {
            if(speakLanguage.equals("FR")) speak("Ce n'est pas tres poli");
            else speak("This is not very polite.");
            return (true);
        }
        if (texte.contains("computer")) {
            if(speakLanguage.equals("FR")) speak("Faites votre requete");
            else speak("State your question");
            return (true);
        }
        if (texte.contains("send")) {
            speak("contents sended");
            return (true);
        }
        return (false);
    }

    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private TextToSpeech tts=null;

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
        say("Speaked: "+texte);
    }



}
