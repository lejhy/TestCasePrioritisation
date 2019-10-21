import java.util.*;

public class HillClimberTestCasePrioritisation implements Solver {
    private final int SUBSET_SIZE;
    private final int MAX_GEN = 500;

    private Map<String, boolean[]> testCases;
    private int generationCount = 0;
    private Random rg = new Random();
    private double bestScore = 0;
    private String[] bestCandidate;

    HillClimberTestCasePrioritisation(String dataSet, int numberOfTests) {
        SUBSET_SIZE = numberOfTests;
        FaultMatrix fm = new FaultMatrix();
        testCases = fm.loadFaultMatrix(dataSet);
    }

    public void solve() {
        bestCandidate = RandomCandidateGenerator.getRandomCandidate(testCases.keySet(), SUBSET_SIZE);
        bestScore = TestCaseOrderEvaluator.fitnessFunction(testCases, bestCandidate);
        while (generationCount < MAX_GEN) {
            generationCount++;
            double oldScore = bestScore;
            Set<String[]> neighbourhood = findAllNeighbours();
            Map<String[], Double> rankedNeighbourhood = new HashMap<>();
            neighbourhood.forEach(s -> rankedNeighbourhood.put(s, TestCaseOrderEvaluator.fitnessFunction(testCases, s)));
            rankedNeighbourhood.forEach((k, v) -> {
                if (v > bestScore) {
                    bestScore = v;
                    bestCandidate = k;
                }
            });
            if (oldScore != bestScore) {
                System.out.println("Generation: " + generationCount + " New best: " + bestScore + Arrays.toString(bestCandidate));
                for (String s : bestCandidate) {
                    for (boolean b : testCases.get(s)) {
                        if (b) {
                            System.out.print("1 ");
                        } else System.out.print("0 ");
                    }
                    System.out.println();
                }
            }
        }
    }

    public Set<String[]> findAllNeighbours() {
        Set<String[]> neighbours = new HashSet<>();
        for (int i = 0; i < SUBSET_SIZE; i++) {
            for (String test: testCases.keySet()) {
                boolean isDuplicate = false;
                for(String existing: bestCandidate) {
                    if(existing.equals(test)) isDuplicate = true;
                }
                if (!isDuplicate) {
                    String[] newCandidate = bestCandidate.clone();
                    newCandidate[i] = test;
                    neighbours.add(newCandidate);
                }
            }
            if (i < SUBSET_SIZE - 1) {
                String[] newCandidate = bestCandidate.clone();
                String temp = newCandidate[i];
                newCandidate[i] = newCandidate[i + 1];
                newCandidate[i + 1] = temp;
                neighbours.add(newCandidate);
            }
        }
        return neighbours;
    }
}
