import java.util.Arrays;
import java.util.Map;

public class RandomTestCasePrioritisation {
    private final int SUBSET_SIZE = 10;
    private double bestScore = 0;
    private String[] bestCandidate;


    RandomTestCasePrioritisation() {
        FaultMatrix fm = new FaultMatrix();
        Map<String, int[]> testCases = fm.loadFaultMatrix("bigfaultmatrix.txt");
        tryRandomSolutions(testCases);
    }

    private void tryRandomSolutions(Map<String, int[]> testCases) {
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
