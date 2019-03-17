package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interactive extends AbstractStory
{
    Map<String, Section> sections;
    Map<String, String> interactiveVariables;

    public Interactive(MainActivity mainActivity)
    {
        super(mainActivity);
    }

    public Interactive(String packageDir) throws Exception
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
                keywords.add(new Keyword(jsonKeyword, interactiveVariables));

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
    }
}
