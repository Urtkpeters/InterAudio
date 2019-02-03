package net.odinary.interaudio.adventure.repository;

import net.odinary.interaudio.adventure.component.entity.Section;

import java.util.HashMap;
import java.util.Map;

public class WorldRepository extends AbstractRepository
{
    private Map<String, Section> sections = new HashMap<>();
    private Section currentSection;

    public void addSection(Section section) { sections.put(section.getName(), section); }

    public String setCurrentSection(String name)
    {
        currentSection = sections.get(name);
        return currentSection.getFilename();
    }

    public Section getSection(String name) { return sections.get(name); }

    public Map<String, Section> getSections() { return sections; }

    public Section getCurrentSection() { return currentSection; }
}