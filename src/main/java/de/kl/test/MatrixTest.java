/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.test;

import de.kl.classifier.Classification;
import de.kl.classifier.Classifier;
import de.kl.classifier.token.Tokenizer;
import de.kl.dict.CategoryDictionary;
import de.kl.dict.FeatureDictionary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author konrad
 */
@Component
public class MatrixTest
{

    private final Classifier classifier;
    private final FeatureDictionary featureDictionary;
    private final Tokenizer tokenizer;
    private final CategoryDictionary categoryDictionary;

    @Autowired
    public MatrixTest(
            Classifier bayes,
            FeatureDictionary featureDictionary,
            CategoryDictionary categoryDictionary,
            Tokenizer tokenizer
    )
    {
        this.classifier = bayes;
        this.featureDictionary = featureDictionary;
        this.categoryDictionary = categoryDictionary;
        this.tokenizer = tokenizer;
    }

    public int[][] getTestMatrix()
    {
        int[][] testCounts = getTestCounts();
        int[][] returnValue = new int[testCounts.length][testCounts.length+1];
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
        List<String> categories = this.categoryDictionary.getCategories();
        int[][] returnValue = new int[categories.size()][categories.size()];
        int i = 0;
        List<Pair<String, String>> testList = new ArrayList<>();
        for (Pair<String, String> entry : featureDictionary.getAllFeature()) {
            if (i % 2 == 0) {
                classifier.learn(entry.getRight(), Arrays.asList(tokenizer.tokenize(entry.getLeft())));
            } else {
                testList.add(entry);
            }
            i++;
        }
        for (Pair<String, String> testEntry : testList) {
            Classification result = classifier.classify(tokenizer.tokenize(testEntry.getLeft()));
            returnValue[categories.indexOf(result.getCategory())][categories.indexOf(testEntry.getRight())]++;
        }
        return returnValue;
    }
}
