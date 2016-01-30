package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;
import com.mbrlabs.mundus.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
public class TerrainComponentWidget extends ComponentWidget<TerrainComponent> {

    private enum Tab {
        RAISE_LOWER, PAINT_HEIGHT, SMOOTH, PAINT
    }

    private Tab tab = Tab.RAISE_LOWER;

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
        brushTable = new BrushTable();
        toolTable.align(Align.center);

        toolTable.pad(4);
        collapsibleContent.add(toolTable).center().expandX().row();
        collapsibleContent.add(brushTable).left().expandX().row();
    }

    public void setBrushMode() {
        this.tab = Tab.RAISE_LOWER;

    }

    public void setPaintMode() {
        this.tab = Tab.PAINT;
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
        private FaTextButton sphereBrushBtn;

        public BrushTable() {
            setBackground(VisUI.getSkin().getDrawable("menu-bg"));
            sphereBrushBtn = new FaTextButton(toolManager.sphereBrushTool.getIconFont());
            add(sphereBrushBtn).width(30);

            sphereBrushBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    activateBrush(toolManager.sphereBrushTool);
                }
            });
        }

        public void activateBrush(TerrainBrush brush) {

            TerrainBrush.BrushMode brushMode = null;
            if(tab == Tab.RAISE_LOWER) {
                brushMode = TerrainBrush.BrushMode.RAISE_LOWER;
            } else if(tab == Tab.PAINT_HEIGHT) {
                brushMode = TerrainBrush.BrushMode.PAINT_HEIGHT;
            } else if(tab == Tab.SMOOTH) {
                brushMode = TerrainBrush.BrushMode.SMOOTH;
            } else if(tab == Tab.PAINT) {
                brushMode = TerrainBrush.BrushMode.PAINT;
            }

            try {
                if(brushMode != null) {
                    brush.setMode(brushMode);
                    toolManager.activateTool(brush);
                    brush.setTerrain(component.getTerrain());
                }
            } catch (TerrainBrush.ModeNotSupportedException e) {
                e.printStackTrace();
                DialogUtils.showErrorDialog(Ui.getInstance(), e.getMessage());
            }

        }

    }



}
