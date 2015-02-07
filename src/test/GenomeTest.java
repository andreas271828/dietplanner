package test;

import evolution.Genome;
import util.Pair;

import java.util.ArrayList;

public class GenomeTest {
    public static void runTests() {
        runGetOffspringGenesTests();
        runApplyMutationsTests();
    }

    private static void runGetOffspringGenesTests() {
        final int[] genesParent1 = {0, 1, 2};
        final int[] genesParent2 = {5, 4, 3, 2, 1};
        Test.testArray(genesParent2, Genome.getOffspringGenes(genesParent1, genesParent2, 0));
        Test.testArray(genesParent1, Genome.getOffspringGenes(genesParent2, genesParent1, 0));
        Test.testArray(new int[]{0, 4, 3, 2, 1}, Genome.getOffspringGenes(genesParent1, genesParent2, 1));
        Test.testArray(new int[]{0, 1, 2, 2, 1}, Genome.getOffspringGenes(genesParent1, genesParent2, 3));
        Test.testArray(new int[]{5, 4, 3}, Genome.getOffspringGenes(genesParent2, genesParent1, 3));
    }

    private static void runApplyMutationsTests() {
        final int[] genes = {0, 1, 2, 3, 4};

        final ArrayList<Pair<Integer, Integer>> mutations1 = new ArrayList<Pair<Integer, Integer>>();
        mutations1.add(new Pair<Integer, Integer>(0, -1));
        Genome.applyMutations(genes, mutations1);
        Test.testArray(new int[]{0, 1, 2, 3, 4}, genes);

        final ArrayList<Pair<Integer, Integer>> mutations2 = new ArrayList<Pair<Integer, Integer>>();
        mutations2.add(new Pair<Integer, Integer>(2, 1));
        Genome.applyMutations(genes, mutations2);
        Test.testArray(new int[]{0, 1, 3, 3, 4}, genes);

        Genome.applyMutations(genes, mutations2);
        Test.testArray(new int[]{0, 1, 4, 3, 4}, genes);

        final ArrayList<Pair<Integer, Integer>> mutations3 = new ArrayList<Pair<Integer, Integer>>();
        mutations3.add(new Pair<Integer, Integer>(3, 100));
        Genome.applyMutations(genes, mutations3);
        Test.testArray(new int[]{0, 1, 4, Genome.GENE_STATES - 1, 4}, genes);
    }
}
