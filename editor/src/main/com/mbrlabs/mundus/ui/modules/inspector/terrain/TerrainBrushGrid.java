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

package com.mbrlabs.mundus.ui.modules.inspector.terrain;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.events.GlobalBrushSettingsChangedEvent;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.ui.widgets.ImprovedSlider;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TerrainBrushGrid extends VisTable
        implements GlobalBrushSettingsChangedEvent.GlobalBrushSettingsChangedListener {

    private TerrainComponentWidget parent;
    private TerrainBrush.BrushMode brushMode;

    private GridGroup grid;
    private ImprovedSlider strengthSlider;

    @Inject
    private ToolManager toolManager;

    public TerrainBrushGrid(TerrainComponentWidget parent) {
        super();
        Mundus.inject(this);
        Mundus.registerEventListener(this);
        this.parent = parent;
        align(Align.left);
        add(new VisLabel("Brushes:")).padBottom(10).padLeft(5).left().row();

        VisTable brushGridContainerTable = new VisTable();
        brushGridContainerTable.setBackground("menu-bg");

        // grid
        grid = new GridGroup(40, 0);
        for (TerrainBrush brush : toolManager.terrainBrushes) {
            grid.addActor(new BrushItem(brush));
        }
        brushGridContainerTable.add(grid).expand().fill().row();

        // brush settings
        final VisTable settingsTable = new VisTable();
        settingsTable.add(new VisLabel("Strength")).left().row();
        strengthSlider = new ImprovedSlider(0, 1, 0.1f);
        strengthSlider.setValue(TerrainBrush.getStrength());
        settingsTable.add(strengthSlider).expandX().fillX().row();
        strengthSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TerrainBrush.setStrength(strengthSlider.getValue());
            }
        });

        add(brushGridContainerTable).expand().fill().padLeft(5).padRight(5).row();
        add(settingsTable).expand().fill().padLeft(5).padRight(5).padTop(5).row();
    }

    public TerrainBrushGrid(TerrainComponentWidget parent, TerrainBrush.BrushMode mode) {
        this(parent);
        this.brushMode = mode;
    }

    public TerrainBrush.BrushMode getBrushMode() {
        return brushMode;
    }

    public void setBrushMode(TerrainBrush.BrushMode brushMode) {
        this.brushMode = brushMode;
    }

    public void activateBrush(TerrainBrush brush) {
        try {
            brush.setMode(brushMode);
            toolManager.activateTool(brush);
            brush.setTerrain(parent.component.getTerrain().getTerrain());
        } catch (TerrainBrush.ModeNotSupportedException e) {
            e.printStackTrace();
            Dialogs.showErrorDialog(Ui.getInstance(), e.getMessage());
        }
    }

    @Override
    public void onSettingsChanged(GlobalBrushSettingsChangedEvent event) {
        strengthSlider.setValue(TerrainBrush.getStrength());
    }

    /**
     *
     */
    private class BrushItem extends VisTable {

        public BrushItem(final TerrainBrush brush) {
            super();
            add(new FaTextButton(brush.getIconFont()));

            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    activateBrush(brush);
                }
            });

        }
    }

}
