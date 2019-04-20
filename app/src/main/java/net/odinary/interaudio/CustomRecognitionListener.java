package net.odinary.interaudio;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class CustomRecognitionListener implements RecognitionListener
{
    public void onReadyForSpeech(Bundle params) {}

    public void onBeginningOfSpeech() {}

    public void onRmsChanged(float rmsdB) {}

    public void onBufferReceived(byte[] buffer) {}

    public void onEndOfSpeech() {}

    public void onError(int error)
    {
        System.out.println("onError: " + error);
    }

    public void onResults(Bundle results)
    {
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        System.out.println("results: " + String.valueOf(data.size()));
    }

    public void onPartialResults(Bundle partialResults) {}

    public void onEvent(int eventType, Bundle params) {}
}