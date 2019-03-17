package net.odinary.interaudio.story.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Keyword
{
    private String word;
    private String variable;
    private String value;
    private Map<String, String> interactiveVariables;

    public Keyword(JSONObject jsonKeyword, Map<String, String> interactiveVariables) throws JSONException
    {
        word = jsonKeyword.getString("keyword");
        variable = jsonKeyword.getString("variable");
        value = jsonKeyword.getString("value");
        this.interactiveVariables = interactiveVariables;
    }
}
