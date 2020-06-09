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
package org.tallison.quaerite.core.serializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.tallison.quaerite.core.ServerConnection;
import org.tallison.quaerite.core.features.CustomHandler;
import org.tallison.quaerite.core.features.WeightableField;
import org.tallison.quaerite.core.features.factories.CustomHandlerFactory;
import org.tallison.quaerite.core.features.factories.QueryFactory;
import org.tallison.quaerite.core.queries.Query;

public class AbstractFeatureSerializer {
    static String DEFAULT_CLASS_NAME_SPACE = "org.tallison.quaerite.core.features.";

    String getClassName(String clazzName) {
        if (!clazzName.contains(".")) {
            if (clazzName.length() < 5) {
                clazzName = clazzName.toUpperCase(Locale.US);
            } else {
                clazzName = clazzName.substring(0,1).toUpperCase(Locale.US) +
                        clazzName.substring(1);
            }

            return DEFAULT_CLASS_NAME_SPACE + clazzName;
        }
        return clazzName;
    }

    static List<WeightableField> toWeightableList(JsonElement weightableArr) {
        if (weightableArr == null) {
            return Collections.EMPTY_LIST;
        } else if (weightableArr.isJsonPrimitive()) {
            return Collections.singletonList(new WeightableField(weightableArr.getAsString()));
        } else if (weightableArr.isJsonArray()) {
            List<WeightableField> ret = new ArrayList<>();
            for (JsonElement el : ((JsonArray)weightableArr)) {
                ret.add(new WeightableField(el.getAsJsonPrimitive().getAsString()));
            }
            return ret;
        } else {
            throw new IllegalArgumentException("Didn't expect json object here:"
                    + weightableArr);
        }

    }

    static List<String> toStringList(JsonElement stringArr) {
        if (stringArr == null) {
            return Collections.EMPTY_LIST;
        } else if (stringArr.isJsonPrimitive()) {
            return Collections.singletonList(stringArr.getAsString());
        } else if (stringArr.isJsonArray()) {
            List<String> ret = new ArrayList<>();
            for (JsonElement el : ((JsonArray)stringArr)) {
                if (! el.isJsonNull()) {
                    ret.add(el.getAsJsonPrimitive().getAsString());
                }
            }
            return ret;
        } else {
            throw new IllegalArgumentException("Didn't expect json object here:" + stringArr);
        }
    }
/*    static List<StringFeature> toStringFeatureList(JsonElement stringArr) {
        List<String> strings = toStringList(stringArr);
        List<StringFeature> stringFeatures = new ArrayList<>();
        for (String s : strings) {
            stringFeatures.add(new StringFeature(s));
        }
        return stringFeatures;
    }*/

/*    static List<StringFeature> toStringFeatureList(String name, JsonArray stringArr) {
        if (stringArr == null) {
            return null;
        }
        List<StringFeature> ret = new ArrayList<>();
        for (JsonElement el : stringArr) {
            ret.add(new StringFeature(name, el.getAsJsonPrimitive().getAsString()));
        }
        return ret;
    }*/

    static List<Float> toFloatList(JsonElement floatArr) {
        if (floatArr == null) {
            return Collections.emptyList();
        } else if (floatArr.isJsonPrimitive()) {
            return Collections.singletonList(floatArr.getAsFloat());
        } else if (floatArr.isJsonArray()) {
            List<Float> ret = new ArrayList<>();
            for (JsonElement el : ((JsonArray)floatArr)) {
                ret.add(el.getAsJsonPrimitive().getAsFloat());
            }
            return ret;
        } else {
            throw new IllegalArgumentException("Did not expect json object: " + floatArr);
        }
    }

    static List<Integer> toIntList(JsonElement intArr) {
        if (intArr == null) {
            return Collections.emptyList();
        } else if (intArr.isJsonPrimitive()) {
            return Collections.singletonList(intArr.getAsInt());
        } else if (intArr.isJsonArray()) {
            List<Integer> ret = new ArrayList<>();
            for (JsonElement el : ((JsonArray)intArr)) {
                ret.add(el.getAsJsonPrimitive().getAsInt());
            }
            return ret;
        } else {
            throw new IllegalArgumentException("Did not expect json object: " + intArr);
        }
    }

    JsonElement stringListJsonArr(List<String> strings) {
        if (strings.size() == 0) {
            return JsonNull.INSTANCE;
        } else if (strings.size() == 1) {
            return new JsonPrimitive(strings.get(0));
        } else {
            JsonArray arr = new JsonArray();
            for (String s : strings) {
                arr.add(s);
            }
            return arr;
        }
    }

    JsonArray floatListToJsonArr(List<Float> defaultWeights) {
        JsonArray arr = new JsonArray();
        for (Float f : defaultWeights) {
            arr.add(f);
        }
        return arr;
    }

    Class determineClass(String clazzName) {
        if (clazzName.equals(QueryFactory.NAME)) {
            return Query.class;
        } else if (clazzName.equals(CustomHandlerFactory.NAME)) {
            return CustomHandler.class;
        } else if (clazzName.equals(ServerConnection.NAME)) {
            return ServerConnection.class;
        }
        if (!clazzName.contains(".")) {
            clazzName = getClassName(clazzName);
        }
        try {
            return Class.forName(clazzName);
        } catch (Exception e) {
            throw new JsonParseException(clazzName + " -> " + e.getMessage());
        }
    }

}
