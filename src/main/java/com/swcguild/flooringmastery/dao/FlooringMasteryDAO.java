/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.dao;

import com.swcguild.flooringmastery.dto.Order;
import com.swcguild.flooringmastery.dto.Product;
import com.swcguild.flooringmastery.dto.StateTax;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 *
 * @author apprentice
 */
public interface FlooringMasteryDAO {

    // Reads a 'config.txt' from the execution directory.
    // Parses it -> returns IsTestMode value
    public boolean readIsTestMode() throws IOException;

    // not necessarily needed.. see init above.
    public void addOrder(Order orderToAdd);
    public void removeOrder(Order order);


    public Order getOrder(int orderNo, LocalDate date);
    public Collection<Order> getOrders(LocalDate date);


    public void saveOrders(DateTimeFormatter customFormat) throws IOException;
    void saveOrder(Order order, DateTimeFormatter customFormat) throws IOException;
    
    public Collection<StateTax> readTaxes() throws IOException;    
    public Collection<Product> readProducts() throws IOException;

}