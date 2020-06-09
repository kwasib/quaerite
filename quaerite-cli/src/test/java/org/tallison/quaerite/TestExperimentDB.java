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

package org.tallison.quaerite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tallison.quaerite.core.Experiment;
import org.tallison.quaerite.core.ExperimentSet;
import org.tallison.quaerite.core.JudgmentList;
import org.tallison.quaerite.core.Judgments;
import org.tallison.quaerite.core.QueryInfo;
import org.tallison.quaerite.core.QueryStrings;
import org.tallison.quaerite.core.ServerConnection;
import org.tallison.quaerite.core.features.WeightableField;
import org.tallison.quaerite.core.features.WeightableListFeature;
import org.tallison.quaerite.core.queries.EDisMaxQuery;
import org.tallison.quaerite.core.queries.LuceneQuery;
import org.tallison.quaerite.core.queries.Query;
import org.tallison.quaerite.core.scorers.AtLeastOneAtN;
import org.tallison.quaerite.db.ExperimentDB;

public class TestExperimentDB {
    private static Path DB_DIR;

    @BeforeAll
    public static void init() throws Exception {
        DB_DIR = Files.createTempDirectory("exp-");
    }

    @AfterAll
    public static void tearDown() throws Exception {
        FileUtils.deleteDirectory(DB_DIR.toFile());
    }

    @Test
    public void testBasicDB() throws Exception {
        ExperimentDB db = ExperimentDB.open(DB_DIR);

        WeightableListFeature weightableListFeature = new WeightableListFeature("qf");
        weightableListFeature.add(new WeightableField("f1^2"));
        weightableListFeature.add(new WeightableField("f2^5"));
        weightableListFeature.add(new WeightableField("f3^10"));

        EDisMaxQuery q = new EDisMaxQuery("actualQuery");
        q.getQF().addAll(weightableListFeature.getWeightableFields());
        Experiment experiment = new Experiment("test1",
                new ServerConnection("http://solr"), q);

        List<Query> filterQueries = new ArrayList<>();
        for (String fq : new String[]{"fq1", "fq2"}) {
            filterQueries.add(new LuceneQuery("defaultField", fq));
        }
        experiment.addFilterQueries(filterQueries);
        db.addExperiment(experiment);
        db.addScorer(new AtLeastOneAtN(1));
        db.addScorer(new AtLeastOneAtN(3));
        db.addScorer(new AtLeastOneAtN(5));
        db.addScorer(new AtLeastOneAtN(10));
        db.close();

        db = ExperimentDB.open(DB_DIR);

        ExperimentSet experimentSet = db.getExperiments();

        Experiment revivified = null;
        for (Experiment e : experimentSet.getExperiments().values()) {
            revivified = e;
            break;
        }

        assertEquals("test1", revivified.getName());
        assertEquals("http://solr", revivified.getServerConnection().getURL());
        List<Query> filterQueries2 = revivified.getFilterQueries();
        assertEquals(2, filterQueries2.size());
        assertIterableEquals(filterQueries, filterQueries2);
        EDisMaxQuery q2 = (EDisMaxQuery) revivified.getQuery();

        //the query string is transient and not serialized
        EDisMaxQuery expected = q.deepCopy();
        assertEquals(expected, q2);
        db.close();

        db = ExperimentDB.open(DB_DIR);

        QueryStrings queryStrings = new QueryStrings();
        queryStrings.setQuery("query1");
        Judgments judgments = new Judgments(new QueryInfo("q1",
                "", queryStrings, 1));

        judgments.addJudgment("id1", 2.0);
        judgments.addJudgment("id2", 4.0);
        judgments.addJudgment("id5", 6.0);

        db.addJudgment(judgments);
        db.close();

        db = ExperimentDB.open(DB_DIR);
        JudgmentList judgmentsList = db.getJudgments();
        assertEquals(1, judgmentsList.getJudgmentsList().size());

        Judgments revivifiedJudgments = judgmentsList.getJudgmentsList().get(0);
        assertEquals("q1", revivifiedJudgments.getQueryInfo().getQueryId());
        assertEquals(4.0, revivifiedJudgments.getJudgment("id2"), 0.01);
        db.close();

    }

}
