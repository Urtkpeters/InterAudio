package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStoryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InteractiveHandler extends AbstractStoryHandler
{
    private Interactive currentInteractive;

    public InteractiveHandler(MainActivity mainActivity) { super(mainActivity); }

    public void start()
    {
        try
        {
            if(MainActivity.testPackageMode) currentStory = new Interactive(mainActivity);
            else currentStory = new Interactive(mainActivity.getFolioHandler().getPackageDir() + jsonFilename);

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
        if(resultPhrase.contains("repeat"))
        {
            // Repeat functionality logic
        }
        else
        {
            Section currentSection = currentInteractive.getCurrentSection();
            Map<String, String> interactiveVariables = currentInteractive.getInteractiveVariables();

            for(Keyword keyword: currentSection.getKeywords())
            {
                if(resultPhrase.contains(keyword.getKeyword()))
                {
                    interactiveVariables.put(keyword.getVariable(), keyword.getValue());
                    return calculatePath();
                }
            }
        }

        return false;
    }

    protected boolean _checkSystemCommands(String resultPhrase) { return false; }
}