/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tallison.quaerite.core.features.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tallison.quaerite.core.ExperimentFactory;
import org.tallison.quaerite.core.GAConfig;
import org.tallison.quaerite.core.features.Feature;
import org.tallison.quaerite.core.features.QF;
import org.tallison.quaerite.core.features.WeightableField;
import org.tallison.quaerite.core.features.WeightableListFeature;


public class TestFeatureFactories {

    @Test
    public void testSimpleQF() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("a");
        fields.add("b^3.2");
        fields.add("c^1.6");
        fields.add("d");
        List<Float> defaultWeights = new ArrayList<>();
        defaultWeights.add(0.0f);
        defaultWeights.add(1.0f);
        defaultWeights.add(2.0f);

        WeightableListFeatureFactory qf = new WeightableListFeatureFactory<QF>(
                "qf", QF.class, fields, defaultWeights, 1,-1);
        //test random
        for (int i = 0; i < 10; i++) {
            boolean foundAuthor = false;
            boolean foundContent = false;
            WeightableListFeature list = (WeightableListFeature)qf.random();
            for (int j = 0; j < list.size(); j++) {
                WeightableField wf = list.get(j);
                if (wf.getFeature().equals("b")) {
                    foundAuthor = true;
                    assertEquals(3.2f, wf.getWeight(), 0.001);
                } else if (wf.getFeature().equals("c")) {
                    foundContent = true;
                    assertEquals(1.6f, wf.getWeight(), 0.001);
                } else {
                    Float weight = wf.getWeight();
                    assertNotNull(weight);
                    assertTrue(weight >= 0.0f && weight <= 10.0f);
                }
            }
            assertTrue(foundAuthor);
            assertTrue(foundContent);
        }

        List<Feature> permutations = qf.permute(200);
        assertEquals(19, permutations.size());
    }

    @Test
    public void testSimplerQF() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("a");
        fields.add("b");
        fields.add("c");
        fields.add("d");
        List<Float> defaultWeights = new ArrayList<>();

        WeightableListFeatureFactory qf = new WeightableListFeatureFactory<QF>(
                "qf", QF.class, fields, defaultWeights, 1, 3);
        List<WeightableListFeature> list = qf.permute(1000);
        assertEquals(9, list.size());
    }

    @Test
    public void testQFNoFixedWeights() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("a");
        fields.add("b");
        fields.add("c");
        fields.add("d");
        List<Float> defaultWeights = new ArrayList<>();
        defaultWeights.add(0.0f);
        defaultWeights.add(1.0f);

        WeightableListFeatureFactory<QF> qf =
                new WeightableListFeatureFactory<>("qf", QF.class, fields, defaultWeights, 1,  -1);
        assertEquals(15, qf.permute(1000).size());
        defaultWeights.add(2.0f);
        qf = new WeightableListFeatureFactory("qf", QF.class, fields, defaultWeights, 1, -1);
        assertEquals(80, qf.permute(1000).size());

        qf = new WeightableListFeatureFactory("qf", QF.class, fields, defaultWeights, 1, 2);

        assertEquals(32, qf.permute(1000).size());
    }



    @Test
    public void testMultipleTrainScorers() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ExperimentFactory.fromJson(newReader(
                    "/test-documents/experiment_features_solr_2.json")).getTrainScorer();
        });
    }


    @Test
    public void testGAConfigSerialization() throws Exception {
        ExperimentFactory experimentFactory = ExperimentFactory.fromJson(
                newReader("/test-documents/experiment_features_solr_3.json")
        );
        GAConfig gaConfig = experimentFactory.getGAConfig();
        assertEquals(20, gaConfig.getNumThreads());
        assertEquals("customIdField", gaConfig.getIdField());
        assertEquals(100, gaConfig.getGenerations());
        assertEquals(2, gaConfig.getNFolds());
        assertEquals(50, gaConfig.getPopulation());
        assertEquals(0.001f, gaConfig.getMutationAmplitude(), 0.00001);
        assertEquals(0.2f, gaConfig.getMutationProbability(), 0.00001);
        assertEquals(0.1f, gaConfig.getCrossoverProbability(), 0.00001);
        assertEquals(0.7f, gaConfig.getReproductionProbability(), 0.00001);
    }

    private Reader newReader(String path) {
        return new BufferedReader(
                new InputStreamReader(
                        TestFeatureFactories.class.getResourceAsStream(path),
                        StandardCharsets.UTF_8
                )
        );
    }
}
