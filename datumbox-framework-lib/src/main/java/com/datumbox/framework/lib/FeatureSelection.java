package com.datumbox.framework.lib;

import com.datumbox.framework.common.ConfigurableFactory;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.text.extractors.UniqueWordSequenceExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.featureselection.ChisquareSelect;
import com.sun.deploy.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by cventdev on 4/19/17.
 */
public class FeatureSelection {

    public void testSelectFeatures(String baseDataFolder, int maxFeatureCount, double aLevel) {
        //logger.info("selectFeatures");
        //createFile("Testing", baseDataFolder + "/abc.txt");
        System.out.println("Getting configurations.........");
        Configuration configuration = getConfiguration();
        System.out.println("basefolder:" + baseDataFolder);
//        Dataframe[] data = Datasets.featureSelectorCategorical(configuration, 1000);
//        Dataframe trainingData = data[0];
//        Dataframe validationData = data[1];
        System.out.println("Preparing Feature Set Data.........");
        Map<Object, List<URI>> trainingDatasets = createTrainingMapForDataFrame2(baseDataFolder);
        Map<Object, List<URI>> validationDatasets = createTestingMapForDataFrame(baseDataFolder);
        System.out.println("Feature Set Data Prepared.........");

        System.out.println("Setting threshold Parameters and chi aquare algorithm alpha level.........");
        String storageName = this.getClass().getSimpleName();
        ChisquareSelect.TrainingParameters param = new ChisquareSelect.TrainingParameters();
        param.setRareFeatureThreshold(2);
        param.setMaxFeatures(20);
        param.setALevel(0.7);
        System.out.println("Initializing word sequence extractor.........");
        UniqueWordSequenceExtractor extractor = new UniqueWordSequenceExtractor(new UniqueWordSequenceExtractor
                .Parameters(), baseDataFolder + "/outliers.txt");

        System.out.println("Extracting all features.........");
        Dataframe trainingData1 = Dataframe.Builder.parseTextFiles2(trainingDatasets, extractor, configuration);
        Dataframe validationData1 = Dataframe.Builder.parseTextFiles2(validationDatasets, extractor, configuration);
        System.out.println("Found size: " + trainingData1);
        ChisquareSelect instance = MLBuilder.create(param, configuration);

        System.out.println("Running algo for feature Selection.........");
        instance.fit_transform(trainingData1);
        System.out.println("Running algo for feature Selected.........");
        instance.save(storageName);
        System.out.println("Running algo for feature Selected save.........");
        instance.close();



        instance = MLBuilder.load(ChisquareSelect.class, storageName, configuration);

        instance.transform(validationData1);

        Set<Object> expResult = new HashSet<>(
                Arrays.asList("high_paid", "has_boat", "has_luxury_car", "has_butler", "has_pool"));
        Set<Object> result = trainingData1.getXDataTypes().keySet();
        String features =  StringUtils.join(result, ",");
        createFile(features, baseDataFolder + "/SelectedFeatures.txt");


        //assertEquals(1, 1);
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

    protected Configuration getConfiguration() {
        String storageEngine = System.getProperty("storageEngine");
        if (storageEngine == null) {
            return Configuration.getConfiguration();
        }
        else {
            Properties p = new Properties();
            if ("InMemory".equals(storageEngine)) {
                p.setProperty("configuration.storageConfiguration", "com.datumbox.framework.storage.inmemory.InMemoryConfiguration");

            }
            else if ("MapDB".equals(storageEngine)) {
                p.setProperty("configuration.storageConfiguration", "com.datumbox.framework.storage.mapdb.MapDBConfiguration");
            }
            else {
                throw new IllegalArgumentException("Unsupported option.");
            }
            return ConfigurableFactory.getConfiguration(Configuration.class, p);
        }
    }

    private Map<Object, List<URI>> createTrainingMapForDataFrame2(String baseDataFolder) {
        File folder = new File(baseDataFolder + "/TRAININGDATA");
        System.out.println("location: " + baseDataFolder + "/TRAININGDATA" + "  size: " + folder.listFiles().length);
        Map<Object, List<URI>> map = new HashMap<>();
        List<URI> list = new ArrayList<URI>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                String clss = file.getName();
                list = new ArrayList<URI>();
                for (File file1 : file.listFiles()) {
                    if (file1.isFile()) {
                        list.add(file1.toURI());
                    }
                }
                map.put(clss, list);
            }
        }
        return map;
    }

    private Map<Object, List<URI>> createTestingMapForDataFrame(String baseDataFolder) {
        File folder = new File(baseDataFolder + "/TRAININGDATA");

        Map<Object, List<URI>> map = new HashMap<>();
        List<URI> list = new ArrayList<URI>();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                list.add(file.toURI());
            }
        }
        map.put("SPAM", list);
        return map;
    }
}
