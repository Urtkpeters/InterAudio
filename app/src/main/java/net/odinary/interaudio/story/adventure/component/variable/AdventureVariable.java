package net.odinary.interaudio.story.adventure.component.variable;

import net.odinary.interaudio.story.component.AbstractComponent;

import org.json.JSONException;
import org.json.JSONObject;

public class AdventureVariable extends AbstractComponent implements VariableInterface
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
    protected double nValue;
    protected double nMax;
    protected String sValue;

    public AdventureVariable(JSONObject variable) throws JSONException
    {
        super(variable, "adventureVariable");

        varType = variable.getString("type");

        switch(varType)
        {
            case booleanType: parseBoolean(variable); break;
            case numberType: parseDouble(variable); break;
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
            case numberType: nValue = 0; break;
            case stringType: sValue = ""; break;
        }
    }

    public AdventureVariable(AdventureVariable cloner, JSONObject override) throws JSONException
    {
        super(cloner);

        this.varType = cloner.varType;
        this.bValue = cloner.bValue;
        this.nValue = cloner.nValue;
        this.nMax = cloner.nMax;
        this.sValue = cloner.sValue;

        switch(varType)
        {
            case booleanType:
                if(override.has("value")) bValue = override.getBoolean("value");
                break;
            case numberType:
                if(override.has("max")) nMax = override.getInt("max");
                if(override.has("value"))
                {
                    double val = override.getDouble("value");

                    if(val == -2) val = nMax;

                    nValue = val;
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
            case numberType: return ((Double) nValue).toString();
            case stringType: return sValue;
            default: return null;
        }
    }

    public String getVarType() { return varType; }

    // ------------------------------------------------------
    // Boolean Section

    private void parseBoolean(JSONObject variable) throws JSONException
    {
        bValue = variable.getBoolean("value");
    }

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
    // Number Section

    private void parseDouble(JSONObject variable) throws JSONException
    {
        nMax = variable.getInt("max");

        if(variable.getInt("value") == -2) nValue = nMax;
        else nValue = variable.getInt("value");
    }

    public boolean checkValue(double var, String relationalOperator)
    {
        switch(relationalOperator)
        {
            case equals: return nValue == var;
            case notEquals: return nValue != var;
            case lessThan: return nValue < var;
            case lessThanOrEquals: return nValue <= var;
            case greaterThan: return nValue > var;
            case greaterThanOrEquals: return nValue >= var;
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    public void setValue(double var)
    {
        // The -1 stands for unlimited uses
        if(nMax != -1 && nMax < var) nValue = nMax;
        else nValue = var;
    }

    public double getNValue() { return nValue; }

    public void setMax(double var) { nValue = var; }

    public double getMax() { return nMax; }

    // ------------------------------------------------------
    // String section

    private void parseString(JSONObject variable) throws JSONException
    {
        sValue = variable.getString("value");
    }

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