import java.util.*;

public class HillClimberTestCasePrioritisation implements Solver {
    private final int SUBSET_SIZE;
    private final int MAX_ITERATION = 10;
    private final int CANDIDATES = 25;

    private Map<String, boolean[]> testCases;
    private Random rg = new Random();
    private double currentBestScore = 0;
    private String[] currentBestCandidate;
    private double bestScore = 0;
    private String[] bestCandidate;

    HillClimberTestCasePrioritisation(String dataSet, int numberOfTests) {
        SUBSET_SIZE = numberOfTests;
        FaultMatrix fm = new FaultMatrix();
        testCases = fm.loadFaultMatrix(dataSet);
    }

    public void solve() {
        for (int i = 0; i < CANDIDATES; i++) {
            currentBestCandidate = RandomCandidateGenerator.getRandomCandidate(testCases.keySet(), SUBSET_SIZE);
            currentBestScore = TestCaseOrderEvaluator.fitnessFunction(testCases, currentBestCandidate);
            for (int iterationCount = 0; iterationCount < MAX_ITERATION; iterationCount++) {
                Set<String[]> neighbourhood = findAllNeighbours();
                Map<String[], Double> rankedNeighbourhood = new HashMap<>();
                neighbourhood.forEach(s -> rankedNeighbourhood.put(s, TestCaseOrderEvaluator.fitnessFunction(testCases, s)));
                rankedNeighbourhood.forEach((k, v) -> {
                    if (v > currentBestScore) {
                        currentBestScore = v;
                        currentBestCandidate = k;
                    }
                });
                if (currentBestScore > bestScore) {
                    System.out.println("Candidate: " + i + " Iteration: " + iterationCount + " New best: " + currentBestScore + Arrays.toString(currentBestCandidate));
                    for (String s : currentBestCandidate) {
                        for (boolean b : testCases.get(s)) {
                            if (b) {
                                System.out.print("1 ");
                            } else System.out.print("0 ");
                        }
                        System.out.println();
                    }
                    bestScore = currentBestScore;
                    bestCandidate = currentBestCandidate;
                }
            }
        }
    }

    public Set<String[]> findAllNeighbours() {
        Set<String[]> neighbours = new HashSet<>();
        for (int i = 0; i < SUBSET_SIZE; i++) {
            for (String test: testCases.keySet()) {
                boolean isDuplicate = false;
                for(String existing: currentBestCandidate) {
                    if(existing.equals(test)) isDuplicate = true;
                }
                if (!isDuplicate) {
                    String[] newCandidate = currentBestCandidate.clone();
                    newCandidate[i] = test;
                    neighbours.add(newCandidate);
                }
            }
            if (i < SUBSET_SIZE - 1) {
                String[] newCandidate = currentBestCandidate.clone();
                String temp = newCandidate[i];
                newCandidate[i] = newCandidate[i + 1];
                newCandidate[i + 1] = temp;
                neighbours.add(newCandidate);
            }
        }
        return neighbours;
    }
}
