package net.odinary.interaudio.story.interactive;

import org.json.JSONException;
import org.json.JSONObject;

public class Keyword
{
    private String keyword;
    private String variable;
    private String value;

    Keyword(JSONObject jsonKeyword) throws JSONException
    {
        // Remove all spaces and special characters. Makes it easier for comparison.
        keyword = jsonKeyword.getString("keyword").replaceAll("\\s+", "").replaceAll("[^\\w\\s]","").toLowerCase();
        variable = jsonKeyword.getString("variable");
        value = jsonKeyword.getString("value");
    }

    public String getKeyword() { return keyword; }

    public String getVariable() { return variable; }

    public String getValue() { return value; }
}
