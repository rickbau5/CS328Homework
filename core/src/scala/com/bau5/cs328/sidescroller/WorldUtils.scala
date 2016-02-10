package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{PolygonShape, BodyDef, Body, World}


/**
  * Created by Rick on 2/9/16.
  */
object WorldUtils {
  def createWorld(): World = new World(Vals.gravity, true)

  def createGround(forWorld: World): Body = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(new Vector2(Vals.groundX, Vals.groundY))
    val body = forWorld.createBody(bodyDef)
    val shape = new PolygonShape()
    shape.setAsBox(Vals.groundWidth / 2, Vals.groundHeight)
    body.createFixture(shape, Vals.groundDensity)
    shape.dispose()
    body
  }

  def createRunner(forWorld: World): Body = {
    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    bodyDef.position.set(new Vector2(Vals.runnerX, Vals.runnerY))
    val body = forWorld.createBody(bodyDef)
    val shape = new PolygonShape()
    shape.setAsBox(Vals.runnerWidth / 2, Vals.runnerHeight / 2)
    body.createFixture(shape, Vals.runnerDensity)
    body.resetMassData()
    shape.dispose()
    body
  }
}
