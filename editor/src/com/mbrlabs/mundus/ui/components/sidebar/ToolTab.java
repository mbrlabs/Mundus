/*
 * Copyright (c) 2015. See AUTHORS file.
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

package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.brushes.Brush;
import com.mbrlabs.mundus.terrain.brushes.BrushManager;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ToolTab extends Tab {

    private static final String TITLE = "Tools";

    private VisTable content;

    @Inject
    private BrushManager brushManager;

    private GridGroup meshBrushGrid;
    private GridGroup textureBrushGrid;
    private GridGroup transformationToolsGrid;

    public ToolTab() {
        super(false, false);
        Mundus.inject(this);
        content = new VisTable();
        content.align(Align.left | Align.top);

        createTransfornamtionTools();
        createTerrainMeshTools();
        createTerrainTextureTools();
    }

    private void createTerrainMeshTools() {
        meshBrushGrid = new GridGroup(40, 5);
        for(Brush brush : brushManager.brushes) {
            meshBrushGrid.addActor(new BrushGridItem(brush));
        }

        // TODO remove
        for(int i = 0; i < 10; i++) {
            meshBrushGrid.addActor(new BrushGridItem(brushManager.brushes.first()));
        }

        // add to sidebar
        content.add(new VisLabel("Terrain mesh brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(meshBrushGrid).expandX().fillX().row();
    }

    private void createTerrainTextureTools() {
        textureBrushGrid = new GridGroup(40, 5);

        content.add(new VisLabel("Terrain texture brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(textureBrushGrid).expandX().fillX().row();
    }

    private void createTransfornamtionTools() {
        transformationToolsGrid = new GridGroup(40, 5);

        content.add(new VisLabel("Transformation tools")).left().pad(5).row();
        content.addSeparator();
        content.add(transformationToolsGrid).expandX().fillX().row();
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    private class BrushGridItem extends VisTable {

        private Brush brush;

        private BrushGridItem(Brush brush) {
            super();
            this.brush = brush;
            setBackground("menu-bg");
            align(Align.center);
            VisImage img = new VisImage(brush.getIcon());
            img.setScaling(Scaling.fit);
            new Tooltip(img, brush.getName());
            add(img).expand().fill().pad(2);

            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    brushManager.activate(brush);
                }
            });
        }

        public Brush getBrush() {
            return brush;
        }
    }

}
