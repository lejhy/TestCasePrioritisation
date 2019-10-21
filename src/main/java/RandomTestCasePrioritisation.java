import java.util.Arrays;
import java.util.Map;

public class RandomTestCasePrioritisation implements Solver {
    private final int SUBSET_SIZE = 10;
    private double bestScore = 0;
    private String[] bestCandidate;
    Map<String, boolean[]> testCases;

    RandomTestCasePrioritisation(String dataSet) {
        FaultMatrix fm = new FaultMatrix();
        testCases = fm.loadFaultMatrix(dataSet);
    }

    public void solve() {
        tryRandomSolutions(testCases);
    }

    private void tryRandomSolutions(Map<String, boolean[]> testCases) {
        String[] candidate;
        double score;
        for (int i = 0; i < 500000; i++) {
            candidate = RandomCandidateGenerator.getRandomCandidate(testCases.keySet(), SUBSET_SIZE);
            score = TestCaseOrderEvaluator.fitnessFunction(testCases, candidate);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
                System.out.println("Best found " + bestScore);
                for (String s : bestCandidate) {
                    System.out.println(Arrays.toString(testCases.get(s)));
                }
            }
        }
    }

}
