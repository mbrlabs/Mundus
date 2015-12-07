package com.mbrlabs.mundus.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public class InputManager extends InputMultiplexer {

    private Stage uiInput;
    private UpdatableInputProcessor worldNavigation;
    private UpdatableInputProcessor currentTool;

    public InputManager(Stage uiInput) {
        this.uiInput = uiInput;

        addProcessor(this.uiInput);
        Gdx.input.setInputProcessor(this);
    }

    public void setCurrentToolInput(UpdatableInputProcessor inputProcessor) {
        currentTool = inputProcessor;
        if(currentTool != null) {
            removeProcessor(currentTool);
        }
        addProcessor(currentTool);
    }

    public void setWorldNavigation(UpdatableInputProcessor inputProcessor) {
        if(worldNavigation != null) {
            removeProcessor(worldNavigation);
        }
        this.worldNavigation = inputProcessor;
        addProcessor(worldNavigation);
    }

    public void update() {
        worldNavigation.update();
        currentTool.update();
        uiInput.act();
    }

}
