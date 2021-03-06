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
package com.francetelecom.clara.cloud.logicalmodel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.francetelecom.clara.cloud.commons.GuiClassMapping;

/**
 * Logical cluster of application server that executes a given application
 * artefact. May also be called an "application container".
 * 
 * @author APOG7416
 * 
 */
@XmlRootElement
@Entity
@Table(name = "CF_JAVA_PROCESSING")
@GuiClassMapping(serviceCatalogName = "cf java processing service", isExternal = false, serviceCatalogNameKey = "cfjava.processing", status = GuiClassMapping.StatusType.SUPPORTED)
public class CFJavaProcessing extends ProcessingNode {

	/**
	 * Default constructor.
	 */
	public CFJavaProcessing() {
	}

	/**
	 * Node Cluster constructor
	 * 
	 * @param label
	 *            the logical execution node name as provided by the end-user.
	 *            Should be unique across {@link CFJavaProcessing} instances in the
	 *            same LogicalDeployment
	 * @param logicalDeployment
	 *
	 */
	public CFJavaProcessing(String label, LogicalDeployment logicalDeployment) throws IllegalArgumentException {
		super(label, logicalDeployment);
	}

}
