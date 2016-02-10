package com.bau5.cs328.sidescroller

import com.badlogic.gdx.Game

/**
  * Created by Rick on 2/9/16.
  */
class Main extends Game {
  override def create(): Unit = {
    setScreen(new GameScreen)
  }
}
