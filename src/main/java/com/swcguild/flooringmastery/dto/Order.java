/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author apprentice
 */
public class Order {

    // System Assigned Properties
    private int orderNum;

    // User Assignable Properties
    private LocalDate date;
    private String customerName;
    private String state;
    private double taxRate;
    private String productType;
    private double area;
    private double costPerSqFt;
    private double laborCostPerSqFt;

    public static Order parseOrder(String orderString, String delimiter, DateTimeFormatter customFormat) {

        // split the props by the delimiter.
        String[] tokens = orderString.split(delimiter);

        // create a new order
        Order order = new Order();

        // Set all of the props by index
        order.setOrderNum(Integer.parseInt(tokens[0]));
        order.setDate(LocalDate.parse(tokens[1], customFormat));
        order.setCustomerName(tokens[2]);
        order.setState(tokens[3]);
        order.setTaxRate(Double.parseDouble(tokens[4]));
        order.setProductType(tokens[5]);
        order.setArea(Double.parseDouble(tokens[6]));
        order.setCostPerSqFt(Double.parseDouble(tokens[7]));
        order.setLaborCostPerSqFt(Double.parseDouble(tokens[8]));
        // return the populated order.
        return order;
    }
    
    // 
    @Override
    public String toString() {
        // pretty print this object
        return "Order Number: " + orderNum + "\n"
            + "Date: " + date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) + "\n"
            + "Customer Name: " + customerName + "\n"
            + "State: " + state + "\n"
            + "Tax Rate: " + taxRate + "\n"
            + "Product Type: " + productType + "\n"
            + "Cost Per Square Foot: " + costPerSqFt + "\n"
            + "Labor Cost Per Square Foot: " + laborCostPerSqFt + "\n"
            + "Area: " + area + "\n";
    }
    
    
    // prints the "::" delimited text row version of this object.
        public String serialize(DateTimeFormatter customFormat) {
        return 
            this.orderNum + "::"
            + this.date.format(customFormat) + "::"
            + this.customerName + "::"
            + this.state + "::"
            + this.taxRate + "::"
            + this.productType + "::"
            + this.area + "::"
            + this.costPerSqFt + "::"
            + this.laborCostPerSqFt + "::"
            + this.getMaterialCost() + "::"
            + this.getLaborCost() + "::"
            + this.getTotal() + "::"
            + this.getTax();
    }
    
    // necessary because in the parse method an order method needs to be called 
    public Order() {
    }

    //Constructor
    public Order(int orderNum, LocalDate date, String customerName, String state,
            double taxRate, String productType, double costPerSqFt, double laborCostPerSqFt, double area) {
        this.date = date;
        this.customerName = customerName;
        this.state = state;
        this.taxRate = taxRate;
        this.productType = productType;
        this.costPerSqFt = costPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
        this.area = area;
    }

    //Getters and Setters
    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getCostPerSqFt() {
        return costPerSqFt;
    }

    public void setCostPerSqFt(double costPerSqFt) {
        this.costPerSqFt = costPerSqFt;
    }

    public double getLaborCostPerSqFt() {
        return laborCostPerSqFt;
    }

    public void setLaborCostPerSqFt(double laborCostPerSqFt) {
        this.laborCostPerSqFt = laborCostPerSqFt;
    }

    public double getMaterialCost() {
        return this.area * this.costPerSqFt;
    }

    public double getLaborCost() {
        return this.laborCostPerSqFt * this.area;
    }

    public double getTax() {
        return this.taxRate * this.getTotalBeforeTax();
    }

    public double getTotalBeforeTax() {
        return this.getLaborCost() + this.getMaterialCost();
    }

    public double getTotal() {
        return this.getTax() + this.getTotalBeforeTax();
    }
}
