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
package org.mitre.quaerite.util;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static float calcMutatedWeight(float min, float max, double amplitude) {
        if (amplitude < 0 || amplitude > 1.0) {
            throw new IllegalArgumentException("amplitude must be >= 0 and <= 1");
        }
        if (Math.abs(max-min) < 0.0000001) {
            return min;
        }
        float length = max - min;
        float mean = (max+min)/2f;

        float amplitudeAdjustedLength = (float)(length*amplitude);
        return getRandomFloat((mean-amplitudeAdjustedLength), (mean+amplitudeAdjustedLength));
    }

    public static float getRandomFloat(float min, float max) {
        //TODO -- fix potential overflow/underflow
        return min + RANDOM.nextFloat() * (max - min);
    }

}