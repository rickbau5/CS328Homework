package com.bau5.cs328.sidescroller

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Batch}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor


/**
  * Created by Rick on 2/10/16.
  */

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
  */
sealed abstract class GameActor[T <: UserData](body: Body, val data: Option[T]) extends Actor {
  if (data.isDefined) {
    body.setUserData(data.get)
  } else {
    assert(Option(body.getUserData).isDefined, "UserData must be defined in constructor, or after initialization of body.")
  }
  val userData = body.getUserData.asInstanceOf[T]

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
  }
}


class Runner(body: Body) extends GameActor(body, Option(new RunnerUserData(Vals.runnerJumpImpulse, Vals.runnerWidth, Vals.runnerHeight))) {
  var jumping = false
  var dodging = false
  var hit = false
  private var collider = Option.empty[Body]

  def jump(): Unit = !(jumping || dodging || hit) match {
    case true =>
      body.applyLinearImpulse(userData.jumpImpulse, body.getWorldCenter, true)
      jumping = true
    case false => ;
  }

  def dodge(): Unit = !(jumping || hit) match {
    case true =>
      body.setTransform(userData.dodgingPosition, userData.dodgeAngle)
      dodging = true
    case false => ;
  }

  def stopDodge(): Unit = {
    if (!hit) {
      body.setTransform(userData.runningPosition.cpy(), 0f)
    }
    dodging = false
  }

  def isDodging: Boolean = dodging

  def onHit(body: Body): Unit = {
    collider = Option(body)
    body.applyAngularImpulse(userData.hitImpulse, true)
    hit = true
  }

  def getCollider: Option[Body] = collider

  def landed(): Unit = jumping = false
}

class Enemy(body: Body) extends GameActor(body, Option.empty[EnemyUserData]) {
  override def act(delta: Float): Unit = {
    super.act(delta)
    body.setLinearVelocity(userData.linearVelocity)
  }
}

class Ground(val body: Body) extends GameActor(body, Option(new GroundUserData))

sealed abstract class UserData
class SizedUserData(w: Float, h: Float) extends UserData {
  def width: Float = w
  def height: Float = h
}
case class GroundUserData() extends UserData
case class RunnerUserData(jumpImpulse: Vector2, w: Float, h: Float) extends SizedUserData(w, h) {
  val runningPosition = Vals.runnerPosition.cpy()
  val dodgingPosition = new Vector2(Vals.runnerDodgeX, Vals.runnerDodgeY)
  val dodgeAngle = (-90 * (Math.PI / 180f)).toFloat
  val hitImpulse = Vals.runnerHitImpulse
}
case class EnemyUserData(w: Float, h: Float) extends SizedUserData(w, h) {
  val linearVelocity = Vals.enemyLinearVelocity
}
