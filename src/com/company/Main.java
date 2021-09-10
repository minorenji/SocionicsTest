/*
    9/9/2021

    A program that conducts a simple Socionics test and returns the most likely sociotype.
 */


package com.company;

public class Main {

    public static void main(String[] args) {
        Test test = new Test("resources/questions.txt");
        test.startTest();
        test.results();
    }
}



