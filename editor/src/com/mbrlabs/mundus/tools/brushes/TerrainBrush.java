package com.mbrlabs.mundus.tools.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.tools.Tool;

/**
 * A Terrain Brush can modify the terrain in various ways (BrushMode).
 *
 * This includes the height of every vertex in the terrain grid & according
 * splatmap.
 *
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public abstract class TerrainBrush extends Tool {

    /**
     * Defines the draw mode of a brush.
     */
    public enum BrushMode {
        /** Raises or lowers the terrain height. */
        RAISE_LOWER,
        /** Sets all vertices of the selection to a specified height. */
        FLATTEN,
        /** TBD */
        SMOOTH,
        /** Paints on the splatmap of the terrain. */
        PAINT
    }

    /**
     * Defines two actions (and it's key codes) every brush and every mode can have.
     *
     * For instance the RAISE_LOWER mode has 'raise' has PRIMARY action and 'lower' as secondary.
     * Pressing the keycode of the secondary & the primary key enables the secondary action.
     **/
    public static enum BrushAction {
        PRIMARY(Input.Buttons.LEFT),
        SECONDARY(Input.Keys.SHIFT_LEFT);

        public final int code;

        private BrushAction(int levelCode) {
            this.code = levelCode;
        }

    }

    /**
     * Thrown if a the brush is set to a mode, which it currently does not support.
     */
    public class ModeNotSupportedException extends Exception {
        public ModeNotSupportedException(String message) {
            super(message);
        }
    }

    protected BrushMode mode;
    protected Terrain terrain;
    protected float radius;
    protected Vector3 brushPos = new Vector3();

    protected SplatTexture.Channel paintChannel;
    protected float paintStrength = 0.5f;

    public TerrainBrush(ProjectContext projectContext, Shader shader, ModelBatch batch) {
        super(projectContext, shader, batch);
    }

    public BrushAction getAction() {
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);

        if(primary && secondary) {
            return BrushAction.SECONDARY;
        } else if(primary) {
            return BrushAction.PRIMARY;
        }

        return null;
    }


    public BrushMode getMode() {
        return mode;
    }

    public void setMode(BrushMode mode) throws ModeNotSupportedException {
        if(!supportsMode(mode)) {
            throw new ModeNotSupportedException(getName() + " does not support " + mode);
        }
        this.mode = mode;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public abstract void scale(float amount);

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(terrain != null) {
            Ray ray = projectContext.currScene.cam.getPickRay(screenX, screenY);
            terrain.getRayIntersection(brushPos, ray);
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    public void setPaintChannel(SplatTexture.Channel paintChannel) {
        this.paintChannel = paintChannel;
    }

    public void setPaintStrength(float paintStrength) {
        this.paintStrength = paintStrength;
    }

    public abstract boolean supportsMode(BrushMode mode);

}
