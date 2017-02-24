/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.classifier.dict;

import de.kl.classifier.Classification;
import java.util.List;

/**
 *
 * @author konrad
 */
public interface FeatureDictionary
{
    public int addFeature(String feature, String category);
    
    public List<Classification> getAllFeature();
}
