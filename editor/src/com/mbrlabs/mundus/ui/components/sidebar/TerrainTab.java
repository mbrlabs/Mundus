package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.terrain.brushes.BrushManager;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.terrain.brushes.Brush;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainTab extends Tab {

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

        // add more sphere brushes to test the grid
        // TODO remove
        for(int i = 0; i < 10; i++) {
            meshBrushGrid.addActor(new BrushGridItem(brushManager.brushes.first()));
        }


        // add mesh brushes
        content.add(new VisLabel("Mesh brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(meshBrushGrid).expandX().fillX().row();


        // add texture brushes
        content.add(new VisLabel("Texture brushes")).left().pad(5).row();
        content.addSeparator();
        content.add(textureBrushGrid).expandX().fillX().row();
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
