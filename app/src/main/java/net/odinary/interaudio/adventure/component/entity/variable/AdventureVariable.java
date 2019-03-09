package net.odinary.interaudio.adventure.component.entity.variable;

import net.odinary.interaudio.adventure.component.entity.AbstractEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdventureVariable extends AbstractEntity implements VariableInterface
{
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

    public AdventureVariable(AdventureVariable cloner)
    {
        super(cloner);

        this.varType = cloner.varType;
        this.bValue = cloner.bValue;
        this.iValue = cloner.iValue;
        this.iMax = cloner.iMax;
        this.sValue = cloner.sValue;
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
            case "==": return bValue == var;
            case "!=": return bValue != var;
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
            case "==": return iValue == var;
            case "!=": return iValue != var;
            case "<": return iValue < var;
            case ">": return iValue > var;
            case "<=": return iValue <= var;
            case ">=": return iValue >= var;
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
            case "==": return sValue.equals(var);
            case "!=": return !sValue.equals(var);
            case "[]": return sValue.contains(var);
            default: System.out.println("Invalid type of relational operator"); return false;
        }
    }

    public void setValue(String var) { sValue = var; }

    public String getSValue() { return sValue; }
}