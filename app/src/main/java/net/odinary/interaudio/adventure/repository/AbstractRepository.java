package net.odinary.interaudio.adventure.repository;

import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.variable.VariableInterface;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRepository
{
    protected Map<String, VariableInterface> variables = new HashMap<>();
    protected Map<String, Action> actions = new HashMap<>();

    public void addVariable(VariableInterface variable) { variables.put(variable.getName(), variable); }

    public void addAction(Action action) { actions.put(action.getName(), action); }

    public VariableInterface getVariable(String variableName) { return variables.get(variableName); }

    public Action getAction(String actionName) { return actions.get(actionName); }

    public Map<String, VariableInterface> getVariables() { return variables; }

    public Map<String, Action> getActions() { return actions; }
}