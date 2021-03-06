package net.odinary.interaudio.story.adventure;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStoryHandler;
import net.odinary.interaudio.story.adventure.component.AdventureAction;
import net.odinary.interaudio.story.adventure.component.Section;
import net.odinary.interaudio.story.adventure.component.variable.AdventureVariable;
import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.story.adventure.component.Entity;
import net.odinary.interaudio.story.adventure.repository.PlayerRepository;
import net.odinary.interaudio.story.adventure.repository.WorldRepository;

import java.util.ArrayList;
import java.util.List;

public class AdventureHandler extends AbstractStoryHandler
{
    private static final String goCommand = "go";
    private static final String statusCommand = "status";
    private static final String inventoryCommand = "inventory";

    private Adventure currentAdventure;
    private ConditionHandler conditionHandler;
    private AdventureTriggerHandler triggerHandler;

    public AdventureHandler(MainActivity mainActivity) { super(mainActivity); }

    public void start()
    {
        try
        {
            currentAdventure = (Adventure) currentStory;

            WorldRepository worldRepository = currentAdventure.getWorldRepository();
            PlayerRepository playerRepository = currentAdventure.getPlayerRepository();

            conditionHandler = new ConditionHandler(worldRepository, playerRepository);
            triggerHandler = new AdventureTriggerHandler(worldRepository, playerRepository, currentAdventure.getEntityRepository(), this);

            if(MainActivity.testPackageMode) currentStory = new Adventure(mainActivity, conditionHandler, triggerHandler);
            else currentStory = new Adventure(mainActivity.getFolioHandler().getPackageDir() + jsonFilename, conditionHandler, triggerHandler);

            clipList.add(worldRepository.getCurrentSection().getFilename());

            playClips();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    protected boolean _parseVoice(String resultPhrase)
    {
        // Remove all spaces and special characters. Makes it easier for comparison.
        String cleanResultPhrase = resultPhrase.replaceAll("\\s+", "").replaceAll("[^\\w\\s]","").toLowerCase();

        Event event = new Event(Event.playerEvent);
        event.setSection(currentAdventure.getWorldRepository().getCurrentSection());

        Boolean validAction = true;

        AdventureAction adventureAction = currentAdventure.getPlayerRepository().getActionFromResult(cleanResultPhrase);

        if(adventureAction != null)
        {
            // This is where there needs to be a differenciation on System commands I think
            List<String> targetTypes = adventureAction.getTargetTypes();

            if(!targetTypes.isEmpty())
            {
                Entity target = currentAdventure.checkTarget(cleanResultPhrase);

                if(target != null)
                {
                    if(targetTypes.contains(target.getType()))
                    {
                        event.setTarget(target);

                        AdventureAction adventureActionOverride = target.getActionOverride(adventureAction.getName());

                        if(adventureActionOverride != null) adventureAction = adventureActionOverride;

                        event.setAdventureAction(adventureAction);

                        List<String> secondaryActions = adventureAction.getSecondaryKeys();

                        if(!secondaryActions.isEmpty())
                        {
                            event.setSecondaryAction(currentAdventure.checkSecondaryActions(cleanResultPhrase, secondaryActions));

                            if(event.getSecondaryAction() != null)
                            {
                                List<String> secondaryTargets = adventureAction.getSecondaryTargets();
                                Entity secondaryTarget = currentAdventure.checkTarget(cleanResultPhrase, event.getTarget().getName());

                                if(event.getSecondaryTarget() == null)
                                {
                                    clipList.add("noTarget");
                                    validAction = false;
                                }
                                else if(!secondaryTargets.contains(secondaryTarget.getType()))
                                {
                                    clipList.add(adventureAction.getFailFilename());
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
                        clipList.add(adventureAction.getFailFilename());
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

        if(validAction)
        {
            performActions(event);
        }
        else
        {
            uiClip = true;
            return false;
        }

        return true;
    }

    private void performActions(Event event)
    {
        PlayerRepository playerRepository = currentAdventure.getPlayerRepository();
        WorldRepository worldRepository = currentAdventure.getWorldRepository();
        AdventureVariable timeVariable = (AdventureVariable) worldRepository.getVariable("time");

        performAction(event);

        while(playerRepository.getTime() < 1)
        {
            timeVariable.setValue(timeVariable.getNValue() + 1);
            checkTimeActions(timeVariable);

            Section currentSection = event.getSection();

            for(Entity character: currentSection.getCharacters())
            {
                Event entityEvent = new Event(Event.characterEvent);
                entityEvent.setSection(currentSection);
                entityEvent.setActor(character);

                character.decideAction(entityEvent);

                performAction(entityEvent);
            }

            if(playerRepository.isAlive()) playerRepository.incrementTime();
            else
            {
                end = true;
                break;
            }
        }
    }

    public void performAction(Event event)
    {
        String conditionResponse = conditionHandler.checkConditions(event);

        if(conditionResponse.isEmpty())
        {
            PlayerRepository playerRepository = currentAdventure.getPlayerRepository();
            AdventureAction adventureAction = event.getAdventureAction();
            String actionFilename = adventureAction.getFilename();

            if(!actionFilename.isEmpty()) clipList.add(actionFilename);

            int eventType = event.getType();

            // Right now it is hard coded for the player time but once the player has been converted into an entity this needs to be on the "actor's" time not the players.
            // I am allowing time to go into the negative in the case that there are needs for moves that take multiple turns to complete
            if(eventType == Event.playerEvent) playerRepository.decrementTime(adventureAction.getTime());

            triggerHandler.runTriggers(event);
        }
        else
        {
            clipList.add(conditionResponse);
        }
    }

    protected boolean _checkSystemCommands(String resultPhrase)
    {
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

    private void checkTimeActions(AdventureVariable timeVariable)
    {
        List<AdventureAction> timeAdventureActions = new ArrayList<>(currentAdventure.getWorldRepository().getActions().values());

        for(AdventureAction timeAdventureAction : timeAdventureActions)
        {
            if(timeVariable.getNValue() % timeAdventureAction.getTime() == 0)
            {
                Event event = new Event(Event.timeEvent);
                event.setAdventureAction(timeAdventureAction);

                performAction(event);
            }
        }
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
//                mediaPath = mainActivity.getFolioHandler().getPackageDir() + currentPath.get("filename") + "." + storyJson.get("audioFileExt");
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

    protected void _endGame()
    {
        currentAdventure = null;
        conditionHandler = null;
        triggerHandler = null;
    }
}