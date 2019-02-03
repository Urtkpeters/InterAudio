package net.odinary.interaudio.adventure.repository;

import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.adventure.component.entity.Action;

import java.util.HashMap;
import java.util.Map;

public class ActionRepository
{
    private Map<String, Action> time = new HashMap<>();
    private Map<String, Action> player = new HashMap<>();
    private Map<String, Action> moves = new HashMap<>();

    public void addAction(String type, String name, Action action) throws PackageLoadException
    {
        switch(type)
        {
            case "time": time.put(name, action); break;
            case "player": player.put(name, action); break;
            case "moves": moves.put(name, action); break;
            default: throw new PackageLoadException("Invalid action type during action repository creation.");
        }
    }

    public Action getAction(String type, String name)
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
}