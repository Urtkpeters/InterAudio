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
        keyword = jsonKeyword.getString("keyword");
        variable = jsonKeyword.getString("variable");
        value = jsonKeyword.getString("value");
    }

    public String getKeyword() { return keyword; }

    public String getVariable() { return variable; }

    public String getValue() { return value; }
}
