package net.odinary.interaudio.adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Player
{
    private static List<Entity> inventory = new ArrayList<>();
    private static Map<String, PlayerVariable> playerVariables = new HashMap<>();

    public static void addPlayerVariable(PlayerVariable playerVariable) { playerVariables.put(playerVariable.getName(), playerVariable); }

    public static PlayerVariable getPlayerVariable(String variableName) { return playerVariables.get(variableName); }

    public static boolean checkPlayerVariable(String variableName, boolean value, String relationalOperator) { return playerVariables.get(variableName).checkValue(value, relationalOperator); }

    public static boolean checkPlayerVariable(String variableName, int value, String relationalOperator) { return playerVariables.get(variableName).checkValue(value, relationalOperator); }

    public static boolean checkPlayerVariable(String variableName, String value, String relationalOperator) { return playerVariables.get(variableName).checkValue(value, relationalOperator); }

    public static void addToInventory(Entity item) { inventory.add(item); }

    public static Entity checkInventory(String resultPhrase)
    {
        for(Entity item: inventory)
        {
            if(resultPhrase.contains(item.getName())) return item;
        }

        return null;
    }

    public static void useItemFromInventory(String itemName)
    {
        // Use item logic here
    }
}