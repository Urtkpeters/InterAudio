package net.odinary.interaudio;

import android.view.View;
import android.widget.Button;

class UIHandler
{
    private MainActivity mainActivity;
    private Button downloadButton;
    private Button playButton;
    private Button deleteButton;
    private Button testButton;

    UIHandler(MainActivity activity)
    {
        mainActivity = activity;
        downloadButton = mainActivity.findViewById(R.id.downloadButton);
        playButton = mainActivity.findViewById(R.id.playButton);
        deleteButton = mainActivity.findViewById(R.id.deleteButton);
        testButton = mainActivity.findViewById(R.id.testButton);
    }

    public void initializeUI()
    {
        if(MainActivity.simpleTestMode || MainActivity.testPackageMode)
        {
            downloadButton.setVisibility(View.INVISIBLE);
            testButton.setVisibility(View.VISIBLE);

            testButton.setOnClickListener((View v) ->
            {
                if(MainActivity.simpleTestMode) SimpleTest.doSimpleTest(mainActivity);
                else mainActivity.getAdventureHandler().start();
            });
        }
        else
        {
            if(mainActivity.getFolioHandler().checkPackage())
            {
                downloadButton.setText(R.string.updateButton);
                playButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            }

            downloadButton.setOnClickListener((View v) ->
            {
                mainActivity.getFolioHandler().downloadPackage();

                downloadButton.setText(R.string.updateButton);
                playButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            });

            playButton.setOnClickListener((View v) ->
            {
                //mainActivity.getInteractiveHandler().startPlay();
                mainActivity.getAdventureHandler().start();
            });

            deleteButton.setOnClickListener((View v) ->
            {
                mainActivity.getFolioHandler().deleteCurrentPackage();
                downloadButton.setText(R.string.downloadButton);
                playButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            });
        }
    }
}