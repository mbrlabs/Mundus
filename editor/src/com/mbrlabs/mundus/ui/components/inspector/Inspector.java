/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.components.inspector;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.scene3d.GameObject;
import com.mbrlabs.mundus.scene3d.components.Component;
import com.mbrlabs.mundus.scene3d.components.ModelComponent;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class Inspector extends VisTable implements GameObjectSelectedEvent.GameObjectSelectedListener, GameObjectModifiedEvent.GameObjectModifiedListener {

    private VisTable root;
    private ScrollPane scrollPane;

    private IdentifierWidget identifierWidget;
    private TransformWidget transformWidget;
    private Array<ComponentWidget> componentWidgets;

    private VisTextButton addComponentBtn;

    private VisTable componentTable;

    @Inject
    private ProjectContext projectContext;

    public Inspector() {
        super();
        Mundus.inject(this);
        Mundus.registerEventListener(this);
        identifierWidget = new IdentifierWidget();
        transformWidget = new TransformWidget();
        componentWidgets = new Array<>();
        addComponentBtn = new VisTextButton("Add Component");
        componentTable = new VisTable();

        init();
        setupUi();
        setupListeners();
    }

    public void init() {
        setBackground("default-pane");
        root = new VisTable();
        scrollPane = new ScrollPane(root);
        add(scrollPane).expand().fill();
    }

    public void setupUi() {
        root.add(new VisLabel("Inspector")).expandX().fillX().pad(3).row();
        root.addSeparator().row();
        root.add(identifierWidget).expand().fillX().row();
        root.add(transformWidget).expand().fillX().row();
        for(BaseInspectorWidget cw : componentWidgets) {
            componentTable.add(cw).row();
        }
        root.add(componentTable).fill().expand().row();
        root.add(addComponentBtn).expandX().fillX().pad(10).row();
    }

    private void buildComponentWidgets() {
        componentWidgets.clear();
        if(projectContext.currScene.currentSelection != null) {
            for(Component component : projectContext.currScene.currentSelection.getComponents()) {
                if(component.getType() == Component.Type.MODEL) {
                    componentWidgets.add(new ModelComponentWidget((ModelComponent)component));
                }
            }
        }
    }

    public void setupListeners() {

    }

    private void setValues(GameObject go) {
        identifierWidget.setValues(go);
        transformWidget.setValues(go);

        buildComponentWidgets();
        componentTable.clearChildren();

        for(ComponentWidget cw : componentWidgets) {
            componentTable.add(cw).expand().fill().row();
            cw.setValues(projectContext.currScene.currentSelection);
        }
    }

    @Override
    public void onGameObjectSelected(GameObjectSelectedEvent gameObjectSelectedEvent) {
        setValues(projectContext.currScene.currentSelection);
    }

    @Override
    public void onGameObjectModified(GameObjectModifiedEvent gameObjectModifiedEvent) {
        identifierWidget.setValues(projectContext.currScene.currentSelection);
        transformWidget.setValues(projectContext.currScene.currentSelection);
    }

}
