package com.mbrlabs.mundus.ui.components.dock;


import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import com.mbrlabs.mundus.ui.components.dock.assets.AssetsDock;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class DockBar extends VisTable implements TabbedPaneListener {

    private AssetsDock assetsDock;
    private TabbedPane tabbedPane;

    public DockBar() {
        super();
        TabbedPane.TabbedPaneStyle tabStyle = new TabbedPane.TabbedPaneStyle(VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class));
        tabStyle.buttonStyle = new VisTextButton.VisTextButtonStyle(VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle.class));
        tabStyle.buttonStyle.font = VisUI.getSkin().getFont("small-font");
        tabStyle.bottomBar = null;

        tabbedPane = new TabbedPane(tabStyle);
        tabbedPane.setAllowTabDeselect(true);
        tabbedPane.addListener(this);

        assetsDock = new AssetsDock();
        tabbedPane.add(assetsDock.getAssetsTab());

        switchedTab(null);

    }

    @Override
    public void switchedTab(Tab tab) {
        if(tab == null) {
            clear();
            add(tabbedPane.getTable()).expand().fill().left().row();
        } else {
            clear();
            add(tab.getContentTable()).left().expand().fill().height(Gdx.graphics.getHeight()*0.4f).row();
            add(tabbedPane.getTable()).expand().fill().left().row();
        }

    }

    @Override
    public void removedTab(Tab tab) {

    }

    @Override
    public void removedAllTabs() {

    }

}
