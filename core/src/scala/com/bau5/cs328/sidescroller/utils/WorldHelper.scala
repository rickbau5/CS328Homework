package com.bau5.cs328.sidescroller.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, PolygonShape, World}
import com.bau5.cs328.sidescroller.actors.RunnerUserData

/**
  * Created by Rick on 2/9/16.
  */
object WorldHelper {
  def createWorld(): World = new World(Vals.gravity, true)

  /**
    * Creates the body for the ground object.
    *
    * @param forWorld World to attach the body to.
    * @return Body for the ground
    */
  def createGroundBody(forWorld: World): Body = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(new Vector2(Vals.groundX, Vals.groundY))
    val body = forWorld.createBody(bodyDef)
    val shape = new PolygonShape()
    shape.setAsBox(Vals.groundWidth / 2, Vals.groundHeight / 2)
    body.createFixture(shape, Vals.groundDensity)
    shape.dispose()
    body
  }

  /**
    * Creates the body for the runner object.
    *
    * @param forWorld World to Attach the body to.
    * @return Body for the runner
    */
  def createRunnerBody(forWorld: World): Body = {
    val bodyDef = new BodyDef()
    // That moment when Java people use Scala reserved keywords...
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    bodyDef.position.set(new Vector2(Vals.runnerX, Vals.runnerY + 1))
    val shape = new PolygonShape()
    shape.setAsBox(Vals.runnerWidth / 2, Vals.runnerHeight / 2)
    val body = forWorld.createBody(bodyDef)
    body.createFixture(shape, Vals.runnerDensity)
    body.resetMassData()
    body.setUserData(new RunnerUserData(Vals.runnerWidth, Vals.runnerHeight))
    body.setAngularDamping(0.0f)
    shape.dispose()
    body
  }

  def createSimpleBody(world: World, x: Float, y: Float, width: Float, height: Float): Body = {
    val bodyDef = new BodyDef
    bodyDef.position.set(new Vector2(x, y + Vals.groundTop - 1.5f))
    val shape = new PolygonShape()
    shape.setAsBox(width / 2, height / 2)
    val body = world.createBody(bodyDef)
    body.createFixture(shape, Vals.runnerDensity)
    body.resetMassData()
    shape.dispose()
    body
  }
}