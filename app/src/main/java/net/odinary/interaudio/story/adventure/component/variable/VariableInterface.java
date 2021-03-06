package net.odinary.interaudio.story.adventure.component.variable;

import net.odinary.interaudio.story.component.Component;

public interface VariableInterface extends Component
{
    String booleanType = "Boolean";
    String numberType = "Number";
    String stringType = "String";

    boolean checkValue(boolean var, String relationalOperator);

    boolean checkValue(double var, String relationalOperator);

    boolean checkValue(String var, String relationalOperator);

    void setValue(boolean var);

    void setValue(double var);

    void setValue(String var);
}