package net.odinary.interaudio.adventure.repositories;

import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.adventure.Action;

import java.util.HashMap;
import java.util.Map;

public class ActionRepository
{
    private static Map<String, Action> time = new HashMap<>();
    private static Map<String, Action> player = new HashMap<>();
    private static Map<String, Action> moves = new HashMap<>();

    public static void addAction(String type, String name, Action action) throws PackageLoadException
    {
        switch(type)
        {
            case "time": time.put(name, action); break;
            case "player": player.put(name, action); break;
            case "moves": moves.put(name, action); break;
            default: throw new PackageLoadException("Invalid action type during action repository creation.");
        }
    }

    public static Action getAction(String type, String name)
    {
        Action returnAction = null;

        switch(type)
        {
            case "time": returnAction = time.get(name); break;
            case "player": returnAction = player.get(name); break;
            case "moves": returnAction = moves.get(name); break;
        }

        return returnAction;
    }

    public static Boolean checkAction(String type, String name)
    {
        switch(type)
        {
            case "time": return time.containsKey(name);
            case "player": return player.containsKey(name);
            case "moves": return moves.containsKey(name);
            default: return false;
        }
    }
}