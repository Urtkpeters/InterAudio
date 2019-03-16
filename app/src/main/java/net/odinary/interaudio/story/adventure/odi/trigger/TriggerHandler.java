package net.odinary.interaudio.story.adventure.odi.trigger;

import net.odinary.interaudio.story.adventure.AdventureHandler;
import net.odinary.interaudio.story.adventure.Event;
import net.odinary.interaudio.story.adventure.component.Component;
import net.odinary.interaudio.story.adventure.component.entity.Action;
import net.odinary.interaudio.story.adventure.component.entity.Entity;
import net.odinary.interaudio.story.adventure.component.entity.Section;
import net.odinary.interaudio.story.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.story.adventure.odi.AbstractOdiHandler;
import net.odinary.interaudio.story.adventure.odi.OdiSegment;
import net.odinary.interaudio.story.adventure.repository.EntityRepository;
import net.odinary.interaudio.story.adventure.repository.PlayerRepository;
import net.odinary.interaudio.story.adventure.repository.WorldRepository;

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

    public void runTriggers(AdventureHandler adventureHandler, Event event)
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
                    case Trigger.numberType: triggerNumber(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.containType: triggerContain(event, leftSegments, rightSegments, operatorSegment); break;
                    case Trigger.actionType: triggerAction(adventureHandler, event, leftSegments); break;
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

    private void triggerNumber(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {
        AdventureVariable leftSide = parseVariable(event, leftSegments.get(0));
        double rightSide = evaluateSide(event, rightSegments);
        double finalValue = leftSide.getNValue();

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

        List<String> rightScopes = rightSegment.getScopes();
        String rightFirstScope = rightScopes.get(1);

        if(rightFirstScope.equals(OdiSegment.eventScope))
        {
            if(rightScopes.get(2).equals(OdiSegment.target)) rightEntity = event.getTarget();
            else if(rightScopes.get(2).equals(OdiSegment.secondaryTarget)) rightEntity = event.getSecondaryTarget();
        }
        else if(rightFirstScope.equals(OdiSegment.entitiesScope))
        {
            rightEntity = entityRepository.getEntity(rightScopes.get(2), rightSegment.getValue());
        }

        if(rightEntity != null)
        {
            // Same as the right segment above
            OdiSegment leftSegment = leftSegments.get(0);
            List<String> leftScopes = leftSegment.getScopes();

            String firstLeftScope = leftScopes.get(0);
            String operator = operatorSegment.getValue();

            if(firstLeftScope.equals(OdiSegment.worldScope))
            {
                String sectionName = leftScopes.get(2);
                Section section = null;

                if(sectionName.equals("current")) section = event.getSection();
                else if(worldRepository.getSections().containsKey(sectionName)) section = worldRepository.getSection(sectionName);

                if(section != null)
                {
                    if(operator.equals("++")) section.addEntity(rightEntity);
                    else if(operator.equals("--")) section.removeEntity(rightEntity.getName());
                }
            }
            else if(firstLeftScope.equals(OdiSegment.playerScope))
            {
                if(operator.equals("++")) playerRepository.addToInventory(rightEntity);
                else if(operator.equals("--")) playerRepository.removeFromInventory(rightEntity.getName());
            }
        }
    }

    private void triggerAction(AdventureHandler adventureHandler, Event event, List<OdiSegment> leftSegments)
    {
        Event secondaryEvent = new Event(event);

        secondaryEvent.setAction(playerRepository.getAction(leftSegments.get(0).getScopes().get(2)));

        adventureHandler.performAction(event);
    }

    private void triggerSystem(Event event, List<OdiSegment> leftSegments, List<OdiSegment> rightSegments, OdiSegment operatorSegment)
    {

    }
}