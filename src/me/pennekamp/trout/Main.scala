package me.pennekamp.trout

import org.lwjgl.opengl.GL11._
import org.lwjgl.glfw.GLFW._

object Main {

  var application: Application = null

  class Game extends ApplicationListener {
    override def init(): Unit = {
      println("Game start.")
    }

    override def render(): Unit = {
      // Clear with a nice blue.
      glClearColor(0.2f, 0.4f, 1.0f, 0.0f)
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    }

    override def dispose(): Unit = {
      println("Game end.")
    }
  }

  class GameInputListener extends InputListener {
    override val keyDown: Input.KeyHandler = {
      case GLFW_KEY_ESCAPE => application.requestClose()
    }

    override val keyUp = PartialFunction.empty
  }

  def main(args: Array[String]) {
    val height = 480
    val config = Application.Config(
      height = height,
      width = (height * (16.0f / 9.0f)).toInt
    )(
      new Game,
      new GameInputListener
    )

    application = new Application(config)
    application.run()
  }

}
