package com.mbrlabs.mundus.ui.components.dialogs;

import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.ui.widgets.TextureGrid;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TextureBrowser extends VisDialog {

    private TextureGrid textureGrid;
    private VisScrollPane scrollPane;

    @Inject
    private ProjectContext projectContext;

    public TextureBrowser() {
        super("Select a texture");
        Mundus.inject(this);

        textureGrid = new TextureGrid(20, 3, projectContext.textures);
        scrollPane = new VisScrollPane(textureGrid);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane);
    }

    public void setTextureListener(TextureGrid.OnTextureClickedListener listener) {
        textureGrid.setListener(listener);
    }

}
