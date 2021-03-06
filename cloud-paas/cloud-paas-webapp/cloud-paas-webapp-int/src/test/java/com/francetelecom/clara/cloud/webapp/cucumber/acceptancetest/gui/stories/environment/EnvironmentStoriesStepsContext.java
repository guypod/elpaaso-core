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
package com.francetelecom.clara.cloud.webapp.cucumber.acceptancetest.gui.stories.environment;


import com.francetelecom.clara.cloud.webapp.cucumber.acceptancetest.gui.stories.GuiStoriesContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Import(GuiStoriesContext.class)
public class EnvironmentStoriesStepsContext {

    @Bean
    public EnvironmentStoriesSteps getEnvironmentStoriesSteps() {
        return new EnvironmentStoriesSteps();
    }


    @Bean(name = "retryTemplate")
    public RetryTemplate getRetryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(retryEvery60s());
        retryTemplate.setRetryPolicy(set30minTimeout());
        return retryTemplate;

    }

    private TimeoutRetryPolicy set30minTimeout() {
        TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
        timeoutRetryPolicy.setTimeout(1800000);
        return timeoutRetryPolicy;
    }

    private FixedBackOffPolicy retryEvery60s() {
        //retry every 60s
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(60000);
        return backOffPolicy;
    }


}
