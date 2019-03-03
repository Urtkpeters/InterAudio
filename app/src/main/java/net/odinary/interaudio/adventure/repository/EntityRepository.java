package net.odinary.interaudio.adventure.repository;

import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.adventure.component.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityRepository extends AbstractRepository
{
    private Map<String, Entity> items = new HashMap<>();
    private Map<String, Entity> objects = new HashMap<>();
    private Map<String, Entity> characters = new HashMap<>();

    public void addEntity(String type, String name, Entity entity) throws PackageLoadException
    {
        switch(type)
        {
            case "item": items.put(name, entity); break;
            case "object": objects.put(name, entity); break;
            case "character": characters.put(name, entity); break;
            default: throw new PackageLoadException("Invalid entity type during entity repository creation.");
        }
    }

    public Entity getEntity(String type, String name)
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

    public Map<String, Entity> getEntities(String type)
    {
        switch(type)
        {
            case "item": return items;
            case "object": return objects;
            case "character": return characters;
            default: return null;
        }
    }
}