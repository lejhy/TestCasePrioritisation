import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

 class TestCaseOrderEvaluator {

     static double fitnessFunction(Map<String, int[]> testCases, String[] candidate) {
        int numberOfFaults = testCases.values().iterator().next().length;
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
        return calculateAPFD(faultFound.values(), numberOfFaults, candidate.length) + faultFound.size(); // APFD + faults found <- so genomes that find more tests would always be prioritised
    }

    // 1 - ((TF1+TF2+TF3+ ... +TFn) / (number of tests * number of faults))) + 1 / (2 * number of tests)
    private static double calculateAPFD(Collection<Integer> faultFoundOrder, int numberOfFaults, int subsetSize) {
        double x = 0.0;
        for (Integer i : faultFoundOrder) { // TF1+TF2+TF3+ ... +TFn
            x += i;
        }
        return 1.0 - (x / (subsetSize * numberOfFaults)) + (1.0 / (2 * subsetSize)); // some of these values could be pre-calculated constants. This would speed up a little bit the calculation but decrease readability
    }

}
