package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStory;
import net.odinary.interaudio.story.component.Action;
import net.odinary.interaudio.story.interactive.odi.trigger.InteractiveTriggerHandler;
import net.odinary.interaudio.story.odi.trigger.Trigger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interactive extends AbstractStory
{
    private Map<String, String> interactiveVariables;
    private Map<String, Section> sections;
    private List<Action> actions;
    private Section currentSection;
    private InteractiveTriggerHandler triggerHandler;

    Interactive(MainActivity mainActivity, InteractiveTriggerHandler triggerHandler)
    {
        super(mainActivity);

        this.triggerHandler = triggerHandler;
    }

    Interactive(String packageDir, InteractiveTriggerHandler triggerHandler) throws Exception
    {
        super(packageDir);

        this.triggerHandler = triggerHandler;
    }

    protected void parseTypeJSON(JSONObject folioJson) throws Exception
    {
        // Parse Actions
        JSONArray actionArray = folioJson.getJSONArray("actions");

        for(int i = 0; i < actionArray.length(); i++)
        {
            JSONObject jsonAction = actionArray.getJSONObject(i);
            actions.add(new Action(jsonAction, triggerHandler));
        }

        // Parse Sections
        JSONArray sectionArray = folioJson.getJSONArray("sections");

        for(int i = 0; i < sectionArray.length(); i++)
        {
            JSONObject jsonSection = sectionArray.getJSONObject(i);

            // Parse Keywords
            JSONArray keywordArray = jsonSection.getJSONArray("keywords");
            List<Keyword> keywords = new ArrayList<>();

            for(int j = 0; j < keywordArray.length(); j++)
            {
                JSONObject jsonKeyword = keywordArray.getJSONObject(j);
                keywords.add(new Keyword(jsonKeyword));

                String variableName = jsonKeyword.getString("variable");

                if(!interactiveVariables.containsKey(variableName)) interactiveVariables.put(variableName, "");
            }

            // Parse Redirects
            JSONArray redirectArray = jsonSection.getJSONArray("redirects");
            List<Redirect> redirects = new ArrayList<>();

            for(int j = 0; j < redirectArray.length(); j++)
            {
                JSONObject jsonRedirect = redirectArray.getJSONObject(j);
                redirects.add(new Redirect(jsonRedirect));

                String variableName = jsonRedirect.getString("variable");

                if(!interactiveVariables.containsKey(variableName)) interactiveVariables.put(variableName, "");
            }

            String sectionName = jsonSection.getString("name");
            sections.put(sectionName, new Section(jsonSection, keywords, redirects));
        }

        currentSection = sections.get(start);
    }

    public void setCurrentSection(Section section) { currentSection = section; }

    public Map<String, Section> getSections() { return sections; }

    public Section getCurrentSection() { return currentSection; }

    public Map<String, String> getInteractiveVariables() { return interactiveVariables; }

    public List<Action> getActions() { return actions; }
}
