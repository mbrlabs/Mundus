package com.mbrlabs.mundus.ui.components.docker;


import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class DockerBar extends VisTable implements TabbedPaneListener {

    private DockerAssets dockerAssets;
    private TabbedPane tabbedPane;

    public DockerBar() {
        super();
        TabbedPane.TabbedPaneStyle tabStyle = new TabbedPane.TabbedPaneStyle(VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class));
        tabStyle.buttonStyle = new VisTextButton.VisTextButtonStyle(VisUI.getSkin().get("toggle", VisTextButton.VisTextButtonStyle.class));
        tabStyle.buttonStyle.font = VisUI.getSkin().getFont("small-font");
        tabStyle.bottomBar = null;

        tabbedPane = new TabbedPane(tabStyle);
        tabbedPane.setAllowTabDeselect(true);
        tabbedPane.addListener(this);

        dockerAssets = new DockerAssets();
        tabbedPane.add(dockerAssets.getAssetsTab());

    }

    @Override
    public void switchedTab(Tab tab) {
        if(tab == null) {
            clear();
            add(tabbedPane.getTable()).expand().fill().left().row();
        } else {
            clear();
            add(tab.getContentTable()).left().expand().fill().minHeight(400).row();
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
