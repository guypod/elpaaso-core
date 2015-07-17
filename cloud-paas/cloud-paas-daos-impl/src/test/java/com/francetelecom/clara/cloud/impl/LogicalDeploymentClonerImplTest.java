/**
 * Copyright (C) 2015 Orange
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.francetelecom.clara.cloud.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.francetelecom.clara.cloud.PersistenceTestUtil;
import com.francetelecom.clara.cloud.commons.xstream.XStreamUtils;
import com.francetelecom.clara.cloud.dao.LogicalDeploymentCloner;
import com.francetelecom.clara.cloud.logicalmodel.ContextRoot;
import com.francetelecom.clara.cloud.logicalmodel.LogicalDeployment;
import com.francetelecom.clara.cloud.logicalmodel.LogicalService;
import com.francetelecom.clara.cloud.logicalmodel.LogicalWebGUIService;
import com.francetelecom.clara.cloud.logicalmodel.ProcessingNode;
import com.francetelecom.clara.cloud.logicalmodel.samplecatalog.SampleAppFactory;
import com.thoughtworks.xstream.XStream;

/**
 * Tests Xml-based LD cloning
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/com/francetelecom/clara/cloud/services/application-context.xml" })
public class LogicalDeploymentClonerImplTest {

    private static Logger logger=LoggerFactory.getLogger(LogicalDeploymentClonerImplTest.class.getName());


    @Autowired
    @Qualifier(value = "springooLogicalModelCatalog")
    SampleAppFactory sampleAppFactory;

    @Autowired
    LogicalDeploymentCloner cloner;

    @Autowired
    PersistenceTestUtil persistenceTestUtil;

    /**
     * Used for debugging and for assertion XML dump size as a way to validate equals
     */
    private final XStream xStream = XStreamUtils.instanciateXstreamForHibernate();


    @Test
    public void testTransientClone() throws Exception {
        testClone(false);
    }

   @Test
    @DirtiesContext
    public void testPersistentClone() throws Exception {
        testClone(true);
    }

    private void testClone(final boolean persist) throws Exception {
        LogicalDeployment original = sampleAppFactory.populateLogicalDeployment(null);

        final String originalXmlDump = xStream.toXML(original);

        if (persist) {
            persistenceTestUtil.persistObject(original);
            original = persistenceTestUtil.reloadLogicalDeployment(original);
        }

        final LogicalDeploymentCloner finalCloner = this.cloner; //syntaxic sugar for anonymous inner class.
        final LogicalDeployment finalOriginal = original;
        Callable<LogicalDeployment> cloneRunneable = new Callable<LogicalDeployment>() {
            @Override
            public LogicalDeployment call() throws Exception {
                LogicalDeployment ld;
                if (persist) {
                    ld = persistenceTestUtil.reloadLogicalDeployment(finalOriginal);
                } else {
                    ld = finalOriginal;
                }
                LogicalDeployment localClone = finalCloner.deepCopy(ld);
                if (persist) {
                    persistenceTestUtil.persistObject(localClone);
                }
                return localClone;
            }
        };

        LogicalDeployment clone;
        if (persist) {
            clone = persistenceTestUtil.executeWithinTransaction(cloneRunneable);
        } else {
            //Direct invocation without a transation (avoid code duplication with persistent case).
            clone = cloneRunneable.call();
        }

        final LogicalDeployment finalClone = clone;

        // Check name unicity
        List<String> names = new ArrayList<String>();
        names.add(finalClone.getName());
        for (ProcessingNode node : finalClone.listProcessingNodes()) {
            if (names.contains(node.getName())) {
                fail("name is not unique: " + node.getName());
            }
            names.add(node.getName());
            assertTrue("name should be 36 characters long", node.getName().length() == 36);
        }
        for (LogicalService service : finalClone.listLogicalServices()) {
            if (names.contains(service.getName())) {
                fail("name is not unique: " + service.getName());
            }
            assertTrue("name should be 36 characters long", service.getName().length() == 36);
        }

        Runnable assertEqualsWithinTransactionRunneable = new Runnable() {
            @Override
            public void run() {
                //Force XML dump to check objects can be properly iterated through, and to force their eager fetching

                LogicalDeployment reloadedOriginal;
                LogicalDeployment reloadedClone;
                if (persist) {
                    reloadedOriginal = persistenceTestUtil.reloadLogicalDeployment(finalOriginal);
                    reloadedClone = persistenceTestUtil.reloadLogicalDeployment(finalClone);
                } else {
                    reloadedOriginal = finalOriginal;
                    reloadedClone = finalClone;
                }

                String prefix = persist ? "Persisted " : "Transient";
                logger.debug(prefix + "Original:\n" + xStream.toXML(reloadedOriginal));
                String cloneXmlDump = xStream.toXML(reloadedClone);
                logger.debug(prefix + "Clone:\n"+ cloneXmlDump);

                assertTrue("Expected XML dump to be as large as original (name prefix adds some chars)", cloneXmlDump.length() >= originalXmlDump.length());
                assertEquals(reloadedOriginal, reloadedOriginal);
                assertEquals(reloadedClone, reloadedOriginal);
                assertNotSame(reloadedOriginal, reloadedClone);

                if (persist) {
                    assertTrue("expected different db Ids, got:" + reloadedOriginal.getId(), reloadedOriginal.getId() != reloadedClone.getId());
                }
            }
        };
        if (persist) {
            persistenceTestUtil.executeWithinTransaction(assertEqualsWithinTransactionRunneable); //Make sure any original or clone lazy loaded fields can be loaded on-demand
        } else {
            assertEqualsWithinTransactionRunneable.run(); //direct execute outside of a transaction.
        }


        //Then make modifications to the clone and verify the original is not affected
        Set<LogicalWebGUIService> webGuis = finalClone.listLogicalServices(LogicalWebGUIService.class);
        for (LogicalWebGUIService webGui : webGuis) {
            webGui.setContextRoot(new ContextRoot("/"));
        }
        assertFalse("expected modified clone to differ from original", finalOriginal.equals(finalClone));

        //verify the persisted original is not affected by modifications made to the clone
        Runnable assertNotEqualsWithinTransactionRunneable = new Runnable() {
            @Override
            public void run() {
                LogicalDeployment reloadedOriginal;
                LogicalDeployment reloadedClone;
                if (persist) {
                    reloadedOriginal = persistenceTestUtil.reloadLogicalDeployment(finalOriginal);
                    persistenceTestUtil.mergeObject(finalClone); //write to DB modifications made to the clone
                    reloadedClone = persistenceTestUtil.reloadLogicalDeployment(finalClone);
                } else {
                    reloadedOriginal = finalOriginal;
                    reloadedClone = finalClone;
                }

                assertFalse("expected modified clone to differ from original", reloadedOriginal.equals(reloadedClone));
            }
        };
        if (persist) {
            persistenceTestUtil.executeWithinTransaction(assertNotEqualsWithinTransactionRunneable); //Make sure any original or clone lazy loaded fields can be loaded on-demand
        } else {
            assertNotEqualsWithinTransactionRunneable.run(); //direct execute outside of a transaction.
        }


    }


}
