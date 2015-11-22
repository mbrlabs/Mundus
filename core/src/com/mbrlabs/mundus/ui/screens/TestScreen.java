package com.mbrlabs.mundus.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.Colors;
import com.mbrlabs.mundus.ui.components.menu.MundusMenuBar;
import com.mbrlabs.mundus.utils.GlUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TestScreen extends BaseScreen {

    private Stage stage;
    private VisTable root;

    private MundusMenuBar menuBar;

    public TestScreen() {
        setupStage();

        // Menu
        menuBar = new MundusMenuBar();
        root.add(menuBar.getTable()).fillX().expandX().row();




        Gdx.input.setInputProcessor(this.stage);

    }

    private void setupStage() {
        this.stage = new Stage(new ScreenViewport());
        root = new VisTable();
        root.setWidth(stage.getWidth());
        root.align(Align.center | Align.top);
        root.setPosition(0, Gdx.graphics.getHeight());
        stage.addActor(this.root);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        GlUtils.clearScreen(Colors.GRAY_222);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
