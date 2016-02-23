package com.bau5.cs328.sidescroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.bau5.cs328.sidescroller.actors.*;
import com.bau5.cs328.sidescroller.actors.environment.Background;
import com.bau5.cs328.sidescroller.actors.environment.Grass;
import com.bau5.cs328.sidescroller.screens.ButtonWithListener;
import com.bau5.cs328.sidescroller.screens.StageWithButtons;
import com.bau5.cs328.sidescroller.utils.*;


/**
 * Created by Rick on 2/9/16.
 */
public class GameStage extends Stage {
    private Main main;

    private World world;
    private static Runner runner;

    private Boolean debug = Vals.debug();

    private TouchType touchType = TouchType.None;

    private Vector3 touchPoint;
    private Rectangle screenRight;
    private Rectangle screenLeft;

    private final float step = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage(Main main) {
        super(new ScalingViewport(Scaling.stretch, Vals.screenWidth(), Vals.screenHeight(),
                    new OrthographicCamera(Vals.screenWidth(), Vals.screenHeight())));
        this.main = main;
        setupWorld();
        setupDebugRenderer();
        setupControlAreas();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Array<Body> bodies = new Array<>(world.getBodyCount());
        world.getBodies(bodies);
        for (Body body : bodies) {
            updateBody(body);
        }

        Array<Actor> actors = getActors();
        for (Actor actor : actors) {
            if (!actor.isVisible() && actor instanceof GameActor && BodyHelper.bodyOnScreen(((GameActor)actor).body())) {
                actor.setVisible(true);
                runner.toFront();
            } else if (actor instanceof StarActor) {
                if (actor.isVisible() && !runner.invincible()) {
                    actor.setVisible(false);
                } else if (!actor.isVisible() && runner.invincible()) {
                    actor.setVisible(true);
                }
            }
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
        if (BodyHelper.bodyLeftBounds(body) || BodyHelper.bodyShouldBeDestroyed(body)) {
            if (body.getUserData() instanceof RunnerUserData) {
                onRunnerHit();
            }
            world.destroyBody(body);
        }
    }

    public void onRunnerHit() {
        Table table = new Table();
        table.setFillParent(true);
        addActor(table);
        table.add(ButtonWithListener.mainMenuButton(main));
    }

    private void setupWorld() {
        world = WorldHelper.createWorld();
        runner = new Runner(WorldHelper.createRunnerBody(world));
        addActor(new Background());
        addActor(new Grass(true));
        addActor(runner);
        addActor(new StarActor());
        world.setContactListener(new ContactHandler(runner, this));

        Mapper.loadActors(world, this);
    }

    private void setupDebugRenderer() {
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Vals.debugWidth(), Vals.debugHeight());
        camera.position.set(camera.viewportWidth / 2, camera. viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupControlAreas() {
        touchPoint = new Vector3();
        screenRight = new Rectangle(getCamera().viewportWidth / 2, 0, getCamera().viewportWidth / 2, getCamera().viewportHeight);
        screenLeft = new Rectangle(0, 0,  getCamera().viewportWidth / 2, getCamera().viewportHeight);
        Gdx.input.setInputProcessor(this);
    }

    public static Runner getRunner() {
        return runner;
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
        if (debug) {
            renderer.render(world, camera.combined);
        }
        if (runner.hit()) {
            this.getBatch().begin();
            StageWithButtons.font().draw(this.getBatch(), new StringBuilder("Game over!").subSequence(0, 10),
                    Vals.screenWidth() / 2 - 80, (int)(Vals.screenHeight() * 0.75));
            this.getBatch().end();
        }
    }
}

enum TouchType {
    Jump, Dodge, None
}