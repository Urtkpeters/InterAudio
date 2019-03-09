package net.odinary.interaudio.adventure.component.entity.variable;

import net.odinary.interaudio.adventure.component.entity.AbstractEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdventureVariable extends AbstractEntity implements VariableInterface
{
    public static final String equals = "==";
    public static final String notEquals = "!=";
    public static final String lessThan = "<";
    public static final String lessThanOrEquals = "<=";
    public static final String greaterThan = ">";
    public static final String greaterThanOrEquals = ">=";
    public static final String contains = "[]";

    protected String varType;
    protected Boolean bValue;
    protected int iValue;
    protected int iMax;
    protected String sValue;

    public AdventureVariable(JSONObject variable) throws JSONException
    {
        super(variable, "adventureVariable");

        varType = variable.getString("type");

        switch(varType)
        {
            case booleanType: parseBoolean(variable); break;
            case integerType: parseInt(variable); break;
            case stringType: parseString(variable); break;
        }
    }

    public AdventureVariable(String name, String filename, String varType)
    {
        super(name, filename, "adventureVariable");

        this.varType = varType;

        switch(varType)
        {
            case booleanType: bValue = false; break;
            case integerType: iValue = 0; break;
            case stringType: sValue = ""; break;
        }
    }

    public AdventureVariable(AdventureVariable cloner, JSONObject override) throws JSONException
    {
        super(cloner);

        this.varType = cloner.varType;
        this.bValue = cloner.bValue;
        this.iValue = cloner.iValue;
        this.iMax = cloner.iMax;
        this.sValue = cloner.sValue;

        switch(varType)
        {
            case booleanType:
                if(override.has("value")) bValue = override.getBoolean("value");
                break;
            case integerType:
                if(override.has("max")) iMax = override.getInt("max");
                if(override.has("value"))
                {
                    int val = override.getInt("value");

                    if(val == -2) val = iMax;

                    iValue = val;
                }
                break;
            case stringType:
                if(override.has("value")) sValue = override.getString("value");
                break;
        }
    }

    public String toString()
    {
        switch(varType)
        {
            case booleanType: return bValue.toString();
            case integerType: return ((Integer) iValue).toString();
            case stringType: return sValue;
            default: return null;
        }
    }

    private void parseBoolean(JSONObject variable) throws JSONException
    {
        bValue = variable.getBoolean("value");
    }

    private void parseInt(JSONObject variable) throws JSONException
    {
        iMax = variable.getInt("max");

        if(variable.getInt("value") == -2) iValue = iMax;
        else iValue = variable.getInt("value");
    }

    private void parseString(JSONObject variable) throws JSONException
    {
        sValue = variable.getString("value");
    }

    public String getVarType() { return varType; }

    // ------------------------------------------------------
    // Boolean Section

    public boolean checkValue(boolean var, String relationalOperator)
    {
        switch(relationalOperator)
        {
            case equals: return bValue == var;
            case notEquals: return bValue != var;
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    public void setValue(boolean var) { bValue = var; }

    public Boolean getBValue() { return bValue; }

    // ------------------------------------------------------
    // Integer Section

    public boolean checkValue(int var, String relationalOperator)
    {
        switch(relationalOperator)
        {
            case equals: return iValue == var;
            case notEquals: return iValue != var;
            case lessThan: return iValue < var;
            case lessThanOrEquals: return iValue <= var;
            case greaterThan: return iValue > var;
            case greaterThanOrEquals: return iValue >= var;
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    public void setValue(int var) { iValue = var; }

    public void setMax(int var) { iValue = var; }

    public int getMax() { return iMax; }

    public Boolean checkMax(int var)
    {
        return false;
    }

    public int getIValue() { return iValue; }

    // ------------------------------------------------------
    // String section

    public boolean checkValue(String var, String relationalOperator)
    {
        switch(relationalOperator)
        {
            case equals: return sValue.equals(var);
            case notEquals: return !sValue.equals(var);
            case contains: return sValue.contains(var);
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    public void setValue(String var) { sValue = var; }

    public String getSValue() { return sValue; }
}