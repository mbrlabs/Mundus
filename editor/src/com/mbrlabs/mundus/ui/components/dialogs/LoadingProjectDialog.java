package com.mbrlabs.mundus.ui.components.dialogs;

import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.data.ProjectContext;
import com.mbrlabs.mundus.core.data.ProjectManager;
import com.mbrlabs.mundus.core.data.home.ProjectRef;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.Callback;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class LoadingProjectDialog extends VisDialog {

    private VisLabel projectName;
    @Inject
    private ProjectManager projectManager;

    public LoadingProjectDialog() {
        super("Loading Project");
        Mundus.inject(this);
        setModal(true);
        setMovable(false);

        projectName = new VisLabel("Project Folder:");

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        root.add(projectName).right().padRight(5);
    }

    public void loadProjectAsync(ProjectRef ref) {
        this.projectName.setText("Loading project: " + ref.getName());
        Ui.getInstance().showDialog(this);

        projectManager.loadProject(ref, new Callback<ProjectContext>() {
            @Override
            public void done(ProjectContext result) {
                projectManager.changeProject(result);
                close();
            }

            @Override
            public void error(String msg) {
                close();
                DialogUtils.showErrorDialog(Ui.getInstance(), msg);
            }
        });
    }


}
