package com.bau5.cs328.sidescroller

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.{Gdx, Screen}

/**
  * Created by Rick on 2/9/16.
  */
class GameScreen extends Screen {
  val stage = new GameStage

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stage.draw()
    stage.act(delta)
  }

  override def hide(): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {}

  override def pause(): Unit = {}


  override def show(): Unit = {}

  override def resume(): Unit = {}
}
