package com.mbrlabs.mundus.editor.ui.modules.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.core.Inject;
import com.mbrlabs.mundus.editor.core.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;

/**
 * @version 09-10-2016
 * @author attilabo
 */
public class ExitDialog extends BaseDialog {

    private static final String TITLE = "Confirm exit";

    @Inject
    private ProjectManager projectManager;

    private VisTextButton exit;
    private VisTextButton saveExit;
    private VisTextButton cancel;

    public ExitDialog() {
        super(TITLE);
        Mundus.inject(this);
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Table root = new Table();
        root.padTop(6).padRight(6).padBottom(10);
        add(root);

        exit = new VisTextButton("Exit");
        saveExit = new VisTextButton("Save and Exit");
        cancel = new VisTextButton("Cancel");

        root.add(new VisLabel("Do you really want to close Mundus?")).grow().center().colspan(3).padBottom(10).row();
        root.add(cancel).padRight(5).grow();
        root.add(exit).padRight(5).grow();
        root.add(saveExit).grow().row();
    }

    private void setupListeners() {
        // cancel
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        // exit
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // save current project & exit
        saveExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                projectManager.saveCurrentProject();
                Gdx.app.exit();
            }
        });
    }

}
