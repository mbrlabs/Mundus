package com.mbrlabs.mundus.tools.brushes;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.tools.Tool;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public abstract class TerrainBrush extends Tool {

    /**
     *
     */
    public enum BrushMode {
        RAISE_LOWER, FLATTEN, SMOOTH, PAINT
    }

    /**
     *
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

    protected SplatTexture.Channel splatChannel;
    protected float splatStrenght = 0.2f;

    public TerrainBrush(ProjectContext projectContext, Shader shader, ModelBatch batch) {
        super(projectContext, shader, batch);
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

    public abstract boolean supportsMode(BrushMode mode);

    public void setSplatChannel(SplatTexture.Channel splatChannel) {
        this.splatChannel = splatChannel;
    }

    public void setSplatStrenght(float splatStrenght) {
        this.splatStrenght = splatStrenght;
    }
}
