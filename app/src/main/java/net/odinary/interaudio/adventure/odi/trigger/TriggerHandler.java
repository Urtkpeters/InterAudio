package net.odinary.interaudio.adventure.odi.trigger;

import net.odinary.interaudio.adventure.Event;
import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.odi.AbstractOdiHandler;
import net.odinary.interaudio.adventure.odi.OdiSegment;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TriggerHandler extends AbstractOdiHandler
{
    public TriggerHandler(WorldRepository worldRepository, PlayerRepository playerRepository) { super(worldRepository, playerRepository); }

    public List<Trigger> parse(JSONArray jsonArray) throws JSONException
    {
        List<Trigger> triggers = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            triggers.add(new Trigger(jsonObject.getString("type"), jsonObject.getString("trigger"), jsonObject.getString("operator")));
        }

        return triggers;
    }

    public void runTriggers(Event event)
    {
        Action action = event.getAction();
        List<Trigger> triggers = action.getTriggers();

        if(triggers.size() != 0)
        {
            for(Trigger trigger: triggers)
            {
                String odiType = trigger.getType();
                List<OdiSegment> leftSegments = trigger.getLeftSegments();
                List<OdiSegment> rightSegments = trigger.getRightSegments();
                OdiSegment operatorSegment = trigger.getOperatorSegment();

                switch(odiType)
                {
                    case Trigger.booleanType: triggerBoolean(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.stringType: triggerString(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.integerType: triggerInt(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.containType: triggerContain(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.actionType: triggerAction(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.systemType: triggerSystem(event, leftSegments, rightSegments, operatorSegment); break;
                }
            }
        }
    }

    private void triggerBoolean(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        boolean rightSide;

        if(rightSegments.get(0).getType() == OdiSegment.raw) rightSide = Boolean.parseBoolean(rightSegments.get(0).getValue());
        else rightSide = parseVariable(event, rightSegments.get(0)).getBValue();

        leftSide.setValue(rightSide);
    }

    private void triggerString(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        String rightSide;

        if(rightSegments.get(0).getType() == OdiSegment.raw) rightSide = rightSegments.get(0).getValue();
        else rightSide = parseVariable(event, rightSegments.get(0)).getSValue();

        leftSide.setValue(rightSide);
    }

    private void triggerInt(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        int rightSide = evaluateSide(event, rightSegments);
        int finalValue = leftSide.getIValue();

        switch(operatorSegment.getValue())
        {
            case "=": finalValue = rightSide; break;
            case "+=": finalValue += rightSide; break;
            case "-=": finalValue -= rightSide; break;
            case "*=": finalValue *= rightSide; break;
            case "/=": finalValue /= rightSide; break;
            default: System.out.println("Invalid type of relational operator"); break;
        }

        leftSide.setValue(finalValue);
    }

    private void triggerContain(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }

    private void triggerAction(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }

    private void triggerSystem(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }
}