package net.odinary.interaudio.adventure;

import java.util.HashMap;
import java.util.Map;

public class World
{
    private static Map<String, Section> sections = new HashMap<>();
    private static Map<String, AdventureVariable> variables = new HashMap<>();
    private static Section currentSection;

    public static void addSection(Section section) { sections.put(section.getName(), section); }

    public static void addVariable(AdventureVariable variable) { variables.put(variable.getName(), variable); }

    public static String setCurrentSection(String name)
    {
        currentSection = sections.get(name);
        return currentSection.getFilename();
    }

    public static Boolean checkSection(String name) { return sections.containsKey(name); }

    public static Boolean checkVariableExists(String name) { return variables.containsKey(name); }

    public static Section getSection(String name) { return sections.get(name); }

    public static AdventureVariable getVariable(String name) { return variables.get(name); }

    public static Section getCurrentSection() { return currentSection; }
}