package com.mbrlabs.mundus.ui.components.dialogs;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class OpenProjectDialog extends BaseDialog {

    private VisTextField path;
    private VisTextButton openBtn;

    public OpenProjectDialog() {
        super("Open Project");
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(new VisLabel("Project Folder:")).right().padRight(5);
        path = new VisTextField();
        root.add(path).width(300).row();
        //mainTable.add(new Separator()).padTop(2).padBottom(2).fill().expand();

        openBtn = new VisTextButton("Open");
        root.add(openBtn).width(93).height(25).padTop(15).colspan(2);
    }

}
