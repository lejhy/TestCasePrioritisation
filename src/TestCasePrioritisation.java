import java.io.*;
import java.util.*;

class TestCasePrioritisation {
    private final int populationSize = 100;
    private final int subsetSize = 10;
    private final int maxGen = 10000;
    private final String fileName = "smallfaultmatrix.txt";
    private Map<String, int[]> testCases = new HashMap<>();
    private int generationCount = 0;
    private ArrayList<String[]> population;
    private ArrayList<String[]> matingPool;
    private Random rg = new Random();
    private double bestScore = 0;
    private int numberOfFaults = 0;

    TestCasePrioritisation() {
        loadFaultMatrix();
        population = generateStartPopulation();
        evolve();
    }

    private void loadFaultMatrix() {
        String st;
        String[] tokens;
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((st = br.readLine()) != null) {
                tokens = st.split(",");
                numberOfFaults = tokens.length - 1;
                int[] faults = new int[numberOfFaults];
                for (int i = 0; i < numberOfFaults; i++) {
                    faults[i] = Integer.parseInt(tokens[i + 1]);
                }
                testCases.put(tokens[0], faults);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evolve() {
        generationCount++;
        while (generationCount < maxGen) {
            generationCount++;
            Map<String[], Double> rankedPop = new HashMap<>();
            population.forEach(s -> rankedPop.put(s, fitnessFunction(s)));
            generateMatingPool(rankedPop);
            generateNewPopulation();
        }
    }

    private ArrayList<String[]> generateStartPopulation() {
        ArrayList<String[]> population = new ArrayList<>();
        Random rg = new Random();
        for (int i = 0; i < populationSize; i++) {
            ArrayList<String> possibleTests = new ArrayList<>(testCases.keySet());
            String[] genome = new String[subsetSize];
            for (int k = 0; k < subsetSize; k++) {
                String randomTest = possibleTests.get(rg.nextInt(possibleTests.size()));
                genome[k] = randomTest;
                possibleTests.remove(randomTest); // genome can't have same test multiple times
            }
            population.add(genome);
        }
        return population;
    }

    // creates matting pool where the fittest dna has the best chances of getting picked
    private void generateMatingPool(Map<String[], Double> rankedPop) {
        matingPool = new ArrayList<>();
        rankedPop.forEach((dna, rank) -> {
            for (int i = 0; i < (rank + 1) * 10; i++) {
                if (rank > bestScore - 1) { // allow only best to enter the pool, also make sure that the best one has the best chance to mate
                    matingPool.add(dna);
                }
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
        if (faultFound.size() < numberOfFaults) return 0; // Didnt find all the faults

        return calculateAPFD(faultFound.values());
    }

    // 1 -  ((TF1+TF2+TF3 + ... TFn) / (number of tests * number of faults))) + 1 / (2 * number of tests)
    private double calculateAPFD(Collection<Integer> faultFoundOrder) {
        double x = 0.0;
        for (Integer i : faultFoundOrder) {
            x += i; //(TF1+TF2+TF3 + ... TFn)
        }
        return 1.0 - (x / (subsetSize * numberOfFaults)) + (1.0 / (2 * subsetSize));
    }

    private void checkBestSoFar(String[] candidate) {
        double score = fitnessFunction(candidate);
        if (bestScore < score) {
            bestScore = score;
            System.out.println("Generation: " + generationCount + " New best: " + score + Arrays.toString(candidate));
            for (String s : candidate) {
                System.out.println(Arrays.toString(testCases.get(s)));
            }
        }
    }

    private void generateNewPopulation() {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize / 2; i++) {
            String[] parentA = matingPool.get(rg.nextInt(matingPool.size()));
            String[] parentB = matingPool.get(rg.nextInt(matingPool.size()));
            List<String> possibleChildATests = new ArrayList<>(testCases.keySet());
            List<String> possibleChildBTests = new ArrayList<>(testCases.keySet());
            String[] childA = new String[subsetSize];
            String[] childB = new String[subsetSize];
            for (int k = 0; k < subsetSize; k++) { // TODO split it up in functions
                if (rg.nextInt(50) == 1) { // 1 in 50 chance of mutation
                    childA[k] = possibleChildATests.get(rg.nextInt(possibleChildATests.size()));
                    possibleChildATests.remove(childA[k]);
                    childB[k] = possibleChildBTests.get(rg.nextInt(possibleChildBTests.size()));
                    possibleChildBTests.remove(childB[k]);
                } else {
                    if (k < subsetSize / 2) { // Sets the first half of the genome
                        childA[k] = parentA[k];
                        possibleChildATests.remove(parentA[k]);
                        childB[k] = parentB[k];
                        possibleChildBTests.remove(parentB[k]);
                    } else { // sets the second half of the genome
                        String pa = parentA[k];
                        String pb = parentB[k];
                        if (!possibleChildATests.contains(pb)) { // Test case already in the genome
                            pb = possibleChildATests.get(rg.nextInt(possibleChildATests.size())); // inserts random test case that haven't been used
                        }
                        if (!possibleChildBTests.contains(pa)) { // Test case already in the genome
                            pa = possibleChildBTests.get(rg.nextInt(possibleChildBTests.size())); // inserts random test case that haven't been used
                        }
                        childA[k] = pb;
                        childB[k] = pa;
                    }
                }
            }
            checkBestSoFar(childA);
            checkBestSoFar(childB);
            population.add(childA);
            population.add(childB);
        }

    }
}