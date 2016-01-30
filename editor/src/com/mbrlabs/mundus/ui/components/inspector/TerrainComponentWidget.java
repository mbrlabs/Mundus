package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
public class TerrainComponentWidget extends ComponentWidget<TerrainComponent> {

    private ToolTable toolTable;
    private BrushTable brushTable;

    @Inject
    private ToolManager toolManager;

    public TerrainComponentWidget(TerrainComponent terrainComponent) {
        super("Terrain Component", terrainComponent);
        Mundus.inject(this);
        setupUI();
    }

    private void setupUI() {
        toolTable = new ToolTable();
        toolTable.align(Align.center);

        toolTable.pad(4);
        collapsibleContent.add(toolTable).center().expandX().row();
    }

    public void setBrushMode() {

    }

    public void setPaintMode() {

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

        private FaTextButton raiseLowerBtn;
        private FaTextButton paintBtn;

        public ToolTable() {
            setBackground(VisUI.getSkin().getDrawable("menu-bg"));
            raiseLowerBtn = new FaTextButton(Fa.SORT);
            paintBtn = new FaTextButton(Fa.PAINT_BRUSH);

            add(raiseLowerBtn).width(30);
            addSeparator(true);
            add(paintBtn).width(30).row();

            raiseLowerBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setBrushMode();
                }
            });

            paintBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setPaintMode();
                }
            });
        }

    }

    /**
     *
     */
    private class BrushTable extends VisTable {

    }



}
