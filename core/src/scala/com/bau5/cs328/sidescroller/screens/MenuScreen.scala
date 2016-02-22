package com.bau5.cs328.sidescroller.screens

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.{Screen, Gdx}
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{TextButton, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.bau5.cs328.sidescroller.{Main, Vals}

/**
  * Created by Rick on 2/22/16.
  */
class MenuScreen(main: Main) extends Screen {
  lazy val skin = {
    val temp = new Skin
    val pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.WHITE)
    pixmap.fill()
    temp.add("white", new Texture(pixmap))
    temp.add("default", new BitmapFont())
    val buttonStyle = new TextButtonStyle()
    buttonStyle.up = temp.newDrawable("white", Color.DARK_GRAY)
    buttonStyle.down = temp.newDrawable("white", Color.DARK_GRAY)
    buttonStyle.over = temp.newDrawable("white", Color.LIGHT_GRAY)
    val parameter = new FreeTypeFontParameter
    parameter.size = 48
    parameter.color = Color.BLACK
    buttonStyle.font = new FreeTypeFontGenerator(Gdx.files.internal("fonts/big_noodle_titling.ttf")).generateFont(parameter)
    temp.add("default", buttonStyle)
    temp
  }

  lazy val stage = {
    val temp = new Stage(
      new StretchViewport(
        Vals.screenWidth,
        Vals.screenHeight,
        new OrthographicCamera(Vals.screenWidth, Vals.screenHeight)
      )
    )
    val table = new Table
    table.setFillParent(true)
    temp.addActor(table)
    table.add(new ButtonWithListener("New Game", skin, () => main.transitionToScreen(new GameScreen)))
    table.row()
    table.add(new ButtonWithListener("Quit", skin, () => Gdx.app.exit()))

    Gdx.input.setInputProcessor(temp)

    temp
  }

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

class ButtonWithListener(text: String, skin: Skin, listenerCallback: () => Unit) extends TextButton(text, skin) {
  addListener(new ButtonListener(listenerCallback))
}

class ButtonListener(callback: () => Unit) extends ChangeListener {
  override def changed(event: ChangeEvent, actor: Actor): Unit = callback.apply()
}
