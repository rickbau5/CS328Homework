package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.math.Rectangle
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
