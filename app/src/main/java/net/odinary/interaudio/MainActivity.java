package net.odinary.interaudio;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.odinary.interaudio.folio.adventure.AdventureHandler;
import net.odinary.interaudio.folio.FolioHandler;
import net.odinary.interaudio.folio.interactive.InteractiveHandler;

public class MainActivity extends AppCompatActivity
{
    public static final boolean debugMode = true;
    public static final boolean simpleTestMode = false;
    public static final boolean testPackageMode = true;
    public static final int SPEECH_INPUT = 100;

    private UIHandler uiHandler;
    private FolioHandler folioHandler;
    private InteractiveHandler interactiveHandler;
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
        folioHandler = new FolioHandler(this);
        interactiveHandler = new InteractiveHandler(this);
        adventureHandler = new AdventureHandler(this);

        uiHandler.initializeUI();
    }

    public String getCurrentPackage() { return currentPackage; }

    public void setCurrentPackage(String newPackage) { currentPackage = newPackage; }

    public UIHandler getUIHandler() { return uiHandler; }

    public FolioHandler getFolioHandler() { return folioHandler; }

    public InteractiveHandler getInteractiveHandler() { return interactiveHandler; }

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
