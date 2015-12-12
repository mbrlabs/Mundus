package com.mbrlabs.mundus.ui.components.sidebar;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import org.lwjgl.openal.AL10;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Sidebar extends TabbedPane implements TabbedPaneListener {

    private TerrainTab terrainTab;
    private EntityTab entityTab;
    private ModelTab modelTab;

    private VisTable contentContainer;

    public Sidebar() {
        super();
        terrainTab = new TerrainTab();
        entityTab = new EntityTab();
        modelTab = new ModelTab();
        contentContainer = new VisTable();
        contentContainer.setBackground(VisUI.getSkin().getDrawable("default-pane"));
        contentContainer.align(Align.topLeft);

        add(modelTab);
        add(terrainTab);
        add(entityTab);

        switchTab(modelTab);
        switchedTab(modelTab);

        addListener(this);
    }

    public TerrainTab getTerrainTab() {
        return terrainTab;
    }

    public EntityTab getEntityTab() {
        return entityTab;
    }

    public ModelTab getModelTab() {
        return modelTab;
    }

    public VisTable getContentContainer() {
        return contentContainer;
    }

    @Override
    public void switchedTab(Tab tab) {
        contentContainer.clear();
        contentContainer.add(tab.getContentTable()).fill().expand();
    }

    @Override
    public void removedTab(Tab tab) {
        // we don't remove tabs from the sidebar
    }

    @Override
    public void removedAllTabs() {
        // we don't remove tabs from the sidebar
    }

}