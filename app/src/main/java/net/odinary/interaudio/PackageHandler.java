package net.odinary.interaudio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PackageHandler
{
    private MainActivity mainActivity;
    private File filesDir;

    PackageHandler(MainActivity activity)
    {
        mainActivity = activity;

        filesDir = mainActivity.getFilesDir();
    }

    public Boolean checkPackage()
    {
        return new File(filesDir + File.separator + mainActivity.getCurrentPackage()).exists();
    }

    public String getPackageDir()
    {
        return filesDir + File.separator + mainActivity.getCurrentPackage() + File.separator;
    }

    public void downloadPackage()
    {
        String packageName = mainActivity.getCurrentPackage();

        try
        {
            File dataDirectory = filesDir;
            String packageFilename = packageName + ".zip";
            File outputFile = new File(dataDirectory + "/" + packageFilename);
            File packageDirectory = new File(dataDirectory + "/" + packageName);

            if((!outputFile.exists() && !packageDirectory.exists()) || MainActivity.debugMode)
            {
                if(packageDirectory.exists() && !deleteFile(filesDir + File.separator + mainActivity.getCurrentPackage())) return;

                URL url = new URL("https://www.odinary.net/" + packageFilename);
                URLConnection connection = url.openConnection();
                int contentLength = connection.getContentLength();

                DataInputStream diStream = new DataInputStream(url.openStream());
                byte[] buffer = new byte[contentLength];

                diStream.readFully(buffer);
                diStream.close();

                if(checkAndCreateDir(dataDirectory.toString()))
                {
                    DataOutputStream doStream = new DataOutputStream(new FileOutputStream(outputFile));
                    doStream.write(buffer);
                    doStream.flush();
                    doStream.close();

                    unzip(dataDirectory.toString(), packageName);
                }
                else
                {
                    // Do something with this
                    System.out.println("Could not find or create data directory.");
                }
            }
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }

    private static void unzip(String location, String packageName)
    {
        if(!checkAndCreateDir(location)) return;
        if(!checkAndCreateDir(location + File.separator + packageName)) return;

        try(ZipInputStream ziStream = new ZipInputStream(new FileInputStream(location + File.separator + packageName + ".zip")))
        {
            ZipEntry zipEntry;

            while ((zipEntry = ziStream.getNextEntry()) != null)
            {
                String path = location + File.separator + packageName + File.separator + zipEntry.getName();

                if(zipEntry.isDirectory())
                {
                    if(!checkAndCreateDir(path)) return;
                }
                else
                {
                    FileOutputStream foStream = new FileOutputStream(path, false);

                    for(int c = ziStream.read(); c != -1; c = ziStream.read())
                    {
                        foStream.write(c);
                    }

                    ziStream.closeEntry();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean checkAndCreateDir(String filepath)
    {
        Boolean existsOrCreated = true;
        File directory = new File(filepath);

        if (!directory.exists() || !directory.isDirectory()) existsOrCreated = directory.mkdirs();

        return existsOrCreated;
    }

    public void deleteCurrentPackage()
    {
        // Do something with this output
        if(!deleteFile(filesDir + File.separator + mainActivity.getCurrentPackage())) System.out.println("Error deleting package.");
    }

    private static Boolean deleteFile(String filePath)
    {
        Boolean success = true;
        File deleteFile = new File(filePath);

        if(deleteFile.exists())
        {
            if(deleteFile.isDirectory())
            {
                String[] files = deleteFile.list();

                for(String childFile : files)
                {
                    File file = new File(deleteFile, childFile);

                    if(!file.isDirectory()) success = file.delete();
                    else success = deleteFile(file.toString());

                    if(!success) break;
                }
            }

            if(success) success = deleteFile.delete();
        }

        return success;
    }
}