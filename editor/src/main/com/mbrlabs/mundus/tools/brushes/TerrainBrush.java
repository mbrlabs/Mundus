package com.mbrlabs.mundus.tools.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.terrain.SplatMap;
import com.mbrlabs.mundus.terrain.SplatTexture;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.events.GlobalBrushSettingsChangedEvent;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.TerrainHeightCommand;
import com.mbrlabs.mundus.history.commands.TerrainPaintCommand;
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

    // used for calculations
    protected static final Vector2 c = new Vector2();
    protected static final Vector2 p = new Vector2();
    protected static final Vector2 v = new Vector2();
    protected static final Color c0 = new Color();
    protected static final Vector3 tVec0 = new Vector3();
    protected static final Vector3 tVec1 = new Vector3();

    // all brushes share the some common settings
    private static GlobalBrushSettingsChangedEvent brushSettingsChangedEvent = new GlobalBrushSettingsChangedEvent();
    private static float strength = 0.5f;
    private static float heightSample = 0f;
    private static SplatTexture.Channel paintChannel;

    // individual brush settings
    protected final Vector3 brushPos = new Vector3();
    protected float radius;
    protected BrushMode mode;
    protected Terrain terrain;
    private BrushAction action;

    // used for brush rendering
    private Model sphereModel;
    private ModelInstance sphereModelInstance;
    private final BoundingBox boundingBox = new BoundingBox();
    private int lastMousePosIndicator = 0;

    // the pixmap brush
    private Pixmap brushPixmap;
    private int pixmapCenter;

    // undo/redo system
    private TerrainHeightCommand heightCommand = null;
    private TerrainPaintCommand paintCommand = null;
    private boolean terrainHeightModified = false;
    private boolean splatmapModified = false;

    public TerrainBrush(ProjectContext projectContext, Shader shader, ModelBatch batch, CommandHistory history, FileHandle pixmapBrush) {
        super(projectContext, shader, batch, history);

        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = modelBuilder.createSphere(1, 1, 1, 30, 30, new Material(), VertexAttributes.Usage.Position);
        sphereModelInstance = new ModelInstance(sphereModel);
        sphereModelInstance.calculateBoundingBox(boundingBox);
        scale(15);

        brushPixmap = new Pixmap(pixmapBrush);
        pixmapCenter = brushPixmap.getWidth() / 2;
    }

    @Override
    public void act() {
        if(action == null) return;
        if(terrain == null) return;

        // sample height
        if(action == BrushAction.SECONDARY && mode == BrushMode.FLATTEN) {
            heightSample = brushPos.y;
            return;
        }

        // only act if mouse has been moved
        if(lastMousePosIndicator == Gdx.input.getX() + Gdx.input.getY()) return;

        if(mode == BrushMode.PAINT) {
            paint();
        } else if(mode == BrushMode.RAISE_LOWER) {
            raiseLower(action);
        } else if(mode == BrushMode.FLATTEN) {
            flatten();
        }
    }

    private void paint() {
        SplatMap sm = terrain.getTerrainTexture().getSplatmap();
        if(sm == null) return;

        Vector3 terrainPos = terrain.getPosition(tVec1);
        final float splatX = ((brushPos.x - terrainPos.x) / (float) terrain.terrainWidth) * sm.getWidth();
        final float splatY = ((brushPos.z - terrainPos.z) / (float) terrain.terrainDepth) * sm.getHeight();
        final float splatRad = (radius / terrain.terrainWidth) * sm.getWidth();
        final Pixmap pixmap = sm.getPixmap();

        for(int smX = 0; smX < pixmap.getWidth(); smX++) {
            for(int smY = 0; smY < pixmap.getHeight(); smY++) {
                final float dst = MathUtils.dst(splatX, splatY, smX, smY);
                if(dst <= splatRad) {
                    final float opacity = getValueOfBrushPixmap(splatX, splatY, smX, smY, splatRad) * 0.5f * strength;
                    int newPixelColor = sm.additiveBlend(pixmap.getPixel(smX, smY), paintChannel, opacity);
                    pixmap.drawPixel(smX, smY, newPixelColor);
                }
            }
        }

        sm.updateTexture();
        splatmapModified = true;
    }

    private void flatten() {
        final Vector3 terPos = terrain.getPosition(tVec1);
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z <  terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                vertexPos.x += terPos.x;
                vertexPos.z += terPos.z;
                float distance = vertexPos.dst(brushPos);

                if(distance <= radius) {
                    final int index = z * terrain.vertexResolution + x;
                    final float diff = Math.abs(terrain.heightData[index] - heightSample);
                    if(diff <= 1f) {
                        terrain.heightData[index] = heightSample;
                    } else if(diff > 1f){
                        final float elevation = getValueOfBrushPixmap(brushPos.x, brushPos.z, vertexPos.x, vertexPos.z, radius);
                        final float newHeight = heightSample * elevation;
                        if(Math.abs(heightSample - newHeight) < Math.abs(heightSample - terrain.heightData[index])) {
                            terrain.heightData[index] = newHeight;
                        }
                    }
                }
            }
        }

        terrain.update();
        terrainHeightModified = true;
    }

    private void raiseLower(BrushAction action) {
        final Vector3 terPos = terrain.getPosition(tVec1);
        float dir = (action == BrushAction.PRIMARY) ? 1 : -1;
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z <  terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                vertexPos.x += terPos.x;
                vertexPos.z += terPos.z;
                float distance = vertexPos.dst(brushPos);

                if(distance <= radius) {
                    float elevation = getValueOfBrushPixmap(brushPos.x, brushPos.z, vertexPos.x, vertexPos.z, radius);
                    terrain.heightData[z * terrain.vertexResolution + x] += dir * elevation * strength;
                }
            }
        }

        terrain.update();
        terrainHeightModified = true;
    }

    /**
     * Interpolates the brush texture in the range of centerX - radius to centerX + radius
     * and centerZ - radius to centerZ + radius. PointZ & pointX lies between these ranges.
     *
     * Interpolation is necessary, since the brush pixmap is fixed sized, whereas the input values can scale.
     * (Input points can be vertices or splatmap texture coordinates)
     *
     * @param centerX
     * @param centerZ
     * @param pointX
     * @param pointZ
     * @param radius
     *
     * @return      the interpolated r-channel value of brush pixmap at pointX, pointZ,
     *              which can be interpreted as terrain height (raise/lower) or opacity (paint)
     */
    private float getValueOfBrushPixmap(float centerX, float centerZ, float pointX, float pointZ, float radius) {
        c.set(centerX, centerZ);
        p.set(pointX, pointZ);
        v.set(p.sub(c));

        final float progress = v.len() / radius;
        v.nor().scl(pixmapCenter * progress);

        final float mapX = pixmapCenter + (int) v.x;
        final float mapY = pixmapCenter + (int) v.y;
        c0.set(brushPixmap.getPixel((int) mapX, (int) mapY));

        return c0.r;
    }

    public void scale(float amount) {
        sphereModelInstance.transform.scl(amount);
        radius = (boundingBox.getWidth()*sphereModelInstance.transform.getScaleX()) / 2f;
    }

    public static float getStrength() {
        return strength;
    }

    public static void setStrength(float strength) {
        TerrainBrush.strength = strength;
        Mundus.postEvent(brushSettingsChangedEvent);
    }

    public static float getHeightSample() {
        return heightSample;
    }

    public static void setHeightSample(float heightSample) {
        TerrainBrush.heightSample = heightSample;
        Mundus.postEvent(brushSettingsChangedEvent);
    }

    public static SplatTexture.Channel getPaintChannel() {
        return paintChannel;
    }

    public static void setPaintChannel(SplatTexture.Channel paintChannel) {
        TerrainBrush.paintChannel = paintChannel;
        Mundus.postEvent(brushSettingsChangedEvent);
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

    public boolean supportsMode(BrushMode mode) {
        switch (mode) {
            case RAISE_LOWER:
            case FLATTEN:
            case PAINT: return true;
        }

        return false;
    }

    @Override
    public void render() {
        if(terrain.isOnTerrain(brushPos.x, brushPos.z)) {
            batch.begin(projectContext.currScene.cam);
            batch.render(sphereModelInstance, shader);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        brushPixmap.dispose();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(terrainHeightModified && heightCommand != null) {
            heightCommand.setHeightDataAfter(terrain.heightData);
            history.add(heightCommand);
        }
        if(splatmapModified && paintCommand != null) {
            final SplatMap sm = terrain.getTerrainTexture().getSplatmap();
            paintCommand.setAfter(sm.getPixmap());
            history.add(paintCommand);
        }
        splatmapModified = false;
        terrainHeightModified = false;
        heightCommand = null;
        paintCommand = null;

        action = null;

        return false;
    }

    private BrushAction getAction() {
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);

        if(primary && secondary) {
            return BrushAction.SECONDARY;
        } else if(primary) {
            return BrushAction.PRIMARY;
        }

        return null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // get action
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);
        if(primary && secondary) {
            action =  BrushAction.SECONDARY;
        } else if(primary) {
            action = BrushAction.PRIMARY;
        } else {
            action = null;
        }

        if(mode == BrushMode.FLATTEN || mode == BrushMode.RAISE_LOWER || mode == BrushMode.SMOOTH) {
            heightCommand = new TerrainHeightCommand(terrain);
            heightCommand.setHeightDataBefore(terrain.heightData);
        } else if(mode == BrushMode.PAINT) {
            final SplatMap sm = terrain.getTerrainTexture().getSplatmap();
            if(sm != null) {
                paintCommand = new TerrainPaintCommand(terrain);
                paintCommand.setBefore(sm.getPixmap());
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(terrain != null) {
            Ray ray = projectContext.currScene.viewport.getPickRay(screenX, screenY);
            terrain.getRayIntersection(brushPos, ray);
        }

        lastMousePosIndicator = screenX + screenY;
        sphereModelInstance.transform.setTranslation(brushPos);

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount < 0) {
            scale(0.9f);
        } else {
            scale(1.1f);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

}
