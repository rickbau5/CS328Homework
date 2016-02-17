package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{PolygonShape, BodyDef, Body, World}

import scala.util.Random


/**
  * Created by Rick on 2/9/16.
  */
object WorldUtils {

  def createWorld(): World = new World(Vals.gravity, true)

  /**
    * Creates the body for the ground object.
    *
    * @param forWorld World to attach the body to.
    * @return Body for the ground
    */
  def createGround(forWorld: World): Body = {
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
  def createRunner(forWorld: World): Body = {
    val bodyDef = new BodyDef()
    // That moment when Java people use Scala reserved keywords...
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    bodyDef.position.set(Vals.runnerPosition)
    val body = forWorld.createBody(bodyDef)
    val shape = new PolygonShape()
    shape.setAsBox(Vals.runnerWidth / 2, Vals.runnerHeight / 2)
    body.createFixture(shape, Vals.runnerDensity)
    body.resetMassData()
    shape.dispose()
    body
  }

  /**
    * Creates an enemy of random type (size).
    *
    * @param forWorld World to attach the body to.
    * @return Body for the new enemy
    */
  def createEnemy(forWorld: World): Body = {
    val enemy = EnemyType.random()
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyDef.BodyType.KinematicBody
    bodyDef.position.set(new Vector2(enemy.x, enemy.y))
    val shape = new PolygonShape()
    shape.setAsBox(enemy.width / 2, enemy.height / 2)
    val body = forWorld.createBody(bodyDef)
    body.createFixture(shape, enemy.density)
    body.setUserData(new EnemyUserData(enemy.width, enemy.height))
    body.resetMassData()
    shape.dispose()
    body
  }
}

/**
  * Enemy types follow below.
  */
object EnemyType {
  val rand = Random
  val all = List(SmallEnemy, WideEnemy, LongEnemy, BigEnemy, SmallFlyingEnemy, WideFlyingEnemy)

//  def random(): EnemyType = all(rand.nextInt(EnemyType.all.length))
  def random(): EnemyType = SmallFlyingEnemy
}
sealed abstract class EnemyType(val width: Float, val height: Float, val x: Float, val y: Float, val density: Float)

object SmallEnemy extends EnemyType(1f, 1f, Vals.enemyX, Vals.enemyShortY, Vals.enemyDensity)
object WideEnemy extends EnemyType(2f, 1f, Vals.enemyX, Vals.enemyShortY, Vals.enemyDensity)
object LongEnemy extends EnemyType(1f, 2f, Vals.enemyX, Vals.enemyLongY, Vals.enemyDensity)
object BigEnemy extends EnemyType(2f, 2f, Vals.enemyX, Vals.enemyLongY, Vals.enemyDensity)
object SmallFlyingEnemy extends EnemyType(1f, 1f, Vals.enemyX, Vals.enemyFlyingY, Vals.enemyDensity)
object WideFlyingEnemy extends EnemyType(2f, 1f, Vals.enemyX, Vals.enemyFlyingY, Vals.enemyDensity)
