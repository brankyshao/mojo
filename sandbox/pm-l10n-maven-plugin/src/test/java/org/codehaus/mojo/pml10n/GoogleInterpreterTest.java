package org.codehaus.mojo.pml10n;
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import junit.framework.TestCase;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 03-Oct-2009 14:42:23
 */
public class GoogleInterpreterTest
    extends TestCase
{
    public void testJSONRequest() throws Exception {
        GoogleInterpreter instance = new GoogleInterpreter( "junittest@mojo.codehaus.org");

        System.out.println( instance.translate( "Hello, how are you today", Locale.ENGLISH, Locale.CHINESE ));
    }
}
