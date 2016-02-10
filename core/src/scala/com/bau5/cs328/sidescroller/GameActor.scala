package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.scenes.scene2d.Actor


/**
  * Created by Rick on 2/10/16.
  */
abstract class GameActor[T <: UserData](body: Body, val userData: T) extends Actor {
  body.setUserData(userData)
}

class Ground(body: Body) extends GameActor(body, new GroundUserData)

class Runner(body: Body) extends GameActor(body, new RunnerUserData(Vals.runnerJumpImpulse)) {
  var jumping = false
  var dodging = false
  def jump(): Unit = jumping && dodging match {
    case false =>
      body.applyLinearImpulse(userData.jumpImpulse, body.getWorldCenter, true)
      jumping = true
    case true => ;
  }

  def dodge(): Unit = jumping match {
    case false =>
      body.setTransform(userData.dodgingPosition, userData.dodgeAngle)
      dodging = true
    case true => ;
  }

  def stopDodge(): Unit = {
    body.setTransform(userData.runningPosition, 0f)
    dodging = false
  }

  def landed(): Unit = jumping = false
}

abstract class UserData
case class GroundUserData() extends UserData
case class RunnerUserData(jumpImpulse: Vector2) extends UserData {
  val runningPosition = new Vector2(Vals.runnerX, Vals.runnerY)
  val dodgingPosition = new Vector2(Vals.runnerDodgeX, Vals.runnerDodgeY)
  val dodgeAngle = (-90 * (Math.PI / 180f)).toFloat
}
