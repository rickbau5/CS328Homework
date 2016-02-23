package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.math.Vector2
import com.bau5.cs328.sidescroller.Vals
import com.bau5.cs328.sidescroller.utils.PowerUpType

/**
  * Created by Rick on 2/20/16.
  */

sealed abstract class UserData(w: Float, h: Float) {
  def width: Float = w
  def height: Float = h
}

case class GroundUserData(w: Float, h: Float) extends UserData(w, h)
case class SimpleUserData(w: Float, h: Float) extends UserData(w, h)
case class RunnerUserData(w: Float, h: Float) extends UserData(w, h) {
  val runningPosition = new Vector2(Vals.runnerX, Vals.runnerY)
  val dodgingPosition = new Vector2(Vals.runnerDodgeX, Vals.runnerDodgeY)
  def runningTransform(vec: Vector2): Vector2 = new Vector2(Vals.runnerX, vec.y + Vals.runnerHeight / 4)
  def dodgeTransform(vec: Vector2): Vector2 = {
    val y = if (vec.y % 1 > 0.8) {
      Math.ceil(vec.y)
    } else {
      Math.floor(vec.y)
    }
    new Vector2(dodgingPosition.x, y.toFloat)
  }
  val dodgeAngle = (90 * (Math.PI / 180f)).toFloat
  val hitImpulse = Vals.runnerHitImpulse
  val jumpImpulse = Vals.runnerJumpImpulse
}
case class PowerUpUserData(w: Float, h: Float, typ: PowerUpType) extends UserData(w, h) with Destroyable
case class DangerousUserData(w: Float, h: Float) extends UserData(w, h)

trait Destroyable {
  private var shouldBeDestroyed = false
  def shouldDestroy(): Boolean = shouldBeDestroyed
  def markForRemoval(): Unit = shouldBeDestroyed = true
}