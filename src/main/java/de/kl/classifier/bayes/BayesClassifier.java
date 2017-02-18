package de.kl.classifier.bayes;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import de.kl.classifier.Classification;
import de.kl.classifier.Classifier;

/**
 * A concrete implementation of the abstract Classifier class.  The Bayes
 * classifier implements a naive Bayes approach to classifying a given set of
 * features: classify(feat1,...,featN) = argmax(P(cat)*PROD(P(featI|cat)
 *
 *
 * @see http://en.wikipedia.org/wiki/Naive_Bayes_classifier
 *
 */
public class BayesClassifier extends Classifier {

    /**
     * Calculates the product of all feature probabilities: PROD(P(featI|cat)
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The product of all feature probabilities.
     */
    private float featuresProbabilityProduct(Collection<String> features,
            String category) {
        float product = 1.0f;
        for (String feature : features)
            product *= this.featureWeighedAverage(feature, category);
        return product;
    }

    /**
     * Calculates the probability that the features can be classified as the
     * category given.
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The probability that the features can be classified as the
     *    category.
     */
    private float categoryProbability(Collection<String> features, String category) {
        return ((float) this.categoryCount(category)
                    / (float) this.getCategoriesTotal())
                * featuresProbabilityProduct(features, category);
    }

    /**
     * Retrieves a sorted <code>Set</code> of probabilities that the given set
     * of features is classified as the available categories.
     *
     * @param features The set of features to use.
     * @return A sorted <code>Set</code> of category-probability-entries.
     */
    private SortedSet<Classification> categoryProbabilities(
            Collection<String> features) {

        /*
         * Sort the set according to the possibilities. Because we have to sort
         * by the mapped value and not by the mapped key, we can not use a
         * sorted tree (TreeMap) and we have to use a set-entry approach to
         * achieve the desired functionality. A custom comparator is therefore
         * needed.
         */
        SortedSet<Classification> probabilities =
                new TreeSet<>(
                        (Classification o1, Classification o2) -> {
                            int toReturn = Float.compare(
                                    o1.getProbability(), o2.getProbability());
                            return toReturn;
        });

        for (String category : this.getCategories())
            probabilities.add(new Classification(
                    features, category,
                    this.categoryProbability(features, category)));
        return probabilities;
    }

    /**
     * Classifies the given set of features.
     *
     * @return The category the set of features is classified as.
     */
    @Override
    public Classification classify(Collection<String> features) {
        SortedSet<Classification> probabilites =
                this.categoryProbabilities(features);

        if (probabilites.size() > 0) {
            return probabilites.last();
        }
        return null;
    }

    /**
     * Classifies the given set of features. and return the full details of the
     * classification.
     *
     * @param features
     * @return The set of categories the set of features is classified as.
     */
    @Override
    public Collection<Classification> classifyDetailed(
            Collection<String> features) {
        return this.categoryProbabilities(features);
        
    }

}
