package com.mbrlabs.mundus.ui.components.inspector;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
public class TerrainComponentWidget extends ComponentWidget<TerrainComponent> {

    private ToolTable toolTable;
    private BrushTabele brushTabele;

    public TerrainComponentWidget(TerrainComponent terrainComponent) {
        super("Terrain Component", terrainComponent);
        setupUI();
    }

    private void setupUI() {
        collapsibleContent.add(new VisLabel("Terrain: "));
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void setValues(GameObject go) {
        Component c = go.findComponentByType(Component.Type.TERRAIN);
        if(c != null) {
            this.component = (TerrainComponent) c;
        }
    }

    /**
     *
     */
    private class ToolTable extends VisTable {

    }

    /**
     *
     */
    private class BrushTabele extends VisTable {

    }



}
