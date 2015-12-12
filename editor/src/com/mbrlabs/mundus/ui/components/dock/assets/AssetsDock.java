package com.mbrlabs.mundus.ui.components.dock.assets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.model.PersistableModel;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.events.ModelImportEvent;
import com.mbrlabs.mundus.events.ReloadAllModelsEvent;
import com.mbrlabs.mundus.events.Subscribe;
import com.mbrlabs.mundus.utils.Log;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class AssetsDock {

    private VisTable root;
    private VisTable filesViewContextContainer;
    private GridGroup filesView;
    private AssetsTab assetsTab;

    @Inject
    private EventBus eventBus;
    @Inject
    private ProjectContext projectContext;

    public AssetsDock() {
        Mundus.inject(this);
        eventBus.register(this);
        initUi();
    }

    private void initUi () {
        root = new VisTable();
        filesViewContextContainer = new VisTable(false);
        filesView = new GridGroup(92, 4);

        VisTable contentsTable = new VisTable(false);
        contentsTable.add(new VisLabel("Assets")).left().padLeft(3).row();
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

//        for(int i = 1; i <= 300; i++) {
//            filesView.addActor(new AssetItem("Asset " + i));
//        }
    }

    @Subscribe
    public void modelImported(ModelImportEvent modelImportEvent) {
        Log.debug("@Subscribe modelImported called");
        AssetItem assetItem = new AssetItem(modelImportEvent.getModel().getName());
        filesView.addActor(assetItem);
    }

    @Subscribe
    public void reloadAllModels(ReloadAllModelsEvent reloadAllModelsEvent) {
        Log.debug("@Subscribe reload models");
        filesView.clearChildren();
        for(PersistableModel model : projectContext.models) {
            filesView.addActor(new AssetItem(model.getName()));
        }
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

    private class AssetItem extends VisTable {

        private VisLabel nameLabel;

        public AssetItem(String name) {
            super();
            setBackground("menu-bg");
            align(Align.center);
            nameLabel = new VisLabel(name, "small");
            nameLabel.setWrap(true);
            add(nameLabel).fill().expand().row();
        }

    }
}
