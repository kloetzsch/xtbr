/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.dict;

import de.kl.classifier.Classification;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author konrad
 */
public interface FeatureDictionary
{
    public int addFeature(String feature, String category);
    
    public List<Pair<String,String>> getAllFeature();
}
