package com.bau5.cs328.sidescroller.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.bau5.cs328.sidescroller.GameStage;
import com.bau5.cs328.sidescroller.actors.PowerUpUserData;
import com.bau5.cs328.sidescroller.actors.Runner;

/**
 * Created by Rick on 2/22/2016.
 */
public class ContactHandler implements ContactListener {
    private Runner runner;
    private GameStage stage;

    public ContactHandler(Runner runner, GameStage stage) {
        this.runner = runner;
        this.stage = stage;
    }

    @Override
    public void beginContact(Contact contact) {
        ContactType contactType = BodyHelper.computeContactType(contact);
        if (contactType == null) {
            return;
        }
        if (contactType instanceof RunnerEnemyContact || contactType instanceof RunnerDangerContact) {
            if(runner.onHit(contactType.runner())) {
                stage.onRunnerHit();
            }
        } else if (runner.isJumping() && contactType instanceof RunnerStaticContact) {
            runner.landed();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        ContactType contactType = BodyHelper.computeContactType(contact);
        // After runner has been hit, let him fall through all objects
        if (contactType instanceof RunnerStaticContact && !runner.hit()) {
            Vector2 norm = oldManifold.getLocalNormal();
            if (norm.equals(new Vector2(1.0f, -0.0f)) && runner.isJumping()) {
                System.out.println("Hit? " + norm);
                contact.setEnabled(false);
                if (runner.onHit(contactType.other())) {
                    stage.onRunnerHit();
                }
            }
        } else if (contactType instanceof PowerUpContact && !runner.hit()) {
            PowerUpUserData powerUp = (PowerUpUserData) ((PowerUpContact) contactType).powerUp().getUserData();
            powerUp.typ().affect(runner);
            powerUp.markForRemoval();
            contact.setEnabled(false);
        } else if (runner.invincible() && BodyHelper.hasDangerousBody(contact)) {
            contact.setEnabled(false);
        } else if (runner.hit()) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
