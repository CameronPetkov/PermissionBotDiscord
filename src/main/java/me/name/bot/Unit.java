package me.name.bot;

public class Unit //JSON format
{
    private String unitCode = null;
    private String fullName = null;
    private String[] abbreviation = null;

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String[] getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String[] abbreviation) {
        this.abbreviation = abbreviation;
    }
}