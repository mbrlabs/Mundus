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

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class CommandHistory {

    public static final int DEFAULT_LIMIT = 50;

    private int limit;
    private int pointer;
    private Array<Command> commands;

    public CommandHistory(int limit) {
        this.limit = limit;
        commands = new Array<>(limit);
        pointer = -1;
    }

    public int add(Command command) {
        if(size() == 0) {
            commands.add(command);
            pointer++;
            return pointer;
        }

        if(pointer < size() - 1) {
            removeCommands(pointer + 1, commands.size - 1);
            commands.add(command);
            pointer++;
        } else {
            if(size() == limit) {
                removeCommand(0);
                commands.add(command);
            } else {
                commands.add(command);
                pointer++;
            }
        }

        return pointer;
    }

    private void removeCommand(int index) {
        commands.get(index).dispose();
        commands.removeIndex(index);
    }

    private void removeCommands(int from, int to) {
        for(int i = from; i <= to; i++) {
            commands.get(i).dispose();
        }

        commands.removeRange(from, to);
    }

    public int goBack() {
        if(pointer >= 0) {
            commands.get(pointer).undo();
            pointer--;
        }

        return pointer;
    }

    public int goForward() {
        if(pointer < commands.size - 1 ) {
            pointer++;
            commands.get(pointer).execute();
        }

        return pointer;
    }

    public int getPointer() {
        return pointer;
    }

    public void clear() {
        for(Command c : commands) {
            c.dispose();
        }
        commands.clear();
        pointer = -1;
    }

    public int size() {
        return commands.size;
    }

}
