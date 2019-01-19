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
        if(MainActivity.simpleTestMode)
        {
            downloadButton.setVisibility(View.INVISIBLE);
            testButton.setVisibility(View.VISIBLE);

            testButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SimpleTest.doSimpleTest(mainActivity);
                }
            });
        }
        else
        {
            if(mainActivity.getPackageHandler().checkPackage())
            {
                downloadButton.setText(R.string.updateButton);
                playButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            }

            downloadButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mainActivity.getPackageHandler().downloadPackage();

                    downloadButton.setText(R.string.updateButton);
                    playButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                }
            });

            playButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //mainActivity.getStoryHandler().startPlay();
                    mainActivity.getAdventureHandler().startPlay();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mainActivity.getPackageHandler().deleteCurrentPackage();
                    downloadButton.setText(R.string.downloadButton);
                    playButton.setVisibility(View.INVISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}