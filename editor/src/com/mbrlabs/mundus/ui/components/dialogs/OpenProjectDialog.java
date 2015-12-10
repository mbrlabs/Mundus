package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.data.home.MundusHome;
import com.mbrlabs.mundus.core.data.home.ProjectRef;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.ui.components.RadioButtonGroup;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class OpenProjectDialog extends BaseDialog {

    private VisTextField path;
    private VisTextButton openBtn;
    private RadioButtonGroup<ProjectRef> projectList;

    @Inject
    private MundusHome home;

    public OpenProjectDialog() {
        super("Open Project");
        Mundus.inject(this);
        setModal(true);

        VisTable root = new VisTable();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        projectList = new RadioButtonGroup<>();
        projectList.left();
        ScrollPane scrollPane = new VisScrollPane(projectList);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        root.add(scrollPane).minWidth(350).maxHeight(400).left().row();

        for(ProjectRef project : home.getProjectRefs().getProjects()) {
            String text = project.getName() + " [" + project.getPath() + "]";
            RadioButtonGroup.RadioButton btn = new RadioButtonGroup.RadioButton(text, project);
            projectList.add(btn);
        }

        openBtn = new VisTextButton("Open");
        root.add(openBtn).width(93).height(25).padTop(15);

        setupListeners();
    }

    private void setupListeners() {

        openBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RadioButtonGroup.RadioButton selected = projectList.getButtonGroup().getChecked();
                ProjectRef projectRef = (ProjectRef)selected.getRefObject();
                close();
                Ui.getInstance().getLoadingProjectDialog().loadProjectAsync(projectRef);
            }
        });

    }



}
