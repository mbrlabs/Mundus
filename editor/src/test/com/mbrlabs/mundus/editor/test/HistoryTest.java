/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.test;

import com.mbrlabs.mundus.history.Command;
import com.mbrlabs.mundus.history.CommandHistory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Marcus Brummer
 * @version 08-02-2016
 */
public class HistoryTest {

    @Test
    public void addThenGoBackAndAdd() {
        CommandHistory history = new CommandHistory(CommandHistory.DEFAULT_LIMIT);
        int ptr;

        ptr = history.add(new MockCommand());
        assertEquals(0, ptr);
        ptr = history.add(new MockCommand());
        assertEquals(1, ptr);
        ptr = history.add(new MockCommand());
        assertEquals(2, ptr);

        assertEquals(3, history.size());

        ptr = history.goBack();
        assertEquals(1, ptr);
        ptr = history.goBack();
        assertEquals(0, ptr);
        ptr = history.goBack();
        assertEquals(-1, ptr);

        ptr = history.add(new MockCommand());
        assertEquals(0, ptr);
        assertEquals(1, history.size());
    }

    @Test
    public void addThenGoBackThenGoForwardOneThenAdd() {
        CommandHistory history = new CommandHistory(CommandHistory.DEFAULT_LIMIT);
        int ptr;

        ptr = history.add(new MockCommand());
        assertEquals(0, ptr);
        ptr = history.add(new MockCommand());
        assertEquals(1, ptr);
        ptr = history.add(new MockCommand());
        assertEquals(2, ptr);

        assertEquals(3, history.size());

        ptr = history.goBack();
        assertEquals(1, ptr);
        ptr = history.goBack();
        assertEquals(0, ptr);
        ptr = history.goBack();
        assertEquals(-1, ptr);

        ptr = history.goForward();
        assertEquals(0, ptr);

        ptr = history.add(new MockCommand());
        assertEquals(1, ptr);
        assertEquals(2, history.size());
    }

    private class MockCommand implements Command {
        @Override
        public void execute() {
        }

        @Override
        public void undo() {
        }

        @Override
        public void dispose() {
        }
    }

}
