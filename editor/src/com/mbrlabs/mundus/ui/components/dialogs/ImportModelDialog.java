package com.mbrlabs.mundus.ui.components.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.UBJsonReader;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.mbrlabs.mundus.core.data.home.MundusHome;
import com.mbrlabs.mundus.ui.Ui;
import com.mbrlabs.mundus.utils.FbxConv;
import com.mbrlabs.mundus.utils.FileFormatUtils;
import com.mbrlabs.mundus.utils.Log;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class ImportModelDialog extends BaseDialog implements Disposable {

    private static final String TAG = ImportModelDialog.class.getSimpleName();

    // UI elements
    private Container fake3dViewport;
    private FileChooser fileChooser;
    private VisTextField modelPath = new VisTextField();
    private VisTextField texturePath = new VisTextField();
    private VisTextButton importBtn = new VisTextButton("IMPORT");
    private VisTextButton fileChooserBtn = new VisTextButton("Select Model & Textures");

    // preview model + instance
    private Model previewModel;
    private ModelInstance previewInstance;

    // temp files/dirs
    private FileHandle tempModelCache;
    private FileHandle tempModelFile;
    private FileHandle tempTextureFile;

    public ImportModelDialog() {
        super("Import Model");
        setModal(true);
        setMovable(false);
        setupUI();
        setupListener();
    }

    private void setupUI() {
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

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setMultiSelectionEnabled(true);
    }

    private void setupListener() {
        // import btn
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
                dispose();
                Ui.getInstance().addActor(fileChooser.fadeIn());
            }
        });

        // file chooser
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                super.selected(files);
                loadAndShowPreview(files);
            }
        });
    }


    private void loadAndShowPreview(Array<FileHandle> files) {
        if(files.size == 2) {
            tempModelCache = MundusHome.getInstance().createTempModelFolder();

            // get model
            FileHandle origModelFile = null;
            if(FileFormatUtils.is3DFormat(files.get(0))) {
                origModelFile = files.get(0);
            } else if(FileFormatUtils.is3DFormat(files.get(1))) {
                origModelFile = files.get(1);
            }

            // get texture
            FileHandle origTextureFile = null;
            if(FileFormatUtils.isPNG(files.get(0))) {
                origTextureFile = files.get(0);
            } else if(FileFormatUtils.isPNG(files.get(1))) {
                origTextureFile = files.get(1);
            }

            // load and show preview
            if(origModelFile != null && origTextureFile != null) {
                if(origModelFile.exists() && origTextureFile.exists()) {
                    copyToTempFolder(origModelFile, origTextureFile);
                    previewModel = new G3dModelLoader(new UBJsonReader()).loadModel(tempModelFile);
                    previewInstance = new ModelInstance(previewModel);
                    showPreview();
                } else {
                    Log.error(TAG, "input files does not exist");
                }
            } else {
                Log.error(TAG, "input files are null");
            }
        }
    }

    private void copyToTempFolder(FileHandle origModel, FileHandle origTexture) {
        // copy texture
        texturePath.setText(origTexture.path());
        origTexture.copyTo(tempModelCache);
        tempTextureFile = Gdx.files.absolute(FilenameUtils.concat(
                origTexture.file().getAbsolutePath(), origTexture.name()));

        // copy (and eventually convert) model
        boolean convert = FileFormatUtils.isFBX(origModel)
                || FileFormatUtils.isCollada(origModel)
                || FileFormatUtils.isWavefont(origModel);
        // fbx/collada/obj -> convert first
        if(convert) {
            FbxConv.FbxConvResult result = new FbxConv().input(origModel.path())
                    .output(tempModelCache.file().getAbsolutePath()).
                            flipTexture(true).execute();
            if(result.isSuccess()) {
                tempModelFile = Gdx.files.absolute(result.getOutputFile());
            }
        } else if(FileFormatUtils.isG3DB(origModel)) {  // g3db -> just copy
            origModel.copyTo(tempModelCache);
            tempModelFile = Gdx.files.absolute(FilenameUtils.concat(
                    tempModelCache.file().getAbsolutePath(), origModel.name()));
        }
        modelPath.setText(origModel.path());
    }

    @Override
    protected void close() {
        dispose();
        super.close();
    }

    private void showPreview() {
        if(fake3dViewport.getActor() != null) {
            fake3dViewport.removeActor(fake3dViewport.getActor());
        }

        previewInstance = new ModelInstance(previewModel);
        Ui.getInstance().wireModelToActor(fake3dViewport, previewInstance);
    }

    @Override
    public void dispose() {
        MundusHome.getInstance().purgeModelCache();
        Ui.getInstance().unwire(fake3dViewport);
        if(previewModel != null) {
            previewModel.dispose();
            previewModel = null;
        }
        tempModelCache = null;
        tempModelFile = null;
        tempTextureFile = null;
        modelPath.setText("");
        texturePath.setText("");
    }
}
