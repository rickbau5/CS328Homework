package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.bau5.cs328.sidescroller.Vals

/**
  * Created by bau5 on 2/22/2016.
  */
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
    if (!(hit || dodging) && body.getAngle != 0) {
      body.setTransform(body.getPosition, 0)
    }
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

