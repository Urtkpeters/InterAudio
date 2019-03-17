package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interactive extends AbstractStory
{
    private Map<String, String> interactiveVariables;
    private Map<String, Section> sections;
    private Section currentSection;

    Interactive(MainActivity mainActivity)
    {
        super(mainActivity);
    }

    Interactive(String packageDir) throws Exception
    {
        super(packageDir);
    }

    protected void parseTypeJSON(JSONObject folioJson) throws Exception
    {
        JSONArray sectionArray = folioJson.getJSONArray("sections");

        for(int i = 0; i < sectionArray.length(); i++)
        {
            JSONObject jsonSection = sectionArray.getJSONObject(i);

            JSONArray keywordArray = jsonSection.getJSONArray("keywords");
            List<Keyword> keywords = new ArrayList<>();

            for(int j = 0; j < keywordArray.length(); j++)
            {
                JSONObject jsonKeyword = keywordArray.getJSONObject(j);
                keywords.add(new Keyword(jsonKeyword));

                String variableName = jsonKeyword.getString("variable");

                if(!interactiveVariables.containsKey(variableName)) interactiveVariables.put(variableName, "");
            }

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
}
