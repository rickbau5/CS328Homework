package com.bau5.cs328.sidescroller.screens

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.{Gdx, Screen}
import com.bau5.cs328.sidescroller.actors.environment.{Background, Grass}
import com.bau5.cs328.sidescroller.{Vals, GameStage, Main}

/**
  * Created by Rick on 2/22/2016.
  */
class MenuScreen(main: Main) extends BaseScreen {
  val stage = StageWithButtons.createStageWithButtons { skin => Seq(
    new ButtonWithListener("New Game", skin, () => {
      if (main.first) {
        main.transitionToScreen(new InfoScreen(main))
        main.first = false
      } else {
        main.transitionToScreen(new GameScreen(main))
      }
    }),
    new ButtonWithListener("Quit", skin, () => Gdx.app.exit())
  )}

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stage.draw()
    stage.act(delta)
  }
}

class InfoScreen(main: Main) extends BaseScreen {
  val stage = StageWithButtons.createStageWithButtons{ skin => Seq.empty[ButtonWithListener]}
  stage.addActor(new Background)
  stage.addActor(new Grass(false))
  val table = new Table
  table.setFillParent(true)
  table.add(new ButtonWithListener("Continue", StageWithButtons.buttonSkin, () => main.transitionToScreen(new GameScreen(main))))
  table.setPosition(table.getX, table.getY - 80)
  stage.addActor(table)

  override def render(delta: Float): Unit = {
    super.render(delta)
    val font = StageWithButtons.font
    val batch = stage.getBatch
    val (midx, midy) = (Vals.screenWidth / 2, Vals.screenHeight / 2)
    batch.begin()
    font.draw(batch, "[Space] to jump", midx - 110, midy + 200)
    font.draw(batch, "[D] to dodge", midx - 90, midy + 140)
    font.draw(batch, "Pick up Stars for invulnerability", midx - 240, midy + 80)
    font.draw(batch, "Avoid scary things, or you'll lose!", midx - 250, midy + 20)
    batch.end()
  }
}

class GameScreen(main: Main) extends BaseScreen {
  val stage = new GameStage(main)
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
