package net.odinary.interaudio.story.adventure.odi;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOdi implements Odi
{
    protected String type;
    protected String operator;
    protected OdiSegment operatorSegment;
    private List<OdiSegment> leftSegments = new ArrayList<>();
    private List<OdiSegment> rightSegments = new ArrayList<>();

    public AbstractOdi(String type, String odi, String operator)
    {
        this.type = type;
        this.operator = operator;

        boolean passedOperator = false;

        List<String> segments = parseNotation(odi, " ");

        for(int i = 0; i < segments.size(); i++)
        {
            OdiSegment tmpSeg = new OdiSegment(parseNotation(segments.get(i), "."));

            if(tmpSeg.getType() == OdiSegmentInterface.operator)
            {
                operatorSegment = tmpSeg;
                passedOperator = true;
            }
            else if(!passedOperator)
            {
                leftSegments.add(tmpSeg);
            }
            else
            {
                rightSegments.add(tmpSeg);
            }
        }
    }

    private List<String> parseNotation(String input, String character)
    {
        List<String> list = new ArrayList<>();

        // The plus one is to account for the additional segment after the end of the last character
        int countPath = (input.length() - input.replace(character, "").length()) + 1;

        for(int i = 0; i < countPath; i++)
        {
            int dotIndex = input.indexOf(character);

            if(dotIndex != -1)
            {
                list.add(input.substring(0, dotIndex));
                input = input.substring(dotIndex + 1, input.length());
            }
            else
            {
                list.add(input);
            }
        }

        return list;
    }

    public List<OdiSegment> getLeftSegments() { return leftSegments; }

    public List<OdiSegment> getRightSegments() { return rightSegments; }

    public OdiSegment getOperatorSegment() { return operatorSegment; }

    public String getType() { return type; }

    public String getOperator() { return operator; }
}
