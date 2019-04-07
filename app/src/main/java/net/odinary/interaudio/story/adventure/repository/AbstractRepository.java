package net.odinary.interaudio.story.adventure.repository;

import net.odinary.interaudio.story.adventure.component.AdventureAction;
import net.odinary.interaudio.story.adventure.component.variable.VariableInterface;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRepository
{
    protected Map<String, VariableInterface> variables = new HashMap<>();
    protected Map<String, AdventureAction> actions = new HashMap<>();

    public void addVariable(VariableInterface variable) { variables.put(variable.getName(), variable); }

    public void addAction(AdventureAction adventureAction) { actions.put(adventureAction.getName(), adventureAction); }

    public VariableInterface getVariable(String variableName) { return variables.get(variableName); }

    public AdventureAction getAction(String actionName) { return actions.get(actionName); }

    public Map<String, VariableInterface> getVariables() { return variables; }

    public Map<String, AdventureAction> getActions() { return actions; }
}