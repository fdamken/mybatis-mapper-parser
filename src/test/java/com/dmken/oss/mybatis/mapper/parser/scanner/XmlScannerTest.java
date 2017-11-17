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
package com.dmken.oss.mybatis.mapper.parser.scanner;

import static com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType.*;

import java.util.Deque;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.dmken.oss.mybatis.mapper.parser.scanner.Token.TokenType;
import com.dmken.oss.mybatis.mapper.parser.scanner.exception.ScannerException;

@SuppressWarnings("javadoc")
public class XmlScannerTest {
    @Test
    public void testSimple() throws Exception {
        final Deque<Token> expected = new LinkedList<>();
        this.add(expected, "<", LANGEL);
        this.add(expected, "?", QMARK);
        this.add(expected, "xml", IDENTIFIER);
        this.add(expected, "version", IDENTIFIER);
        this.add(expected, "=", EQUALS);
        this.add(expected, "1.0", STRINGLIT);
        this.add(expected, "encoding", IDENTIFIER);
        this.add(expected, "=", EQUALS);
        this.add(expected, "UTF-8", STRINGLIT);
        this.add(expected, "?", QMARK);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "!", EXMARK);
        this.add(expected, "DOCTYPE", IDENTIFIER);
        this.add(expected, "xml", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "root", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "child", IDENTIFIER);
        this.add(expected, "order", IDENTIFIER);
        this.add(expected, "=", EQUALS);
        this.add(expected, "1", STRINGLIT);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "name", IDENTIFIER);
        this.add(expected, "value", IDENTIFIER);
        this.add(expected, "=", EQUALS);
        this.add(expected, "Fabian Damken", STRINGLIT);
        this.add(expected, "/", SLASH);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "male", IDENTIFIER);
        this.add(expected, "/", SLASH);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "birthday", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "day", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "24", CONTENT);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "day", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "month", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "12", CONTENT);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "month", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "year", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "1997", CONTENT);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "year", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "special", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<>&'\"", CONTENT);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "special", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "birthday", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "child", IDENTIFIER);
        this.add(expected, ">", RANGEL);
        this.add(expected, "<", LANGEL);
        this.add(expected, "/", SLASH);
        this.add(expected, "root", IDENTIFIER);
        this.add(expected, ">", RANGEL);

        this.check("xml/simple.xml", expected);
    }

    private void add(final Deque<Token> tokens, final String token, final TokenType type) {
        tokens.offer(new Token(-1, -1, token, type));
    }

    private void check(final String path, final Deque<Token> expected) throws ScannerException {
        final Deque<Token> actual = XmlScanner.scan(XmlScannerTest.class.getClassLoader().getResourceAsStream(path));
        Assert.assertEquals(expected, actual);
    }
}
