package net.odinary.interaudio.adventure.odi;

import net.odinary.interaudio.adventure.Event;
import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.odi.trigger.Trigger;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;

import java.util.List;

public abstract class AbstractOdiHandler implements OdiHandler
{
    protected WorldRepository worldRepository;
    protected PlayerRepository playerRepository;

    public AbstractOdiHandler(WorldRepository worldRepository, PlayerRepository playerRepository)
    {
        this.worldRepository = worldRepository;
        this.playerRepository = playerRepository;
    }

    protected int evaluateSide(Event event, List<OdiSegment> segments)
    {
        int sideValue = 0;
        String arithmeticOperator = "+";

        if(segments.size() > 1)
        {
            for(OdiSegment segment: segments)
            {
                boolean isOperator = false;
                int tmpSide = 0;

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
            if(segments.get(0).getType() == OdiSegment.raw) sideValue = Integer.parseInt(segments.get(0).getValue());
            else sideValue = parseVariable(event, segments.get(0)).getIValue();
        }

        return sideValue;
    }

    protected AdventureVariable parseVariable(Event event, OdiSegment path)
    {
        String varName = path.getVariable();

        switch(path.getScope())
        {
            case OdiSegment.world:
                if(worldRepository.getVariables().containsKey(varName)) return (AdventureVariable) worldRepository.getVariable(varName);
                break;
            case OdiSegment.player:
                if(playerRepository.getVariables().containsKey(varName)) return (AdventureVariable) playerRepository.getVariable(varName);
                break;
            case OdiSegment.target:
                Entity target = event.getTarget();
                if(target.checkVariableExists(varName)) return target.getVariable(varName);
                break;
            case OdiSegment.secondaryTarget:
                Entity secondaryTarget = event.getSecondaryTarget();
                if(secondaryTarget.checkVariableExists(varName)) return secondaryTarget.getVariable(varName);
                break;
        }

        return null;
    }
}