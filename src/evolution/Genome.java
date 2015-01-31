package evolution;

import util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class Genome {
    public final static int GENE_STATES = 16;
    private final static double MUTATION_RATE = 0.1;
    private final static double DUPLICATION_RATE = 0.02;
    private final static double REDUCTION_RATE = 0.02;
    private final static Random RANDOM = new Random();

    private final int[] genes;

    public Genome() {
        genes = new int[1];
        genes[0] = RANDOM.nextInt(GENE_STATES);
    }

    private Genome(final int[] genes) {
        this.genes = genes;
    }

    public double getGene(int index) {
        final int gene = genes[index % genes.length];
        return (double) gene / (GENE_STATES - 1);
    }

    public int getGenomeLength() {
        return genes.length;
    }

    public static Genome[] recombine(final Optional<Genome> parent1, final Optional<Genome> parent2, final int offspringCnt) {
        final Genome[] offspring = new Genome[offspringCnt];
        int curOffspringCnt = 0;

        final int[] genesParent1 = getGenes(parent1);
        final int[] genesParent2 = getGenes(parent2);
        final int shorterLength = Math.min(genesParent1.length, genesParent2.length);
        final int crossOverPoint = RANDOM.nextInt(shorterLength + 1);
        while (curOffspringCnt < offspringCnt) {
            final int[] genesOffspring1 = getRandomOffspringGenes(genesParent1, genesParent2, crossOverPoint);
            if (genesOffspring1.length > 0) {
                offspring[curOffspringCnt++] = new Genome(genesOffspring1);
            }

            if (curOffspringCnt < offspringCnt) {
                final int[] genesOffspring2 = getRandomOffspringGenes(genesParent2, genesParent1, crossOverPoint);
                if (genesOffspring2.length > 0) {
                    offspring[curOffspringCnt++] = new Genome(genesOffspring2);
                }
            }
        }

        return offspring;
    }

    private static int[] getGenes(final Optional<Genome> genome) {
        return genome.map(new Function<Genome, int[]>() {
            @Override
            public int[] apply(final Genome genome) {
                return genome.genes;
            }
        }).orElse(new int[0]);
    }

    private static int[] getRandomOffspringGenes(final int[] genesParent1, final int[] genesParent2, final int crossOverPoint) {
        final int factor = RANDOM.nextDouble() < DUPLICATION_RATE ? 2 : 1;
        final int addend = RANDOM.nextDouble() < REDUCTION_RATE ? -1 : 0;
        final int[] genesOffspring = getOffspringGenes(genesParent1, genesParent2, crossOverPoint, factor, addend);

        if (genesOffspring.length > 0) {
            final int mutationCnt = (int) Math.round(MUTATION_RATE * genesOffspring.length);
            final ArrayList<Pair<Integer, Integer>> mutations = getRandomMutations(genesOffspring, mutationCnt);
            applyMutations(genesOffspring, mutations);
        }

        return genesOffspring;
    }

    public static int[] getOffspringGenes(final int[] genesParent1,
                                          final int[] genesParent2,
                                          final int crossOverPoint,
                                          final int factor,
                                          final int addend) {
        final int genomeLengthOffspring = genesParent2.length * factor + addend;
        if (genomeLengthOffspring < 1) {
            return new int[0];
        }

        final int[] genesOffspring = new int[genomeLengthOffspring];
        int locus = 0;
        while (locus < genomeLengthOffspring) {
            final int copyLengthParent1 = Math.min(crossOverPoint, genomeLengthOffspring - locus);
            System.arraycopy(genesParent1, 0, genesOffspring, locus, copyLengthParent1);
            locus += copyLengthParent1;
            final int copyLengthParent2 = Math.min(genesParent2.length - copyLengthParent1, genomeLengthOffspring - locus);
            System.arraycopy(genesParent2, copyLengthParent1, genesOffspring, locus, copyLengthParent2);
            locus += copyLengthParent2;
        }
        return genesOffspring;
    }

    private static ArrayList<Pair<Integer, Integer>> getRandomMutations(final int[] genes, final int mutationCnt) {
        final ArrayList<Pair<Integer, Integer>> mutations = new ArrayList<Pair<Integer, Integer>>();
        for (int i = 0; i < mutationCnt; ++i) {
            final int locus = RANDOM.nextInt(genes.length);
            final int change = RANDOM.nextBoolean() ? 1 : -1;
            mutations.add(new Pair<Integer, Integer>(locus, change));
        }
        return mutations;
    }

    public static void applyMutations(final int[] genes, final ArrayList<Pair<Integer, Integer>> mutations) {
        for (final Pair<Integer, Integer> mutation : mutations) {
            genes[mutation.a()] = Math.min(Math.max(genes[mutation.a()] + mutation.b(), 0), GENE_STATES - 1);
        }
    }
}
