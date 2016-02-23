package com.bau5.cs328.sidescroller.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.World
import com.bau5.cs328.sidescroller.actors._
import com.bau5.cs328.sidescroller.GameStage

import scala.io.Source

/**
  * Created by Rick on 2/20/16.
  */
object Mapper {
  type GameActorType =  GameActor[_ <: UserData]

  var entries: Map[Float, List[GameActorType]] = _
  var lastChunk = 0

  def loadActors(world: World, stage: GameStage): Unit = {
    val lines = Source.fromFile(Gdx.files.internal("maps/map.txt").path())
      .getLines()
      .toList
      .reverse
    var y = 0
    val ret = lines.map(_.toCharArray).flatMap { array =>
      val folded = array.foldLeft((List.empty[(Float, GameActorType)], 1f)) { case ((list, x), char) =>
        val actor = Option(char match {
          case 'g' => TexturedCollidable(world, x, y, "grassCliffLeft")
          case '-' => TexturedCollidable(world, x, y, "grass")
          case 'G' => TexturedCollidable(world, x, y, "grassCliffRight")
          case 's' => new DangerousCollidable(WorldHelper.createSimpleBody(world, x, y - 0.25f, 1, 0.5f), "spikes", 1, 0.5f, 1, 2)
          case 'r' => new DangerousCollidable(WorldHelper.createSimpleBody(world, x, y, 1, 1), "rock", 1, 1, 1, 2)
          case 'i' => TexturedCollidable(world, x, y, 2, 2, "star", Option(new PowerUpUserData(1, 1, new InvincibilityPowerUp)))
          case 'x' => TexturedCollidable(world, x, y, 1, 1, "signExit", Option(new ExitUserData()))
          case 'a' => TexturedCollidable(world, x, y, "signRight")
          case ' ' => null
        })
        if (actor.isDefined) {
          val a = actor.get
          if (x > Vals.screenWidth / 32) {
            a.setVisible(false)
          }
          (list ++ List(x -> a), x + 1)
        } else {
          (list, x + 1)
        }
      }
      y += 1
      folded._1.toMap
    }.groupBy(_._1)
      .map(e => e._1 -> e._2.map(b => b._2.body.getPosition.y -> b._2))
      .toList
      .sortBy(_._1)

    entries = ret.map(e => e._1 -> e._2.map(_._2)).toMap
    entries foreach (_._2 foreach stage.addActor)
  }
}
