package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor
import com.bau5.cs328.sidescroller.Vals


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
abstract class GameActor[T <: UserData](val body: Body, data: Option[T]) extends Actor {
  if (data.isDefined) {
    body.setUserData(data.get)
  }
  val screenRectangle = new Rectangle

  def userData: Option[T] = Option(body.getUserData.asInstanceOf[T])

  def -->(func: (T) => Unit): Unit = userData.foreach(func)

  override def act(delta: Float): Unit = {
    super.act(delta)
    if(userData.isDefined) {
      --> { d =>
        val posX = body.getPosition.x * 1.02f - d.width / 2
        val posY = body.getPosition.y * .98f - d.height / 2
        screenRectangle.x = transform(posX)
        screenRectangle.y = transform(posY)
        screenRectangle.width = transform(d.width * 1.05f)
        screenRectangle.height = transform(d.height * .95f)
      }
    } else {
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
  private var invincibilityTimer = 0

  def jump(): Unit = !(jumping || dodging || hit) match {
    case true =>
      --> { data =>
        val diff = data.jumpImpulse.y - body.getLinearVelocity.y
        if (diff > 0) {
          body.applyLinearImpulse(new Vector2(0, diff), body.getWorldCenter, true)
        }
      }
      jumping = true
    case false => ;
  }

  def dodge(): Unit = !(jumping || hit) match {
    case true =>
      body.setAngularDamping(1.0f)
      --> (data => body.setTransform(data.dodgeTransform(body.getPosition.sub(0.0f, data.w / 2)), data.dodgeAngle))
      dodging = true
    case false => ;
  }

  def stopDodge(): Unit = {
    if (!hit) {
      --> (data => body.setTransform(data.runningTransform(body.getPosition), 0f))
    }
    dodging = false
  }

  def isDodging: Boolean = dodging
  def isJumping: Boolean = jumping

  def onHit(body: Body): Unit = invincible() || hit match {
    case false =>
      collider = Option(body)
      // Re-enable angular velocity for the body
      --> (data => body.applyAngularImpulse(data.hitImpulse, true))
      hit = true
    case _ => ;
  }

  def setLocation(screenCoord: Vector2): Unit = {
    --> (data => body.setTransform(new Vector2(screenCoord.x / Vals.ratio, screenCoord.y / Vals.ratio), 0))
  }

  def getCollider: Option[Body] = collider

  def landed(): Unit = jumping = false

  def setInvincible(): Unit = {
    invincibilityTimer = 200
  }

  def invincible(): Boolean = invincibilityTimer > 0

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    if (!(hit || dodging) && body.getAngle != 0) {
      body.setTransform(body.getPosition, 0)
    }
    val angle = (body.getAngle / (Math.PI / 180f)).toFloat
    batch.draw(textureRegion,
      screenRectangle.x, screenRectangle.y,
      screenRectangle.width / 2, screenRectangle.height / 2,
      screenRectangle.width, screenRectangle.height,
      1, 1.1f,
      angle
    )
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    //cancel x velocity, want the runner to stay in position.
    body.setLinearVelocity(new Vector2(0, body.getLinearVelocity.y))
    if (invincibilityTimer > 0) {
      invincibilityTimer -= 1
      if (invincibilityTimer == 0) println("No longer invincible. " + invincible())
    }
    if (body.getPosition.x != Vals.runnerX) {
      body.setTransform(new Vector2(Vals.runnerX, body.getPosition.y), body.getAngle)
    }
  }
}

class Enemy(body: Body) extends GameActor(body, Option.empty[EnemyUserData]) {
  private val textureRegion = new TextureRegion(new Texture("first.png"))
  override def act(delta: Float): Unit = {
    --> (data => body.setLinearVelocity(data.linearVelocity))
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    val x = screenRectangle.x - (screenRectangle.width * 0.1f)
    val y = screenRectangle.y + screenRectangle.width / 2
    val width = screenRectangle.width * 1.2f

    println("Drawing enemy at %f %f", x, y)
    batch.draw(textureRegion, x, y, width, screenRectangle.height)
  }
}

class Ground(body: Body) extends GameActor(body, Option(new GroundUserData(Vals.groundWidth, Vals.groundHeight)))
