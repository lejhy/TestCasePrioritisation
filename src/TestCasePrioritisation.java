import java.io.*;
import java.util.*;

class TestCasePrioritisation {
    private final int POPULATION_SIZE = 150;
    private final int SUBSET_SIZE = 10;
    private final double MUTATION_RATE = 0.1;
    private final double CROSSOVER_RATE = 0.99;
    private final int MAX_GEN = 10000;
    private final String FILE_NAME = "bigfaultmatrix.txt";

    private Map<String, int[]> testCases;
    private int generationCount = 0;
    private List<String[]> population;
    private List<String[]> matingPool;
    private Random rg = new Random();
    private double bestScore = 0;
    private String[] bestIndividual;
    private int numberOfFaults = 0;

    TestCasePrioritisation() {
        testCases = loadFaultMatrix();
        population = generateStartPopulation();
        evolve();
    }

    private Map<String, int[]> loadFaultMatrix() {
        String st;
        String[] tokens;
        Map<String, int[]> tests = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource(FILE_NAME)).getFile());
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((st = br.readLine()) != null) {
                tokens = st.split(",");
                numberOfFaults = tokens.length - 1;
                int[] faults = new int[numberOfFaults];
                for (int i = 0; i < numberOfFaults; i++) {
                    faults[i] = Integer.parseInt(tokens[i + 1]);
                }
                tests.put(tokens[0], faults);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tests;
    }

    private void evolve() {
        Map<String[], Double> rankedPop = new HashMap<>();
        while (generationCount < MAX_GEN) {
            generationCount++;
            rankedPop.clear();
            population.forEach(s -> rankedPop.put(s, fitnessFunction(s)));
            generateMatingPool(rankedPop);
            generateNewPopulation();
        }
    }

    private List<String[]> generateStartPopulation() {
        List<String[]> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<String> possibleTests = new ArrayList<>(testCases.keySet());
            String[] genome = new String[SUBSET_SIZE];
            for (int k = 0; k < SUBSET_SIZE; k++) {
                String randomTest = possibleTests.get(rg.nextInt(possibleTests.size()));
                genome[k] = randomTest;
                possibleTests.remove(randomTest); // genome can't have the same test multiple times
            }
            population.add(genome);
        }
        return population;
    }

    // creates matting pool where the fittest dna has the best chances of getting picked
    private void generateMatingPool(Map<String[], Double> rankedPop) {
        matingPool = new ArrayList<>();
        rankedPop.forEach((dna, rank) -> {// TODO make sure that fittest have advantage
            if (rank > bestScore - 0.03) {// allow only best to enter the pool
//                for (int i = 0; i < rank * 100; i++) {
                matingPool.add(dna);
//                }
            }
        });
    }

    private double fitnessFunction(String[] candidate) {
        int position = 1;
        Map<Integer, Integer> faultFound = new HashMap<>();
        for (String test : candidate) {
            int[] faults = testCases.get(test);
            for (int i = 0; i < numberOfFaults; i++) { // For each fault tracks in which position it was found e.g. fault "1" fas found after "4" tests
                if (!faultFound.containsKey(i) && faults[i] == 1) {
                    faultFound.put(i, position);
                }
            }
            position++;
        }
        return calculateAPFD(faultFound.values()) + faultFound.size(); // APFD + faults found <- so genomes that find more tests would always be prioritised
    }

    // 1 - ((TF1+TF2+TF3+ ... +TFn) / (number of tests * number of faults))) + 1 / (2 * number of tests)
    private double calculateAPFD(Collection<Integer> faultFoundOrder) {
        double x = 0.0;
        for (Integer i : faultFoundOrder) { // TF1+TF2+TF3+ ... +TFn
            x += i;
        }
        return 1.0 - (x / (SUBSET_SIZE * numberOfFaults)) + (1.0 / (2 * SUBSET_SIZE)); // some of these values could be pre-calculated constants. This would speed up a little bit the calculation but decrease readability
    }

    private void checkBestSoFar(String[] candidate) {
        double score = fitnessFunction(candidate);
        if (bestScore < score) {
            bestScore = score;
            bestIndividual = candidate;
            System.out.println("Generation: " + generationCount + " New best: " + score + Arrays.toString(candidate));
            for (String s : candidate) {
                System.out.println(Arrays.toString(testCases.get(s)));
            }
        }
    }

    private void generateNewPopulation() {
        population = new ArrayList<>();
        while (population.size() < POPULATION_SIZE) {
            String[] parentA = matingPool.get(rg.nextInt(matingPool.size()));
            String[] parentB = matingPool.get(rg.nextInt(matingPool.size()));
            if (Math.random() < CROSSOVER_RATE) { // There is a chance that the parent will enter the next population without crossover or mutation
                population.add(crossover(parentA, parentB));
                population.add(crossover(parentB, parentA));
            } else {
                population.add(parentA);
                population.add(parentB);
            }
        }
        if (bestIndividual != null) {
            population.add(0, bestIndividual); // best individual always survives
        }
    }

    private String[] crossover(String[] p1, String[] p2) {
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
}