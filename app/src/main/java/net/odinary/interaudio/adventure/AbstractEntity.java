package net.odinary.interaudio.adventure;

import org.json.JSONException;
import org.json.JSONObject;

abstract class AbstractEntity
{
    String name;
    String type;
    String filename;

    AbstractEntity(JSONObject entityJson, String type) throws JSONException
    {
        this.name = entityJson.getString("name");
        this.filename = entityJson.getString("filename");
        this.type = type;
    }

    AbstractEntity(AbstractEntity cloner)
    {
        this.name = cloner.name;
        this.filename = cloner.filename;
    }

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public String getType() { return type; }
}