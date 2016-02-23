package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.bau5.cs328.sidescroller.GameStage
import com.bau5.cs328.sidescroller.utils.Vals

/**
  * Created by Rick on 2/22/2016.
  */
class StarActor extends Actor {
  val textureRegion = GameStage.atlas.findRegion("star")

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    if (GameStage.getRunner.invincible()) {
      batch.draw(textureRegion, Vals.screenWidth * 0.05f, Vals.screenHeight * 0.7f, 100, 100)
    }
  }
}
