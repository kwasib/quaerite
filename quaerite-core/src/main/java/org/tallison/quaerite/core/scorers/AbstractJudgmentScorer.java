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
 *
 */
package org.tallison.quaerite.core.scorers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tallison.quaerite.core.Judgments;
import org.tallison.quaerite.core.SearchResultSet;

public abstract class AbstractJudgmentScorer
        extends DistributionalScoreAggregator implements JudgmentScorer {

    public static double ERROR_VALUE = Double.MAX_VALUE;
    private boolean useForTrain = false;
    private boolean useForTest = false;
    private boolean exportPMatrix = false;
    private Map<String, String> params;

    public AbstractJudgmentScorer(String name, int atN) {
        super(name, atN);
        params = Collections.EMPTY_MAP;
    }

    public AbstractJudgmentScorer(String name, int atN, Map<String, String> params) {
        super(name, atN);
        this.params = new HashMap<>();
        this.params.putAll(params);
    }

    public abstract double score(Judgments judgments,
                                 SearchResultSet searchResultSet);




    public boolean getUseForTrain() {
        return useForTrain;
    }

    public void setUseForTrain() {
        this.useForTrain = true;
    }

    public boolean getUseForTest() {
        return useForTest;
    }

    public void setUseForTest() {
        this.useForTest = true;
    }

    public boolean getExportPMatrix() {
        return exportPMatrix;
    }

    public void setExportPMatrix() {
        this.exportPMatrix = true;
    }

    public void setParams(Map<String, String> map) {
        params.clear();
        params.putAll(map);
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "AbstractJudgmentScorer{" +
                "useForTrain=" + useForTrain +
                ", useForTest=" + useForTest +
                ", exportPMatrix=" + exportPMatrix +
                ", params=" + params +
                '}';
    }
}
