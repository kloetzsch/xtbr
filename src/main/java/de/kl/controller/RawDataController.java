/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kl.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author konrad
 */
@RequestMapping("data/raw")
@Controller
public class RawDataController
{

    private final MongoTemplate mongoTemplate;
    private final DBCollection rawDataCollection;

    @Autowired
    public RawDataController(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
        this.rawDataCollection = this.mongoTemplate.getCollection("rawData");
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getFirstRawRecord(Model model)
    {
        model.addAttribute("recordNumber", 1);
        return this.getRawRecord(model, 0);
    }

    @RequestMapping(value = "/{recordNumber}", method = RequestMethod.GET)
    public String getFirstRawRecord(Model model, @PathVariable("recordNumber") int recordNumber)
    {
        if (recordNumber < 1) {
            recordNumber = 1;
        }
        int collectionCount = (int) rawDataCollection.count();
        if (recordNumber > collectionCount) {
            recordNumber = collectionCount;
        }
        model.addAttribute("recordNumber", recordNumber);
        return this.getRawRecord(model, recordNumber-1);
    }

    private String getRawRecord(Model model, int numToSkip)
    {
        DBObject findOne = rawDataCollection.findOne(new BasicDBObject(), new DBCollectionFindOptions().skip(numToSkip));
        model.addAttribute("rawData", findOne);
        return "data/raw";
    }
}
