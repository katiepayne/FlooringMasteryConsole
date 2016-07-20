/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.dao;

import com.swcguild.flooringmastery.dto.Order;
import com.swcguild.flooringmastery.dto.Product;
import com.swcguild.flooringmastery.dto.StateTax;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author apprentice
 */
public class FlooringMasteryDAOImpl implements FlooringMasteryDAO {

    // Logbook Map< OrderNum, Order >
    private final List<Order> orderLogbook;

    // Delimiter
    private final String DELIMITER = "::";

    // Current Application Path 
    // ( the base for our needed files ( via relative paths from here ) )
    private final String currentDir = "/home/apprentice/Documents/_repos/katie-sutherland-individual-work/labs/FlooringMastery/";

    private DateTimeFormatter customFormat;

    private boolean testMode;

    public DateTimeFormatter getCustomFormat() {
        return customFormat;
    }

    public int getOrderCount() {
        return this.orderLogbook.size();
    }

    public boolean isTestMode() {
        return testMode;
    }

    // public FlooringMasteryDAOImpl(){};
    // Default Constructor : Allowing the File Read IO Exception to bubble up.
    public FlooringMasteryDAOImpl(DateTimeFormatter customFormat) throws FileNotFoundException, IOException {

        // Instanciate the map
        this.orderLogbook = new ArrayList();

        this.testMode = readIsTestMode();
        this.customFormat = customFormat;

        // call the Load method to get all the orders from the file system.
        loadOrdersFromFile();
    }

    private void loadOrdersFromFile() throws FileNotFoundException {

        // get the current directory ( where the order data files are stored )
        // and nested into the next depth.
        File dir = new File(this.currentDir + "Orders");

        // Store the files in an array.
        File[] files = dir.listFiles();

        // loop over them all
        for (File f : files) {

            // get the filename for comparison
            String fileName = f.getName();

            // define a regular expression:
            // Orders_\\d{8}.txt => Orders_< 8 digits here of date >.txt
            Pattern validOrderFiles = Pattern.compile("Orders_\\d{8}.txt");

            // perform the match
            Matcher orderFile = validOrderFiles.matcher(fileName);

            // was it a match?
            if (orderFile.matches()) {

                // It was, process the file.
                // create a scanner -> BufferReader -> FileReader -> From this.Path
                // aka: read in the data files text
                Scanner sc = new Scanner(new BufferedReader(new FileReader(this.currentDir + "Orders/" + fileName)));

                // reusable variable to hold the current line.
                String currentLine;

                // loop while there are more lines
                while (sc.hasNextLine()) {

                    // get the next line text
                    currentLine = sc.nextLine();

                    // Use the Order class to parse the string.
                    Order order = Order.parseOrder(currentLine, DELIMITER, this.customFormat);

                    // add the order
                    this.orderLogbook.add(order);
                }

                // close the scanner
                sc.close();
            }
        }
    }

    // saves a collection of orders to the file system.
    @Override
    public void saveOrders(DateTimeFormatter customFormat) {

        PrintWriter writer = null;
        try {
            // If this is testmode, no need to really save, so exit early.
            if (testMode) {
                return;
            }
            // store the items in a sorted list
            ArrayList<Order> sortedOrders = new ArrayList(this.orderLogbook);
            // since orders are saved based on their date, sort by date.
            sortedOrders.sort((orderA, orderB) -> orderA.getDate().compareTo(orderB.getDate()));
            // boolean to let us know when the date changes, aka new file.
            Order currentOrder = sortedOrders.get(0);
            // build the orders directory
            File dir = new File(this.currentDir + "Orders");
            // outer scoped filename, as this may change within the while loop.
            String fileName = dir + "/Orders_" + currentOrder.getDate().format(customFormat).replaceAll("-", "") + ".txt";
            // Create an outter scoped PrintWriter -> FileWriter, as this may
            // change within the while loop.
            writer = new PrintWriter(new FileWriter(fileName));
            // loop over the orders ( sorted by date )
            for (Order order : sortedOrders) {

                // Build the same filename as 'fileName' above, but
                // with the current order's date values to see if they match.
                // Also, remove the hyphens from the filename.
                String newFileName = dir + "/Orders_" + order.getDate().format(customFormat).replaceAll("-", "") + ".txt";

                // If the filename has changed, make a NEW writer.
                if (!newFileName.equals(fileName)) {

                    // make a new writer.
                    writer = new PrintWriter(new FileWriter(newFileName));

                    // Update the fileName variable in outter scope.
                    // this way it will be the same as the next item's date
                    // as we are processing items from a new date now.
                    fileName = newFileName;
                }

                // call the overriden to string to format this as a row.
                writer.println(order.serialize(customFormat));

                // flush before you leave the loop
                writer.flush();
            }
            writer.close();
        } catch (IOException ex) {
//            Logger.getLogger(FlooringMasteryDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
        } finally {
            writer.close();
        }
    }

    // saves a single order
    @Override
    public void saveOrder(Order order, DateTimeFormatter customFormat) throws IOException {

        // If this is testmode, no need to really save, so exit early.
        if (testMode) {
            return;
        }

        // build the orders directory
        File dir = new File(this.currentDir + "Orders");

        // build the filename.
        String fileName = dir + "/Orders_" + order.getDate().format(customFormat).replaceAll("-", "") + ".txt";

        // create a PrintWriter -> FileWriter - Append = True, so we 
        // dont overwrite existing entries
        PrintWriter writer = new PrintWriter(new FileWriter(fileName, true));

        // call the overriden to string to format this as a row.
        writer.println(order.toString());

        // flush before you leave the loop
        writer.flush();
    }

    // Overload that does not auto set the date and order number
    public void updateOrder(Order orderToAdd) {

        // small validation, make sure this item is not already in the list
        if (!orderLogbook.contains(orderToAdd)) {
            orderLogbook.add(orderToAdd);
        }
    }

    // adds a single order, returns the order number
    @Override
    public void addOrder(Order orderToAdd) {

        // generates the order number ( that we return to the user )
        int orderNum = orderLogbook.size() + 1;

        // set the order number
        orderToAdd.setOrderNum(orderNum);
        orderToAdd.setDate(LocalDate.now());

        orderLogbook.add(orderToAdd);
    }

    @Override
    public Collection<Order> getOrders(LocalDate localDate) {
        List<Order> itemsList = new ArrayList(this.orderLogbook);

        // filter to only items of equal date
        Collection<Order> filteredOrders = itemsList.stream().filter(o -> {
            return o.getDate().equals(localDate);
        }).collect(Collectors.toList());

        // return the filtered items list.
        return filteredOrders;
    }

    @Override
    public void removeOrder(Order order) {
        this.orderLogbook.remove(order);
    }

    @Override
    public Order getOrder(int orderNo, LocalDate date) {
        List<Order> itemsList = new ArrayList(this.orderLogbook);

        // filter to only items of order number
        List<Order> filteredItems = itemsList.stream().filter(o -> {
            return o.getOrderNum() == orderNo && o.getDate().equals(date);
        }).collect(Collectors.toList());

        // return the filtered items list.
        if (filteredItems.size() == 1) // Return the single match.
        {
            return filteredItems.get(0);
        }

        // no matches found, return null
        return null;
    }

    @Override
    public boolean readIsTestMode() throws IOException {
        // create a scanner -> BufferReader -> FileReader -> From the path: this.currentDir + "config.txt"
        // aka: read in the data files text
        Scanner sc = new Scanner(new BufferedReader(new FileReader(this.currentDir + "config.txt")));

        // reusable variable to hold the current line.
        String currentLine = "";

        // loop while there are more lines
        while (sc.hasNextLine() && currentLine != null) {

            // If the file has testmode set to true, return true. 
            if ("Test".equalsIgnoreCase(currentLine)) {
                return true;
            } // Else If it is set to false in the config file, return false.
            else if ("Prod".equalsIgnoreCase(currentLine)) {
                return false;
            } // We shouldnt get to here, but in case we have invalid lines...
            else {
                // keep processing... by getting the next line of text
                currentLine = sc.nextLine();
            }
        }

        // If the file has testmode set to true, return true. 
        if (currentLine != null && "Test".equalsIgnoreCase(currentLine)) {
            return true;
        } // Else If it is set to false in the config file, return false.
        else if (currentLine != null && "Prod".equalsIgnoreCase(currentLine)) {
            return false;
        }

        // assume test if no valid items were found.
        return true;
    }

    @Override
    public Collection<StateTax> readTaxes() throws IOException {
        ArrayList<StateTax> collection = new ArrayList();

        String path = this.currentDir + "Data/Taxes.txt";

        // create a scanner -> BufferReader -> FileReader -> From the path: this.currentDir + "Data/<TYPE>.txt"
        // aka: read in the data files text
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));

        // throw away the header
        if (sc.hasNextLine()) {
            sc.nextLine();
        }

        // reusable variable to hold the current line.
        String currentLine = sc.nextLine();

        // loop while there are more lines
        while (sc.hasNextLine() && currentLine != null) {

            // Parse and add
            collection.add(StateTax.parseStateTax(currentLine));

            currentLine = sc.nextLine();
        }

        // add the last item
        if (currentLine != null && !currentLine.isEmpty()) {
            collection.add(StateTax.parseStateTax(currentLine));
        }

        // return the results.
        return collection;
    }

    @Override
    public Collection<Product> readProducts() throws IOException {
        ArrayList<Product> collection = new ArrayList();

        // create a scanner -> BufferReader -> FileReader -> From the path: this.currentDir + "Data/<TYPE>.txt"
        // aka: read in the data files text
        String path = this.currentDir + "Data/Products.txt";

        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));

        // throw away the header
        if (sc.hasNextLine()) {
            sc.nextLine();
        }

        // reusable variable to hold the current line.
        String currentLine = sc.nextLine();

        // loop while there are more lines
        while (sc.hasNextLine() && currentLine != null) {

            // Parse and add
            collection.add(Product.parseProduct(currentLine));

            currentLine = sc.nextLine();
        }

        // add the last item
        if (currentLine != null && !currentLine.isEmpty()) {
            collection.add(Product.parseProduct(currentLine));
        }

        // return the results.
        return collection;
    }
}
