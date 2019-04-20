package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStoryHandler;
import net.odinary.interaudio.story.adventure.Event;
import net.odinary.interaudio.story.component.Action;
import net.odinary.interaudio.story.interactive.odi.trigger.InteractiveTriggerHandler;

import java.util.List;
import java.util.Map;

public class InteractiveHandler extends AbstractStoryHandler
{
    private Interactive currentInteractive;
    private InteractiveTriggerHandler triggerHandler;

    public InteractiveHandler(MainActivity mainActivity) { super(mainActivity); }

    public void start()
    {
        try
        {
            triggerHandler = new InteractiveTriggerHandler(this);

            if(MainActivity.testPackageMode) currentStory = new Interactive(mainActivity, triggerHandler);
            else currentStory = new Interactive(mainActivity.getFolioHandler().getPackageDir() + jsonFilename, triggerHandler);

            end = false;
            currentInteractive = (Interactive) currentStory;

            clipList.add(currentInteractive.getCurrentSection().getFilename());

            if(!calculatePath()) playClips();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean calculatePath()
    {
        Map<String, String> interactiveVariables = currentInteractive.getInteractiveVariables();
        Section currentSection = currentInteractive.getCurrentSection();
        List<Keyword> keywords = currentSection.getKeywords();

        if(keywords.size() > 0 && interactiveVariables.get(keywords.get(0).getVariable()).equals("")) return true;

        Map<String, Section> sections = currentInteractive.getSections();
        List<Redirect> redirects = currentSection.getRedirects();
        String nextSection = "";

        if(redirects.size() > 1)
        {
            for(Redirect redirect: currentSection.getRedirects())
            {
                if(redirect.getValue().equals(interactiveVariables.get(redirect.getVariable())))
                {
                    nextSection = redirect.getSection();
                    break;
                }
            }
        }
        else if(redirects.size() > 0)
        {
            nextSection = redirects.get(0).getSection();
        }
        else
        {
            // Need to relay some error here.
            return false;
        }

        if(nextSection.equals(""))
        {
            // Need to relay some error here.
            return false;
        }
        else if(nextSection.equals("end"))
        {
            end = true;
            return true;
        }
        else
        {
            currentSection = sections.get(nextSection);
            clipList.add(currentSection.getFilename());
            currentInteractive.setCurrentSection(currentSection);
            calculatePath();

            return true;
        }
    }

    protected boolean _parseVoice(String resultPhrase)
    {
        // Remove all spaces and special characters. Makes it easier for comparison.
        String cleanResultPhrase = resultPhrase.replaceAll("\\s+", "").replaceAll("[^\\w\\s]","").toLowerCase();

        Action systemAction = null;

        for(Action action: currentInteractive.getActions())
        {
            for(String keyword: action.getKeywords())
            {
                if(resultPhrase.contains(keyword))
                {
                    systemAction = action;
                    break;
                }
            }

            if(systemAction != null) break;
        }

        if(systemAction != null)
        {
            triggerHandler.runTriggers(systemAction);
            return true;
        }
        else
        {
            Section currentSection = currentInteractive.getCurrentSection();
            Map<String, String> interactiveVariables = currentInteractive.getInteractiveVariables();

            for(Keyword keyword: currentSection.getKeywords())
            {
                if(cleanResultPhrase.contains(keyword.getKeyword()))
                {
                    interactiveVariables.put(keyword.getVariable(), keyword.getValue());
                    return calculatePath();
                }
            }
        }

        return false;
    }

    public void executeSystemCommand(String commandName)
    {
        uiClip = true;

        switch(commandName)
        {
            case saveCommand:
                // The below is for adding a success sound
                //clipList.add();
                save();
                break;
            case loadCommand:
                // The below is for adding a success sound
                //clipList.add();
                load();
                break;
            case quitCommand:
                // The below is for adding a success sound
                //clipList.add();
                quit();
                break;
            case repeatCommand:
                // The below is for adding a success sound
                //clipList.add();
                clipList.addAll(lastClipList);
                break;
        }
    }

    public void save()
    {

    }

    public void load()
    {

    }

    public void quit()
    {

    }

    protected boolean _checkSystemCommands(String resultPhrase) { return false; }

    protected void _endGame()
    {
        currentInteractive = null;
    }
}