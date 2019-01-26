package net.odinary.interaudio.adventure;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdventureHandler
{
    private static final String jsonFilename = "audioAdventure.json";

    private Adventure currentAdventure;
    private MainActivity mainActivity;

    private Boolean uiClip = false;
    private List<String> clipList = new ArrayList<>();
    private List<String> lastClipList = new ArrayList<>();

    AdventureHandler(MainActivity activity)
    {
        mainActivity = activity;
    }

    public void startPlay()
    {
        try
        {
            currentAdventure = new Adventure(mainActivity.getPackageHandler().getPackageDir() + jsonFilename);

            clipList.add(World.getCurrentSection().getFilename());

            playClips();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void playClips()
    {
        String clipFile = mainActivity.getPackageHandler().getPackageDir() + clipList.get(0) + "." + currentAdventure.getAudioFileExt();

        if(!uiClip)
        {
            lastClipList.clear();
            lastClipList.add(clipList.get(0));
        }

        clipList.remove(0);

        MediaPlayer mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(clipFile));
        createMediaListener(mediaPlayer);

        mediaPlayer.start();
    }

    private void createMediaListener(MediaPlayer mediaPlayer)
    {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
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

                        // This action will ultimately trigger onActivityResult
                        mainActivity.startActivityForResult(intent, MainActivity.SPEECH_INPUT);
                    }
                    catch(ActivityNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void parseVoice(Intent data)
    {
        // Get the resulting VTT responses from Google
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        Event event = new Event(World.getCurrentSection());

        try
        {
            String resultPhrase = result.get(0);

            if(resultPhrase.contains("repeat"))
            {
                clipList = lastClipList;
            }
            else
            {
                Boolean validAction = true;

                Action action = Player.getActionFromResult(resultPhrase);

                if(action != null)
                {
                    List<String> targetTypes = action.getTargetTypes();

                    if(!targetTypes.isEmpty())
                    {
                        Entity target = currentAdventure.checkTarget(resultPhrase, targetTypes);

                        if(target != null)
                        {
                            event.setTarget(target);

                            Action actionOverride = target.getActionOverride(action.getName());

                            if(actionOverride != null) action = actionOverride;

                            event.setAction(action);

                            List<String> secondaryActions = action.getSecondaryKeys();

                            if(!secondaryActions.isEmpty())
                            {
                                event.setSecondaryAction(currentAdventure.checkSecondaryActions(resultPhrase, secondaryActions));

                                if(event.getSecondaryAction() != null)
                                {
                                    List<String> secondaryTargets = action.getSecondaryTargets();
                                    event.setSecondaryTarget(currentAdventure.checkTarget(resultPhrase, secondaryTargets, event.getTarget().getName()));

                                    if(event.getSecondaryTarget() == null)
                                    {
                                        clipList.add("noTarget");
                                        validAction = false;
                                    }
                                }
                                else
                                {
                                    clipList.add("noAction");
                                    validAction = false;
                                }
                            }
                        }
                        else
                        {
                            clipList.add("noTarget");
                            validAction = false;
                        }
                    }
                }
                else
                {
                    clipList.add("noAction");
                    validAction = false;
                }

                if(validAction) performAction(event);
                else uiClip = true;
            }

            playClips();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void performAction(Event event)
    {
        // Adventure vars will no longer be stored here
        String conditionResponse = Conditions.checkConditions(event);

        clipList.add(conditionResponse);

        if(!conditionResponse.isEmpty())
        {
            // Add response filename to array of filenames to be played

            // Deduct time

            // Set vars

            // Check triggers
        }
        else
        {

        }
    }

    private void createPlaybackAndListener(String nextSection)
    {
//        MediaPlayer mediaPlayer = null;
//        Boolean pathFound = false;
//        String mediaPath = "";
//
//        if(nextSection != null && !nextSection.isEmpty()) pathFound = loadNextSection(nextSection);
//
//        if(pathFound)
//        {
//            try
//            {
//                JSONArray setVariablesArray = currentPath.getJSONArray("setVariables");
//
//                for(int i = 0; i < setVariablesArray.length(); i++)
//                {
//                    JSONObject setVar = setVariablesArray.getJSONObject(i);
//
//                    storyVariables.get(setVar.getString("variable")).put("value", setVar.getString("value"));
//                }
//
//                mediaPath = mainActivity.getPackageHandler().getPackageDir() + currentPath.get("filename") + "." + storyJson.get("audioFileExt");
//            }
//            catch(JSONException e)
//            {
//                e.printStackTrace();
//            }
//
//            mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(mediaPath));
//        }
//
//        if(!pathFound || mediaPlayer == null)
//        {
//            mediaPlayer = MediaPlayer.create(mainActivity.getApplicationContext(), R.raw.error);
//        }
//
//        createVoiceListener(mediaPlayer);
//
//        mediaPlayer.start();
    }
}