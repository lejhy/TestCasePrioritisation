
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
            int numberOfTests;
            switch (selection) {
                case 1:
                    dataSet = "smallfaultmatrix.txt";
                    numberOfFaults = 9;
                    numberOfTests = 5;
                    break;
                case 2:
                    dataSet = "bigfaultmatrix.txt";
                    numberOfFaults = 38;
                    numberOfTests = 10;
                    break;
                default:
                    return;
            }

            System.out.println("(0) Exit");
            System.out.println("(1) Genetic Algorithm");
            System.out.println("(2) Hill Climber");
            System.out.println("(3) Random");
            System.out.println("(4) MOEA Framework NSGA-II");
            System.out.println("Select solver:");
            selection = scanner.nextInt();
            Solver solver;
            switch (selection) {
                case 1:
                    solver = new TestCasePrioritisation(dataSet);
                    break;
                case 2:
                    solver = new HillClimberTestCasePrioritisation(dataSet, numberOfTests);
                    break;
                case 3:
                    solver = new RandomTestCasePrioritisation(dataSet);
                    break;
                case 4:
                    solver = new MOEASolver(dataSet, numberOfFaults, numberOfTests, "NSGAII");
                    break;
                default:
                    return;
            }
            solver.solve();
        }
    }
}