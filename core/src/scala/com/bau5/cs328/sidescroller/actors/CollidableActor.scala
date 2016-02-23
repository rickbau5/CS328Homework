package com.bau5.cs328.sidescroller.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.physics.box2d.{Body, World}
import com.bau5.cs328.sidescroller.utils.{WorldHelper, Vals}
import com.bau5.cs328.sidescroller.GameStage

/**
  * Created by Rick on 2/20/16.
  */
class CollidableActor[D <: UserData](body: Body, w: Float, h: Float, d: Option[D]) extends GameActor(body, d){
  override def act(delta: Float): Unit = {
    super.act(delta)
    val pos = body.getPosition
    if (!GameStage.getRunner.hit && !Vals.freeze && !GameStage.hasWon) {
      body.setTransform(pos.add(-7f * delta, 0), 0)
    }
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
  }
}

object TexturedCollidable {
  def apply[D <: UserData](world: World, x: Float, y: Float, xS: Float, yS: Float, path: String, d: Option[D]): TexturedCollidable[D] = {
    new TexturedCollidable(WorldHelper.createSimpleBody(world, x, y, 1, 1), path, 1, 1, xS, yS, d)
  }
  def apply[D <: UserData](world: World, x: Float, y: Float, path: String, d: Option[D]): TexturedCollidable[D] = {
    new TexturedCollidable(WorldHelper.createSimpleBody(world, x, y, 1, 1), path, 1, 1, 1, 1, d)
  }
  def apply(world: World, x: Float, y: Float, path: String): TexturedCollidable[_ <: UserData] = {
    TexturedCollidable(world, x, y, path, Option(new SimpleUserData(1, 1)))
  }
}
class TexturedCollidable[D <: UserData](body: Body, texturePath: String, w: Float, h: Float, scaleX: Float, scaleY: Float, d: Option[D]) extends CollidableActor(body, w, h, d) {
  val textureRegion = GameStage.atlas.findRegion(texturePath)

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    if (scaleX > 1 || scaleY > 1) {
      val xOff = if (scaleX > 1) transform(w / scaleX) else 0
      var yOff = 0
      --> (d => if (d.isInstanceOf[PowerUpUserData]) yOff = transform(0.5f).toInt)
      batch.draw(textureRegion, screenRectangle.x - xOff, screenRectangle.y - yOff, 0, 0, screenRectangle.width, screenRectangle.height, scaleX, scaleY, 0)
    } else {
      batch.draw(textureRegion, screenRectangle.x, screenRectangle.y, screenRectangle.width, screenRectangle.height)
    }
  }
}

class DangerousCollidable(body: Body, texturePath: String, w: Float, h: Float, xs: Float = 1, ys: Float = 1) extends TexturedCollidable(body, texturePath, w, h, xs, ys, Option(new DangerousUserData(w, h))) {
  body.setUserData(new DangerousUserData(w, h))
}