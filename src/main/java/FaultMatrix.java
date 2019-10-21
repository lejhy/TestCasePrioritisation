import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class FaultMatrix {

    Map<String, boolean[]> loadFaultMatrix(String fileName) {
        String st;
        String[] tokens;
        Map<String, boolean[]> tests = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((st = br.readLine()) != null) {
                tokens = st.split(",");
                int numberOfFaults = tokens.length - 1;
                boolean[] faults = new boolean[numberOfFaults];
                for (int i = 0; i < numberOfFaults; i++) {
                    faults[i] = Integer.parseInt(tokens[i + 1]) == 1;
                }
                tests.put(tokens[0], faults);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tests;
    }

}
