/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.dto;

/**
 *
 * @author apprentice
 */
public class StateTax {
    String State;
    Double TaxRate;

    public StateTax() {
    }

    public StateTax(String State, Double TaxRate) {
        this.State = State;
        this.TaxRate = TaxRate;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public Double getTaxRate() {
        return TaxRate;
    }

    public void setTaxRate(Double TaxRate) {
        this.TaxRate = TaxRate;
    }
            
    public String serialize() {
        return
            this.State  + "," +
            this.TaxRate;
    }
    
    public static StateTax parseStateTax(String stateTaxCsvRow ) {
        String[] tokens = stateTaxCsvRow.split(",");
        return new StateTax(tokens[0], Double.parseDouble(tokens[1]));

    }
}