
import MOEA.TestCaseProblem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("(0) Exit");
            System.out.println("(1) Small");
            System.out.println("(2) Big");
            System.out.println("Select dataSet:");
            int selection = scanner.nextInt();
            String dataSet;
            int numberOfFaults;
            switch (selection) {
                case 1:
                    dataSet = "smallfaultmatrix.txt";
                    numberOfFaults = 38;
                    break;
                case 2:
                    dataSet = "bigfaultmatrix.txt";
                    numberOfFaults = 9;
                    break;
                default:
                    return;
            }

            System.out.println("(0) Exit");
            System.out.println("(1) Genetic Algorithm");
            System.out.println("(2) Hill Climber");
            System.out.println("(3) MOEA Framework NSGA-II");
            System.out.println("Select solver:");
            selection = scanner.nextInt();
            Solver solver;
            switch (selection) {
                case 1:
                    solver = new TestCasePrioritisation(dataSet);
                    break;
//                case 2:
//
//                    break;
                case 3:
                    solver = new MOEASolver(dataSet, numberOfFaults, "NSGAII");
                    break;
                default:
                    return;
            }
            solver.solve();
        }
    }
}