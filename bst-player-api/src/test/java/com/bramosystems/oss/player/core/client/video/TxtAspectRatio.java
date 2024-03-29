/*
 *  Copyright 2009 Sikirulai Braheem <sbraheem at bramosystems dot com>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package com.bramosystems.oss.player.core.client.video;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sikirulai Braheem <sbraheem at bramosystems dot com>
 */
public class TxtAspectRatio extends GWTTestCase {

    public TxtAspectRatio() {
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("4:3", new AspectRatio(4, 3).toString());
    }

    @Test
    public void testParse() {
        System.out.println("parse");
        assertEquals(new AspectRatio(2, 2), AspectRatio.parse("2:2"));
    }

    @Override
    public String getModuleName() {
        return "com.bramosystems.oss.player.core.Core";
    }
}