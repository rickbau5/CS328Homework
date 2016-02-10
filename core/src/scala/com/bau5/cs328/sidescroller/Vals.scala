package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2


/**
  * Created by Rick on 2/9/16.
  */
object Vals {
  val screenWidth = 800
  val screenHeight = 480

  val gravity = new Vector2(0, -10f)

  val groundX = 0
  val groundY = 0
  val groundWidth = 50f
  val groundHeight = 2f
  val groundDensity = 0f

  val viewportWidth = 20
  val viewportHeight = 13

  val runnerX = 2
  val runnerY = groundY + groundHeight
  val runnerWidth = 1f
  val runnerHeight = 2f
  val runnerDensity = 0.5f
  val runnerDodgeX = 2f
  val runnerDodgeY = 1.5f
  val runnerJumpImpulse = new Vector2(0.0f, 7.0f)
}
