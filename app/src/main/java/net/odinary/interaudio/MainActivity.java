package net.odinary.interaudio;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.odinary.interaudio.adventure.AdventureHandler;

public class MainActivity extends AppCompatActivity
{
    public static final boolean debugMode = true;
    public static final boolean simpleTestMode = false;
    public static final boolean testPackageMode = true;
    public static final int SPEECH_INPUT = 100;

    private UIHandler uiHandler;
    private PackageHandler packageHandler;
    private StoryHandler storyHandler;
    private AdventureHandler adventureHandler;

    private String currentPackage = "package";
    private String packageType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        uiHandler = new UIHandler(this);
        packageHandler = new PackageHandler(this);
        storyHandler = new StoryHandler(this);
        adventureHandler = new AdventureHandler(this);

        uiHandler.initializeUI();
    }

    public String getCurrentPackage() { return currentPackage; }

    public void setCurrentPackage(String newPackage) { currentPackage = newPackage; }

    public UIHandler getUIHandler() { return uiHandler; }

    public PackageHandler getPackageHandler() { return packageHandler; }

    public StoryHandler getStoryHandler() { return storyHandler; }

    public AdventureHandler getAdventureHandler() { return adventureHandler; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SPEECH_INPUT && resultCode == RESULT_OK && data != null)
        {
            adventureHandler.parseVoice(data);
        }
    }
}
