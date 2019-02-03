package net.odinary.interaudio.adventure.component.entity.variable;

import net.odinary.interaudio.adventure.component.ComponentInterface;

public interface VariableInterface extends ComponentInterface
{
    String booleanType = "Boolean";
    String integerType = "Integer";
    String stringType = "String";

    boolean checkValue(boolean var, String relationalOperator);

    boolean checkValue(int var, String relationalOperator);

    boolean checkValue(String var, String relationalOperator);
}