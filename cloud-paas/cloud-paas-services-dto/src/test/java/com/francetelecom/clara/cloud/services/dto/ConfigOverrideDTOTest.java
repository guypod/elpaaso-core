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
package com.francetelecom.clara.cloud.services.dto;

import com.francetelecom.clara.cloud.commons.TechnicalException;
import com.francetelecom.clara.cloud.commons.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigOverrideDTOTest {

    @Test(expected = TechnicalException.class)
    public void javax_validation_should_reject_null_configset() {
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO(null, "key", "value", "comment");
        ValidatorUtil.validate(configOverrideDTO);
    }
    @Test(expected = TechnicalException.class)
    public void javax_validation_should_reject_empty_configset() {
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO("", "key", "value", "comment");
        ValidatorUtil.validate(configOverrideDTO);
    }
    @Test(expected = TechnicalException.class)
    public void javax_validation_should_reject_null_key() {
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO("configSet", null, "value", "comment");
        ValidatorUtil.validate(configOverrideDTO);
    }
    @Test()
    public void javax_validation_should_not_reject_empty_key() {
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO("configSet", "", "value", "comment");
        ValidatorUtil.validate(configOverrideDTO);
    }
    @Test(expected = TechnicalException.class)
    public void javax_validation_should_reject_too_large_comment() {
        String comment = StringUtils.rightPad("comment", ConfigOverrideDTO.MAX_CONFIG_COMMENT_LENGTH + 2, 'X');
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO("configSet", "key", "value", comment);
        ValidatorUtil.validate(configOverrideDTO);
    }
    @Test(expected = TechnicalException.class)
    public void javax_validation_should_reject_too_large_value() {
        String value = StringUtils.rightPad("value", ConfigOverrideDTO.MAX_CONFIG_VALUE_LENGTH + 2, 'X');
        ConfigOverrideDTO configOverrideDTO = new ConfigOverrideDTO("configSet", "key", value, "comment");
        ValidatorUtil.validate(configOverrideDTO);
    }

}