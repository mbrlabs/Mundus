package com.mbrlabs.mundus.ui.components.dialogs;

import com.kotcrab.vis.ui.widget.*;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class NewProjectDialog extends BaseDialog {

    private VisTextField path;
    private VisTextField projectName;

    private VisTextButton createBtn;

    public NewProjectDialog() {
        super("Create New Project");
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(new VisLabel("Project Name:")).right().padRight(5);
        this.projectName = new VisTextField();
        root.add(this.projectName).height(21).width(300).fillX();
        root.row().padTop(10);
        root.add(new VisLabel("Project Folder:")).right().padRight(5);
        path = new VisTextField();
        root.add(path).width(300).padBottom(15).row();
        //mainTable.add(new Separator()).padTop(2).padBottom(2).fill().expand();

        createBtn = new VisTextButton("Create");
        root.add(createBtn).width(93).height(25).colspan(2);
    }


}
