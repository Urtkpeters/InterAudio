package net.odinary.interaudio.adventure.condition;

import net.odinary.interaudio.adventure.Action;
import net.odinary.interaudio.adventure.AdventureVariable;
import net.odinary.interaudio.adventure.Entity;
import net.odinary.interaudio.adventure.Event;
import net.odinary.interaudio.adventure.Player;
import net.odinary.interaudio.adventure.Section;
import net.odinary.interaudio.adventure.World;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConditionHandler
{
    private static final String andOperator = "AND";
    private static final String orOperator = "OR";

    public static List<Condition> parseConditions(JSONArray conditionsArray) throws JSONException
    {
        List<Condition> conditions = new ArrayList<>();

        for(int i = 0; i < conditionsArray.length(); i++)
        {
            JSONObject condition = conditionsArray.getJSONObject(i);

            conditions.add(new Condition(condition.getString("type"), condition.getString("condition"), condition.getString("operator"), condition.getString("failFilename")));
        }

        return conditions;
    }

    public static String checkConditions(Event event)
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

                // This comparison will probably not work. I need to rework it
                if(overrideConditions.size() > 0 && !conditions.equals(overrideConditions)) returnFilename = checkConditions(event, event.getAction().getConditions());

                if(returnFilename == null || returnFilename.isEmpty()) returnFilename = overrideAction.getFilename();
            }
            else
            {
                returnFilename = action.getFilename();
            }
        }

        return returnFilename;
    }

    private static String checkConditions(Event event, List<Condition> conditions)
    {
        String failFilename = null;

        if(conditions.size() != 0)
        {
            List<Boolean> finalValues = new ArrayList<>();

            for(Condition condition: conditions)
            {
                String comparisonType = condition.getType();
                List<ConditionSegment> leftSegments = condition.getLeftSegments();
                List<ConditionSegment> rightSegments = condition.getRightSegments();
                ConditionSegment operatorSegment = condition.getOperatorSegment();

                switch(comparisonType)
                {
                    case Condition.booleanComparison: finalValues.add(compareBoolean(event, leftSegments, rightSegments, operatorSegment)); break;
                    case Condition.integerComparison: finalValues.add(compareInt(event, leftSegments, rightSegments, operatorSegment)); break;
                    case Condition.stringComparison: finalValues.add(compareString(event, leftSegments, rightSegments, operatorSegment)); break;
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

    private static AdventureVariable parseVariable(Event event, ConditionSegment path)
    {
        String varName = path.getVariable();

        switch(path.getScope())
        {
            case ConditionSegment.global:
                if(World.checkVariableExists(varName)) return World.getVariable(varName);
                break;
            case ConditionSegment.player:
                if(Player.checkVariableExists(varName)) return Player.getVariable(varName);

                break;
            case ConditionSegment.target:
                Entity target = event.getTarget();
                if(target.checkVariableExists(varName)) return target.getVariable(varName);

                break;
            case ConditionSegment.secondaryTarget:
                Entity secondaryTarget = event.getSecondaryTarget();
                if(secondaryTarget.checkVariableExists(varName)) return secondaryTarget.getVariable(varName);

                break;
        }

        return null;
    }

    private static Boolean compareBoolean(Event event, List<ConditionSegment> leftSegments, List<ConditionSegment> rightSegments, ConditionSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        boolean rightSide;

        if(rightSegments.get(0).getType() == ConditionSegment.raw) rightSide = Boolean.parseBoolean(rightSegments.get(0).getValue());
        else rightSide = parseVariable(event, rightSegments.get(0)).getBValue();

        return leftSide.checkValue(rightSide, operatorSegment.getValue());
    }

    private static Boolean compareString(Event event, List<ConditionSegment> leftSegments, List<ConditionSegment> rightSegments, ConditionSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        String rightSide;

        if(rightSegments.get(0).getType() == ConditionSegment.raw) rightSide = rightSegments.get(0).getValue();
        else rightSide = parseVariable(event, rightSegments.get(0)).getSValue();

        return leftSide.checkValue(rightSide, operatorSegment.getValue());
    }

    private static Boolean compareInt(Event event, List<ConditionSegment> leftSegments, List<ConditionSegment> rightSegments, ConditionSegment operatorSegment)
    {
        int leftSide = evaluateSide(event, leftSegments);
        int rightSide = evaluateSide(event, rightSegments);

        switch(operatorSegment.getValue())
        {
            case "==": return leftSide == rightSide;
            case "!=": return leftSide != rightSide;
            case "<": return leftSide < rightSide;
            case ">": return leftSide > rightSide;
            case "<=": return leftSide <= rightSide;
            case ">=": return leftSide >= rightSide;
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    private static int evaluateSide(Event event, List<ConditionSegment> segments)
    {
        int sideValue = 0;
        String arithmeticOperator = "+";

        if(segments.size() > 1)
        {
            for(ConditionSegment segment: segments)
            {
                boolean isOperator = false;
                int tmpSide = 0;

                if(segment.getType() == ConditionSegment.raw)
                {
                    String tmpVal = segment.getValue();

                    if(tmpVal.contains("+") && tmpVal.contains("-") && tmpVal.contains("*") && tmpVal.contains("/"))
                    {
                        isOperator = true;
                        arithmeticOperator = tmpVal;
                    }
                    else tmpSide = Integer.parseInt(tmpVal);
                }
                else tmpSide = parseVariable(event, segment).getIValue();

                if(!isOperator)
                {
                    switch(arithmeticOperator)
                    {
                        case "+": sideValue += tmpSide; break;
                        case "-": sideValue -= tmpSide; break;
                        case "*": sideValue *= tmpSide; break;
                        case "/": if(tmpSide != 0) sideValue /= tmpSide; break;
                        default: System.out.println("Invalid type of operator segment"); break;
                    }
                }
            }
        }
        else if(segments.size() > 0)
        {
            if(segments.get(0).getType() == ConditionSegment.raw) sideValue = Integer.parseInt(segments.get(0).getValue());
            else sideValue = parseVariable(event, segments.get(0)).getIValue();
        }

        return sideValue;
    }
}