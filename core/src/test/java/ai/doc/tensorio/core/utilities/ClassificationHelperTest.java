/*
 * ClassificationHelperTest.java
 * TensorIO
 *
 * Created by Philip Dow on 7/6/2020
 * Copyright (c) 2020 - Present doc.ai (http://doc.ai)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.doc.tensorio.core.utilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ClassificationHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    //region Smoothing Tests

    @Test
    public void testSmoothingPreservesCurrentValues() {
        Map<String,Float> currentValues = new HashMap<>();
        currentValues.put("foo", 1.0f);
        currentValues.put("bar", 10.0f);
        currentValues.put("baz", 100.0f);
        Map<String,Float> newValues = new HashMap<>();

        List<Map.Entry<String,Float>> currentList = new ArrayList<>(currentValues.entrySet());
        List<Map.Entry<String,Float>> newList = new ArrayList<>(newValues.entrySet());

        List<Map.Entry<String,Float>> outList = ClassificationHelper.smoothClassification(currentList, newList, 1.0f, 0.0f);
        Map<String,Float> outMap = outList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(outMap.containsKey("foo"));
        assertTrue(outMap.containsKey("bar"));
        assertTrue(outMap.containsKey("baz"));

        assertEquals(outMap.get(("foo")), 1.0f, 0.01f);
        assertEquals(outMap.get(("bar")), 10.0f, 0.01f);
        assertEquals(outMap.get(("baz")), 100.0f, 0.01f);
    }

    @Test
    public void testSmoothingTakesNewValues() {
        Map<String,Float> currentValues = new HashMap<>();
        Map<String,Float> newValues = new HashMap<>();
        newValues.put("foo", 1.0f);
        newValues.put("bar", 10.0f);
        newValues.put("baz", 100.0f);

        List<Map.Entry<String,Float>> currentList = new ArrayList<>(currentValues.entrySet());
        List<Map.Entry<String,Float>> newList = new ArrayList<>(newValues.entrySet());

        List<Map.Entry<String,Float>> outList = ClassificationHelper.smoothClassification(currentList, newList, 0.0f, 0.0f);
        Map<String,Float> outMap = outList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(outMap.containsKey("foo"));
        assertTrue(outMap.containsKey("bar"));
        assertTrue(outMap.containsKey("baz"));

        assertEquals(outMap.get(("foo")), 1.0f, 0.01f);
        assertEquals(outMap.get(("bar")), 10.0f, 0.01f);
        assertEquals(outMap.get(("baz")), 100.0f, 0.01f);
    }

    @Test
    public void testSmoothingCombinesValues() {
        Map<String,Float> currentValues = new HashMap<>();
        currentValues.put("foo", 1.0f);
        currentValues.put("bar", 10.0f);
        currentValues.put("baz", 100.0f);
        Map<String,Float> newValues = new HashMap<>();
        newValues.put("foo", 1000.0f);
        newValues.put("bar", 10000.0f);
        newValues.put("baz", 100000.0f);

        List<Map.Entry<String,Float>> currentList = new ArrayList<>(currentValues.entrySet());
        List<Map.Entry<String,Float>> newList = new ArrayList<>(newValues.entrySet());

        List<Map.Entry<String,Float>> outList = ClassificationHelper.smoothClassification(currentList, newList, 0.8f, 0.0f);
        Map<String,Float> outMap = outList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(outMap.containsKey("foo"));
        assertTrue(outMap.containsKey("bar"));
        assertTrue(outMap.containsKey("baz"));

        assertEquals(outMap.get(("foo")), 200.8f, 0.01f);
        assertEquals(outMap.get(("bar")), 2008.0f, 0.01f);
        assertEquals(outMap.get(("baz")), 20080.0f, 0.01f);
    }

    @Test
    public void testSmoothingAppliesThreshold() {
        Map<String,Float> currentValues = new HashMap<>();
        currentValues.put("foo", 1.0f);
        currentValues.put("bar", 10.0f);
        currentValues.put("baz", 100.0f);
        Map<String,Float> newValues = new HashMap<>();
        newValues.put("foo", 1000.0f);
        newValues.put("bar", 10000.0f);
        newValues.put("baz", 100000.0f);

        List<Map.Entry<String,Float>> currentList = new ArrayList<>(currentValues.entrySet());
        List<Map.Entry<String,Float>> newList = new ArrayList<>(newValues.entrySet());

        List<Map.Entry<String,Float>> outList = ClassificationHelper.smoothClassification(currentList, newList, 0.8f, 2000.0f);
        Map<String,Float> outMap = outList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertFalse(outMap.containsKey("foo"));
        assertTrue(outMap.containsKey("bar"));
        assertTrue(outMap.containsKey("baz"));

        assertEquals(outMap.get(("bar")), 2008.0f, 0.01f);
        assertEquals(outMap.get(("baz")), 20080.0f, 0.01f);
    }

    //endRegion
}