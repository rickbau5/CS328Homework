package com.bau5.cs328.sidescroller

import com.badlogic.gdx.physics.box2d.{Body, Contact}


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
      case (a: RunnerUserData, b: GroundUserData) => new RunnerGroundContact(bodyA, bodyB)
      case (b: GroundUserData, a: RunnerUserData) => new RunnerGroundContact(bodyB, bodyA)
      case (a: RunnerUserData, b: EnemyUserData)  => new RunnerEnemyContact(bodyA, bodyB)
      case (b: EnemyUserData, a: RunnerUserData)  => new RunnerEnemyContact(bodyB, bodyA)
    }
  }

  def getNonRunner(contact: Contact): Body = {
    val (a, b) = (contact.getFixtureA.getBody, contact.getFixtureB.getBody)
    ((a, a.getUserData), (b, b.getUserData)) match {
      case ((_, _: RunnerUserData), non) => non._1
      case (non, (_, _: RunnerUserData)) => non._1
    }
  }

  def continueContact(contact: Contact, runner: Runner): Boolean = {
    (contact.getFixtureA.getBody, contact.getFixtureB.getBody) match {
      case (r, o) if isRunner(r) && runner.getCollider.map(_ == o).get => true
      case (o, r) if isRunner(r) && runner.getCollider.map(_ == o).get => true
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

  /**
    * Check if body is an enemy, accesses the user data field.
    *
    * @param body Body in question
    * @return True if enemy, false otherwise
    */
  def isEnemy(body: Body): Boolean = body.getUserData match {
    case _ : EnemyUserData => true
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
class RunnerGroundContact(runner: Body, val ground: Body) extends ContactType(runner, ground)
class RunnerEnemyContact(runner: Body, val enemy: Body) extends ContactType(runner, enemy)
