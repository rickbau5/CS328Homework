package com.bau5.cs328.sidescroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;


/**
 * Created by Rick on 2/9/16.
 */
public class GameStage extends Stage implements ContactListener {
    private World world;
    private Ground ground;
    private Runner runner;

    // ;)
    private TouchType touchType = TouchType.None;

    private Vector3 touchPoint;
    private Rectangle screenRight;
    private Rectangle screenLeft;

    private final float step = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage() {
        super(new ScalingViewport(Scaling.stretch, Vals.screenWidth(), Vals.screenHeight(),
                    new OrthographicCamera(Vals.screenWidth(), Vals.screenHeight())));
        renderer = new Box2DDebugRenderer();
        setupWorld();
        setupCamera();
        setupControlAreas();
        createEnemy();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Array<Body> bodies = new Array<>(world.getBodyCount());
        world.getBodies(bodies);
        for (Body body : bodies) {
            updateBody(body);
        }

        accumulator += delta;
        while (accumulator >= delta) {
            world.step(step, 6, 2);
            accumulator -= step;
        }

        if (touchType != TouchType.None && !runner.isDodging() && !runner.isJumping()) {
            switch (touchType) {
                case Jump: runner.jump();
                case Dodge: runner.dodge();
            }
        }
    }

    private void updateBody(Body body) {
        if (!(body.getUserData() instanceof GroundUserData) && !BodyHelper.bodyOnScreen(body)) {
            // TODO: GameScreen transition
            if (BodyHelper.isRunner(body) && runner.hit()) {
                Gdx.app.exit();
            }
            if (BodyHelper.isEnemy(body) && !runner.hit()) {
                createEnemy();
            }
            world.destroyBody(body);
        }
    }

    private void createEnemy() {
        Enemy enemy = new Enemy(WorldUtils.createEnemy(world));
        addActor(enemy);
    }

    private void setupWorld() {
        world = WorldUtils.createWorld();
        ground = new Ground(WorldUtils.createGround(world));
        runner = new Runner(WorldUtils.createRunner(world));
        addActor(ground);
        addActor(runner);
        addActor(new Background());
        addActor(new Grass());
        world.setContactListener(this);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Vals.viewportWidth(), Vals.viewportHeight());
        camera.position.set(camera.viewportWidth / 2, camera. viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupControlAreas() {
        touchPoint = new Vector3();
        screenRight = new Rectangle(getCamera().viewportWidth / 2, 0, getCamera().viewportWidth / 2, getCamera().viewportHeight);
        screenLeft = new Rectangle(0, 0,  getCamera().viewportWidth / 2, getCamera().viewportHeight);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        getCamera().unproject(touchPoint.set(screenX, screenY, 0));
        if (screenRight.contains(touchPoint.x, touchPoint.y)) {
            runner.jump();
            touchType = TouchType.Jump;
        } else if (!runner.isDodging() && screenLeft.contains(touchPoint.x, touchPoint.y)) {
            runner.dodge();
            touchType = TouchType.Dodge;
        }
        System.out.println("Touch Down");

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (runner.isDodging()) {
            runner.stopDodge();
        }
        touchType = TouchType.None;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.SPACE) {
            runner.jump();
            touchType = TouchType.Jump;
            return true;
        } else if (keyCode == Input.Keys.D && !runner.isDodging()){
            touchType = TouchType.Dodge;
            runner.dodge();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode == Input.Keys.D && runner.isDodging()) {
            runner.stopDodge();
            touchType = TouchType.None;
            return true;
        } else if (keyCode == Input.Keys.SPACE && runner.isJumping()) {
            touchType = TouchType.None;
            return true;
        }
        return super.keyUp(keyCode);
    }

    @Override
    public void draw() {
        super.draw();
        renderer.render(world, camera.combined);
    }

    @Override
    public void beginContact(Contact contact) {
        ContactType contactType = BodyHelper.computeContactType(contact);
        if (contactType instanceof RunnerEnemyContact) {
            runner.onHit(contactType.runner());
        } else if (runner.isJumping() && BodyHelper.computeContactType(contact) instanceof RunnerGroundContact) {
            runner.landed();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // After runner has been hit, let him fall through all objects
        if (runner.hit() && !BodyHelper.continueContact(contact, runner)) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}

enum TouchType {
    Jump, Dodge, None
}