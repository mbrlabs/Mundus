package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.FbxConv;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class ImportModelDialog extends BaseDialog {

    private Actor fake3dViewport;

    private VisTextField path = new VisTextField();
    private VisTextField name = new VisTextField();;
    private VisTextButton importBtn = new VisTextButton("IMPORT");
    private VisTextButton fileChooserBtn = new VisTextButton("Add");

    private Model previewModel;
    private ModelInstance previewInstance;

    public ImportModelDialog() {
        super("Import Model");
        setModal(true);
        setMovable(false);
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        VisTable inputTable = new VisTable();
        fake3dViewport = new Actor();

        root.add(inputTable).width(300).height(300);
        root.add(fake3dViewport).width(300).height(300);

        inputTable.left().top();
        inputTable.add(new VisLabel("Model:")).padRight(10);
        inputTable.add(path).width(200);
        inputTable.add(fileChooserBtn).padLeft(20).row();
        inputTable.add(new VisLabel("Name: ")).padRight(10).padTop(10);
        inputTable.add(name).width(200).padTop(10).colspan(2).row();
        inputTable.add(importBtn).width(100).padTop(15).colspan(3).center();

        importBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showPreview();
            }
        });

        fileChooserBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Ui.getInstance().getFileChooser().setListener(new FileChooserAdapter() {
                    @Override
                    public void selected(FileHandle file) {
                        super.selected(file);
                        path.setText(file.path());
                        FbxConv.FbxConvResult result = new FbxConv().input(file.path())
                                .output(FilenameUtils.getFullPath(file.file().getAbsolutePath())).flipTexture(true).execute();

                        removePreview();
                        previewModel = new G3dModelLoader(new UBJsonReader()).loadModel(Gdx.files.absolute(result.getOutputFile()));
                        previewInstance = new ModelInstance(previewModel);
                        showPreview();
                    }
                });

                Ui.getInstance().addActor(Ui.getInstance().getFileChooser().fadeIn());
            }
        });


    }

    @Override
    protected void close() {
        Ui.getInstance().unwire(fake3dViewport);
        super.close();
    }

    private void showPreview() {
        previewInstance = new ModelInstance(previewModel);
        Ui.getInstance().wireModelToActor(fake3dViewport, previewInstance);
    }

    private void removePreview() {
        Ui.getInstance().unwire(fake3dViewport);
        if(previewModel != null) {
            previewModel.dispose();
            previewModel = null;
        }
    }




}
