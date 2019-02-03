package net.odinary.interaudio.adventure.repository;

import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository extends AbstractRepository
{
    private List<Entity> inventory = new ArrayList<>();

    public Boolean checkVariableExists(String variableName) { return variables.containsKey(variableName); }

    public Action getActionFromResult(String resultPhrase)
    {
        for(String key: actions.keySet())
        {
            Action action = actions.get(key);

            if(resultPhrase.contains(actions.get(key).getName())) return action;
        }

        return null;
    }

    public void addToInventory(Entity item) { inventory.add(item); }

    public List<Entity> getInventory() { return inventory; }

    public Entity checkInventory(String resultPhrase)
    {
        for(Entity item: inventory)
        {
            if(resultPhrase.contains(item.getName())) return item;
        }

        return null;
    }

    public void useItemFromInventory(String itemName)
    {
        // Use item logic here
    }
}