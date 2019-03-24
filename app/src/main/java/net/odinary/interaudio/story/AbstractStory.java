package net.odinary.interaudio.story;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class AbstractStory
{
    protected String packageName;
    protected String packageType;
    protected String author;
    protected String audioFileExt;
    protected String start;
    protected String errorSound;

    public AbstractStory(MainActivity mainActivity)
    {
        try(InputStream inputStream = mainActivity.getResources().openRawResource(R.raw.audioadventure))
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }

            parseJSON(new JSONObject(writer.toString()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public AbstractStory(String packageDir) throws Exception
    {
        File storyFile = new File(packageDir);

        if(!storyFile.exists()) throw new Exception("Could not find json file");

        try(InputStream inputStream = new FileInputStream(storyFile))
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }

            parseJSON(new JSONObject(writer.toString()));
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();

            throw new Exception("Failure to read package json");
        }
    }

    public void parseJSON(JSONObject packageJson) throws Exception
    {
        try
        {
            packageName = packageJson.getString("packageName");
            packageType = packageJson.getString("packageType");
            author = packageJson.getString("author");
            audioFileExt = packageJson.getString("audioFileExt");
            start = packageJson.getString("start");
            errorSound = packageJson.getString("errorSound");

            parseTypeJSON(packageJson);
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            throw new Exception("Error getting data from package JSON");
        }
    }

    protected abstract void parseTypeJSON(JSONObject packageJson) throws Exception;

    public String getPackageName() { return packageName; }

    public String getPackageType() { return packageType; }

    public String getAuthor() { return author; }

    public String getAudioFileExt() { return audioFileExt; }

    public String getStart() { return start; }

    public String getErrorSound() { return errorSound; }
}
