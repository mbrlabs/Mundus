/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.modules.dialogs;

import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.mbrlabs.mundus.commons.model.MTexture;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.events.ProjectChangedEvent;
import com.mbrlabs.mundus.events.TextureImportEvent;
import com.mbrlabs.mundus.ui.widgets.TextureGrid;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TextureBrowser extends BaseDialog implements TextureImportEvent.TextureImportListener, ProjectChangedEvent.ProjectChangedListener {

    private TextureGrid<MTexture> textureGrid;
    private VisScrollPane scrollPane;

    @Inject
    private ProjectManager projectManager;

    public TextureBrowser() {
        super("Select a texture");
        Mundus.inject(this);
        Mundus.registerEventListener(this);

        textureGrid = new TextureGrid<>(60, 7, projectManager.current().textures);
        scrollPane = new VisScrollPane(textureGrid);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).maxSize(600, 400).size(600, 400);
    }

    public void setTextureListener(TextureGrid.OnTextureClickedListener listener) {
        textureGrid.setListener(listener);
    }

    @Override
    public void onProjectChanged(ProjectChangedEvent projectChangedEvent) {
        textureGrid.setTextures(projectManager.current().textures);
    }

    @Override
    public void onTextureImported(TextureImportEvent importEvent) {
        textureGrid.setTextures(projectManager.current().textures);
    }
}
