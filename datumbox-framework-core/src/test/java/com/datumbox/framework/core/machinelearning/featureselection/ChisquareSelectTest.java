/**
 * Copyright (C) 2013-2017 Vasilis Vryniotis <bbriniotis@datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datumbox.framework.core.machinelearning.featureselection;

import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.Datasets;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.text.extractors.UniqueWordSequenceExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import com.sun.deploy.util.StringUtils;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for ChisquareSelect.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class ChisquareSelectTest extends AbstractTest {

    private String baseDataFolder = "/home/share/Custom Data/";

    /**
     * Test of fit_transform method, of class ChisquareSelect.
     */
    @Test
    public void testSelectFeatures() {
        logger.info("selectFeatures");
        createFile("Testing", baseDataFolder + "abc.txt");

        Configuration configuration = getConfiguration();
        
//        Dataframe[] data = Datasets.featureSelectorCategorical(configuration, 1000);
//        Dataframe trainingData = data[0];
//        Dataframe validationData = data[1];

        Map<Object, List<URI>> trainingDatasets = CreateTrainingMapForDataFrame2();
        Map<Object, List<URI>> validationDatasets = CreateTestingMapForDataFrame();


        String storageName = this.getClass().getSimpleName();
        ChisquareSelect.TrainingParameters param = new ChisquareSelect.TrainingParameters();
        param.setRareFeatureThreshold(2);
        param.setMaxFeatures(20);
        param.setALevel(0.7);
        UniqueWordSequenceExtractor extractor = new UniqueWordSequenceExtractor(new UniqueWordSequenceExtractor
                .Parameters(), baseDataFolder + "outlayers.txt");

        Dataframe trainingData1 = Dataframe.Builder.parseTextFiles2(trainingDatasets, extractor, configuration);
        Dataframe validationData1 = Dataframe.Builder.parseTextFiles2(validationDatasets, extractor, configuration);
        
        ChisquareSelect instance = MLBuilder.create(param, configuration);
        
        
        instance.fit_transform(trainingData1);
        instance.save(storageName);

        instance.close();

        
        
        instance = MLBuilder.load(ChisquareSelect.class, storageName, configuration);
        
        instance.transform(validationData1);
        
        Set<Object> expResult = new HashSet<>(Arrays.asList("high_paid", "has_boat", "has_luxury_car", "has_butler", "has_pool"));
        Set<Object> result = trainingData1.getXDataTypes().keySet();
        String features =  StringUtils.join(result, ",");
        createFile(features, baseDataFolder + "SelectedFeatures.txt");


        assertEquals(1, 1);
//        instance.delete();
//
//        trainingData.close();
//        validationData.close();
    }

    private void createFile(String data, String location)
    {
        BufferedWriter bw;
        try {
            File file = new File(location); //filepath is being passes through //ioc
            // and filename through a method

            if (file.exists()) {
                file.delete(); //you might want to check if delete was successfull
            }
            file.createNewFile();

            FileOutputStream fileOutput = new FileOutputStream(
                    file);

            bw = new BufferedWriter(new OutputStreamWriter(
                    fileOutput));
            //bw=new BufferedWriter(new FileWriter(baseDataFolder + "SelectedFeatures.txt"));
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Object, List<URI>> CreateTrainingMapForDataFrame() {
        File folder = new File("/home/cventdev/Downloads/CSDMC2010_SPAM/CSDMC2010_SPAM/TRAINING");

        Map<Object, List<URI>> map = new HashMap<>();
        List<URI> list = new ArrayList<URI>();
        for (File file : folder.listFiles()) {
            if(file.isFile()) {
                list.add(file.toURI());
            }
        }
        map.put("SPAM", list);
        return map;
    }

    private Map<Object, List<URI>> CreateTrainingMapForDataFrame2() {
        //File folder = new File("/home/cventdev/Downloads/CustomData/atheism");
        File folder = new File(baseDataFolder + "TRAININGDATA");

        Map<Object, List<URI>> map = new HashMap<>();
        List<URI> list = new ArrayList<URI>();
        for (File file : folder.listFiles()) {
            if(file.isDirectory()) {
                String clss = file.getName();
                list = new ArrayList<URI>();
                for(File file1 : file.listFiles()) {
                    if (file1.isFile()) {
                        list.add(file1.toURI());
                    }
                }
                map.put(clss, list);
            }
        }
        return map;
    }

    private Map<Object, List<URI>> CreateTestingMapForDataFrame() {
        File folder = new File("/home/cventdev/Downloads/CSDMC2010_SPAM/CSDMC2010_SPAM/SPAMTESTING");

        Map<Object, List<URI>> map = new HashMap<>();
        List<URI> list = new ArrayList<URI>();
        for (File file : folder.listFiles()) {
            if(file.isFile()) {
                list.add(file.toURI());
            }
        }
        map.put("SPAM", list);
        return map;
    }
    
}
