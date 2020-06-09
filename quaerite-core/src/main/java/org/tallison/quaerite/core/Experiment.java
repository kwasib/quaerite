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
package org.tallison.quaerite.core;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tallison.quaerite.core.features.CustomHandler;
import org.tallison.quaerite.core.queries.Query;
import org.tallison.quaerite.core.serializers.QuerySerializer;


public class Experiment {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Query.class, new QuerySerializer())
            .create();
    private String name;
    private ServerConnection serverConnection;
    private CustomHandler customHandler;
    private Query query;
    private final List<Query> filterQueries = new ArrayList<>();

    public Experiment(String name, ServerConnection serverConnection,
                      CustomHandler customHandler, Query query) {
        this.customHandler = customHandler;
        this.name = name;
        this.serverConnection = serverConnection;
        this.query = query;
    }

    public Experiment(String name, ServerConnection
            serverConnection, Query query) {
        this(name, serverConnection, null, query);
    }

    //consider adding clone to experiment with a new name
    public void setName(String name) {
        this.name = name;
    }


    public String toJson() {
        return GSON.toJson(this);
    }


    public CustomHandler getCustomHandler() {
        return customHandler;
    }

    public static Experiment fromJson(String s) {
        return GSON.fromJson(s, Experiment.class);
    }

    public static Experiment fromJson(Reader r) {
        return GSON.fromJson(r, Experiment.class);
    }


    public String getName() {
        return name;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     *
     * @return a deep copy of the query that can be used by a single thread
     */
    public Query getQuery() {
        return (Query)query.deepCopy();
    }

    public List<Query> getFilterQueries() {
        //workaround for serialization that can leave a null filterqueries -- fix this at some point
        if (filterQueries == null) {
            return Collections.EMPTY_LIST;
        }
        return filterQueries;
    }

    public void addFilterQueries(List<Query> queries) {
        filterQueries.addAll(queries);
    }


    @Override
    public String toString() {
        return "Experiment{" +
                "name='" + name + '\'' +
                ", serverConnectionProperties=" + serverConnection +
                ", customHandler=" + customHandler +
                ", query=" + query +
                ", filterQueries=" + filterQueries +
                '}';
    }

    public Experiment deepCopy() {
        Experiment cp = new Experiment(getName(), getServerConnection(), getCustomHandler(), getQuery());
        List<Query> cpFq = new ArrayList<>();
        for (Query q : filterQueries) {
            cpFq.add((Query)q.deepCopy());
        }
        cp.addFilterQueries(cpFq);
        return cp;
    }

    public void setServerConnection(
            ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public void setCustomHandler(CustomHandler customHandler) {
        this.customHandler = customHandler;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (serverConnection != null ?
                !serverConnection.equals(that.serverConnection) :
                that.serverConnection != null)
            return false;
        if (customHandler != null ? !customHandler.equals(that.customHandler) : that.customHandler != null)
            return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        return filterQueries != null ? filterQueries.equals(that.filterQueries) : that.filterQueries == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (serverConnection != null ? serverConnection.hashCode() : 0);
        result = 31 * result + (customHandler != null ? customHandler.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (filterQueries != null ? filterQueries.hashCode() : 0);
        return result;
    }
}
