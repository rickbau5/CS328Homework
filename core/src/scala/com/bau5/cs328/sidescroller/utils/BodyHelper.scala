package com.bau5.cs328.sidescroller.utils

import com.badlogic.gdx.physics.box2d.{Body, Contact}
import com.bau5.cs328.sidescroller.actors._


/**
  * Created by Rick on 2/10/16.
  *
  * Implemented in Scala cause class matching ftw
  */
object BodyHelper {
  /**
    * Determines the type of contact from the two bodies of the contact.
    *
    * @param contact The contact containing the bodies
    * @return ContactType that is the type of contact
    */
  def computeContactType(contact: Contact): ContactType = {
    val (bodyA, bodyB) = (contact.getFixtureA.getBody, contact.getFixtureB.getBody)

    (bodyA.getUserData, bodyB.getUserData) match {
      case (a: RunnerUserData, b: GroundUserData) => new RunnerStaticContact(bodyA, bodyB)
      case (b: GroundUserData, a: RunnerUserData) => new RunnerStaticContact(bodyB, bodyA)
      case (a: RunnerUserData, b: SimpleUserData) => new RunnerStaticContact(bodyA, bodyB)
      case (b: SimpleUserData, a: RunnerUserData) => new RunnerStaticContact(bodyB, bodyA)
      case (a: RunnerUserData, b: DangerousUserData) => new RunnerDangerContact(bodyA, bodyB)
      case (b: DangerousUserData, a: RunnerUserData) => new RunnerDangerContact(bodyB, bodyA)
      case (a: RunnerUserData, b: PowerUpUserData) => new PowerUpContact(bodyA, bodyB)
      case (b: PowerUpUserData, a: RunnerUserData) => new PowerUpContact(bodyB, bodyA)
      case (a: RunnerUserData, b: ExitUserData) => new ExitContact(bodyA, bodyB)
      case (b: ExitUserData, a: RunnerUserData) => new ExitContact(bodyB, bodyA)
      case _ => NoContact
    }
  }

  def hasDangerousBody(contact: Contact): Boolean = {
    (contact.getFixtureA.getBody.getUserData, contact.getFixtureB.getBody.getUserData) match {
      case (_: DangerousUserData, _) => true
      case (_, _: DangerousUserData) => true
      case _ => false
    }
  }

  /**
    * Determines wether the body is on the screen
    *
    * @param body Body in question
    * @return True if on screen, false if not
    */
  def bodyOnScreen(body: Body): Boolean = body.getUserData match {
    case data : UserData => body.getPosition.x + data.width / 2 > 0 && body.getPosition.y + data.height / 2 > 0
    case _ => false
  }

  def bodyLeftBounds(body: Body): Boolean = body.getUserData match {
    case data : UserData => body.getPosition.x + data.width <= 0 || body.getPosition.y + data.height <= 0
    case _ => false

  }

  def bodyShouldBeDestroyed(body: Body): Boolean = body.getUserData match {
    case destroyable: Destroyable => destroyable.shouldDestroy()
    case _ => false
  }

  /**
    * Check if body is a runner, accesses the user data field.
    * @param body Body in question
    * @return True if runner, false otherwise
    */
  def isRunner(body: Body): Boolean = body.getUserData match {
    case _ : RunnerUserData => true
    case _ => false
  }
}

sealed abstract class ContactType(val runner: Body, val other: Body)
class RunnerStaticContact(runner: Body, val ground: Body) extends ContactType(runner, ground)
class RunnerEnemyContact(runner: Body, val enemy: Body) extends ContactType(runner, enemy)
class RunnerDangerContact(runner: Body, val danger: Body) extends ContactType(runner, danger)
class PowerUpContact(runner: Body, val powerUp: Body) extends ContactType(runner, powerUp)
class ExitContact(runner: Body, val exit: Body) extends ContactType(runner, exit)
object NoContact extends ContactType(null, null)

sealed abstract class PowerUpType {
  def affect(runner: Runner): Unit
}
class InvincibilityPowerUp extends PowerUpType {
  override def affect(runner: Runner): Unit = {
    runner.setInvincibilityTimer(200)
  }
}