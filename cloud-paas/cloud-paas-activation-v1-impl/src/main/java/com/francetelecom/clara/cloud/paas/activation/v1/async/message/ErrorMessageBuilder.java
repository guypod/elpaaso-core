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
package com.francetelecom.clara.cloud.paas.activation.v1.async.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.util.Assert;

public class ErrorMessageBuilder {

	private ErrorMessage message;

	public ErrorMessageBuilder(Session session, Throwable error,
			String communicationId) throws JMSException {
		Assert.notNull(session, "session should not be null");
		Assert.notNull(error, "error should not be null");
		ObjectMessage msg = session.createObjectMessage(error);
		this.message = new ErrorMessage(msg);
		this.message.setCommunicationId(communicationId);
	}

	public Message build() throws JMSException {
		return this.message;
	}

}
