package net.odinary.interaudio.adventure;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.odi.trigger.TriggerHandler;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdventureHandler
{
    private static final String saveCommand = "save";
    private static final String loadCommand = "load";
    private static final String quitCommand = "quit";
    private static final String repeatCommand = "repeat";
    private static final String goCommand = "go";
    private static final String statusCommand = "status";
    private static final String inventoryCommand = "inventory";
    private static final String jsonFilename = "audioAdventure.json";

    private Adventure currentAdventure;
    private MainActivity mainActivity;
    private ConditionHandler conditionHandler;
    private TriggerHandler triggerHandler;

    private Boolean uiClip = false;
    private List<String> clipList = new ArrayList<>();
    private List<String> lastClipList = new ArrayList<>();

    public AdventureHandler(MainActivity activity)
    {
        mainActivity = activity;
    }

    public void startPlay()
    {
        try
        {
            WorldRepository worldRepository = currentAdventure.getWorldRepository();
            PlayerRepository playerRepository = currentAdventure.getPlayerRepository();

            conditionHandler = new ConditionHandler(worldRepository, playerRepository);
            triggerHandler = new TriggerHandler(worldRepository, playerRepository, currentAdventure.getEntityRepository());

            if(MainActivity.testPackageMode) currentAdventure = new Adventure(mainActivity, conditionHandler, triggerHandler);
            else currentAdventure = new Adventure(mainActivity.getPackageHandler().getPackageDir() + jsonFilename, conditionHandler, triggerHandler);

            clipList.add(worldRepository.getCurrentSection().getFilename());

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

    private MediaPlayer getPathClip()
    {
        return MediaPlayer.create(mainActivity.getApplicationContext(), Uri.parse(mainActivity.getPackageHandler().getPackageDir() + clipList.get(0) + "." + currentAdventure.getAudioFileExt()));
    }

    private MediaPlayer getResClip()
    {
        return MediaPlayer.create(mainActivity.getApplicationContext(), mainActivity.getResources().getIdentifier(clipList.get(0), "raw", mainActivity.getPackageName()));
    }

    private void createMediaListener(MediaPlayer mediaPlayer)
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

                    // This action will ultimately trigger onActivityResult
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
        Event event = new Event(currentAdventure.getWorldRepository().getCurrentSection());

        String resultPhrase = result.get(0);

        boolean systemCommand = checkSystemCommands(resultPhrase);

        if(!systemCommand)
        {
            Boolean validAction = true;

            Action action = currentAdventure.getPlayerRepository().getActionFromResult(resultPhrase);

            if(action != null)
            {
                List<String> targetTypes = action.getTargetTypes();

                if(!targetTypes.isEmpty())
                {
                    Entity target = currentAdventure.checkTarget(resultPhrase);

                    if(target != null)
                    {
                        if(targetTypes.contains(target.getType()))
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
                                    Entity secondaryTarget = currentAdventure.checkTarget(resultPhrase, event.getTarget().getName());

                                    if(event.getSecondaryTarget() == null)
                                    {
                                        clipList.add("noTarget");
                                        validAction = false;
                                    }
                                    else if(!secondaryTargets.contains(secondaryTarget.getType()))
                                    {
                                        clipList.add(action.getFailFilename());
                                        validAction = false;
                                    }
                                    else
                                    {
                                        event.setSecondaryTarget(secondaryTarget);
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
                            clipList.add(action.getFailFilename());
                            validAction = false;
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

    public void performAction(Event event)
    {
        PlayerRepository playerRepository = currentAdventure.getPlayerRepository();

        String conditionResponse = conditionHandler.checkConditions(event);

        if(conditionResponse.isEmpty())
        {
            Action action = event.getAction();

            clipList.add(action.getFilename());

            // I am allowing time to go into the negative in the case that there are needs for moves that take multiple turns to complete
            playerRepository.decrementTime(action.getTime());

            triggerHandler.runTriggers(this, event);
        }
        else
        {
            clipList.add(conditionResponse);
        }
    }

    private boolean checkSystemCommands(String resultPhrase)
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

        if(resultPhrase.contains(goCommand))
        {
            return go(resultPhrase);
        }

        if(resultPhrase.contains(statusCommand))
        {
            // Do status action
        }

        if(resultPhrase.contains(inventoryCommand))
        {
            // Do inventory action
        }

        return false;
    }

    private boolean go(String resultPhrase)
    {
        String targetSection = currentAdventure.checkDirection(resultPhrase);

        if(targetSection != null)
        {
            clipList.add(currentAdventure.getWorldRepository().setCurrentSection(targetSection));
            return true;
        }

        return false;
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