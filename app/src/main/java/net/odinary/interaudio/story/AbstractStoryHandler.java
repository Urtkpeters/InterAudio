package net.odinary.interaudio.story;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;

import net.odinary.interaudio.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractStoryHandler
{
    public static final String saveCommand = "save";
    public static final String loadCommand = "load";
    public static final String quitCommand = "quit";
    public static final String repeatCommand = "repeat";
    public static final String jsonFilename = "story.json";

    protected MainActivity mainActivity;
    protected AbstractStory currentStory;
    protected List<String> clipList = new ArrayList<>();
    protected List<String> lastClipList = new ArrayList<>();
    protected Boolean uiClip = false;



    protected AbstractStoryHandler(MainActivity mainActivity) { this.mainActivity = mainActivity; }

    public abstract void start();

    protected void playClips()
    {
        if(!uiClip)
        {
            lastClipList.clear();
            lastClipList.add(clipList.get(0));
        }

        MediaPlayer mediaPlayer;

        if(MainActivity.testPackageMode) mediaPlayer = getResClip();
        else mediaPlayer = getPathClip();

        clipList.remove(0);
        createMediaListener(mediaPlayer);

        mediaPlayer.start();
    }

    protected MediaPlayer getPathClip()
    {
        return MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(mainActivity.getFolioHandler().getPackageDir() + clipList.get(0) + "." + currentStory.getAudioFileExt()));
    }

    protected MediaPlayer getResClip()
    {
        return MediaPlayer.create(mainActivity.getApplicationContext(), mainActivity.getResources().getIdentifier(clipList.get(0), "raw", mainActivity.getPackageName()));
    }

    protected void createMediaListener(MediaPlayer mediaPlayer)
    {
        mediaPlayer.setOnCompletionListener((MediaPlayer mediaPlayerListen) ->
        {
            if(clipList.size() > 0)
            {
                playClips();
            }
            else
            {
                try
                {
                    // When audio is done playing, trigger VTT
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                    // This action will ultimately trigger MainActivity.onActivityResult
                    mainActivity.startActivityForResult(intent, MainActivity.SPEECH_INPUT);
                }
                catch(ActivityNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void parseVoice(Intent data)
    {
        // Get the resulting VTT responses from Google
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        _parseVoice(result);
    }

    protected abstract void _parseVoice(ArrayList<String> result);

    protected boolean checkSystemCommands(String resultPhrase)
    {
        if(resultPhrase.contains(saveCommand))
        {
            // Do save action
        }

        if(resultPhrase.contains(loadCommand))
        {
            // Do load action
        }

        if(resultPhrase.contains(quitCommand))
        {
            // Do quit action
        }

        if(resultPhrase.contains(repeatCommand))
        {
            clipList = lastClipList;
        }

        return _checkSystemCommands(resultPhrase);
    }

    protected abstract boolean _checkSystemCommands(String resultPhrase);

    protected void gameover()
    {

    }
}
