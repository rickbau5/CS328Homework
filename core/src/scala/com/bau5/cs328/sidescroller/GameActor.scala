package com.bau5.cs328.sidescroller

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Batch}
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor


/**
  * Base class for the Actors that will be used in the game. Contains a convenience
  * method to access a correctly typed instance of UserData. UserData must be passed as
  * part of the constructor, or the underlying body must have a valid UserData type
  * already set.
  *
  * @param body The body used to construct the Actor.
  * @param data Option containing the UserData, can be empty. If empty, will use the
  *             UserData that is assigned to the body. Fails if neither are set.
  * @tparam T The type of UserData, must be a descendent of custom UserData type. Allows
  *           for typed access of UserData for each Actor type
  *
  * Created by Rick on 2/10/16.2
  *
  */
sealed abstract class GameActor[T <: UserData](val body: Body, data: Option[T]) extends Actor {
  if (data.isDefined) {
    body.setUserData(data.get)
  }
  val screenRectangle = new Rectangle
  def userData: Option[T] = Option(body.getUserData.asInstanceOf[T])
  def ->(func: (T) => Unit): Boolean = {
    if (userData.isDefined) {
      userData.foreach(func)
      true
    } else {
      false
    }
  }
  override def act(delta: Float): Unit = {
    super.act(delta)
    if(userData.isDefined) {
      -> { d =>
        val posX = body.getPosition.x
        val posY = body.getPosition.y
        screenRectangle.x = transform(posX - d.width / 2)
        screenRectangle.y = transform(posY - d.height / 2)
        screenRectangle.width = transform(d.width)
        screenRectangle.height = transform(d.height)
      }
    } else {
      println("Removed " + this)
      remove()
    }
  }

  def transform(num: Float) = Vals.ratio * num
}

class Runner(body: Body) extends GameActor(body, Option.empty[RunnerUserData]) {
  private val textureRegion = new TextureRegion(new Texture("first.png"))
  private var jumping = false
  private var dodging = false
  var hit = false
  private var collider = Option.empty[Body]

  def jump(): Unit = !(jumping || dodging || hit) match {
    case true =>
      -> (data => body.applyLinearImpulse(data.jumpImpulse, body.getWorldCenter, true))
      jumping = true
    case false => ;
  }

  def dodge(): Unit = !(jumping || hit) match {
    case true =>
      -> (data => body.setTransform(userData.get.dodgingPosition, userData.get.dodgeAngle))
      dodging = true
    case false => ;
  }

  def stopDodge(): Unit = {
    if (!hit) {
      -> (data => body.setTransform(data.runningPosition, 0f))
    }
    dodging = false
  }

  def isDodging: Boolean = dodging
  def isJumping: Boolean = jumping

  def onHit(body: Body): Unit = {
    collider = Option(body)
    -> (data => body.applyAngularImpulse(data.hitImpulse, true))
    hit = true
  }

  def setLocation(screenCoord: Vector2): Unit = {
    -> (data => body.setTransform(new Vector2(screenCoord.x / Vals.ratio, screenCoord.y / Vals.ratio), 0))
  }

  def getCollider: Option[Body] = collider

  def landed(): Unit = jumping = false

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    screenRectangle.y = screenRectangle.y * 2
    println("Drawing player at %f %f", screenRectangle.x, screenRectangle.y)
    batch.draw(textureRegion, screenRectangle.x, screenRectangle.y, screenRectangle.width, screenRectangle.height)
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
  }
}

class Enemy(body: Body) extends GameActor(body, Option.empty[EnemyUserData]) {
  private val textureRegion = new TextureRegion(new Texture("first.png"))
  override def act(delta: Float): Unit = {
    super.act(delta)
    -> (data => body.setLinearVelocity(data.linearVelocity))
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    val x = screenRectangle.x - (screenRectangle.width * 0.1f)
    val y = screenRectangle.y
    val width = screenRectangle.width * 1.2f

    println("Drawing enemy at %f %f", x, y)
    batch.draw(textureRegion, x, y, width, screenRectangle.height)
  }
}

class Ground(body: Body) extends GameActor(body, Option(new GroundUserData(Vals.groundWidth, Vals.groundHeight)))

sealed abstract class UserData(w: Float, h: Float) {
  def width: Float = w
  def height: Float = h
}
case class GroundUserData(w: Float, h: Float) extends UserData(w, h)
case class RunnerUserData(w: Float, h: Float) extends UserData(w, h) {
  val runningPosition = new Vector2(Vals.runnerX, Vals.runnerY)
  val dodgingPosition = new Vector2(Vals.runnerDodgeX, Vals.runnerDodgeY)
  val dodgeAngle = (-90 * (Math.PI / 180f)).toFloat
  val hitImpulse = Vals.runnerHitImpulse
  val jumpImpulse = Vals.runnerJumpImpulse
}
case class EnemyUserData(w: Float, h: Float) extends UserData(w, h) {
  val linearVelocity = Vals.enemyLinearVelocity
}