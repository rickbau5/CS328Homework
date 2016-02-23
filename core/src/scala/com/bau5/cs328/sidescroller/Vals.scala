package com.bau5.cs328.sidescroller

import com.badlogic.gdx.math.Vector2


/**
  * Created by Rick on 2/9/16.
  */
object Vals {
  val screenWidth = 800
  val screenHeight = 480
  val ratio = 38

  val debugWidth = 20
  val debugHeight = 13
  val debug = true

  val gravity = new Vector2(0, -10f)

  val groundX = 0
  val groundY = 0
  val groundWidth = 25f * 2
  val groundHeight = 4f
  val groundTop = groundY + groundHeight - 1f
  val groundDensity = 0f

  val runnerX = 2
  val runnerWidth = 0.9f
  val runnerHeight = 1.8f
  val runnerY = groundTop - runnerHeight / 2
  val runnerDensity = 1.0f
  val runnerDodgeX = runnerX
  val runnerDodgeY = runnerY - runnerWidth / 2
  val runnerJumpImpulse = new Vector2(0.0f, 12.0f)
  val runnerHitImpulse = 7f

  val enemyLinearVelocity = new Vector2(-10f, 0)
  val enemyX = 25f
  val enemyDensity = 0.5f
  val enemyShortY = groundTop + 0.5f
  val enemyLongY = groundTop + 1f
  val enemyFlyingY = groundTop + 2f

  val simpleEnemyX = 25f
  val simpleEnemyY = groundTop
}
