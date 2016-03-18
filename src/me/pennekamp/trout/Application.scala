package me.pennekamp.trout

import java.nio.DoubleBuffer
import java.lang.Math.floor

import me.pennekamp.trout.input.{InputListener, Key, MousePress}
import org.lwjgl.{BufferUtils, Version}
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, GLFWMouseButtonCallback}
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil._

object Application {
  case class Config(
    height: Int = 600,
    width: Int = 400
  )(
    val applicationListener: ApplicationListener,
    val inputListener: InputListener
  )
}

class Application(private val config: Application.Config) {

  // We need to strongly reference callback instances, so don't make them local variables.
  private val errorCallback: GLFWErrorCallback = GLFWErrorCallback.createPrint(System.err)

  private val keyCallback: GLFWKeyCallback = GLFWKeyCallback.create {
    (window: Long, key: Key, scancode: Int, action: Int, mods: Int) =>
      action match {
        case GLFW_PRESS => config.inputListener.keyDown.applyOrElse(key, handleUnboundKey("pressed"))
        case GLFW_RELEASE => config.inputListener.keyUp.applyOrElse(key, handleUnboundKey("released"))
        case _ =>
      }
  }

  private val mouseButtonCallback: GLFWMouseButtonCallback = GLFWMouseButtonCallback.create {
    (window: Long, button: Int, action: Int, mods: Int) =>
      action match {
        case GLFW_PRESS => config.inputListener.mouseDown.applyOrElse(
          MousePress(mousePosition, button), handleUnregisteredMousePress("press")
        )
        case GLFW_RELEASE => config.inputListener.mouseDown.applyOrElse(
          MousePress(mousePosition, button), handleUnregisteredMousePress("release")
        )
        case _ =>
      }
  }

  // The window handle
  private var windowHandle: Long = 0

  def run(): Unit = {
    println(s"Running with LWJGL version: ${Version.getVersion}")

    try {
      init()
      loop()

      // Destroy window and window callbacks
      glfwDestroyWindow(windowHandle)
      //keyCallback.free()
      //mouseButtonCallback.free()
    } finally {
      // Terminate GLFW and free the GLFWErrorCallback
      glfwTerminate()
      //errorCallback.free()
    }
  }

  // Buffers for getting the mouse position.
  private val bufferMpX: DoubleBuffer = BufferUtils.createDoubleBuffer(1)
  private val bufferMpY: DoubleBuffer = BufferUtils.createDoubleBuffer(1)

  def mousePosition: ScreenPosition = {
    bufferMpX.rewind()
    bufferMpY.rewind()
    glfwGetCursorPos(windowHandle, bufferMpX, bufferMpY)
    ScreenPosition(floor(bufferMpX.get()).toInt, floor(bufferMpY.get()).toInt)
  }

  private def handleUnboundKey(actionName: String)(key: Key): Unit = {
    println(s"Unbound key $actionName: $key")
  }

  private def handleUnregisteredMousePress(actionName: String)(mousePress: MousePress): Unit = {
    println(s"Unhandled mouse button $actionName: $mousePress")
  }

  private def init(): Unit = {
    // Setup an error callback.
    glfwSetErrorCallback(errorCallback)

    // Initialize GLFW.
    if (glfwInit() != GLFW_TRUE) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }

    glfwDefaultWindowHints() // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

    // This needs to be an OpenGL 3.2+ CORE profile, so that the confusing
    // OS X OpenGL support doesn't crash the application. OpenGL 3.0 is not
    // choosable on OS X, for example.
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    // Create the window
    windowHandle = glfwCreateWindow(config.width, config.height, "Hello World!", NULL, NULL)
    if (windowHandle == NULL) {
      throw new RuntimeException("Failed to create the GLFW window")
    }

    // Setup the callbacks.
    glfwSetKeyCallback(windowHandle, keyCallback)
    glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback)

    // Get the resolution of the primary monitor
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center our window
    glfwSetWindowPos(
      windowHandle,
      (vidmode.width() - config.width) / 2,
      (vidmode.height() - config.height) / 2
    )

    // Make the OpenGL context current
    glfwMakeContextCurrent(windowHandle)

    // Enable v-sync
    glfwSwapInterval(1)

    // Make the window visible
    glfwShowWindow(windowHandle)
  }

  private def loop(): Unit = {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    val capabilities = GL.createCapabilities()

    println(s"OpenGL 2.0 available: ${capabilities.OpenGL20}")
    println(s"OpenGL 2.1 available: ${capabilities.OpenGL21}")
    println(s"OpenGL 3.0 available: ${capabilities.OpenGL30}")
    println(s"OpenGL 3.2 available: ${capabilities.OpenGL32}")
    println(s"OpenGL 4.1 available: ${capabilities.OpenGL41}")

    // We need to create the listener before we call the render method.
    config.applicationListener.init()

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (glfwWindowShouldClose(windowHandle) == GLFW_FALSE) {
      config.applicationListener.render()

      glfwSwapBuffers(windowHandle) // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents()
    }

    // The application is about to be closed.
    config.applicationListener.dispose()
  }

  def requestClose(): Unit = {
    glfwSetWindowShouldClose(windowHandle, GLFW_TRUE)
  }

}
