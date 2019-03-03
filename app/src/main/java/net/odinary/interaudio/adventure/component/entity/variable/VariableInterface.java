package net.odinary.interaudio.adventure.component.entity.variable;

import net.odinary.interaudio.adventure.component.Component;

public interface VariableInterface extends Component
{
    String booleanType = "Boolean";
    String integerType = "Integer";
    String stringType = "String";

    boolean checkValue(boolean var, String relationalOperator);

    boolean checkValue(int var, String relationalOperator);

    boolean checkValue(String var, String relationalOperator);
}