import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

class RandomCandidateGenerator {

    static String[] getRandomCandidate(Set<String> possibleCandidates, int size) {
        Random rg = new Random();
        List<String> pc = new ArrayList<>(possibleCandidates);
        String[] candidate = new String[size];
        for (int i = 0; i < size; i++) {
            String gene = pc.get(rg.nextInt(pc.size()));
            candidate[i] = gene;
            pc.remove(gene);
        }
        return candidate;
    }

}
