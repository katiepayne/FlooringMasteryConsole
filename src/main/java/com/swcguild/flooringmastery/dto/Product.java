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
public class Product {
    String ProductType;
    Double CostPerSquareFoot;
    Double LaborCostPerSquareFoot;

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String ProductType) {
        this.ProductType = ProductType;
    }

    public Double getCostPerSquareFoot() {
        return CostPerSquareFoot;
    }

    public void setCostPerSquareFoot(Double CostPerSquareFoot) {
        this.CostPerSquareFoot = CostPerSquareFoot;
    }

    public Double getLaborCostPerSquareFoot() {
        return LaborCostPerSquareFoot;
    }

    public void setLaborCostPerSquareFoot(Double LaborCostPerSquareFoot) {
        this.LaborCostPerSquareFoot = LaborCostPerSquareFoot;
    }

    public Product() {
    }
       
    public Product(String ProductType, Double CostPerSquareFoot, Double LaborCostPerSquareFoot) {
        this.ProductType = ProductType;
        this.CostPerSquareFoot = CostPerSquareFoot;
        this.LaborCostPerSquareFoot = LaborCostPerSquareFoot;
    }
    
    public String serialize() {
        return
            this.ProductType + "," +
            this.CostPerSquareFoot  + "," +
            this.LaborCostPerSquareFoot;
    }
    
    public static Product parseProduct( String productCsvRow ) {
        String[] tokens = productCsvRow.split(",");
        return new Product(tokens[0], Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    }
}
