import MOEA.TestCaseProblem;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.List;

public class MOEASolver implements Solver{

    private final String FILE_NAME;
    private final int NUMBER_OF_FAULTS;
    private final int NUMBER_OF_TESTS;
    private final String ALGORITHM;

    public MOEASolver(String dataSet, int numberOfFaults, int numberOfTests, String algorithm) {
        this.FILE_NAME = dataSet;
        this.NUMBER_OF_FAULTS = numberOfFaults;
        this.NUMBER_OF_TESTS = numberOfTests;
        this.ALGORITHM = algorithm;
    }

    public void solve(){
        List<NondominatedPopulation> result = new Executor()
                .withProblem(new TestCaseProblem(FILE_NAME, NUMBER_OF_FAULTS, NUMBER_OF_TESTS))
                .withAlgorithm(ALGORITHM)
                .withMaxEvaluations(25000)
                .runSeeds(1);

        for (NondominatedPopulation population: result) {
            for (Solution solution: population) {
                System.out.format("Fitness: %f\n", solution.getObjective(0));
                for (int i = 0; i < solution.getNumberOfVariables(); i++) {
                    System.out.format("Variable %d: %s\n", i, solution.getVariable(i));
                }

            }
        }
    }

}
