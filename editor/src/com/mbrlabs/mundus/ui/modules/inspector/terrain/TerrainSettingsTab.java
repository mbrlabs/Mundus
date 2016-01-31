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

package com.mbrlabs.mundus.ui.modules.inspector.terrain;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TerrainSettingsTab extends Tab {

    private VisTable table;

    public TerrainSettingsTab() {
        super(false, false);
        table = new VisTable();
        table.align(Align.left);
        table.add(new VisLabel("Settings"));
    }

    @Override
    public String getTabTitle() {
        return "Settings";
    }

    @Override
    public Table getContentTable() {
        return table;
    }

}
