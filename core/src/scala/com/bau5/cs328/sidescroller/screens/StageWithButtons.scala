package com.bau5.cs328.sidescroller.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.{Color, OrthographicCamera, Pixmap, Texture}
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.bau5.cs328.sidescroller.{Main, Vals}

/**
  * Created by Rick on 2/22/2016.
  */
object StageWithButtons {
  private val param = {
    val parameter = new FreeTypeFontParameter
    parameter.size = 48
    parameter.color = Color.BLACK
    parameter
  }

  val font = new FreeTypeFontGenerator(Gdx.files.internal("fonts/big_noodle_titling.ttf")).generateFont(param)
  val buttonSkin = {
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
    buttonStyle.font = font
    temp.add("default", buttonStyle)
    temp
  }

  def createStageWithButtons(func: (Skin) => Seq[ButtonWithListener]): Stage = {
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
    func(buttonSkin).foreach { e =>
      table.add(e)
      table.row()
    }
    Gdx.input.setInputProcessor(temp)
    temp
  }
}

object ButtonWithListener {
  def mainMenuButton(main: Main): ButtonWithListener = {
    new ButtonWithListener("Main Menu", StageWithButtons.buttonSkin, () => main.transitionToScreen(new MenuScreen(main)))
  }
}
class ButtonWithListener(text: String, skin: Skin, listenerCallback: () => Unit) extends TextButton(text, skin) {
  addListener(new ButtonListener(listenerCallback))
}

class ButtonListener(callback: () => Unit) extends ChangeListener {
  override def changed(event: ChangeEvent, actor: Actor): Unit = callback.apply()
}
