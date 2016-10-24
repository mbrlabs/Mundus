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

package com.mbrlabs.mundus.editor.ui.modules.dock

import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetsDock
import com.mbrlabs.mundus.editor.ui.widgets.MundusSplitPane

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
class DockBar(private val splitPane: MundusSplitPane) : VisTable(), TabbedPaneListener {

    private val assetsDock = AssetsDock()
    private val tabbedPane: TabbedPane


    init {
        val style = TabbedPane.TabbedPaneStyle(
                VisUI.getSkin().get(TabbedPane.TabbedPaneStyle::class.java))
        style.buttonStyle = VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle::class.java))

        tabbedPane = TabbedPane(style)
        tabbedPane.isAllowTabDeselect = true
        tabbedPane.addListener(this)

        tabbedPane.add(assetsDock)
        add(tabbedPane.table).expandX().fillX().left().bottom().height(30f).row()
    }

    override fun switchedTab(tab: Tab?) {
        if (tab != null) {
            splitPane.setSecondWidget(tab.contentTable)
            splitPane.setSplitAmount(0.8f)
        } else {
            splitPane.setSecondWidget(null)
            splitPane.setSplitAmount(1f)
        }
        splitPane.invalidate()
    }

    override fun removedTab(tab: Tab) {
        // user can't do that
    }

    override fun removedAllTabs() {
        // user can't do that
    }

}
