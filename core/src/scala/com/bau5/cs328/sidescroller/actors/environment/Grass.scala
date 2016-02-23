package com.bau5.cs328.sidescroller.actors.environment

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.bau5.cs328.sidescroller.{GameStage, Vals}

/**
  * Created by Rick on 2/17/16.
  */
class Grass(moving: Boolean) extends Actor {
  // Grass max height is 228px (aka image height)
  val textureRegion = new TextureRegion(
    new Texture("ground.png")
  )

  private var xOff = 0

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    // scrolling texture
    if (GameStage.getRunner != null && !GameStage.getRunner.hit && moving) {
      xOff += 1
    }
    if (xOff >= Vals.screenWidth) {
      xOff = 0
    }
    batch.draw(textureRegion, -xOff, 0, Vals.screenWidth, 114)
    batch.draw(textureRegion, Vals.screenWidth - xOff, 0, Vals.screenWidth, 114)
  }
}
