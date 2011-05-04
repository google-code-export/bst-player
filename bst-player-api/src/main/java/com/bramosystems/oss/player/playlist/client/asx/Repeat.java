/*
 * Copyright 2011 Administrator.
 *
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
 */

package com.bramosystems.oss.player.playlist.client.asx;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Repeat {
    private int count;
    private List<ASXEntry> entry;
    private List<String> entryRef;

    public Repeat() {
        count = Integer.MAX_VALUE;
        entry = new ArrayList<ASXEntry>();
        entryRef = new ArrayList<String>();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ASXEntry> getEntries() {
        return entry;
    }

    public void setEntries(List<ASXEntry> entry) {
        this.entry = entry;
    }

    public List<String> getEntryRefs() {
        return entryRef;
    }

    public void setEntryRefs(List<String> entryRef) {
        this.entryRef = entryRef;
    }

    @Override
    public String toString() {
        return "Repeat{" + "count=" + count + ", entry=" + entry + ", entryRef=" + entryRef + '}';
    }
}
