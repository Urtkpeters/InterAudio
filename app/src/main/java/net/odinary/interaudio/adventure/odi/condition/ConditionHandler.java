package net.odinary.interaudio.adventure.odi.condition;

import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.odi.AbstractOdiHandler;
import net.odinary.interaudio.adventure.odi.OdiSegment;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConditionHandler extends AbstractOdiHandler
{
    public static final String equals = "==";
    public static final String notEquals = "!=";
    public static final String lessThan = "<";
    public static final String lessThanOrEquals = "<=";
    public static final String greaterThan = ">";
    public static final String greaterThanOrEquals = ">=";

    private static final String andOperator = "AND";
    private static final String orOperator = "OR";

    public ConditionHandler(WorldRepository worldRepository, PlayerRepository playerRepository) { super(worldRepository, playerRepository); }

    public List<Condition> parse(JSONArray conditionsArray) throws JSONException
    {
        List<Condition> conditions = new ArrayList<>();

        for(int i = 0; i < conditionsArray.length(); i++)
        {
            JSONObject condition = conditionsArray.getJSONObject(i);

            conditions.add(new Condition(condition.getString("type"), condition.getString("condition"), condition.getString("operator"), condition.getString("failFilename")));
        }

        return conditions;
    }

    public String checkConditions(Event event)
    {
        Action action = event.getAction();
        List<Condition> conditions = action.getConditions();

        String returnFilename = checkConditions(event, conditions);

        if(returnFilename == null || returnFilename.isEmpty())
        {
            Entity target = event.getTarget();
            String actionName = action.getName();

            if(target.checkActionOverride(actionName))
            {
                Action overrideAction = target.getActionOverride(actionName);
                List<Condition> overrideConditions = overrideAction.getConditions();

                if(overrideConditions.size() > 0) returnFilename = checkConditions(event, event.getAction().getConditions());

                if(returnFilename == null || returnFilename.isEmpty()) returnFilename = overrideAction.getFilename();
            }
            else
            {
                returnFilename = action.getFilename();
            }
        }

        return returnFilename;
    }

    private String checkConditions(Event event, List<Condition> conditions)
    {
        String failFilename = null;

        if(conditions.size() != 0)
        {
            List<Boolean> finalValues = new ArrayList<>();

            for(Condition condition: conditions)
            {
                String comparisonType = condition.getType();
                List<OdiSegment> leftSegments = condition.getLeftSegments();
                List<OdiSegment> rightSegments = condition.getRightSegments();
                OdiSegment operatorSegment = condition.getOperatorSegment();

                switch(comparisonType)
                {
                    case Condition.booleanType: finalValues.add(compareBoolean(event, leftSegments, rightSegments, operatorSegment)); break;
                    case Condition.integerType: finalValues.add(compareDouble(event, leftSegments, rightSegments, operatorSegment)); break;
                    case Condition.stringType: finalValues.add(compareString(event, leftSegments, rightSegments, operatorSegment)); break;
                }
            }

            Boolean booleanOperator = true;
            Boolean currentVal = true;

            for(int i = 0; i < finalValues.size(); i++)
            {
                if(booleanOperator && currentVal) currentVal = finalValues.get(i);
                else if(!booleanOperator) currentVal = finalValues.get(i);

                // This needs to not be a hard coded string
                booleanOperator = conditions.get(i).getOperator().equals(andOperator);

                if(!finalValues.get(i)) failFilename = conditions.get(i).getFailFilename();

                if(!booleanOperator && currentVal) return null;
            }
        }

        return failFilename;
    }

    private Boolean compareBoolean(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        boolean rightSide;

        if(rightSegments.get(0).getType() == OdiSegment.raw) rightSide = Boolean.parseBoolean(rightSegments.get(0).getValue());
        else rightSide = parseVariable(event, rightSegments.get(0)).getBValue();

        return leftSide.checkValue(rightSide, operatorSegment.getValue());
    }

    private Boolean compareString(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        String rightSide;

        if(rightSegments.get(0).getType() == OdiSegment.raw) rightSide = rightSegments.get(0).getValue();
        else rightSide = parseVariable(event, rightSegments.get(0)).getSValue();

        return leftSide.checkValue(rightSide, operatorSegment.getValue());
    }

    private Boolean compareDouble(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        double leftSide = evaluateSide(event, leftSegments);
        double rightSide = evaluateSide(event, rightSegments);

        switch(operatorSegment.getValue())
        {
            case equals: return leftSide == rightSide;
            case notEquals: return leftSide != rightSide;
            case lessThan: return leftSide < rightSide;
            case lessThanOrEquals: return leftSide <= rightSide;
            case greaterThan: return leftSide > rightSide;
            case greaterThanOrEquals: return leftSide >= rightSide;
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }
}