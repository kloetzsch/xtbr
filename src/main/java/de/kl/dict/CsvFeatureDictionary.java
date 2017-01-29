/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.dict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author konrad
 */
@Component
public class CsvFeatureDictionary implements FeatureDictionary
{

    List<Pair<String, String>> dictionary = new ArrayList<>();
    private final String trainingFile;

    @Autowired
    public CsvFeatureDictionary(
            @Value("${training.file}") String trainingFile
    ) throws IOException
    {
        this.trainingFile = trainingFile;
        File file = new File(trainingFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (Stream<String> stream = Files.lines(Paths.get(trainingFile))) {
            stream.forEach(line -> process(line));
        }
    }

    @Override
    public int addFeature(String feature, String category)
    {
        try {
            this.dictionary.add(Pair.of(feature, category));
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(trainingFile, true), "UTF-8"))) {
                writer.append(category + "\t" + feature.replace("\t", " ")+ "\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return this.dictionary.size();
    }

    @Override
    public List<Pair<String, String>> getAllFeature()
    {
        
        return this.dictionary;
    }

    private void process(String line)
    {
        String category = line.substring(0, line.indexOf("\t"));
        String feature = line.substring(line.indexOf("\t"));
        this.dictionary.add(Pair.of(feature, category));
    }

}
