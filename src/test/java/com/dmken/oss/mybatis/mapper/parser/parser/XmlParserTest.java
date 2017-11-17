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
package com.dmken.oss.mybatis.mapper.parser.parser;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.dmken.oss.mybatis.mapper.parser.parser.exception.ParserException;
import com.dmken.oss.mybatis.mapper.parser.scanner.Token;
import com.dmken.oss.mybatis.mapper.parser.scanner.XmlScanner;
import com.dmken.oss.mybatis.mapper.parser.scanner.XmlScannerTest;
import com.dmken.oss.mybatis.mapper.parser.scanner.exception.ScannerException;
import com.dmken.oss.mybatis.mapper.parser.tree.SelfClosingXmlTag;
import com.dmken.oss.mybatis.mapper.parser.tree.SimpleXmlValue;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlDocument;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlTag;
import com.dmken.oss.mybatis.mapper.parser.tree.XmlVersion;

@SuppressWarnings("javadoc")
public class XmlParserTest {
    @Test
    public void test() throws Exception {
        final Map<String, String> childMap = new HashMap<>();
        childMap.put("order", "1");
        final Map<String, String> nameMap = new HashMap<>();
        nameMap.put("value", "Fabian Damken");

        final XmlDocument doc = new XmlDocument(XmlVersion.XML_1_0, Charset.forName("UTF-8"), "DOCTYPE xml", //
                new XmlTag("root", new HashMap<>(), Arrays.asList( //
                        new XmlTag("child", childMap, Arrays.asList( //
                                new SelfClosingXmlTag("name", nameMap), //
                                new SelfClosingXmlTag("male", new HashMap<>()), //
                                new XmlTag("birthday", new HashMap<>(), Arrays.asList( //
                                        new XmlTag("day", new HashMap<>(), Arrays.asList(new SimpleXmlValue("24"))), //
                                        new XmlTag("month", new HashMap<>(), Arrays.asList(new SimpleXmlValue("12"))), //
                                        new XmlTag("year", new HashMap<>(), Arrays.asList(new SimpleXmlValue("1997"))), //
                                        new XmlTag("special", new HashMap<>(), Arrays.asList(new SimpleXmlValue("<>&'\""))) //
                                )) //
                        )) //
                )) //
        );
        this.check("xml/simple.xml", doc);
    }

    private void check(final String path, final XmlDocument expected) throws ScannerException, ParserException {
        final Deque<Token> tokens = XmlScanner.scan(XmlScannerTest.class.getClassLoader().getResourceAsStream(path));
        final XmlDocument actual = XmlParser.parse(tokens);
        Assert.assertEquals(expected, actual);
    }
}
