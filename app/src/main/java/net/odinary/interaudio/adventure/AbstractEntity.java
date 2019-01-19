package net.odinary.interaudio.adventure;

import org.json.JSONException;
import org.json.JSONObject;

abstract class AbstractEntity
{
    String name;
    String filename;

    AbstractEntity(JSONObject entityJson) throws JSONException
    {
        this.name = entityJson.getString("name");
        this.filename = entityJson.getString("filename");
    }

    AbstractEntity(AbstractEntity cloner)
    {
        this.name = cloner.name;
        this.filename = cloner.filename;
    }

    public String getName() { return name; }

    public String getFilename() { return filename; }
}