package net.odinary.interaudio.story.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Section
{
    private String name;
    private String filename;
    private List<Keyword> keywords;
    private List<Redirect> redirects;

    public Section(JSONObject jsonSection, List<Keyword> keywords, List<Redirect> redirects) throws JSONException
    {
        name = jsonSection.getString("name");
        filename = jsonSection.getString("filename");
        this.keywords = keywords;
        this.redirects = redirects;
    }

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public List<Keyword> getKeywords() { return keywords; }

    public List<Redirect> getRedirects() { return redirects; }
}
