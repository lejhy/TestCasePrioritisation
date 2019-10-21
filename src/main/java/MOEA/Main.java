package MOEA;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        //configure and run this experiment
        List<NondominatedPopulation> result = new Executor()
                .withProblem("UF1")
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(100000)
                .runSeeds(10);

        //display the results
        System.out.format("Objective1  Objective2%n");
        for (NondominatedPopulation population: result) {
            for (Solution solution : population) {
                System.out.format("%.4f      %.4f%n",
                        solution.getObjective(0),
                        solution.getObjective(1));
            }
        }
    }

}
