package net.odinary.interaudio.adventure.odi.trigger;

import net.odinary.interaudio.adventure.Event;
import net.odinary.interaudio.adventure.component.Component;
import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.component.entity.Section;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.odi.AbstractOdiHandler;
import net.odinary.interaudio.adventure.odi.OdiSegment;
import net.odinary.interaudio.adventure.repository.EntityRepository;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TriggerHandler extends AbstractOdiHandler
{
    private EntityRepository entityRepository;

    public TriggerHandler(WorldRepository worldRepository, PlayerRepository playerRepository, EntityRepository entityRepository)
    {
        super(worldRepository, playerRepository);

        this.entityRepository = entityRepository;
    }

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
        // Even if there are more than one segment only get the first as there should only ever be one
        OdiSegment rightSegment = rightSegments.get(0);
        Entity rightEntity = null;

        switch(rightSegment.getScope())
        {
            case OdiSegment.entities:
                rightEntity = entityRepository.getEntity(rightSegment.getVariable(), rightSegment.getValue());
                break;
            case OdiSegment.target:
                rightEntity = event.getTarget();
                break;
            case OdiSegment.secondaryTarget:
                rightEntity = event.getSecondaryTarget();
                break;
        }

        if(rightEntity != null)
        {
            // Same as the right segment above
            OdiSegment leftSegment = leftSegments.get(0);
            String leftScope = leftSegment.getScope();
            String operator = operatorSegment.getValue();

            if(leftScope.equals(OdiSegment.sections))
            {
                String varName = leftSegment.getVariable();
                Section section = null;

                if(varName.equals("current")) section = event.getSection();
                else if(worldRepository.getSections().containsKey(varName)) section = worldRepository.getSection(varName);

                if(section != null)
                {
                    if(operator.equals("++")) section.addEntity(rightEntity);
                    else if(operator.equals("--")) section.removeEntity(rightEntity.getName());
                }
            }
            else if(leftScope.equals(OdiSegment.inventory))
            {
                if(operator.equals("++")) playerRepository.addToInventory(rightEntity);
                else if(operator.equals("--")) playerRepository.removeFromInventory(rightEntity.getName());
            }
        }
    }

    private void triggerAction(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }

    private void triggerSystem(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }
}