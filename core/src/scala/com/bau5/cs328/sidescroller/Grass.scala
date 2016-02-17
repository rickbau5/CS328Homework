package com.bau5.cs328.sidescroller

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Actor

/**
  * Created by Rick on 2/17/16.
  */
class Grass extends Actor {
  val textureRegion = new TextureRegion(
    new Texture("ground.png")
  )

  // Grass max height is 228px (aka image height)

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.draw(textureRegion, 0, 0, Vals.screenWidth, 114)
  }
}
