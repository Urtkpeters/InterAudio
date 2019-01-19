package net.odinary.interaudio.adventure;

class Event
{
    private Action action;
    private String secondaryAction;
    private Entity target;
    private Entity secondaryTarget;
    private Section section;

    Event(Section currentSection) { section = currentSection; }

    public void setAction(Action action) { this.action = action; }

    public void setSecondaryAction(String secondaryAction) { this.secondaryAction = secondaryAction; }

    public void setTarget(Entity target) { this.target = target; }

    public void setSecondaryTarget(Entity secondaryTarget) { this.secondaryTarget = secondaryTarget; }

    public void setSection(Section section) { this.section = section; }

    public Action getAction() { return action; }

    public String getSecondaryAction() { return secondaryAction; }

    public Entity getTarget() { return target; }

    public Entity getSecondaryTarget() { return secondaryTarget; }

    public Section getSection() { return section; }
}