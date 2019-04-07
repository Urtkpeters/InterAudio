package net.odinary.interaudio.story.adventure.repository;

import net.odinary.interaudio.story.adventure.component.AdventureAction;
import net.odinary.interaudio.story.adventure.component.Entity;
import net.odinary.interaudio.story.adventure.component.variable.AdventureVariable;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository extends AbstractRepository
{
    private List<Entity> inventory = new ArrayList<>();
    private int time = 0;
    private int timeRegen = 1;

    public AdventureAction getActionFromResult(String resultPhrase)
    {
        for(String key: actions.keySet())
        {
            AdventureAction adventureAction = actions.get(key);

            if(resultPhrase.contains(actions.get(key).getName())) return adventureAction;

            for(String keyword: adventureAction.getKeywords())
            {
                if(resultPhrase.contains(keyword)) return adventureAction;
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