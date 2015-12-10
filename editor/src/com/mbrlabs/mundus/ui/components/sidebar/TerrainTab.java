package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.core.BrushManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.brushes.Brush;
import com.mbrlabs.mundus.terrain.brushes.SphereBrush;
import com.mbrlabs.mundus.ui.ReloadableData;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainTab extends Tab implements ReloadableData {

    private static final String TITLE = "Terrain";

    private VisTable content;

    @Inject
    private BrushManager brushManager;

    private GridGroup meshBrushGrid;
    private GridGroup textureBrushGrid;

    public TerrainTab() {
        super(false, false);
        Mundus.inject(this);
        content = new VisTable();
        content.align(Align.left | Align.top);

        meshBrushGrid = new GridGroup(40, 5);
        textureBrushGrid = new GridGroup(40, 5);

        for(Brush brush : brushManager.brushes) {
            meshBrushGrid.addActor(new BrushGridItem(brush));
        }

        // add mesh brushes
        content.add(new VisLabel("Mesh brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(meshBrushGrid).left().row();


        // add texture brushes
        content.add(new VisLabel("Texture brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(textureBrushGrid).left().row();
    }

    @Override
    public String getTabTitle() {
        return TITLE;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void reloadData() {

    }

    private class BrushGridItem extends VisTable {

        private Brush brush;

        private BrushGridItem(Brush brush) {
            super();
            this.brush = brush;
            setBackground("window-bg");
            VisImage img = new VisImage(brush.getIcon());
            img.setScaling(Scaling.fit);
            new Tooltip(img, brush.getName());
            add(img).expand().fill().pad(2).row();

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
