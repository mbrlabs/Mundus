package com.mbrlabs.mundus.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 29-11-2015
 */
public class MyStage extends Stage {

    private PerspectiveCamera cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private Map<Actor, ModelInstance> models = new HashMap<Actor, ModelInstance>();
    private ModelBatch modelBatch = new ModelBatch();
    private Environment environment = new Environment();

    private Vector2 temp2 = new Vector2();
    private Vector3 temp3 = new Vector3();

    private BoundingBox boundingBox;

    public MyStage(Viewport viewport) {
        super(viewport);
        cam.position.set(0, 0, -2);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 20f;
        cam.update();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    public void wireModelToActor(Actor actor, ModelInstance model) {
        models.put(actor, model);
        boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        boundingBox.getMax(temp3);
        cam.project(temp3);

        float modelWidthOnScreen = Math.max(Math.max(temp3.x, temp3.z), temp3.y);
        float scaleFactor = (actor.getWidth() / modelWidthOnScreen) * 0.8f;

        model.transform.scl(scaleFactor, scaleFactor, scaleFactor);


        // calculate 4 points of actor in world coordinates
        temp2.x = 0;
        temp2.y = 0;
        Vector2 actorPos = actor.localToStageCoordinates(temp2);
        Vector3 actorLeftBottom = new Vector3(actorPos.x, actorPos.y, 0);
        Vector3 actorRightBottom = new Vector3(actorPos.x + actor.getWidth(), actorPos.y, 0);
        Vector3 actorLeftTop = new Vector3(actorPos.x, actorPos.y + actor.getHeight(), 0);
        Vector3 actorRightTop = new Vector3(actorPos.x + actor.getWidth(), actorPos.y + actor.getHeight(), 0);

        cam.unproject(actorLeftBottom);
        cam.unproject(actorRightBottom);
        cam.unproject(actorLeftTop);
        cam.unproject(actorRightTop);

        float maxWidth = Math.abs(actorRightBottom.x) - Math.abs(actorLeftBottom.x);

        float ratio = (maxWidth*4f) / Math.max(Math.max(boundingBox.getWidth(), boundingBox.getHeight()), boundingBox.getDepth());
        model.transform.scl(ratio);
        model.transform.setTranslation(-actorLeftBottom.x - maxWidth, 0,0);


    }

    public void unwire(Actor actor) {
        models.remove(actor);
    }

    @Override
    public void draw() {
        super.draw();
        cam.update();

        modelBatch.begin(cam);
        for(Actor actor : models.keySet()) {
            ModelInstance model = models.get(actor);

            model.transform.rotate(0, 1, 0, 1);
            modelBatch.render(model, environment);

        }
        modelBatch.end();

    }


    @Override
    public void dispose() {
        super.dispose();
        modelBatch.dispose();
    }
}
