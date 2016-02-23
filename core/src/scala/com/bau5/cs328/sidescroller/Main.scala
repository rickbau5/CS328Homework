package com.bau5.cs328.sidescroller

import com.badlogic.gdx.{Game, Screen}
import com.bau5.cs328.sidescroller.screens.MenuScreen

/**
  * Created by Rick on 2/9/16.
  */
class Main extends Game {
  var first = true

  override def create(): Unit = {
    transitionToScreen(new MenuScreen(this))
  }

  def transitionToScreen(screen: Screen): Unit = {
    Option(getScreen).foreach(_.dispose())
    setScreen(screen)
  }
}
