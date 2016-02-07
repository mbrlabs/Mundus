/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.history;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class CommandHistory {

    public static final int DEFAULT_LIMIT = 20;

    private int limit;
    private int pointer;
    private Array<Command> commands;

    public CommandHistory(int limit) {
        this.limit = limit;
        commands = new Array<>(limit);
        pointer = -1;
    }

    public int add(Command command) {
        if(pointer + 1 < size()) {
            commands.removeRange(pointer + 1, commands.size - 1);
            commands.add(command);
            pointer++;
            Log.debug("HISTORY ADD", "pointer < size() ==> pointer: " + pointer);
        } else {
            if(size() == limit) {
                commands.removeIndex(0);
                commands.add(command);
                Log.debug("HISTORY ADD", "pointer == limit ==> pointer: " + pointer);
            } else {
                commands.add(command);
                pointer++;
                Log.debug("HISTORY ADD", "pointer < limit pointer == size ==> pointer: " + pointer);
            }
        }

        return pointer;
    }

    public int goBack() {
        if(pointer >= 0) {
            commands.get(pointer).undo();
            pointer--;
        }
        Log.debug("HISTORY BACK", "pointer: " + pointer);

        return pointer;
    }

    public int goForward() {
        if(pointer < commands.size - 1 ) {
            pointer++;
            commands.get(pointer).execute();
        }
        Log.debug("HISTORY FORWARD", "pointer: " + pointer);

        return pointer;
    }

    public int getPointer() {
        return pointer;
    }

    public void clear() {
        commands.clear();
        pointer = 0;
    }

    public int size() {
        return commands.size;
    }

}
