package net.odinary.interaudio.adventure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player
{
    private static List<Entity> inventory = new ArrayList<>();
    private static Map<String, PlayerVariable> variables = new HashMap<>();
    private static Map<String, Action> actions = new HashMap<>();

    public static void addPlayerVariable(PlayerVariable playerVariable) { variables.put(playerVariable.getName(), playerVariable); }

    public static PlayerVariable getVariable(String variableName) { return variables.get(variableName); }

    public static Boolean checkVariableExists(String variableName) { return variables.containsKey(variableName); }

    public static boolean checkVariable(String variableName, boolean value, String relationalOperator) { return variables.get(variableName).checkValue(value, relationalOperator); }

    public static boolean checkVariable(String variableName, int value, String relationalOperator) { return variables.get(variableName).checkValue(value, relationalOperator); }

    public static boolean checkVariable(String variableName, String value, String relationalOperator) { return variables.get(variableName).checkValue(value, relationalOperator); }

    public static void addAction(Action action) { actions.put(action.getName(), action); }

    public static Action getAction(String name) { return actions.get(name); }

    public static Action getActionFromResult(String resultPhrase)
    {
        for(String key: actions.keySet())
        {
            Action action = actions.get(key);

            if(resultPhrase.contains(actions.get(key).getName())) return action;
        }

        return null;
    }

    public static void addToInventory(Entity item) { inventory.add(item); }

    public static List<Entity> getInventory() { return inventory; }

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