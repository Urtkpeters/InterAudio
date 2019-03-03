package net.odinary.interaudio.adventure.odi;

import java.util.List;

public interface OdiSegmentInterface
{
    int empty = 0;
    int variable = 1;
    int operator = 2;
    int raw = 3;

    String worldScope = "world";
    String sections = "sections";
    String current = "current";

    String playerScope = "player";
    String inventory = "inventory";

    String entitiesScope = "entities";
    String objects = "objects";
    String characters = "characters";
    String items = "items";

    String eventScope = "event";
    String actor = "actor";
    String target = "target";
    String secondaryTarget = "secondaryTarget";

    Integer getType();
    List<String> getScopes();
    String getValue();
}
