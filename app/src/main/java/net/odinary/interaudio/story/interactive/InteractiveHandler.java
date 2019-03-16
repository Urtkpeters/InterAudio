package net.odinary.interaudio.story.interactive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.R;
import net.odinary.interaudio.story.AbstractStoryHandler;

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

public class InteractiveHandler extends AbstractStoryHandler
{
    private JSONObject currentPath;
    private Interactive currentInteractive;

    public InteractiveHandler(MainActivity mainActivity) { super(mainActivity); }

    public void start()
    {
        try
        {
            if(MainActivity.testPackageMode) currentStory = new Interactive(mainActivity);
            else currentStory = new Interactive(mainActivity.getFolioHandler().getPackageDir() + jsonFilename);

            currentInteractive = (Interactive) currentStory;

            clipList.add(currentInteractive.getStart());

            playClips();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void _parseVoice(ArrayList<String> result)
    {
        // The below is missing logic from the original createMediaListener from this class that is now on AbstractStoryHandler
//        String redirect = currentPath.getString("redirect");
//
//        if(redirect != null && !redirect.isEmpty()) createPlaybackAndListener(redirect);
//        else System.out.println("End of story!");


        // The below is very dependent on how the JSON ends up being formed
//        try
//        {
//            String nextSection = "";
//            String resultPhrase = result.get(0);
//
//            if(resultPhrase.contains("repeat"))
//            {
//                nextSection = currentPath.getString("section");
//            }
//            else
//            {
//                JSONArray keywordsArray = currentPath.getJSONArray("keywords");
//
//                for(int i = 0; i < keywordsArray.length(); i++)
//                {
//                    JSONObject keywordObj = keywordsArray.getJSONObject(i);
//
//                    if(resultPhrase.contains(keywordObj.getString("keyword")))
//                    {
//                        nextSection = keywordObj.getString("section");
//                        break;
//                    }
//                }
//            }
//







//        Boolean pathFound = false;
//
//        if(nextSection != null && !nextSection.isEmpty()) pathFound = loadNextSection(nextSection);
//
//        if(pathFound)
//        {
        // All of the below will need to be reworked once done with JSON all of this will be interacting with objects and not JSON


        // Set interactive variables
//                JSONArray setVariablesArray = currentPath.getJSONArray("setVariables");
//
//                for(int i = 0; i < setVariablesArray.length(); i++)
//                {
//                    JSONObject setVar = setVariablesArray.getJSONObject(i);
//
//                    storyVariables.get(setVar.getString("variable")).put("value", setVar.getString("value"));
//                }


        // Add clip to clip list
//                clipList.add(currentPath.getString("filename"));
//            }

            playClips();





//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    private Boolean loadNextSection(String nextSection)
    {
//        try
//        {
//            JSONArray pathsArray = storyJson.getJSONObject("sections").getJSONArray(nextSection);
//
//            for(int i = 0; i < pathsArray.length(); i++)
//            {
//                JSONObject path = pathsArray.getJSONObject(i);
//                JSONArray conditionsArray = path.getJSONArray("conditions");
//
//                if(conditionsArray.length() > 0)
//                {
//                    Boolean allConditionsMet = true;
//
//                    for(int j = 0; j < conditionsArray.length(); j++)
//                    {
//                        JSONObject condition = conditionsArray.getJSONObject(j);
//
//                        if(!storyVariables.get(condition.getString("variable")).getString("value").equals(condition.getString("value"))) allConditionsMet = false;
//                    }
//
//                    if(allConditionsMet)
//                    {
//                        currentPath = path;
//                        return true;
//                    }
//                }
//                else
//                {
//                    currentPath = path;
//                    return true;
//                }
//            }
//        }
//        catch(JSONException e)
//        {
//            e.printStackTrace();
//        }

        return false;
    }

    protected boolean _checkSystemCommands(String resultPhrase) { return false; }
}