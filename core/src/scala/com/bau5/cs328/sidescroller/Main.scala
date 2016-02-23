package com.bau5.cs328.sidescroller

import com.badlogic.gdx.{Screen, Game}
import com.bau5.cs328.sidescroller.screens.{MenuScreen, GameScreen}

/**
  * Created by Rick on 2/9/16.
  */
class Main extends Game {
  override def create(): Unit = {
    transitionToScreen(new MenuScreen(this))
//    transitionToScreen(new GameScreen)
  }
  def transitionToScreen(screen: Screen): Unit = {
    Option(getScreen).foreach(_.dispose())
    setScreen(screen)
  }
}
