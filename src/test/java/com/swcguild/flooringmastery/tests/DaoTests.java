/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.tests;

import com.swcguild.flooringmastery.dao.FlooringMasteryDAOImpl;
import com.swcguild.flooringmastery.dto.Order;
import com.swcguild.flooringmastery.dto.Product;
import com.swcguild.flooringmastery.dto.StateTax;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author apprentice
 */
public class DaoTests {

    FlooringMasteryDAOImpl dao;
    DateTimeFormatter customFormat;

    public DaoTests() throws IOException {
        this.customFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        this.dao = new FlooringMasteryDAOImpl(customFormat);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void readIsTestModeTest() {

        // Set the default value to the fail value.
        boolean result = true;

        // Attempt to read the mode.
        try {
            // The test file is set to 'Prod', so test mode should be false.
            result = dao.readIsTestMode();
        } catch (IOException ex) {
            Logger.getLogger(DaoTests.class.getName()).log(Level.SEVERE, null, ex);
        }

        // To test, we assert the logic from above: 
        // -> "so test mode should be false"
        assertFalse("Asserting that the mode is Production.", result);
    }

    @Test
    public void initTest() {

        // NOTE: The init method should do the following things:
        //
        // dao.testMode = testMode;
        // dao.customFormat = customFormat;        
        //
        // call the Load method to get all the orders from the file system.
        // dao.loadOrdersFromFile();
        // Null Check.
        assertFalse("Test mode has not yet been set, so we assert that it is false ( the default boolean value ).", dao.isTestMode());
        assertNull("Custom Format has not yet been set, so we assert that it is null.", dao.getCustomFormat());
        assertEquals("No Orders have yet been loaded. Assert the Orders have zero items.", dao.getOrderCount(), 0);


        // Test that the info from the note, was set properly.
        assertTrue("Initialized with test mode = true, so assert that dao.testMode is true.", dao.isTestMode());
        assertNotNull("Custom Format has been set, so we assert that it is not null.", dao.getCustomFormat());
        assertNotEquals("Orders have now been loaded. Assert the Orders do not have zero items.", dao.getOrderCount(), 0);

    }

    @Test
    public void addOrderTest() {

        // Create a dummy order.
        Order order = new Order(
            999,
            LocalDate.now(),
            "Test Customer",
            "OH",
            0,
            "Test Prod",
            0,
            0,
            0
        );
        
        int initialCount = dao.getOrderCount();
        
            // add the dummy order.
            dao.addOrder(order);
        
        assertEquals("Ensure that the order count has incremented by one.", dao.getOrderCount(), initialCount + 1 );        
    }

    @Test
    public void removeOrderTest() {
        // Create a dummy order.
        Order order = new Order(
            999,
            LocalDate.now(),
            "Test Customer",
            "OH",
            0,
            "Test Prod",
            0,
            0,
            0
        );
                
        // Attempt to add an order.
            // add the dummy order.
            dao.addOrder(order);
        
           
        // Get the count after the add.
        int addedCount = dao.getOrderCount();

        // remove the dummy order.
        dao.removeOrder( order );
        
        assertEquals("Ensure that the order count has decremented by one.", dao.getOrderCount(), addedCount - 1 );        
    }

    @Test
    public void getOrderTest() {
        // Create a dummy order.
        Order order = new Order(
            999,
            LocalDate.now(),
            "Test Customer",
            "OH",
            0,
            "Test Prod",
            0,
            0,
            0
        );
                
        // add the dummy order.
        dao.addOrder(order);
        
        // Not we should be able to get that order from the collection.
        Order resolvedOrder = dao.getOrder(order.getOrderNum(), order.getDate());
        
        assertEquals("Ensure that the added order and the resolved order are the same.", order, resolvedOrder);
    }
    
    @Test
    public void getOrdersTest() {
        // Create a dummy order.
        Order order = new Order(
            999,
            LocalDate.MIN,
            "Test Customer",
            "OH",
            0,
            "Test Prod",
            0,
            0,
            0
        );
        
        // add the dummy order.
        dao.updateOrder(order);

        // Try to resolve orders with that date ( should only get the one we just added ).
        Collection<Order> ordersForTestDate = dao.getOrders(LocalDate.MIN);
        
        assertEquals( "We ensure that the special test date queryed only returns one value", 1, ordersForTestDate.size() );
    }
    
    @Test
    public void readTaxesTest() {
        try {
            assertNotEquals( "Ensure that at least one tax is loaded from the data source file.", dao.readTaxes().size(), 0);
        } catch (IOException ex) {
            Logger.getLogger(DaoTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void readProductsTest() {
        try {
            assertNotEquals( "Ensure that at least one tax is loaded from the data source file.", dao.readProducts().size(), 0);
        } catch (IOException ex) {
            Logger.getLogger(DaoTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
