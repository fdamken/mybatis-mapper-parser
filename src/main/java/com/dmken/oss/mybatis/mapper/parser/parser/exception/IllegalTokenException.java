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
package com.dmken.oss.mybatis.mapper.parser.parser.exception;

import java.util.Arrays;

import com.dmken.oss.mybatis.mapper.parser.scanner.Token;
import com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType;
import com.dmken.oss.mybatis.mapper.parser.util.FormatUtil;

@SuppressWarnings("javadoc")
public class IllegalTokenException extends ParserException {
    private static final long serialVersionUID = 6188158534075049639L;

    public IllegalTokenException(final Token actual, final TokenType... expected) {
        super("Illegal token " + actual + ". Expected one of " + Arrays.toString(expected) + " Location: "
                + FormatUtil.formatSourceLocation(actual.getLine(), actual.getColumn()));
    }
}
