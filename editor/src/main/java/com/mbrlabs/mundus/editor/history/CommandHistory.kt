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

package com.mbrlabs.mundus.editor.history

import com.badlogic.gdx.utils.Array

/**
 * Add commands to undo/redo a previously called process.
 *
 * @author Marcus Brummer
 * @version 07-02-2016
 */
class CommandHistory(private val limit: Int) {

    private var pointer: Int = 0
    private val commands: Array<Command>

    init {
        commands = Array<Command>(limit)
        pointer = -1
    }

    fun add(command: Command): Int {
        if (size() == 0) {
            commands.add(command)
            pointer++
            return pointer
        }

        if (pointer < size() - 1) {
            removeCommands(pointer + 1, commands.size - 1)
            commands.add(command)
            pointer++
        } else {
            if (size() == limit) {
                removeCommand(0)
                commands.add(command)
            } else {
                commands.add(command)
                pointer++
            }
        }

        return pointer
    }

    private fun removeCommand(index: Int) {
        commands.get(index).dispose()
        commands.removeIndex(index)
    }

    private fun removeCommands(from: Int, to: Int) {
        for (i in from..to) {
            commands.get(i).dispose()
        }

        commands.removeRange(from, to)
    }

    fun goBack(): Int {
        if (pointer >= 0) {
            commands.get(pointer).undo()
            pointer--
        }

        return pointer
    }

    fun goForward(): Int {
        if (pointer < commands.size - 1) {
            pointer++
            commands.get(pointer).execute()
        }

        return pointer
    }

    fun clear() {
        for (c in commands) {
            c.dispose()
        }
        commands.clear()
        pointer = -1
    }

    fun size(): Int {
        return commands.size
    }

    companion object {

        val DEFAULT_LIMIT = 50
    }

}
