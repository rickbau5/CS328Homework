package com.bau5.cs328.sidescroller

import com.badlogic.gdx.physics.box2d.Contact


/**
  * Created by Rick on 2/10/16.
  *
  * implemented in scala cause class matching ftw
  */
object ContactHandler {
  def checkRunnerAndGroundContact(contact: Contact): Boolean = {
    (contact.getFixtureA.getBody.getUserData, contact.getFixtureB.getBody.getUserData) match {
      case (r: RunnerUserData, g: GroundUserData) => true
      case (g: GroundUserData, r: RunnerUserData) => true
      case _ => false
    }
  }
}
