package net.odinary.interaudio.folio.interactive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InteractiveHandler
{
    private static final String storyFileName = "story.json";

    private MainActivity mainActivity;
    private JSONObject storyJson;
    private JSONObject currentPath;
    private Map<String, JSONObject> storyVariables;

    public InteractiveHandler(MainActivity activity)
    {
        mainActivity = activity;
    }

    public void startPlay()
    {
        if(!readStory()) return;

        try
        {
            currentPath = storyJson.getJSONObject("sections").getJSONArray("beginning").getJSONObject(0);
            JSONArray variableArray = storyJson.getJSONArray("variables");
            storyVariables = new HashMap<>();

            for(int i = 0; i < variableArray.length(); i++)
            {
                JSONObject variable = variableArray.getJSONObject(i);

                variable.put("value",variable.getString("default"));

                storyVariables.put(variable.getString("name"), variable);
            }

            // Get the question audio file
            MediaPlayer mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(mainActivity.getFolioHandler().getPackageDir() + currentPath.get("filename") + "." + storyJson.get("audioFileExt")));
            createVoiceListener(mediaPlayer);


            mediaPlayer.start();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void createVoiceListener(MediaPlayer mediaPlayer)
    {
        mediaPlayer.setOnCompletionListener((MediaPlayer mediaPlayerListen) ->
        {
            try
            {
                if(currentPath.getJSONArray("keywords").length() > 0)
                {
                    // When audio is done playing, trigger VTT
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                    // This action will ultimately trigger onActivityResult
                    mainActivity.startActivityForResult(intent, MainActivity.SPEECH_INPUT);
                }
                else
                {
                    String redirect = currentPath.getString("redirect");

                    if(redirect != null && !redirect.isEmpty()) createPlaybackAndListener(redirect);
                    else System.out.println("End of story!");
                }
            }
            catch(JSONException|ActivityNotFoundException e)
            {
                e.printStackTrace();
            }
        });
    }

    public void parseVoice(Intent data)
    {
        // Get the resulting VTT responses from Google
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        try
        {
            String nextSection = "";
            String resultPhrase = result.get(0);

            if(resultPhrase.contains("repeat"))
            {
                nextSection = currentPath.getString("section");
            }
            else
            {
                JSONArray keywordsArray = currentPath.getJSONArray("keywords");

                for(int i = 0; i < keywordsArray.length(); i++)
                {
                    JSONObject keywordObj = keywordsArray.getJSONObject(i);

                    if(resultPhrase.contains(keywordObj.getString("keyword")))
                    {
                        nextSection = keywordObj.getString("section");
                        break;
                    }
                }
            }

            createPlaybackAndListener(nextSection);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private Boolean loadNextSection(String nextSection)
    {
        try
        {
            JSONArray pathsArray = storyJson.getJSONObject("sections").getJSONArray(nextSection);

            for(int i = 0; i < pathsArray.length(); i++)
            {
                JSONObject path = pathsArray.getJSONObject(i);
                JSONArray conditionsArray = path.getJSONArray("conditions");

                if(conditionsArray.length() > 0)
                {
                    Boolean allConditionsMet = true;

                    for(int j = 0; j < conditionsArray.length(); j++)
                    {
                        JSONObject condition = conditionsArray.getJSONObject(j);

                        if(!storyVariables.get(condition.getString("variable")).getString("value").equals(condition.getString("value"))) allConditionsMet = false;
                    }

                    if(allConditionsMet)
                    {
                        currentPath = path;
                        return true;
                    }
                }
                else
                {
                    currentPath = path;
                    return true;
                }
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private void createPlaybackAndListener(String nextSection)
    {
        MediaPlayer mediaPlayer = null;
        Boolean pathFound = false;
        String mediaPath = "";

        if(nextSection != null && !nextSection.isEmpty()) pathFound = loadNextSection(nextSection);

        if(pathFound)
        {
            try
            {
                JSONArray setVariablesArray = currentPath.getJSONArray("setVariables");

                for(int i = 0; i < setVariablesArray.length(); i++)
                {
                    JSONObject setVar = setVariablesArray.getJSONObject(i);

                    storyVariables.get(setVar.getString("variable")).put("value", setVar.getString("value"));
                }

                mediaPath = mainActivity.getFolioHandler().getPackageDir() + currentPath.get("filename") + "." + storyJson.get("audioFileExt");
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

            mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(mediaPath));
        }

        if(!pathFound || mediaPlayer == null)
        {
            mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), R.raw.error);
        }

        createVoiceListener(mediaPlayer);

        mediaPlayer.start();
    }

    private Boolean readStory()
    {
        File storyFile = new File(mainActivity.getFolioHandler().getPackageDir() + storyFileName);

        // Do something with this
        if(!storyFile.exists()) return false;

        try(InputStream inputStream = new FileInputStream(storyFile))
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }

            storyJson = new JSONObject(writer.toString());
        }
        catch(IOException |JSONException exception)
        {
            exception.printStackTrace();

            return false;
        }

        return true;
    }
}