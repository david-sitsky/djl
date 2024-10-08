/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.repository;

import ai.djl.MalformedModelException;
import ai.djl.modality.Input;
import ai.djl.modality.Output;
import ai.djl.ndarray.NDList;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZooTest {

    @Test
    public void testCriteria() throws ModelNotFoundException, IOException {
        Block block = Blocks.identityBlock();
        Path modelDir = Paths.get("build/model");
        Files.createDirectories(modelDir);

        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelPath(modelDir)
                        .optBlock(block)
                        .optOption("hasParameter", "false")
                        .optEngine("PyTorch")
                        .build();

        criteria.downloadModel();
        Assert.assertTrue(criteria.isDownloaded());

        criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optEngine("Nonexist")
                        .build();
        Assert.assertThrows(criteria::isDownloaded);
    }

    @Test
    public void testCriteriaToBuilder() {
        Criteria<Input, Output> criteria1 =
                Criteria.builder()
                        .setTypes(Input.class, Output.class)
                        .optEngine("testEngine1")
                        .optModelName("testModelName")
                        .build();

        Criteria<Input, Output> criteria2 = criteria1.toBuilder().optEngine("testEngine2").build();

        Assert.assertEquals("testEngine1", criteria1.getEngine());
        Assert.assertEquals("testEngine2", criteria2.getEngine());
        Assert.assertEquals("testModelName", criteria1.getModelName());
        Assert.assertEquals("testModelName", criteria2.getModelName());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidCriteria()
            throws ModelNotFoundException, MalformedModelException, IOException {
        Criteria<?, ?> criteria = Criteria.builder().build();
        criteria.loadModel();
    }

    @Test
    public void testModelZooResolver() {
        ModelZoo.setModelZooResolver(groupId -> null);
        ModelZoo zoo = ModelZoo.getModelZoo("unknown");
        Assert.assertNull(zoo);
    }
}
