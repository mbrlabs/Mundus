package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
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

    public TerrainTab() {
        super(false, false);
        Mundus.inject(this);
        content = new VisTable();

        // Sphere brush


        for(Brush brush : brushManager.brushes) {
            VisImageButton btn = new VisImageButton(brush.getIcon());
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    brushManager.activate(brush);
                }
            });
            content.add(btn).row();
        }


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

}
