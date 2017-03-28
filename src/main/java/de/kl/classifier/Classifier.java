package de.kl.classifier;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Abstract base extended by any concrete classifier. It implements the basic functionality for storing categories or
 * features and can be used to calculate basic probabilities – both category and feature probabilities. The classify
 * function has to be implemented by the concrete classifier class.
 *
 *
 */
public class Classifier
{

    /**
     * Initial capacity of category dictionaries.
     */
    private static final int INITIAL_CATEGORY_DICTIONARY_CAPACITY = 16;

    /**
     * Initial capacity of feature dictionaries. It should be quite big, because the features will quickly outnumber the
     * categories.
     */
    private static final int INITIAL_FEATURE_DICTIONARY_CAPACITY = 32;

    /**
     * A dictionary mapping features to their number of occurrences in each known category.
     */
    private final Map<String, Map<String, Integer>> featureCountPerCategory;

    /**
     * A dictionary mapping features to their number of occurrences.
     */
    private final Map<String, Integer> totalFeatureCount;

    /**
     * A dictionary mapping categories to their number of occurrences.
     */
    private final Map<String, Integer> totalCategoryCount;

    /**
     * Constructs a new classifier without any trained knowledge.
     */
    public Classifier()
    {
        this.featureCountPerCategory
                = new HashMap<>(
                        Classifier.INITIAL_CATEGORY_DICTIONARY_CAPACITY);
        this.totalFeatureCount
                = new HashMap<>(
                        Classifier.INITIAL_FEATURE_DICTIONARY_CAPACITY);
        this.totalCategoryCount
                = new HashMap<>(
                        Classifier.INITIAL_CATEGORY_DICTIONARY_CAPACITY);
    }

    /**
     * Returns a <code>Set</code> of features the classifier knows about.
     *
     * @return The <code>Set</code> of features the classifier knows about.
     */
    public Set<String> getFeatures()
    {
        return this.totalFeatureCount.keySet();
    }

    /**
     * Returns a <code>Set</code> of categories the classifier knows about.
     *
     * @return The <code>Set</code> of categories the classifier knows about.
     */
    public Set<String> getCategories()
    {
        return this.totalCategoryCount.keySet();
    }

    /**
     * Retrieves the total number of categories the classifier knows about.
     *
     * @return The total category count.
     */
    public int getCategoriesTotal()
    {
        int toReturn = 0;
        for (Integer categoryCount : this.totalCategoryCount.values()) {
            toReturn += categoryCount;
        }
        return toReturn;
    }

    /**
     * Increments the count of a given feature in the given category. This is equal to telling the classifier, that this
     * feature has occurred in this category.
     *
     * @param feature The feature, which count to increase.
     * @param category The category the feature occurred in.
     */
    public void incrementFeature(String feature, String category)
    {
        Map<String, Integer> features
                = this.featureCountPerCategory.get(category);
        if (features == null) {
            this.featureCountPerCategory.put(category,
                    new HashMap<>(
                            Classifier.INITIAL_FEATURE_DICTIONARY_CAPACITY));
            features = this.featureCountPerCategory.get(category);
        }
        Integer count = features.get(feature);
        if (count == null) {
            features.put(feature, 0);
            count = features.get(feature);
        }
        features.put(feature, ++count);

        Integer totalCount = this.totalFeatureCount.get(feature);
        if (totalCount == null) {
            this.totalFeatureCount.put(feature, 0);
            totalCount = this.totalFeatureCount.get(feature);
        }
        this.totalFeatureCount.put(feature, ++totalCount);
    }

    /**
     * Increments the count of a given category. This is equal to telling the classifier, that this category has
     * occurred once more.
     *
     * @param category The category, which count to increase.
     */
    public void incrementCategory(String category)
    {
        Integer count = this.totalCategoryCount.get(category);
        if (count == null) {
            this.totalCategoryCount.put(category, 0);
            count = this.totalCategoryCount.get(category);
        }
        this.totalCategoryCount.put(category, ++count);
    }

    /**
     * Retrieves the number of occurrences of the given feature in the given category.
     *
     * @param feature The feature, which count to retrieve.
     * @param category The category, which the feature occurred in.
     * @return The number of occurrences of the feature in the category.
     */
    public int featureCount(String feature, String category)
    {
        Map<String, Integer> features
                = this.featureCountPerCategory.get(category);
        if (features == null) {
            return 0;
        }
        Integer count = features.get(feature);
        return (count == null) ? 0 : count;
    }

    /**
     * Retrieves the number of occurrences of the given category.
     *
     * @param category The category, which count should be retrieved.
     * @return The number of occurrences.
     */
    public int categoryCount(String category)
    {
        Integer count = this.totalCategoryCount.get(category);
        return (count == null) ? 0 : count;
    }

    /**
     * {@inheritDoc}
     */
    public float featureProbability(String feature, String category)
    {
        if (this.categoryCount(category) == 0) {
            return 0;
        }
        return (float) this.featureCount(feature, category)
                / (float) this.categoryCount(category);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with overall weight of <code>1.0</code> and an
     * assumed probability of <code>0.5</code>. The probability defaults to the overall feature probability.
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @return The weighed average probability.
     */
    public float featureWeighedAverage(String feature, String category)
    {
        return this.featureWeighedAverage(feature, category,
                null, 1.0f, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with overall weight of <code>1.0</code>, an
     * assumed probability of <code>0.5</code> and the given object to use for probability calculation.
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @return The weighed average probability.
     */
    public float featureWeighedAverage(String feature, String category,
            Classifier calculator)
    {
        return this.featureWeighedAverage(feature, category,
                calculator, 1.0f, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with the given weight and an assumed probability
     * of <code>0.5</code> and the given object to use for probability calculation.
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @param weight The feature weight.
     * @return The weighed average probability.
     */
    public float featureWeighedAverage(String feature, String category,
            Classifier calculator, float weight)
    {
        return this.featureWeighedAverage(feature, category,
                calculator, weight, 0.5f);
    }

    /**
     * Retrieves the weighed average <code>P(feature|category)</code> with the given weight, the given assumed
     * probability and the given object to use for probability calculation.
     *
     * @param feature The feature, which probability to calculate.
     * @param category The category.
     * @param calculator The calculating object.
     * @param weight The feature weight.
     * @param assumedProbability The assumed probability.
     * @return The weighed average probability.
     */
    public float featureWeighedAverage(String feature, String category,
            Classifier calculator, float weight,
            float assumedProbability)
    {

        /*
         * use the given calculating object or the default method to calculate
         * the probability that the given feature occurred in the given
         * category.
         */
        final float basicProbability
                = (calculator == null)
                        ? this.featureProbability(feature, category)
                        : calculator.featureProbability(feature, category);

        Integer totals = this.totalFeatureCount.get(feature);
        if (totals == null) {
            totals = 0;
        }
        return (weight * assumedProbability + totals * basicProbability)
                / (weight + totals);
    }

    /**
     * Train the classifier by telling it that the given features resulted in the given category.
     *
     * @param category The category the features belong to.
     * @param features The features that resulted in the given category.
     */
    public void learn(String category, Collection<String> features)
    {
        this.learn(new Classification(features, category));
    }

    /**
     * Train the classifier by telling it that the given features resulted in the given category.
     *
     * @param classification The classification to learn.
     */
    public void learn(Classification classification)
    {

        for (String feature : classification.getFeatureset()) {
            this.incrementFeature(feature, classification.getCategory());
        }
        this.incrementCategory(classification.getCategory());

    }

    /**
     * Retrieves a sorted <code>Set</code> of probabilities that the given set
     * of features is classified as the available categories.
     *
     * @param features The set of features to use.
     * @return A sorted <code>Set</code> of category-probability-entries.
     */
    protected SortedSet<Classification> categoryProbabilities(Collection<String> features)
    {
        /*
         * Sort the set according to the possibilities. Because we have to sort
         * by the mapped value and not by the mapped key, we can not use a
         * sorted tree (TreeMap) and we have to use a set-entry approach to
         * achieve the desired functionality. A custom comparator is therefore
         * needed.
         */
        SortedSet<Classification> probabilities = new TreeSet<>((Classification o1, Classification o2) -> {
            int toReturn = Float.compare(o1.getProbability(), o2.getProbability());
            return toReturn;
        });
        for (String category : this.getCategories()) {
            probabilities.add(new Classification(features, category, this.categoryProbability(features, category)));
        }
        return probabilities;
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
    protected float categoryProbability(Collection<String> features, String category)
    {
        return ((float) this.categoryCount(category) / (float) this.getCategoriesTotal()) * featuresProbabilityProduct(features, category);
    }

    /**
     * Classifies the given set of features.
     *
     * @return The category the set of features is classified as.
     */
    public Classification classify(Collection<String> features)
    {
        SortedSet<Classification> probabilites = this.categoryProbabilities(features);
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
    public Collection<Classification> classifyDetailed(Collection<String> features)
    {
        return this.categoryProbabilities(features);
    }

    /**
     * Calculates the product of all feature probabilities: PROD(P(featI|cat)
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The product of all feature probabilities.
     */
    protected float featuresProbabilityProduct(Collection<String> features, String category)
    {
        float product = 1.0F;
        for (String feature : features) {
            product *= this.featureWeighedAverage(feature, category);
        }
        return product;
    }

}
