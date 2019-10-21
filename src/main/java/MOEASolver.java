import MOEA.TestCaseProblem;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class MOEASolver implements Solver{

    private final String FILE_NAME;
    private final int NUMBER_OF_FAULTS;
    private final String ALGORITHM;

    public MOEASolver(String dataSet, int numberOfFaults, String algorithm) {
        this.FILE_NAME = dataSet;
        this.NUMBER_OF_FAULTS = numberOfFaults;
        this.ALGORITHM = algorithm;
    }

    public void solve(){
        NondominatedPopulation result = new Executor()
                .withProblem(new TestCaseProblem(FILE_NAME, NUMBER_OF_FAULTS))
                .withAlgorithm(ALGORITHM)
                .withMaxEvaluations(10000)
                .run();

        for (Solution solution: result) {
            System.out.format("Fitness: %f\n", solution.getObjective(0));
            for (int i = 0; i < solution.getNumberOfVariables(); i++){
                System.out.format("Variable %d: %s\n", i, solution.getVariable(i));
            }
        }
    }

}
