package net.odinary.interaudio.folio.adventure.repository;

import net.odinary.interaudio.folio.adventure.component.entity.Action;
import net.odinary.interaudio.folio.adventure.component.entity.Entity;
import net.odinary.interaudio.folio.adventure.component.entity.variable.AdventureVariable;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository extends AbstractRepository
{
    private List<Entity> inventory = new ArrayList<>();
    private int time = 0;
    private int timeRegen = 1;

    public Action getActionFromResult(String resultPhrase)
    {
        for(String key: actions.keySet())
        {
            Action action = actions.get(key);

            if(resultPhrase.contains(actions.get(key).getName())) return action;

            for(String keyword: action.getKeywords())
            {
                if(resultPhrase.contains(keyword)) return action;
            }
        }

        return null;
    }

    public void decrementTime(int actionTime) { time -= actionTime; }

    public void incrementTime() { time += timeRegen; }

    public int getTime() { return time; }

    public void setTimeRegen(int timeRegen) { this.timeRegen = timeRegen; }

    public boolean isAlive() { return variables.get("Alive").checkValue(true, AdventureVariable.equals); }

    public void addToInventory(Entity item) { inventory.add(item); }

    public void removeFromInventory(String itemName)
    {
        for(Entity item: inventory)
        {
            if(item.getName().equals(itemName)) inventory.remove(item);
        }
    }

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