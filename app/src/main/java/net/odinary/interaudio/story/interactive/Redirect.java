package net.odinary.interaudio.story.interactive;

import org.json.JSONException;
import org.json.JSONObject;

public class Redirect
{
    private String variable;
    private String value;
    private String section;

    Redirect(JSONObject jsonRedirect) throws JSONException
    {
        variable = jsonRedirect.getString("variable");
        value = jsonRedirect.getString("value");
        section = jsonRedirect.getString("section");
    }

    public String getVariable() { return variable; }

    public String getValue() { return value; }

    public String getSection() { return section; }
}
