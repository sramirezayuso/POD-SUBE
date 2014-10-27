package ar.edu.itba.pod.mmxivii.sube.common;

public class Operation
{
    private String description;
    private double amount;

    public Operation(String description, double amount)
    {
        this.description = description;
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public double getAmount()
    {
        return amount;
    }
}