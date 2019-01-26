package net.odinary.interaudio.adventure;

import java.util.HashMap;
import java.util.Map;

public class World
{
    private static Map<String, Section> sections = new HashMap<>();
    private static Section currentSection;

    public static void addSection(Section section) { sections.put(section.getName(), section); }

    public static Section getSection(String name) { return sections.get(name); }

    public static Section getCurrentSection() { return currentSection; }

    public static void setCurrentSection(String name) { currentSection = sections.get(name); }

    public static Boolean checkSection(String name) { return sections.containsKey(name); }
}