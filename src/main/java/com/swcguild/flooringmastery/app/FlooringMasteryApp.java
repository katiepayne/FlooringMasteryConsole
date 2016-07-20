/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swcguild.flooringmastery.app;

import com.swcguild.flooringmastery.controller.FlooringMasteryController;
import java.io.IOException;

/**
 *
 * @author apprentice
 */
public class FlooringMasteryApp {

    public static void main(String[] args) throws IOException {

        FlooringMasteryController flooringMasteryRunner = new FlooringMasteryController();

        flooringMasteryRunner.run();
    }

}
