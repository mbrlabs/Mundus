package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.tools.ToolManager;
import com.mbrlabs.mundus.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.widgets.FaTextButton;

/**
 * @author Marcus Brummer
 * @version 29-01-2016
 */
public class TerrainComponentWidget extends ComponentWidget<TerrainComponent> implements TabbedPaneListener {

    private TabbedPane tabbedPane;
    private VisTable tabContainer = new VisTable();

    private RaiseLowerTab raiseLowerTab;
    private FlattenTab flattenTab;
    private PaintTab paintTab;

    @Inject
    private ToolManager toolManager;

    public TerrainComponentWidget(TerrainComponent terrainComponent) {
        super("Terrain Component", terrainComponent);
        Mundus.inject(this);

        tabbedPane = new TabbedPane();
        tabbedPane.addListener(this);

        raiseLowerTab = new RaiseLowerTab();
        flattenTab = new FlattenTab();
        paintTab = new PaintTab();

        tabbedPane.add(raiseLowerTab);
        tabbedPane.add(flattenTab);
        tabbedPane.add(paintTab);

        setupUI();

        tabbedPane.switchTab(0);
    }

    private void setupUI() {
        collapsibleContent.add(tabbedPane.getTable()).padBottom(7).row();
        collapsibleContent.add(tabContainer).expand().fill().row();
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

    @Override
    public void switchedTab(Tab tab) {
        tabContainer.clearChildren();
        tabContainer.add(tab.getContentTable()).expand().fill();
    }

    @Override
    public void removedTab(Tab tab) {
        // no
    }

    @Override
    public void removedAllTabs() {
        // nope
    }

    /**
     *
     */
    private class RaiseLowerTab extends Tab {

        private VisTable table;

        public RaiseLowerTab() {
            super(false, false);
            table = new VisTable();
            table.align(Align.left);
            table.add(new VisLabel("Up/Down")).row();
        }

        @Override
        public String getTabTitle() {
            return "Up/Down";
        }

        @Override
        public Table getContentTable() {
            return table;
        }
    }

    /**
     *
     */
    private class FlattenTab extends Tab {

        private VisTable table;

        public FlattenTab() {
            super(false, false);
            table = new VisTable();
            table.align(Align.left);
            table.add(new VisLabel("Flatten"));
        }

        @Override
        public String getTabTitle() {
            return "Flatten";
        }

        @Override
        public Table getContentTable() {
            return table;
        }
    }

    /**
     *
     */
    private class PaintTab extends Tab {

        private VisTable table;

        public PaintTab() {
            super(false, false);
            table = new VisTable();
            table.align(Align.left);
            table.add(new VisLabel("Paint"));
        }

        @Override
        public String getTabTitle() {
            return "Paint";
        }

        @Override
        public Table getContentTable() {
            return table;
        }
    }


    /**
     *
     */
    private class BrushTable extends VisTable {
        private FaTextButton sphereBrushBtn;
        private TerrainBrush.BrushMode brushMode;

        public BrushTable() {
            sphereBrushBtn = new FaTextButton(toolManager.sphereBrushTool.getIconFont(), FaTextButton.styleBg);
            add(new VisLabel("Brushes:")).padBottom(10).row();
            add(sphereBrushBtn).width(30);

            sphereBrushBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    activateBrush(toolManager.sphereBrushTool);
                }
            });
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
                brush.setTerrain(component.getTerrain());
            } catch (TerrainBrush.ModeNotSupportedException e) {
                e.printStackTrace();
                DialogUtils.showErrorDialog(Ui.getInstance(), e.getMessage());
            }

        }

    }



}
