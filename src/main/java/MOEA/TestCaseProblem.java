package MOEA;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TestCaseProblem extends AbstractProblem {

    private final String FILE_NAME;
    private final int NUMBER_OF_FAULTS;
    private List<boolean[]> tests = new ArrayList<>();

    public TestCaseProblem(String dataSet, int numberOfFaults) {
        super(numberOfFaults, 1, 1);
        NUMBER_OF_FAULTS = numberOfFaults;
        FILE_NAME = dataSet;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Objects.requireNonNull(getClass().getClassLoader().getResource(FILE_NAME)).getFile()));
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] tokens = line.split(",");
                if ((tokens.length - 1) != NUMBER_OF_FAULTS) throw new RuntimeException("Wrong number of faults");
                boolean[] faults = new boolean[NUMBER_OF_FAULTS];
                for(int i = 0; i < NUMBER_OF_FAULTS; i++) {
                    faults[i] = Integer.parseInt(tokens[i + 1]) == 1;
                }
                tests.add(faults);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read file: " + FILE_NAME);
        }
    }

    public Solution newSolution() {
        Solution solution = new Solution(
                getNumberOfVariables(),
                getNumberOfObjectives(),
                getNumberOfConstraints()
        );

        for(int i = 0; i < solution.getNumberOfVariables(); i++) {
            solution.setVariable(i, new BinaryIntegerVariable(0, tests.size() - 1));
        }

        return solution;
    }

    public void evaluate(Solution solution) {
        int[] testOrder = EncodingUtils.getInt(solution);

        int fitness = 0;

        boolean[] detectedFaults = new boolean[NUMBER_OF_FAULTS];
        int totalDetectedFaults = 0;
        for(int testIndex: testOrder) {
            boolean[] test = tests.get(testIndex);
            for(int i = 0; i < NUMBER_OF_FAULTS; i++) {
                if (test[i] && !detectedFaults[i]) {
                    detectedFaults[i] = true;
                    totalDetectedFaults++;
                }
            }
            fitness += totalDetectedFaults;
        }

        int constraint = 0;
        List<Integer> orderedTests = Arrays.stream(testOrder).boxed().sorted(Comparator.comparingInt(o -> o)).collect(Collectors.toList());
        int lastTest = -1;
        for(int test: orderedTests) {
            constraint += lastTest == test ? 1 : 0;
        }

        solution.setObjective(0, -fitness);
        solution.setConstraint(0, constraint);
    }
}
