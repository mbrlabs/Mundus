package com.mbrlabs.mundus.ui.components.docker;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class DockerAssets {

    private VisTable root;
    private VisTable filesViewContextContainer;
    private GridGroup filesView;
    private AssetsTab assetsTab;

    public DockerAssets() {
        initUi();
    }

    private void initUi () {
        root = new VisTable();
        filesViewContextContainer = new VisTable(false);
        filesView = new GridGroup(92, 4);

        VisTable contentsTable = new VisTable(false);
        contentsTable.add(new Separator()).padTop(3).expandX().fillX();
        contentsTable.row();
        contentsTable.add(filesViewContextContainer).expandX().fillX();
        contentsTable.row();
        contentsTable.add(createScrollPane(filesView, true)).expand().fill();

        VisSplitPane splitPane = new VisSplitPane(new VisLabel("file tree here"), contentsTable, false);
        splitPane.setSplitAmount(0.2f);

        root = new VisTable();
        root.setBackground("window-bg");
        root.add(splitPane).expand().fill();

        assetsTab = new AssetsTab();

        filesView.setTouchable(Touchable.enabled);
        filesView.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {


                return false;
            }
        });
    }

    private VisScrollPane createScrollPane (Actor actor, boolean disableX) {
        VisScrollPane scrollPane = new VisScrollPane(actor);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(disableX, false);
        return scrollPane;
    }


    public AssetsTab getAssetsTab() {
        return assetsTab;
    }

    public VisTable getRoot() {
        return root;
    }

    /**
     *
     */
    private class AssetsTab extends Tab {

        public AssetsTab() {
            super(false, false);
        }

        @Override
        public String getTabTitle() {
            return "Assets";
        }

        @Override
        public Table getContentTable() {
            return root;
        }
    }
}
