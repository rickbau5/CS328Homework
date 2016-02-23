package com.bau5.cs328.sidescroller.screens

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.{Gdx, Screen}
import com.bau5.cs328.sidescroller.{GameStage, Main}

/**
  * Created by Rick on 2/22/2016.
  */
class MenuScreen(main: Main) extends BaseScreen {
  val stage = StageWithButtons.createStageWithButtons { skin => Seq(
    new ButtonWithListener("New Game", skin, () => main.transitionToScreen(new GameScreen)),
    new ButtonWithListener("Quit", skin, () => Gdx.app.exit())
  )}

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stage.draw()
    stage.act(delta)
  }
}

class GameScreen extends BaseScreen {
  val stage = new GameStage
}

abstract class BaseScreen extends Screen {
  val stage: Stage

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
