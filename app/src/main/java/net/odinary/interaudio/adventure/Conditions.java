package net.odinary.interaudio.adventure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Conditions
{
    public static List<HashMap<String, String>> parseConditions(JSONArray conditionsArray) throws JSONException
    {
        List<HashMap<String, String>> conditions = new ArrayList<>();

        for(int i = 0; i < conditionsArray.length(); i++)
        {
            JSONObject condition = conditionsArray.getJSONObject(i);

            conditions.add(i, new HashMap<String, String>());

            conditions.get(i).put("condition", condition.getString("condition"));
            conditions.get(i).put("operator", condition.getString("operator"));
            conditions.get(i).put("failFilename", condition.getString("failFilename"));
        }

        return conditions;
    }

    public static String checkConditions(Event event, HashMap<String, HashMap<String, AdventureVariable>> variables)
    {
        List<HashMap<String, String>> conditions = event.getAction().getConditions();
        String failFilename = null;

        if(conditions.size() != 0)
        {
            List<Boolean> finalValues = new ArrayList<>();

            for(HashMap<String, String> conditionRow: conditions)
            {
                String condition = conditionRow.get("condition");

                System.out.println("Condition: " + condition);

                List<String> segments = parseNotation(condition, " ");

                System.out.println("Segments: " + segments);

                List<String> firstPath = parseNotation(segments.get(0), ".");
                List<String> secondPath;
                AdventureVariable firstVar = null;

                if(firstPath.get(0).equals("variables"))
                {
                    firstVar = variables.get(firstPath.get(1)).get(firstPath.get(2));
                }
                else if(firstPath.get(0).equals("event"))
                {
                    if(firstPath.get(1).equals("section"))
                    {
                        // Do shit with sections here
                    }
                    else
                    {
                        if(firstPath.get(1).equals("target"))
                        {
                            firstVar = event.getTarget().getVariable(firstPath.get(2));
                        }
                        else if(firstPath.get(1).equals("secondaryTarget"))
                        {
                            firstVar = event.getSecondaryTarget().getVariable(firstPath.get(2));
                        }
                    }
                }

                if(firstVar != null)
                {
                    System.out.println("First Var: " + firstVar.getVariableName());
                    System.out.println("First Value: " + firstVar);

                    String compType = firstVar.getType();

                    if(compType != null)
                    {
                        // I SHOULD TRY AND SEE IF THERE IS A BETTER WAY OF GETTING THE VALUES OTHER THAN HARD CODED INTEGERS
                        switch(compType)
                        {
                            case "Boolean":
                                secondPath = parseNotation(segments.get(2), ".");

                                if(secondPath.size() <= 1) finalValues.add(firstVar.checkValue(Boolean.parseBoolean(secondPath.get(0)), segments.get(1)));
                                    // THESE LONG HARD CODED PATHS SHOULD NOT BE HERE, THEY NEED TO BE DYNAMICALLY PULLED
                                else finalValues.add(firstVar.checkValue(variables.get(secondPath.get(1)).get(secondPath.get(2)).getBValue(), segments.get(1)));

                                break;
                            case "Integer":
                                finalValues.add(compareInt(variables, segments, 0, 0, 0, null));
                                break;
                            case "String":
                                secondPath = parseNotation(segments.get(2), ".");
                                finalValues.add(firstVar.checkValue(variables.get(secondPath.get(1)).get(secondPath.get(2)).getSValue(), segments.get(1)));
                                break;
                        }
                    }
                }
            }

            Boolean andOperator = true;
            Boolean currentVal = true;

            for(int i = 0; i < finalValues.size(); i++)
            {
                if(andOperator && currentVal) currentVal = finalValues.get(i);
                else if(!andOperator) currentVal = finalValues.get(i);

                andOperator = conditions.get(i).get("operator").equals("AND");

                if(!finalValues.get(i)) failFilename = conditions.get(i).get("failFilename");

                if(!andOperator && currentVal) return null;
            }
        }

        return failFilename;
    }

    private static Boolean compareInt(HashMap<String, HashMap<String, AdventureVariable>> variables, List<String> segments, int currentIndex, int currentLeftValue, int currentRightValue, String relationalOperator)
    {
        if(segments.size() > currentIndex + 2)
        {
            String operatorSegment = segments.get(currentIndex + 1);

            if(operatorSegment.contains("=") || operatorSegment.contains(">") || operatorSegment.contains("<"))
            {
                String segmentOne = segments.get(currentIndex);
                List<String> listOne = parseNotation(segmentOne, ".");

                // THIS CAN BE DIFFERENT LENGTH DEPENDING ON WHAT THE ORIGINAL VARIABLE IS (TARGET VS PLAYER VAR)
                // I SHOULD PROBABLY JUST COME UP WITH A SEPERATE FUNCTION TO PARSE THE DIFFERENCE AND RETURN A VALUE IN THE SEVERAL PLACES IT IS NEEDED
                AdventureVariable varOne = variables.get(listOne.get(1)).get(listOne.get(2));

                return compareInt(variables, segments, currentIndex + 2, varOne.getIValue(), currentRightValue, operatorSegment);
            }
            else
            {
                int intOne;

                if(currentIndex == 0)
                {
                    String segmentOne = segments.get(currentIndex);
                    List<String> listOne = parseNotation(segmentOne, ".");
                    AdventureVariable varOne = variables.get(listOne.get(1)).get(listOne.get(2));

                    intOne = varOne.getIValue();
                }
                else
                {
                    if(relationalOperator == null) intOne = currentLeftValue;
                    else intOne = currentRightValue;
                }

                String segmentTwo = segments.get(currentIndex + 2);
                int intTwo;

                if(segmentTwo.contains("variables") || segmentTwo.contains("target") || segmentTwo.contains("secondaryTarget") || segmentTwo.contains("section"))
                {
                    List<String> listTwo = parseNotation(segmentTwo, ".");
                    AdventureVariable varTwo = variables.get(listTwo.get(1)).get(listTwo.get(2));
                    intTwo = varTwo.getIValue();
                }
                else
                {
                    intTwo = Integer.parseInt(segmentTwo);
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
                    return compareInt(variables, segments, currentIndex + 2, intOne, currentRightValue, null);
                }
                else
                {
                    return compareInt(variables, segments, currentIndex + 2, currentLeftValue, intOne, relationalOperator);
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