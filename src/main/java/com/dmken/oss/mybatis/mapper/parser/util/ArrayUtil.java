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
package com.dmken.oss.mybatis.mapper.parser.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for working with arrays.
 *
 */
@UtilityClass
public class ArrayUtil {
    /**
     * Converts the given char array to a string array with the name length.
     *
     * @param chars
     *            The char array to convert.
     * @return The string array.
     */
    public String[] toStringArray(final char... chars) {
        final String[] result = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            result[i] = String.valueOf(chars[i]);
        }
        return result;
    }
}
