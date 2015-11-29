package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.data.home.MundusHome;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.FbxConv;
import org.apache.commons.io.FilenameUtils;

import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class ImportModelDialog extends BaseDialog {

    private Container fake3dViewport;

    private VisTextField modelPath = new VisTextField();
    private VisTextField texturePath = new VisTextField();
    private VisTextButton importBtn = new VisTextButton("IMPORT");
    private VisTextButton fileChooserBtn = new VisTextButton("Select Model & Textures");

    private Model previewModel;
    private ModelInstance previewInstance;

    private FileHandle tempModelCache;

    public ImportModelDialog() {
        super("Import Model");
        setModal(true);
        setMovable(false);
        Table root = new Table();
        //root.debugAll();
        root.padTop(6).padRight(6).padBottom(22);
        add(root);

        VisTable inputTable = new VisTable();
        fake3dViewport = new Container();
        fake3dViewport.setBackground(VisUI.getSkin().getDrawable("default-pane"));
        fake3dViewport.setActor(new VisLabel("PREVIEW"));

        root.add(inputTable).width(300).height(300).padRight(10);
        root.add(fake3dViewport).width(300).height(300);

        inputTable.left().top();
        inputTable.add(fileChooserBtn).fillX().expandX().padBottom(10).row();
        inputTable.add(new VisLabel("Model File")).left().padBottom(5).row();
        inputTable.add(modelPath).fillX().expandX().padBottom(10).row();
        inputTable.add(new VisLabel("Texture File")).left().padBottom(5).row();
        inputTable.add(texturePath).fillX().expandX().row();
        inputTable.add(importBtn).fillX().expand().bottom();

        importBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showPreview();
            }
        });

        // button launches file chooser
        fileChooserBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                // add handler
                Ui.getInstance().getFileChooser().setListener(new FileChooserAdapter() {
                    @Override
                    public void selected(Array<FileHandle> files) {
                        super.selected(files);
                        handleInputFiles(files);
                    }
                });
                // show file chooser
                Ui ui = Ui.getInstance();
                FileChooser fileChooser = ui.getFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                Ui.getInstance().addActor(Ui.getInstance().getFileChooser().fadeIn());
            }
        });
    }

    private void handleInputFiles(Array<FileHandle> files) {
        if(files.size == 2) {
            tempModelCache = createTempModelFolder();

            // get model
            FileHandle modelFile = null;
            if(files.get(0).path().endsWith("fbx")) {
                modelFile = files.get(0);
            } else if(files.get(1).path().endsWith("fbx")) {
                modelFile = files.get(1);
            }

            // get texture
            FileHandle textureFile = null;
            if(files.get(0).path().endsWith("png")) {
                textureFile = files.get(0);
            } else if(files.get(1).path().endsWith("png")) {
                textureFile = files.get(1);
            }

            // copy (& evtentually convert) model & texture into temp folder
            if(textureFile != null) {
                texturePath.setText(textureFile.path());
                textureFile.copyTo(tempModelCache);
            }
            FbxConv.FbxConvResult result = null;
            if(modelFile != null) {
                result = new FbxConv().input(modelFile.path())
                        .output(tempModelCache.file().getAbsolutePath()).
                                flipTexture(true).execute();
                modelPath.setText(modelFile.path());
            }

            // load Model & texture and show preview
            if(result != null && result.isSuccess()) {
                removePreview();
                FileHandle convertedModel = Gdx.files.absolute(result.getOutputFile());
                previewModel = new G3dModelLoader(new UBJsonReader()).loadModel(convertedModel);
                previewInstance = new ModelInstance(previewModel);
                showPreview();
            }

        }
    }

    private FileHandle createTempModelFolder() {
        String tempFolderPath = FilenameUtils.concat(
                MundusHome.MODEL_CACHE_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    @Override
    protected void close() {
        for(FileHandle f : Gdx.files.absolute(MundusHome.MODEL_CACHE_DIR).list()) {
            f.deleteDirectory();
        }
        Ui.getInstance().unwire(fake3dViewport);
        super.close();
    }

    private void showPreview() {
        if(fake3dViewport.getActor() != null) {
            fake3dViewport.removeActor(fake3dViewport.getActor());
        }
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
