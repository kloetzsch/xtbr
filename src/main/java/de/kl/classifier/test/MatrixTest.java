package de.kl.classifier.test;

import de.kl.classifier.Classification;
import de.kl.classifier.Classifier;
import de.kl.classifier.dict.CategoryDictionary;
import de.kl.classifier.dict.FeatureDictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author konrad
 */
@Component
public class MatrixTest
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MatrixTest.class);

    private final FeatureDictionary featureDictionary;
    private final CategoryDictionary categoryDictionary;

    @Autowired
    public MatrixTest(
            FeatureDictionary featureDictionary,
            CategoryDictionary categoryDictionary
    )
    {
        this.featureDictionary = featureDictionary;
        this.categoryDictionary = categoryDictionary;
    }

    public int[][] getTestMatrix()
    {
        int[][] testCounts = getTestCounts();
        int[][] returnValue = new int[testCounts.length][testCounts.length + 1];
        for (int rowCounter = 0; rowCounter < testCounts.length; rowCounter++) {
            int sum = IntStream.of(testCounts[rowCounter]).sum();
            for (int columnCounter = 0; columnCounter < testCounts[rowCounter].length; columnCounter++) {
                if (sum == 0) {
                    returnValue[rowCounter][columnCounter] = 0;
                } else {
                    returnValue[rowCounter][columnCounter] = testCounts[rowCounter][columnCounter] * 100 / sum;
                }
            }
            returnValue[rowCounter][testCounts.length] = sum;
        }
        return returnValue;
    }

    private int[][] getTestCounts()
    {
        Classifier classifier = new Classifier();
        List<String> categories = this.categoryDictionary.getCategories();
        int[][] returnValue = new int[categories.size()][categories.size()];
        int i = 0;
        List<Classification> testList = new ArrayList<>();
        for (Classification entry : featureDictionary.getAllFeature()) {
            if (i % 2 == 0) {
                classifier.learn(entry);
            } else {
                testList.add(entry);
            }
            i++;
        }
        for (Classification testEntry : testList) {
            Classification result = classifier.classify(testEntry.getFeatureset());
            returnValue[categories.indexOf(testEntry.getCategory())][categories.indexOf(result.getCategory())]++;
            if (!testEntry.getCategory().equals(result.getCategory())) {
                LOGGER.info("expected: {}, actual: {} for {}.", testEntry.getCategory(), result.getCategory(), testEntry.getFeatureset());
            }
        }
        return returnValue;
    }
}
