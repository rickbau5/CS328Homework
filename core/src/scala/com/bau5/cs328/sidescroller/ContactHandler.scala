package com.bau5.cs328.sidescroller

import com.badlogic.gdx.physics.box2d.Contact


/**
  * Created by Rick on 2/10/16.
  *
  * Implemented in Scala cause class matching ftw
  */
object ContactHandler {
  def checkRunnerAndGroundContact(contact: Contact): Boolean = {
    (contact.getFixtureA.getBody.getUserData, contact.getFixtureB.getBody.getUserData) match {
      case (RunnerUserData(_), GroundUserData()) => true
      case (GroundUserData(), RunnerUserData(_)) => true
      case _ => false
    }
  }
}
