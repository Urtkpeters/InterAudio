package net.odinary.interaudio.story.adventure.odi;

import net.odinary.interaudio.story.adventure.Event;
import net.odinary.interaudio.story.adventure.component.entity.Action;
import net.odinary.interaudio.story.adventure.component.entity.Entity;
import net.odinary.interaudio.story.adventure.component.entity.Section;
import net.odinary.interaudio.story.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.story.adventure.odi.trigger.Trigger;
import net.odinary.interaudio.story.adventure.repository.PlayerRepository;
import net.odinary.interaudio.story.adventure.repository.WorldRepository;

import java.util.List;

public abstract class AbstractOdiHandler implements OdiHandler
{
    public static final String add = "+";
    public static final String subtract = "-";
    public static final String multiply = "*";
    public static final String divide = "/";

    protected WorldRepository worldRepository;
    protected PlayerRepository playerRepository;

    public AbstractOdiHandler(WorldRepository worldRepository, PlayerRepository playerRepository)
    {
        this.worldRepository = worldRepository;
        this.playerRepository = playerRepository;
    }

    protected double evaluateSide(Event event, List<OdiSegment> segments)
    {
        double sideValue = 0;
        String arithmeticOperator = "+";

        if(segments.size() > 1)
        {
            for(OdiSegment segment: segments)
            {
                boolean isOperator = false;
                double tmpSide = 0;

                if(segment.getType() == OdiSegment.raw)
                {
                    String tmpVal = segment.getValue();

                    if(tmpVal.contains("+") && tmpVal.contains("-") && tmpVal.contains("*") && tmpVal.contains("/"))
                    {
                        isOperator = true;
                        arithmeticOperator = tmpVal;
                    }
                    else tmpSide = Integer.parseInt(tmpVal);
                }
                else tmpSide = parseVariable(event, segment).getNValue();

                if(!isOperator)
                {
                    switch(arithmeticOperator)
                    {
                        case add: sideValue += tmpSide; break;
                        case subtract: sideValue -= tmpSide; break;
                        case multiply: sideValue *= tmpSide; break;
                        case divide: if(tmpSide != 0) sideValue /= tmpSide; break;
                        default: System.out.println("Invalid type of operator segment"); break;
                    }
                }
            }
        }
        else if(segments.size() > 0)
        {
            if(segments.get(0).getType() == OdiSegment.raw) sideValue = Integer.parseInt(segments.get(0).getValue());
            else sideValue = parseVariable(event, segments.get(0)).getNValue();
        }

        return sideValue;
    }

    protected AdventureVariable parseVariable(Event event, OdiSegment segment)
    {
        List<String> scopes = segment.getScopes();

        switch(scopes.get(0))
        {
            case OdiSegment.worldScope:
                if(scopes.get(1).equals(OdiSegment.sections))
                {
                    Section section;

                    if(scopes.get(2).equals(OdiSegment.current))
                    {
                        section = event.getSection();
                    }
                    else
                    {
                        section = worldRepository.getSection(scopes.get(2));
                    }

                    Entity entity = section.getEntity(scopes.get(4));

                    if(entity != null)
                    {
                        return entity.getVariable(scopes.get(5));
                    }
                }
                else
                {
                    String varName = scopes.get(1);

                    if(worldRepository.getVariables().containsKey(varName)) return (AdventureVariable) worldRepository.getVariable(varName);
                }

                break;
            case OdiSegment.playerScope:
                String playerScope = scopes.get(1);

                if(playerScope.equals(OdiSegment.inventory))
                {
                    Entity item = playerRepository.checkInventory(scopes.get(2));

                    if(item != null)
                    {
                        return item.getVariable(scopes.get(3));
                    }
                }
                else
                {
                    return (AdventureVariable) playerRepository.getVariable(scopes.get(2));
                }

                break;
            case OdiSegment.eventScope:
                switch(scopes.get(1))
                {
                    case OdiSegment.actor:
                        String actorVarName = scopes.get(2);
                        Entity actor = event.getActor();
                        if(actor.checkVariableExists(actorVarName)) return actor.getVariable(actorVarName);
                        break;
                    case OdiSegment.target:
                        String targetVarName = scopes.get(2);
                        Entity target = event.getTarget();
                        if(target.checkVariableExists(targetVarName)) return target.getVariable(targetVarName);
                        break;
                    case OdiSegment.secondaryTarget:
                        String secTargetVarName = scopes.get(2);
                        Entity secondaryTarget = event.getSecondaryTarget();
                        if(secondaryTarget.checkVariableExists(secTargetVarName)) return secondaryTarget.getVariable(secTargetVarName);
                        break;
                }

                break;
        }

        return null;
    }
}