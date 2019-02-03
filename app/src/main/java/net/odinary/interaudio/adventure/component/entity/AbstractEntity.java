package net.odinary.interaudio.adventure.component.entity;

import net.odinary.interaudio.adventure.component.ComponentInterface;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractEntity implements ComponentInterface
{
    protected String name;
    protected String type;
    protected String filename;

    protected AbstractEntity(JSONObject entityJson, String type) throws JSONException
    {
        this.name = entityJson.getString("name");
        this.filename = entityJson.getString("filename");
        this.type = type;
    }

    protected AbstractEntity(AbstractEntity cloner)
    {
        this.name = cloner.name;
        this.filename = cloner.filename;
        this.type = cloner.type;
    }

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public String getType() { return type; }
}