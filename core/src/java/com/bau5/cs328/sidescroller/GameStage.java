package com.bau5.cs328.sidescroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;


/**
 * Created by Rick on 2/9/16.
 */
public class GameStage extends Stage implements ContactListener {
    private World world;
    private Ground ground;
    private Runner runner;

    private Vector3 touchPoint;
    private Rectangle screenRight;

    private final float step = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage() {
        renderer = new Box2DDebugRenderer();
        setupWorld();
        setupCamera();
        setupControlAreas();
    }

    private void setupWorld() {
        world = WorldUtils.createWorld();
        ground = new Ground(WorldUtils.createGround(world));
        runner = new Runner(WorldUtils.createRunner(world));
        addActor(ground);
        addActor(runner);
        world.setContactListener(this);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Vals.viewportWidth(), Vals.viewportHeight());
        camera.position.set(camera.viewportWidth / 2, camera. viewportHeight / 2, 0f);
        camera .update();
    }

    private void setupControlAreas() {
        touchPoint = new Vector3();
        screenRight = new Rectangle(getCamera().viewportWidth / 2, 0, getCamera().viewportWidth / 2, getCamera().viewportHeight);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        getCamera().unproject(touchPoint.set(screenX, screenY, 0));
        if (screenRight.contains(touchPoint.x, touchPoint.y)) {
            runner.jump();
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        accumulator += delta;
        while (accumulator >= delta) {
            world.step(step, 6, 2);
            accumulator -= step;
        }
    }

    @Override
    public void draw() {
        super.draw();
        renderer.render(world, camera.combined);
    }

    @Override
    public void beginContact(Contact contact) {
        if (runner.jumping() && ContactHandler.checkRunnerAndGroundContact(contact)) {
            runner.landed();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
