package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2


/**
  * Created by Rick on 2/9/16.
  */
object Vals {
  val screenWidth = 800
  val screenHeight = 480
  val viewportWidth = 20
  val viewportHeight = 13

  val gravity = new Vector2(0, -10f)

  val groundX = 0
  val groundY = 0
  val groundWidth = 50f
  val groundHeight = 4f
  val groundTop = groundY + groundHeight / 2
  val groundDensity = 0f

  val runnerWidth = 1f
  val runnerHeight = 2f
  val runnerX = 2
  val runnerY = groundTop
  val runnerPosition = new Vector2(runnerX, runnerY + runnerHeight / 2)
  val runnerDensity = 0.5f
  val runnerDodgeX = 2f
  val runnerDodgeY = groundTop + 0.5f
  val runnerJumpImpulse = new Vector2(0.0f, 7.0f)
  val runnerHitImpulse = 7f

  val enemyLinearVelocity = new Vector2(-10f, 0)
  val enemyX = 25f
  val enemyDensity = 0.5f
  val enemyShortY = groundTop + 0.5f
  val enemyLongY = groundTop + 1f
  val enemyFlyingY = groundTop + 2f
}
