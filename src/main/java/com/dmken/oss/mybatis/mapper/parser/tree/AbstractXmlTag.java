/*-
 * #%L
 * MyBatis Mapper Parser
 * %%
 * Copyright (C) 2017 Fabian Damken
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.dmken.oss.mybatis.mapper.parser.tree;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents an abstract XML tag (either open or self-closing).
 *
 * @see XmlTag
 * @see SelfClosingXmlTag
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractXmlTag extends AbstractXmlValue {
    /**
     * The name of the XML tag.
     *
     */
    private final String name;
    /**
     * The parameters of the XML tag.
     *
     */
    private final Map<String, String> parameters;
}
