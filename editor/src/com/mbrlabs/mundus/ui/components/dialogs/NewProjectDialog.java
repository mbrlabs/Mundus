package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.data.ProjectManager;
import com.mbrlabs.mundus.core.data.home.ProjectRef;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class NewProjectDialog extends BaseDialog {

    private VisTextField projectPath;
    private VisTextField projectName;

    private VisTextButton createBtn;

    @Inject
    private ProjectManager projectManager;

    public NewProjectDialog() {
        super("Create New Project");
        Mundus.inject(this);
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(new VisLabel("Project Name:")).right().padRight(5);
        this.projectName = new VisTextField();
        root.add(this.projectName).height(21).width(300).fillX();
        root.row().padTop(10);
        root.add(new VisLabel("Project Folder:")).right().padRight(5);
        projectPath = new VisTextField();
        root.add(projectPath).width(300).padBottom(15).row();
        //mainTable.add(new Separator()).padTop(2).padBottom(2).fill().expand();

        createBtn = new VisTextButton("Create");
        root.add(createBtn).width(93).height(25).colspan(2);

        setupListeners();
    }

    private void setupListeners() {

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = projectName.getText();
                String path = projectPath.getText();
                if(validateInput(name, path)) {
                    ProjectRef ref = projectManager.createProject(name, path);
                    close();
                    Ui.getInstance().getLoadingProjectDialog().loadProjectAsync(ref);
                }

            }
        });

    }

    private boolean validateInput(String name, String path) {
        return name != null && name.length() > 0 && path != null && path.length() > 0;
    }






}
