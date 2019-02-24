package net.odinary.interaudio.adventure.odi;

public interface OdiSegmentInterface
{
    int empty = 0;
    int variable = 1;
    int operator = 2;
    int raw = 3;

    String player = "player";
    String world = "world";
    String sections = "sections";
    String target = "target";
    String secondaryTarget = "secondaryTarget";
    String inventory = "inventory";
    String entities = "entities";

    Integer getType();
    String getScope();
    String getVariable();
    String getValue();
}
