package evolution;

import util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class Genome {
    public static final int GENE_STATES = 16;
    private static final double MUTATION_RATE = 0.1;
    private static final Random RANDOM = new Random();

    private final int[] genes;

    public static Genome genome(final int genomeLength) {
        return new Genome(genomeLength);
    }

    private Genome(final int genomeLength) {
        genes = new int[genomeLength];
        for (int i = 0; i < 1; ++i) {
            genes[i] = RANDOM.nextInt(GENE_STATES);
        }
    }

    private Genome(final int[] genes) {
        this.genes = genes;
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
            final int[] genesOffspring1 = getMutatedOffspringGenes(genesParent1, genesParent2, crossOverPoint);
            if (genesOffspring1.length > 0) {
                offspring[curOffspringCnt++] = new Genome(genesOffspring1);
            }

            if (curOffspringCnt < offspringCnt) {
                final int[] genesOffspring2 = getMutatedOffspringGenes(genesParent2, genesParent1, crossOverPoint);
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

    private static int[] getMutatedOffspringGenes(final int[] genesParent1, final int[] genesParent2, final int crossOverPoint) {
        final int[] genesOffspring = getOffspringGenes(genesParent1, genesParent2, crossOverPoint);

        final int mutationCnt = (int) Math.round(MUTATION_RATE * genesOffspring.length);
        final ArrayList<Pair<Integer, Integer>> mutations = getRandomMutations(genesOffspring, mutationCnt);
        applyMutations(genesOffspring, mutations);

        return genesOffspring;
    }

    public static int[] getOffspringGenes(final int[] genesParent1,
                                          final int[] genesParent2,
                                          final int crossOverPoint) {
        final int[] genesOffspring = new int[genesParent2.length];
        System.arraycopy(genesParent1, 0, genesOffspring, 0, crossOverPoint);
        System.arraycopy(genesParent2, crossOverPoint, genesOffspring, crossOverPoint, genesParent2.length - crossOverPoint);
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

    public class Iterator {
        final Genome genome;
        private int index = 0;

        public Iterator(final Genome genome) {
            this.genome = genome;
        }

        public double getNextGene() {
            final int gene = genome.genes[index];
            if (index == genome.genes.length - 1) {
                index = 0;
            } else {
                ++index;
            }
            return (double) gene / (GENE_STATES - 1);
        }
    }

    public Iterator getIterator() {
        return new Iterator(this);
    }
}
