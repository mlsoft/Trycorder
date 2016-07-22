package net.ddns.mlsoftlaberge.trycorder.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by mlsoft on 08/07/16.
 */
public class Speak {
    // =========================================================================
    // usage of text-to-speech to speak a sensence
    private static TextToSpeech tts=null;
    private String lang="EN";

    // init the text to speech engine
    public Speak(Context context) {
        if(tts==null) {
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                    }
                }
            });
        }
    }

    public void setLang(String lng) {
        lang=lng;
        if (lang.equals("FR")) {
            tts.setLanguage(Locale.FRENCH);
        } else if (lang.equals("EN")) {
            tts.setLanguage(Locale.US);
        } else {
            // default prechoosen language
        }
    }

    public void speak(String texte) {
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
    }

    public void speak(String texte,String lng) {
        setLang(lng);
        tts.speak(texte, TextToSpeech.QUEUE_ADD, null);
    }

}
