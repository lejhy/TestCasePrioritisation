import java.util.*;

class TestCasePrioritisation implements Solver {
    private final int POPULATION_SIZE = 500;
    private final int SUBSET_SIZE = 10;
    private final double MUTATION_RATE = 0.15;
    private final double CROSSOVER_RATE = 0.99;
    private final int MAX_GEN = 500;

    private Map<String, boolean[]> testCases;
    private int generationCount = 0;
    private Set<String[]> population;
    private Random rg = new Random();
    private double bestScore = 0;
    private String[] bestCandidate;

    TestCasePrioritisation(String dataSet) {
        FaultMatrix fm = new FaultMatrix();
        testCases = fm.loadFaultMatrix(dataSet);
        population = generateStartPopulation();
    }


    public void solve() {
        Map<String[], Double> rankedPop = new HashMap<>();
        while (generationCount < MAX_GEN) {
            generationCount++;
            rankedPop.clear();
            population.forEach(s -> rankedPop.put(s, TestCaseOrderEvaluator.fitnessFunction(testCases, s)));
            List<String[]> matingPool = getMatingPool(rankedPop);
            population = generateNewPopulation(matingPool);
        }
    }

    private Set<String[]> generateStartPopulation() {
        Set<String[]> population = new HashSet<>();
        for (int i = 0; population.size() < POPULATION_SIZE; i++) {
            population.add(RandomCandidateGenerator.getRandomCandidate(testCases.keySet(), SUBSET_SIZE));
        }
        return population;
    }

    // keeps 5% fittest individuals
    private List<String[]> getMatingPool(Map<String[], Double> rankedPop) {
        List<String[]> matingPool = new LinkedList<>();

        rankedPop.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(POPULATION_SIZE / 20)
                .forEach(v -> {
                    matingPool.add(v.getKey());
                });
        return matingPool;
    }

    private Set<String[]> generateNewPopulation(List<String[]> matingPool) {
        Set<String[]> newPopulation = new HashSet<>();
        //Elitism
        if (bestCandidate != null) {
            newPopulation.add(bestCandidate); // best individual goes into new population unchanged
        }
        //Mating
        while (newPopulation.size() < POPULATION_SIZE) {
            String[] parentA = matingPool.get(rg.nextInt(matingPool.size()));
            String[] parentB = matingPool.get(rg.nextInt(matingPool.size()));
            if (Math.random() < CROSSOVER_RATE) { // There is a chance that the parent will enter the next population without crossover or mutation
                newPopulation.add(crossoverAndMutate(parentA, parentB));
                newPopulation.add(crossoverAndMutate(parentB, parentA));
            } else {
                newPopulation.add(parentA);
                newPopulation.add(parentB);
            }
        }
        return newPopulation;
    }

    private String[] crossoverAndMutate(String[] p1, String[] p2) {
        List<String> availableGenes = new ArrayList<>(testCases.keySet());
        String[] child = new String[SUBSET_SIZE];
        for (int k = 0; k < SUBSET_SIZE; k++) {
            if (Math.random() < MUTATION_RATE) {
                child[k] = availableGenes.get(rg.nextInt(availableGenes.size()));
                availableGenes.remove(child[k]);
            } else {
                if (k < SUBSET_SIZE / 2) { // sets the first half of the genome
                    child[k] = p1[k];
                    availableGenes.remove(p1[k]);
                } else { // sets the second half of the genome
                    String pb = p2[k];
                    if (!availableGenes.contains(pb)) { // Test case already in the genome
                        pb = availableGenes.get(rg.nextInt(availableGenes.size())); // inserts random gene that hasn't been used
                    }
                    child[k] = pb;
                }
            }
        }
        checkBestSoFar(child);
        return child;
    }

    private void checkBestSoFar(String[] candidate) {
        double score = TestCaseOrderEvaluator.fitnessFunction(testCases, candidate);
        if (bestScore < score) {
            bestScore = score;
            bestCandidate = candidate;
            System.out.println("Generation: " + generationCount + " New best: " + score + Arrays.toString(candidate));
            for (String s : bestCandidate) {
                for (boolean b : testCases.get(s)) {
                    if (b) System.out.print("1 ");
                    else System.out.print("0 ");
                }
                System.out.println();
            }
        }
    }
}