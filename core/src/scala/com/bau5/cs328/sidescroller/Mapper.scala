package com.bau5.cs328.sidescroller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.World
import com.bau5.cs328.sidescroller.actors._

import scala.io.Source

/**
  * Created by Rick on 2/20/16.
  */
object Mapper {
  type GameActorType =  GameActor[_ <: UserData]

  var entries: Map[Float, List[GameActorType]] = _
  var lastChunk = 0

  def loadActors(world: World, stage: GameStage): Unit = {
    val lines = Source.fromFile(Gdx.files.internal("map.txt").path())
      .getLines()
      .toList
      .reverse
    var y = 0
    val ret = lines.map(_.toCharArray).flatMap { array =>
      val folded = array.foldLeft((List.empty[(Float, GameActorType)], 1f)) { case ((list, x), char) =>
        val actor = Option(char match {
          case 'g' => TexturedCollidable(world, x, y, "grassCliffLeft.png")
          case '-' => TexturedCollidable(world, x, y, "grass.png")
          case 'G' => TexturedCollidable(world, x, y, "grassCliffRight.png")
          case 's' => new DangerousCollidable(WorldUtils.createSimpleBody(world, x, y - 0.25f, 1, 0.5f), "spikes.png", 1, 0.5f)
          case 'r' => new DangerousCollidable(WorldUtils.createSimpleBody(world, x, y - 0.25f, 1, 0.5f), "rock.png", 1, 0.5f)
          case 'i' => TexturedCollidable(world, x, y, 2, 2, "star.png", Option(new PowerUpUserData(1, 1, new InvincibilityPowerUp)))
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

  def tick(stage: GameStage, ticks: Float): Unit = {
    val chunk = ticks / Vals.ratio
    var i = 0
    entries.get(chunk).foreach { actors =>
      i += actors.length
      actors foreach { actor =>
        actor.setVisible(true)
        stage.addActor(actor)
      }
    }
    if (i > 0) println(s"Added $i actors to the stage for chunk $chunk.")
  }
}
