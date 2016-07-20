/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.controller;

import com.swcguild.flooringmastery.consoleio.ConsoleIO;
import com.swcguild.flooringmastery.dao.FlooringMasteryDAO;
import com.swcguild.flooringmastery.dao.FlooringMasteryDAOImpl;
import com.swcguild.flooringmastery.dto.Order;
import com.swcguild.flooringmastery.dto.Product;
import com.swcguild.flooringmastery.dto.StateTax;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author apprentice
 */
public class FlooringMasteryController {

    // class uses this for io operations.
    ConsoleIO console;

    // DAO
    FlooringMasteryDAO daoLayer;

    DateTimeFormatter customFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    List<Product> products = new ArrayList();

    List<StateTax> taxes = new ArrayList();

    // Default Constructor
    public FlooringMasteryController() throws IOException {
        // Instanciate the console
        this.console = new ConsoleIO();
        
        this.daoLayer = new FlooringMasteryDAOImpl(customFormat);
        try {
            // Setup the save format and mode.
            this.products = new ArrayList(daoLayer.readProducts());
            this.taxes = new ArrayList(daoLayer.readTaxes());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    // Menu printing and selection loop.
    public void run() throws IOException {

        boolean keepRunning = true;
        int userChoice;

        while (keepRunning) {
            printMenu();
            userChoice = console.readInt("Please select a choice: \n");
            switch (userChoice) {
                case 1: // Display Orders
                    displayOrders();
                    break;
                case 2: // add an order
                    addOrder();
                    break;
                case 3: // edit order
                    editOrder();
                    break;
                case 4: // remove order
                    removeOrder();
                    break;
                case 5: // save current work
                    saveCurrentWork();
                    break;
                case 6: // Quit
                    console.print("Thank you for using Flooring Mastery... happy flooring!");
                    keepRunning = false;
                    break;
                default:
                    console.print("Please use a valid selection.");
            }
        }
    }

    // Print menu options
    public void printMenu() {
        console.print("1. Display Orders");
        console.print("2. Add an Order");
        console.print("3. Edit an Order");
        console.print("4. Remove an Order");
        console.print("5. Save Current Work");
        console.print("6. Quit");
        console.print("");
    }

    // Converts numeric selections into their product proper name.
    private String cleanProductInput(String selection) {

        // store a local variable to use as the return value
        // after it has been cleaned ( if needed )
        String cleanProduct = selection;

        // handle number input
        switch (selection) {
            case "1":
                cleanProduct = "Carpet";
                break;

            case "2":
                cleanProduct = "Laminate";
                break;

            case "3":
                cleanProduct = "Tile";
                break;

            case "4":
                cleanProduct = "Wood";
                break;
        }

        return cleanProduct;
    }

    // Adds an order to the data file / map
    public void addOrder() {
        // give the order number an initial value
        int orderNum = 0;

        // prompt for the name
        String customerName = console.readString("Please enter your name: ");

        // StateTax info object.
        StateTax stateTax = null;

        // give the running flag
        boolean keepRunning = true;
        while (keepRunning) {

            // prompt for the state
            String stateStr = console.readString("Please enter your State: ").toUpperCase();

            // filtered to only the state with the same abbreviation
            List<StateTax> filteredStateTax = this.taxes.stream().filter(s -> {
                return s.getState().equalsIgnoreCase(stateStr);
            }).collect(Collectors.toList());

            // do we have a single result?
            if (filteredStateTax.size() == 1) {

                // we have a match, no need to keep looping.
                keepRunning = false;

                // get the single result
                stateTax = filteredStateTax.get(0);

            } else {
                // no match, try again
                console.print("Please choose a valid State Abbreviation.");
            }
        }

        // Product info object.
        Product product = null;

        // set the keep running flag = true
        keepRunning = true;
        while (keepRunning) {

            // prompt for the product type
            String productType = console.readString("What type of product would you like? "
                    + "\n1. Carpet \n2. Laminate \n3. Tile \n4. Wood \n\n");

            // cleanse numeric selections, just in case
            String cleanProduct = cleanProductInput(productType);

            // filtered to only the product with the same name
            List<Product> filteredProducts = this.products.stream().filter(p -> {
                return p.getProductType().equalsIgnoreCase(cleanProduct);
            }).collect(Collectors.toList());

            // do we have a single result?
            if (filteredProducts.size() == 1) {

                // we have a match, no need to keep looping.
                keepRunning = false;

                // get the single result
                product = filteredProducts.get(0);

            } else {
                // no match, try again
                console.print("Please choose a valid State Abbreviation.");
            }
        }

        // read the area
        double area = console.readDouble("What is the area of the space?\n");

        // validate the input
        while (area == -1) {
            area = console.readDouble("Invalid area, try again... What is the area of the space?", true);
        }

        // Create the new order.
        Order newOrder = new Order(
                orderNum,
                LocalDate.now(),
                customerName,
                stateTax.getState(),
                stateTax.getTaxRate(),
                product.getProductType(),
                product.getCostPerSquareFoot(),
                product.getLaborCostPerSquareFoot(),
                area
        );

        // pretty print the data to the user.       
        console.print(newOrder.toString());

        // have them verify it all looks good.
        int commitChoice = console.readInt("Do you wish to commit this item?\n1. Yes\n2. No\n", 1, 2);

        // If they chose yes. commit, otherwise discard
        if (commitChoice == 1) {
            // Yes
            daoLayer.addOrder(newOrder);
            console.print("Your order has been successfully committed!\n\n");
        } else {
            // No
            console.print("Order Discarded.\n");
        }
    }

    public void removeOrder() throws IOException {

        // store the order number
        int orderNum;

        // store the order date
        String date;

        // store date properly.
        LocalDate dateFormatted;

        // Loop until they get it right ( yes this could be a user trap )
        boolean continueLooping = true;
        while (continueLooping) {

            // Prompt for the order number
            orderNum = console.readInt("What is the order number for your order?\n");

            // Prompt for the order date
            date = console.readString("What is the order date? Use the following format: " + LocalDate.now().format(this.customFormat) + "\n");

            // Format the date properly.
            dateFormatted = LocalDate.parse(date, customFormat);

            // Ensure the order is resolved.
            Order order = daoLayer.getOrder(orderNum, dateFormatted);

            // ensure an order was found, and that the dates match what the user provided
            if (order != null && order.getDate().format(customFormat).equals(dateFormatted.format(customFormat))) {

                // break out of the loop after this cycle.
                continueLooping = false;

                // Confirm the removal.
                String confirm = console.readString("Are you sure you want to remove this order?\n1. Yes\n2. No\n");

                switch (confirm.toLowerCase()) {
                    case "1":
                    case "yes":
                        // Choice = YES
                        daoLayer.removeOrder(order);
                        console.print("Your order has been successfully deleted.\n");
                        break;

                    case "2":
                    case "no":
                        // Choice = NO
                        console.print("Your order has not been deleted.\n");
                }
            } else {
                console.print("Unable to find your order... Please Try again.\n");
            }
        }
    }

    // display Orders
    public void displayOrders() {

        // loop flag
        boolean keepProcessing = true;

        // will hold a valid local date
        LocalDate localDate = null;

        // loop
        while (keepProcessing) {

            // prompt and get the user to give a date
            String dateStr = console.readString("Provide the order date to search, in the following format: " + LocalDate.now().format(this.customFormat) + "\n(blank for today)");

            // allow lazy entries to default to today
            if (dateStr.isEmpty()) {

                // default
                localDate = LocalDate.now();
            } else {

                // try to parse the users string
                localDate = LocalDate.parse(dateStr, this.customFormat);
            }

            // Should we end the loop?
            if (localDate != null) {

                // break out of the loop
                keepProcessing = false;
            } else {

                // fail
                console.print("Invalid date, try again...");
            }
        }

        // Get all orders.
        Collection<Order> orders = daoLayer.getOrders(localDate);

        // Loop over each.
        for (Order order : orders) {

            // We like nice formatting.
            console.print("\n########################\n");

            // pretty print the value
            console.print(order.toString());

            // We like nice formatting... cont'd
            console.print("\n########################\n");

        }
    }

    // Edit an Order
    private void editOrder() throws IOException {
        
        String dateStr = console.readString("Input the Order date in the following format: " + LocalDate.now().format(customFormat));
        LocalDate localDate = LocalDate.parse(dateStr, this.customFormat);        
        
        // read the order number        
        int orderNo = console.readInt("Input the Order number:");

        // retrieve the order by its number
        Order order = daoLayer.getOrder(orderNo, localDate);
             
        // was an order found?
        if (order != null) {
            
            // Validate the date.
            if (order.getDate().equals(localDate)) {

                // The date is valid... 
                // pretty print the order, before we begin editing
                console.print(order.toString());

                // DATE
                // Date
                String newDate = console.readString("The 'Date' is " + order.getDate().format(this.customFormat) + ", now enter a new value:");
                // Set Date
                if (!newDate.isEmpty()) {
                    order.setDate(LocalDate.parse(newDate, this.customFormat));
                }

                // CUSTOMER NAME        
                // Get Customer Name
                String custName = console.readString("The 'Customer Name' is " + order.getCustomerName() + ", now enter a new value:");
                // Set Customer Name
                if (!custName.isEmpty()) {
                    order.setCustomerName(custName);
                }

                // STATE
                // State.
                String state = console.readString("The 'State' is " + order.getState() + ", now enter a new value:");
                // Set State.
                if (!state.isEmpty()) {
                    order.setState(state);
                }

                // TAX RATE
                // Tax Rate.
                Double taxRate = console.readDouble("The 'Tax Rate' is " + Double.toString(order.getTaxRate()) + ", now enter a new value:", true);
                // Set Tax Rate.
                if (taxRate != -1) // -1 = Blank -> No Change
                {
                    order.setTaxRate(taxRate);
                }

                // PRODUCT TYPE
                // Product Type.
                String productType = console.readString("The 'Product Type' is " + order.getProductType() + ", now enter a new value:");
                // Set Product Type.
                if (!productType.isEmpty()) {
                    order.setProductType(productType);
                }

                // AREA
                // Get Area.
                Double area = console.readDouble("The 'Area' is " + order.getArea() + ", now enter a new value:", true);
                // Set Area.
                if (area != -1) // -1 = Blank -> No Change
                {
                    order.setArea(area);
                }

                // COST PER SQ FT 
                // Get Cost Per Sq. Ft.
                Double cpsf = console.readDouble("The 'Cost Per Sq. Ft.' is " + order.getCostPerSqFt() + ", now enter a new value:", true);
                // Set Cost Per Sq. Ft.
                if (cpsf != -1) // -1 = Blank -> No Change
                {
                    order.setCostPerSqFt(cpsf);
                }

                // LABOR COST PSQ
                // Labor Cost PSQ.
                Double laborCostPSQ = console.readDouble("The 'Labor Cost PSQ' is " + Double.toString(order.getLaborCostPerSqFt()) + ", now enter a new value:", true);
                // Set Labor Cost PSQ.
                if (laborCostPSQ != -1) // -1 = Blank -> No Change
                {
                    order.setLaborCostPerSqFt(laborCostPSQ);
                }

                // Save changes
                daoLayer.saveOrder(order, this.customFormat);

                // print the success
                console.print("Order has been Updated:\n" + order.toString() + "\n");
            }
        } else {
            console.print("\nOrder not found.\n\n");
        }
    }

    private void saveCurrentWork() throws IOException {
        daoLayer.saveOrders(this.customFormat);
        console.print("\n\n---- Orders have been successfully saved ----\n\n");
    }
}
