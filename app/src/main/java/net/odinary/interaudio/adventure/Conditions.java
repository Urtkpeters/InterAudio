package net.odinary.interaudio.adventure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Conditions
{
    public static List<Condition> parseConditions(JSONArray conditionsArray) throws JSONException
    {
        List<Condition> conditions = new ArrayList<>();

        for(int i = 0; i < conditionsArray.length(); i++)
        {
            JSONObject condition = conditionsArray.getJSONObject(i);

            conditions.add(new Condition(condition.getString("condition"), condition.getString("operator"), condition.getString("failFilename")));
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

                // I am really not sure if this list > hashmap comparison using .equals is going to work.
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
                List<ConditionSegment> conditionSegments = condition.getSegments();
                ConditionSegment firstPath = conditionSegments.get(0);
                ConditionSegment secondPath;
                String operator;

                AdventureVariable firstVar = parseVariable(event, firstPath);

                if(firstVar != null)
                {
                    String compType = firstVar.getType();

                    switch(compType)
                    {
                        case AdventureVariable.booleanType:
                            secondPath = conditionSegments.get(2);
                            operator = conditionSegments.get(1).getValue();

                            if(secondPath.getType() == ConditionSegment.raw) finalValues.add(firstVar.checkValue(Boolean.parseBoolean(secondPath.getValue()), operator));
                            else
                            {
                                AdventureVariable secondVar = parseVariable(event, secondPath);
                                finalValues.add(firstVar.checkValue(secondVar.getBValue(), operator));
                            }
                            break;
                        case AdventureVariable.integerType:
                            finalValues.add(compareInt(event, conditionSegments, 0, 0, 0, null));
                            break;
                        case AdventureVariable.stringType:
                            secondPath = conditionSegments.get(2);
                            operator = conditionSegments.get(1).getValue();

                            if(secondPath.getType() == ConditionSegment.raw) finalValues.add(firstVar.checkValue(secondPath.getValue(), operator));
                            else
                            {
                                AdventureVariable secondVar = parseVariable(event, secondPath);
                                finalValues.add(firstVar.checkValue(secondVar.getSValue(), operator));
                            }
                            break;
                    }
                }
            }

            Boolean andOperator = true;
            Boolean currentVal = true;

            for(int i = 0; i < finalValues.size(); i++)
            {
                if(andOperator && currentVal) currentVal = finalValues.get(i);
                else if(!andOperator) currentVal = finalValues.get(i);

                // This needs to not be a hard coded string
                andOperator = conditions.get(i).getOperator().equals("AND");

                if(!finalValues.get(i)) failFilename = conditions.get(i).getFailFilename();

                if(!andOperator && currentVal) return null;
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

                // I don't have a storage place for global variables yet.

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
            case ConditionSegment.sections:
                Section currentSection = event.getSection();

                // Section shit is complete different

                break;
        }

        return null;
    }

    private static Boolean compareInt(Event event, List<ConditionSegment> segments, int currentIndex, int currentLeftValue, int currentRightValue, String relationalOperator)
    {
        if(segments.size() > currentIndex + 2)
        {
            String operatorSegment = segments.get(currentIndex + 1).getValue();

            if(operatorSegment.contains("=") || operatorSegment.contains(">") || operatorSegment.contains("<"))
            {
                AdventureVariable varOne = parseVariable(event, segments.get(currentIndex));

                return compareInt(event, segments, currentIndex + 2, varOne.getIValue(), currentRightValue, operatorSegment);
            }
            else
            {
                int intOne;

                if(currentIndex == 0)
                {
                    AdventureVariable varOne = parseVariable(event, segments.get(currentIndex));

                    intOne = varOne.getIValue();
                }
                else
                {
                    if(relationalOperator == null) intOne = currentLeftValue;
                    else intOne = currentRightValue;
                }

                int intTwo;
                ConditionSegment segmentTwo = segments.get(currentIndex + 2);

                if(segmentTwo.getType() == ConditionSegment.variable)
                {
                    AdventureVariable varTwo = parseVariable(event, segments.get(currentIndex + 2));
                    intTwo = varTwo.getIValue();
                }
                else
                {
                    intTwo = Integer.parseInt(segmentTwo.getValue());
                }

                switch(operatorSegment)
                {
                    case "+": intOne += intTwo; break;
                    case "-": intOne -= intTwo; break;
                    case "*": intOne *= intTwo; break;
                    case "/": intOne /= intTwo; break;
                    default: System.out.println("Invalid type of operator segment"); break;
                }

                if(relationalOperator == null)
                {
                    return compareInt(event, segments, currentIndex + 2, intOne, currentRightValue, null);
                }
                else
                {
                    return compareInt(event, segments, currentIndex + 2, currentLeftValue, intOne, relationalOperator);
                }
            }
        }
        else
        {
            switch(relationalOperator)
            {
                case "==": return currentLeftValue == currentRightValue;
                case "!=": return currentLeftValue != currentRightValue;
                case "<": return currentLeftValue < currentRightValue;
                case ">": return currentLeftValue > currentRightValue;
                case "<=": return currentLeftValue <= currentRightValue;
                case ">=": return currentLeftValue >= currentRightValue;
                default: System.out.println("Invalid type of relational operator"); return false;
            }
        }
    }

    private static List<String> parseNotation(String input, String character)
    {
        List<String> list = new ArrayList<>();

        // The plus one is to account for the additional segment after the end of the last character
        int countPath = (input.length() - input.replace(character, "").length()) + 1;

        for(int i = 0; i < countPath; i++)
        {
            int dotIndex = input.indexOf(character);

            if(dotIndex != -1)
            {
                list.add(input.substring(0, dotIndex));
                input = input.substring(dotIndex + 1, input.length());
            }
            else
            {
                list.add(input);
            }
        }

        return list;
    }
}