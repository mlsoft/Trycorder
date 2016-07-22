package net.ddns.mlsoftlaberge.trycorder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mlsoft on 08/07/16.
 * Not used, unstable.
 */
public class Listen implements RecognitionListener {
    // ========================================================================================
    // functions to listen to the voice recognition callbacks

    private static SpeechRecognizer mSpeechRecognizer = null;
    private static Intent mSpeechRecognizerIntent = null;
    private Context mContext;
    private Fragment mFragment;

    // ======================================================================================
    public interface OnListenListener {
        public void onListenText(String text);
    }

    private OnListenListener mOnListenListener;

    // constructor
    public Listen(Context context, Fragment fragment) {
        mContext=context;
        mFragment=fragment;
        try {
            // Assign callback listener which the holding activity must implement.
            mOnListenListener = (OnListenListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                    + " must implement OnTrywalkieInteractionListener");
        }
        initlisten();
        setlanguage("EN");
    }

    // ========================================================================================
    // functions to control the speech process

    private void initlisten() {
        if (mSpeechRecognizer == null) {
            // ============== initialize the audio listener and talker ==============
            //AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "net.ddns.mlsoftlaberge.trycorder");
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
            //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200);
            // produce a FC on android 4.0.3
            //mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
        }
    }

    public void setlanguage(String lang) {
        if (lang.equals("FR")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        } else if (lang.equals("EN")) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        } else {
            // automatic
        }
    }

    public void listen() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    // =================================================================================
    // listener for the speech recognition service


    @Override
    public void onBeginningOfSpeech() {
        Log.d("listen", "beginning of speech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("listen", "end of speech");
    }

    @Override
    public void onError(int error) {
        Log.d("listen", "error: " + error);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("listen", "partial results");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("listen", "ready for speech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> dutexte = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (dutexte != null) Log.d("listen", "nb results: " + dutexte.size());
        if (dutexte != null && dutexte.size() > 0) {
            mOnListenListener.onListenText(dutexte.get(0));
        }
    }


}
