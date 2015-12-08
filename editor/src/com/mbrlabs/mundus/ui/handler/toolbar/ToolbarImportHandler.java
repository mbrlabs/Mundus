package com.mbrlabs.mundus.ui.handler.toolbar;

import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.utils.FbxConv;
import com.mbrlabs.mundus.ui.Ui;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ToolbarImportHandler extends ChangeListener {

    private FbxConv fbxConv;

    private G3dModelLoader g3dbLoader;

    public ToolbarImportHandler() {
        fbxConv = new FbxConv();
        g3dbLoader = new G3dModelLoader(new UBJsonReader());
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        final Ui ui = Ui.getInstance();
//        ui.getFileChooser().setListener(fileChooserAdapter);
//        ui.addActor(ui.getFileChooser().fadeIn());
        ui.showDialog(ui.getImportModelDialog());
    }

//    private class FCAdapterImportModel extends FileChooserAdapter {
//        @Override
//        public void selected(FileHandle file) {
//            Ui ui = Ui.getInstance();
//
//            String pathToFile = file.path();
//            String outputPath = FilenameUtils.getFullPath(pathToFile);
//
//            fbxConv.clear();
//            fbxConv.input(pathToFile).output(outputPath).flipTexture(true).outputFormat(FbxConv.OUTPUT_FORMAT_G3DB);
//            fbxConv.execute(result -> {
//                Log.debug("Import result: " + result.isSuccess());
//                Log.debug("Import log: " + result.getLog());
//                Model model = g3dbLoader.loadModel(Gdx.files.absolute(result.getOutputFile()));
//                Mundus.projectContext.models.add(model);
//                ui.getModelList().getItems().add(model);
//                Mundus.projectContext.entities.add(new ModelInstance(ui.getModelList().getItems().first()));
//                ui.getModelList().layout();
//            });
//
//        }
//    }

}
