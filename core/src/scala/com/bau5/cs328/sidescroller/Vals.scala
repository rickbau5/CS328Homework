package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2


/**
  * Created by Rick on 2/9/16.
  */
object Vals {
  val screenWidth = 800
  val screenHeight = 480
  val ratio = 32

  val debugWidth = 20
  val debugHeight = 13
  val debug = true

  val gravity = new Vector2(0, -10f)

  val groundX = 0
  val groundY = 0
  val groundWidth = 25f * 2
  val groundHeight = 2f
  val groundTop = groundY + groundHeight
  val groundDensity = 0f

  val runnerX = 2
  val runnerY = groundTop
  val runnerWidth = 1f
  val runnerHeight = 2f
  val runnerDensity = 0.5f
  val runnerDodgeX = 2f
  val runnerDodgeY = groundTop
  val runnerJumpImpulse = new Vector2(0.0f, 7.0f)
  val runnerHitImpulse = 7f

  val enemyLinearVelocity = new Vector2(-10f, 0)
  val enemyX = 25f
  val enemyDensity = 0.5f
  val enemyShortY = groundTop + 0.5f
  val enemyLongY = groundTop + 1f
  val enemyFlyingY = groundTop + 2f
}
