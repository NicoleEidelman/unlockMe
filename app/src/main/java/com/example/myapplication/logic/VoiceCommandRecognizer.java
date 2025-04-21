package com.example.myapplication.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceCommandRecognizer {

    private final Context context;
    private final OnCommandRecognizedListener callback;
    private SpeechRecognizer speechRecognizer;

    public interface OnCommandRecognizedListener {
        void onCommandRecognized(String command);
    }

    public VoiceCommandRecognizer(Context context, OnCommandRecognizedListener callback) {
        this.context = context;
        this.callback = callback;
    }

    public void startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String result = matches.get(0);
                    if ("open".equalsIgnoreCase(result.trim())) {
                        callback.onCommandRecognized("OPEN");
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        speechRecognizer.startListening(intent);
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}