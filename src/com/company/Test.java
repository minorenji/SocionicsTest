package com.company;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

// Test object that creates and administers a test.
class Test {
    final int MARGIN = 10;

    // ArrayList to contain questions as Question object.
    private ArrayList<Question> questions = new ArrayList<>();
    private Dictionary<String, Integer> traits = new Hashtable<>();
    private Dictionary<String, String> resultsDict = new Hashtable<>();
    String[] traitsList = {"extraversion", "introversion", "irrationality", "rationality", "sensing", "intuition",
            "ethics", "logics"};

    // Constructor that reads question.txt and populates ArrayList with Questions.
    Test(String directory) {
        BufferedReader reader;
        String section = "";

        //Populating the traits Dictionary with the proper trait keys.
        for (String trait : traitsList) {
            traits.put(trait, 0);
        }

        // Reading the question file line by line.
        try {
            reader = new BufferedReader(new FileReader(directory));
            String line = reader.readLine();

            // While loop to break out if there are no more lines.
            while (line != null) {

                // Check if this line is a new section. Set the current section to new section if so.
                if (line.startsWith("s-")) {
                    section = line.substring(3);
                }

                // Check if this line contains a question. Parse the two options and add Question to List.
                else if (line.startsWith("q-")) {
                    questions.add(new Question(line.substring(3, line.indexOf(":") - 1), line.substring(line.indexOf(":") + 2),
                            section));
                }
                line = reader.readLine();
            }
        }

        //If the directory is not found.
        catch (IOException e) {
            e.printStackTrace();
        }

        //Shuffle question order.
        Collections.shuffle(questions);
    }

    // Method to execute the test.
    void startTest() {
        int responseValue = 0;
        int qNo = 1;
        String input = "";
        Scanner console = new Scanner(System.in);
        System.out.println("Administering test...\n For each question input a number from [A] 1 - 5 [B] to indicate " +
                        "which option you relate to more.\n Try not to answer '3' if possible.\n Type 'quit' to exit.");
        System.out.println("--------------------------------");

        // Iterating through question list.
        for (Question question : questions) {
            System.out.println("Question "+ qNo + ": ");
            System.out.println("A: \"" + question.options[0] + "\" or B: \"" + question.options[1] + "\"");
            input = console.nextLine();

            // While loop to control invalid inputs.
            while (true) {
                if (input.toLowerCase().contains("quit")) {
                    System.exit(1);
                }
                try {
                    if (Integer.parseInt(input) > 0 && Integer.parseInt(input) < 6) {
                        break;
                    }

                    // If input is integer but not between 0 or 5.
                    System.out.println("Invalid input.");
                    input = console.nextLine();
                }

                // Catch non-integer input.
                catch (NumberFormatException | NullPointerException e) {
                    System.out.println("Invalid input.");
                    input = console.nextLine();
                }

            }

            // Subtract 3 to find the distance from 3.
            responseValue = Integer.parseInt(input) - 3;

            // Modify trait value based on the trait the question is measuring.
            switch (question.metric) {
                case "Extraversion":

                    // If the response value is under 0 that means it was below 3, Indicating a preference for the first
                    // trait. Vice versa for the second trait.
                    if (responseValue < 0) {
                        traits.put("extraversion", traits.get("extraversion") + Math.abs(responseValue));

                    }
                    else {
                        traits.put("introversion", traits.get("introversion") + Math.abs(responseValue));

                    }
                    break;
                case "Rationality":
                    if (responseValue < 0) {
                        traits.put("rationality", traits.get("rationality") + Math.abs(responseValue));
                    }
                    else{
                        traits.put("irrationality", traits.get("irrationality") + Math.abs(responseValue));
                    }
                    break;
                case "Sensing/Intuition":
                    if (responseValue < 0) {
                        traits.put("sensing", traits.get("sensing") + Math.abs(responseValue));
                    }
                    else{
                        traits.put("intuition", traits.get("intuition") + Math.abs(responseValue));
                    }
                    break;
                case "Ethics/Logic":
                    if (responseValue < 0) {
                        traits.put("logics", traits.get("logics") + Math.abs(responseValue));
                    }
                    else{
                        traits.put("ethics", traits.get("ethics") + Math.abs(responseValue));
                    }
                    break;
            }
            // Increase question number.
            qNo++;
        }
    }

    void results() {

        // StringBuilder that contains the final dominant dichotomies in the form of 0s and 1s.
        StringBuilder resultsCode = new StringBuilder();
        int inconclusive = 0;

        // Iterate through the list of traits.
        for (int i = 0; i <= 6; i += 2){

            // First check whether the margin is high enough for a conclusive result.
            if (Math.abs(traits.get(traitsList[i]) - traits.get(traitsList[i + 1])) > MARGIN) {
                if (traits.get(traitsList[i]) > traits.get(traitsList[i + 1])) {
                    resultsDict.put("Trait " + (i / 2 + 1), traitsList[i]);
                    resultsCode.append(0);
                }
                else {
                    resultsDict.put("Trait " + (i / 2 + 1), traitsList[i + 1]);
                    resultsCode.append(1);
                }
            }
            else {
                inconclusive++;
                resultsDict.put("Trait " + (i / 2 + 1), "inconclusive");
                resultsCode.append(2);
            }
        }

        // If more than one trait is inconclusive, no result is produced.
        if (inconclusive > 1) {
            System.out.println("Too many dichotomies were inconclusive.");
            System.out.println("Your trait totals are as follows: ");
            System.out.println(traits);
            return;
        }

        // If no traits are inconclusive, only one type is possible.
        if (inconclusive == 0) {
            System.out.println("\nYour responses indicate that you are a(n) " + typeParser(resultsCode.toString()) + ".");
        }

        // If one trait is inconclusive, two types are possible.
        else {
            System.out.print("\nYour responses indicate that you are a(n) " + typeParser(resultsCode.toString().replace('2', '0')));
            System.out.println(" or a(n) " + typeParser(resultsCode.toString().replace('2', '1')) + ".");
        }

        System.out.println("Your trait totals are as follows: ");
        System.out.println(traits);

    }

    private static String typeParser(String typeCode) {
        /*
            2 = inconclusive
            First Digit: 0 = extraversion, 1 = introversion
            Second Digit: 0 = irrationality, 1 = rationality
            Third Digit: 0 = sensing, 1 = intuition
            Fourth Digit: 0 = ethics, 1 = logics
        */

        // Switch statement for every type.
        switch (typeCode) {
            case "0100":
                return "ESE";
            case "1111":
                return "LII";
            case "0011":
                return "ILE";
            case "1000":
                return "SEI";
            case "0001":
                return "SLE";
            case "1010":
                return "IEI";
            case "0110":
                return "EIE";
            case "1101":
                return "LSI";
            case "0000":
                return "SEE";
            case "1011":
                return "ILI";
            case "0111":
                return "LIE";
            case "1100":
                return "ESI";
            case "0101":
                return "LSE";
            case "1110":
                return "EII";
            case "0010":
                return "IEE";
            case "1001":
                return "SLI";
        }
        return null;
    }
}
