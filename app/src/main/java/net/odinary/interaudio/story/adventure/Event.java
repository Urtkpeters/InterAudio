package net.odinary.interaudio.story.adventure;

import net.odinary.interaudio.story.adventure.component.AdventureAction;
import net.odinary.interaudio.story.adventure.component.Entity;
import net.odinary.interaudio.story.adventure.component.Section;

public class Event
{
    public static final int playerEvent = 0;
    public static final int characterEvent = 1;
    public static final int timeEvent = 2;

    private int type;
    private Entity actor;
    private AdventureAction adventureAction;
    private String secondaryAction;
    private Entity target;
    private Entity secondaryTarget;
    private Section section;

    public Event(int type) { this.type = type; }

    public Event(Event cloner)
    {
        this.actor = cloner.actor;
        this.adventureAction = cloner.adventureAction;
        this.secondaryAction = cloner.secondaryAction;
        this.target = cloner.target;
        this.secondaryTarget = cloner.secondaryTarget;
        this.section = cloner.section;
    }

    public void setActor(Entity actor) { this.actor = actor; }

    public void setAdventureAction(AdventureAction adventureAction) { this.adventureAction = adventureAction; }

    public void setSecondaryAction(String secondaryAction) { this.secondaryAction = secondaryAction; }

    public void setTarget(Entity target) { this.target = target; }

    public void setSecondaryTarget(Entity secondaryTarget) { this.secondaryTarget = secondaryTarget; }

    public void setSection(Section section) { this.section = section; }

    public int getType() { return type; }

    public Entity getActor() { return actor; }

    public AdventureAction getAdventureAction() { return adventureAction; }

    public String getSecondaryAction() { return secondaryAction; }

    public Entity getTarget() { return target; }

    public Entity getSecondaryTarget() { return secondaryTarget; }

    public Section getSection() { return section; }
}