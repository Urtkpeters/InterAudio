package net.odinary.interaudio.adventure;

import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.component.entity.Section;

public class Event
{
    public static final int playerEvent = 0;
    public static final int characterEvent = 1;
    public static final int timeEvent = 2;

    private int type;
    private Entity actor;
    private Action action;
    private String secondaryAction;
    private Entity target;
    private Entity secondaryTarget;
    private Section section;

    public Event(int type) { this.type = type; }

    public Event(Event cloner)
    {
        this.actor = cloner.actor;
        this.action = cloner.action;
        this.secondaryAction = cloner.secondaryAction;
        this.target = cloner.target;
        this.secondaryTarget = cloner.secondaryTarget;
        this.section = cloner.section;
    }

    public void setActor(Entity actor) { this.actor = actor; }

    public void setAction(Action action) { this.action = action; }

    public void setSecondaryAction(String secondaryAction) { this.secondaryAction = secondaryAction; }

    public void setTarget(Entity target) { this.target = target; }

    public void setSecondaryTarget(Entity secondaryTarget) { this.secondaryTarget = secondaryTarget; }

    public void setSection(Section section) { this.section = section; }

    public int getType() { return type; }

    public Entity getActor() { return actor; }

    public Action getAction() { return action; }

    public String getSecondaryAction() { return secondaryAction; }

    public Entity getTarget() { return target; }

    public Entity getSecondaryTarget() { return secondaryTarget; }

    public Section getSection() { return section; }
}