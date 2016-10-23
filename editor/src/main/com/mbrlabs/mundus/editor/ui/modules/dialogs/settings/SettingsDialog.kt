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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSplitPane
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
class SettingsDialog : BaseDialog("Settings") {

    private val splitPane: VisSplitPane
    private val settingsSelection: VerticalGroup

    private val generalSettings: GeneralSettingsTable

    private val registry: Registry = Mundus.inject()

    init {
        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        settingsSelection = VerticalGroup()
        settingsSelection.addActor(VisLabel("General"))
        settingsSelection.addActor(VisLabel("Appearance"))
        settingsSelection.addActor(VisLabel("Export"))

        generalSettings = GeneralSettingsTable(registry)

        splitPane = VisSplitPane(settingsSelection, generalSettings, false)
        splitPane.setSplitAmount(0.3f)
        root.add(splitPane).width(700f).minHeight(400f).fill().expand()
    }

}
