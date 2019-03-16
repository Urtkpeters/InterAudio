package net.odinary.interaudio.story.adventure.odi;

import java.util.List;

public class OdiSegment implements OdiSegmentInterface
{
    protected int type = empty;
    protected List<String> scopes;
    protected String value;

    public OdiSegment(List<String> segment)
    {
        int segmentSize = segment.size();

        if(segmentSize > 0)
        {
            String firstPiece = segment.get(0);

            if(firstPiece.equals("variable"))
            {
                type = variable;

                for(int i = 1; i < segment.size() - 2; i++)
                {
                    scopes.add(segment.get(i));
                }

                // Have to minus one because because lists start with zero numbering
                value = segment.get(segmentSize - 1);
            }
            else if(firstPiece.contains("=") || firstPiece.contains(">") || firstPiece.contains("<") || firstPiece.contains("{") || firstPiece.contains("[") || firstPiece.contains("+") || firstPiece.contains("-"))
            {
                type = operator;

                value = firstPiece;
            }
            else
            {
                type = raw;

                value = firstPiece;
            }
        }
    }

    public Integer getType() { return type; }

    public List<String> getScopes() { return scopes; }

    public String getValue() { return value; }
}
