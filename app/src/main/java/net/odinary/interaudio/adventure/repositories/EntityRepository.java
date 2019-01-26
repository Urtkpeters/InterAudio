package net.odinary.interaudio.adventure.repositories;

import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.adventure.Action;
import net.odinary.interaudio.adventure.AdventureVariable;
import net.odinary.interaudio.adventure.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityRepository
{
    private static Map<String, Entity> items = new HashMap<>();
    private static Map<String, Entity> objects = new HashMap<>();
    private static Map<String, Entity> characters = new HashMap<>();
    private static Map<String, AdventureVariable> variables = new HashMap<>();
    private static Map<String, Action> moves = new HashMap<>();

    public static void addEntity(String type, String name, Entity entity) throws PackageLoadException
    {
        switch(type)
        {
            case "item": items.put(name, entity); break;
            case "object": objects.put(name, entity); break;
            case "character": characters.put(name, entity); break;
            default: throw new PackageLoadException("Invalid entity type during entity repository creation.");
        }
    }

    public static void addEntityVariable(String name, AdventureVariable adventureVariable) { variables.put(name, adventureVariable); }

    public static void addEntityMove(String name, Action action) { moves.put(name, action); }

    public static Entity getEntity(String type, String name)
    {
        Entity returnEntity = null;

        switch(type)
        {
            case "item": returnEntity = items.get(name); break;
            case "object": returnEntity = objects.get(name); break;
            case "character": returnEntity = characters.get(name); break;
        }

        return returnEntity;
    }

    public static AdventureVariable getEntityVariable(String name) { return variables.get(name); }

    public static Action getEntityMove(String name) { return moves.get(name); }

    public static Boolean checkEntityExists(String type, String name)
    {
        switch(type)
        {
            case "item": return items.containsKey(name);
            case "object": return objects.containsKey(name);
            case "character": return characters.containsKey(name);
            default: return false;
        }
    }

    public static Boolean checkEntityVariableExists(String name) { return variables.containsKey(name); }

    public static Boolean checkEntityMoveExists(String name) { return moves.containsKey(name); }
}