package com.bau5.cs328.sidescroller.actors.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.bau5.cs328.sidescroller.utils.Vals

/**
  * Created by Rick on 2/17/16.
  */
class Background extends Actor {
  val textureRegion = new TextureRegion(
    new Texture(Gdx.files.internal("textures/background.png"))
  )

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.draw(textureRegion, 0, 0, Vals.screenWidth, Vals.screenHeight)
  }
}
