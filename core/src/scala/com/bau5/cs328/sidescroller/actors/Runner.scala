package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion, TextureAtlas, Batch}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.bau5.cs328.sidescroller.GameStage
import com.bau5.cs328.sidescroller.utils.Vals

/**
  * Created by Rick on 2/22/2016.
  */
class Runner(body: Body) extends GameActor(body, Option.empty[RunnerUserData]) {
  private val atlas = new TextureAtlas("textures/player.pack")
  private val dodgingTexture = atlas.findRegion("dodge")
  private val hitTexture = atlas.findRegion("hurt")
  private val standingTexture = atlas.findRegion("standing")
  private val jumpingTexture = atlas.findRegion("jumping")
  private val running = {
    val frames = new com.badlogic.gdx.utils.Array[TextureRegion]
    for (i <- 1 to 3) {
      frames.add(atlas.findRegion(s"walking$i"))
    }
    new Animation(0.1f, frames)
  }

  private var jumping = false
  private var dodging = false
  var hit = false
  private var collider = Option.empty[Body]
  private var invincibilityTimer = 0
  private var time = 0f

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

  def onHit(body: Body): Boolean = invincible() || hit match {
    case false =>
      collider = Option(body)
      // Re-enable angular velocity for the body
      --> (data => body.applyAngularImpulse(data.hitImpulse, true))
      body.applyLinearImpulse(new Vector2(8, 0), body.getWorldCenter, true)
      hit = true
      true
    case _ => false
  }

  def setLocation(screenCoord: Vector2): Unit = {
    --> (data => body.setTransform(new Vector2(screenCoord.x / Vals.ratio, screenCoord.y / Vals.ratio), 0))
  }

  def getCollider: Option[Body] = collider

  def landed(): Unit = jumping = false

  def invincible(): Boolean = invincibilityTimer > 0

  def setInvincibilityTimer(time: Int): Unit = invincibilityTimer = time

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    val angle = (body.getAngle / (Math.PI / 180f)).toFloat
    if (GameStage.hasWon) {
      batch.draw(standingTexture,
        screenRectangle.x, screenRectangle.y,
        screenRectangle.width / 2, screenRectangle.height / 2,
        screenRectangle.width, screenRectangle.height,
        1, 1.1f,
        0
      )
    } else if (dodging || hit || jumping) {
      val texture = if (dodging) {
        dodgingTexture
      } else if (hit) {
        hitTexture
      } else {
        jumpingTexture
      }
      batch.draw(texture,
        screenRectangle.x, screenRectangle.y,
        screenRectangle.width / 2, screenRectangle.height / 2,
        screenRectangle.width, screenRectangle.height,
        1, 1.1f,
        angle
      )
    } else {
      batch.draw(running.getKeyFrame(time, true), screenRectangle.x, screenRectangle.y, screenRectangle.width, screenRectangle.height)
      time += Gdx.graphics.getDeltaTime
    }
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    //cancel x velocity, want the runner to stay in position.
    if (!(hit || dodging) && body.getAngle != 0) {
      body.setTransform(body.getPosition, 0)
    }
    if (body.getLinearVelocity.y > 7) {
      body.setLinearVelocity(new Vector2(body.getLinearVelocity.x, 7))
    }
    if (!hit) {
      body.setLinearVelocity(new Vector2(0, body.getLinearVelocity.y))
    }
    if (invincibilityTimer > 0) {
      invincibilityTimer -= 1
    }
    if (body.getPosition.x != Vals.runnerX) {
      body.setTransform(new Vector2(Vals.runnerX, body.getPosition.y), body.getAngle)
    }
  }
}

