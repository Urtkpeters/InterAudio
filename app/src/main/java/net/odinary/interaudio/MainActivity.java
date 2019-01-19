package net.odinary.interaudio;

/*
        To do:
            Come up with a name for the project
            Put into a git available location
            Create git project
            Put all of the below into git


            Go to the Adventure -> checkTargets and FIX THE FACT THAT IT DOESN'T CHECK INVENTORY FOR TARGETS
            Create a player class to store player specific variables
            Add inventory to the player class
            All of the container references should be using the objects themselves, not just references to the objects.
            Finish condition comparisons for integer and the place holder values
            Max value comparisons should be built into the engine
            Finish refactoring the code for speech
            Create time functionality
             - Test Point -
            Refactor the StoryHandler to make it derivative of the same common type as the AdventureHandler
            Go through the Story.json to make sure it shares as many common factors with the Adventure json
            Make sure the JSON is uniform
             - JSON Format Valdiate -
            Refactor the code
             - Test Point -
            Downloading animation
            Add a scrolling list to select a package
            Dynamically read the packages from the external directory
            Selecting a package opens a sub-screen that will display the Play and Delete buttons
            The default Google popup should be removed or replaced with something that matches the overall theme

            I am worried about abuse of the repeat functionality to increase resources for the player
            Hard coded actions should be part of the JSON eventually
            There may need to be different repeat types
            See if there is a better way of return values from Adventure Variables
            I can and probably should take all of the extra directions out from the sections and just detect if they exist in the code


        Ideas:
            Download from list of packages
            Purchase from list of packages
            Add checks for additional options past the most likely option if there are no matches
                When voice-to-text responds it can respond with multiple likely responses with the most likely in position zero
            Add an offline VTT option
            Add streaming or downloading of packages

            AA
                Regen Amount
                Regen Speed

        Complete:
            Directions should be setup just like entities in the sections { "direction": "north", "name": "livingRoom" } and then the container turned into an array
            Actions and movesets should be overrides stuctured in the same way as entity vars
            Go through the JSON to fix the entityVars section on all other entities than the Troll
            Go through the conditions and replace key words with new types of operators (like == or !=)
            Repeat should play the last chain of clips, not just the last clip
            Sounds clip array should be source of the media played, convert the function on AdventureHandler accordingly
            Error sounds should be placed into the clip array
            Create an "array" to store the string of clips on the AdventureHandler
            Record error sounds
            Record movesets
            Make the JSON uniform
            Add the "base actions" to the JSON
            Create Audio Adventure package type
            Create Interactive Audiobook package type
            Split the code into files
            Add a repeat default keyword to repeat the current section
            Add a default, no result found, result path
            Reform the json to allow for more complex story telling
            Add a trigger to the download that once it is finished to display the Play and Delete buttons
            Hide the play and delete buttons
            Change Download to Update after it is downloaded
            Add a new 'Delete' button
            Play audio from external directory
            Read json from external directory
            Extract package
            Download package from odinary.net to phone's external directory
            Add a new 'Download' button
            Find a simple compressible format to store the story package in
            Find a good place to store the packages on the phone
            Play the audio listed from the json object
            Convert the if into looped ifs based on the properties of the json object
            Change button to 'Play'
            Read json into an object from res directory
            Convert story to .json
            Fix the contains, it is not working properly
            Play the question clip and if answer is A then play answer A clip and if answer is B then play answer B clip
            Create a test answer B audio clip
            Create a test answer A audio clip
            Create a test question audio clip
            Check the voice-to-text output to see if it contains a keyword
            When the button is pressed it should play the audio clip and when it is finished trigger voice-to-text
            Make voice-to-text change the text of the label to what was heard
            When button is pressed prompt voice-to-text
            When button is pressed play test clip
            Create test audio clip
            When button is pressed change text of label
            Add Button

 */

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.odinary.interaudio.adventure.AdventureHandler;

public class MainActivity extends AppCompatActivity
{
    public static final boolean debugMode = true;
    public static final boolean simpleTestMode = true;
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
