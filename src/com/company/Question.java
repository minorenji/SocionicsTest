package com.company;

// Question object to contain the options and trait for each question.
public class Question {
    String[] options = new String[2];
    String metric = "";

    Question(String option1, String option2, String metric) {
        this.options[0] = option1;
        this.options[1] = option2;
        this.metric = metric;
    }
    public String toString() {
        return "Option 1: " + options[0] + ", Option 2: " + options[1] + ", Metric: " + metric;
    }
}
