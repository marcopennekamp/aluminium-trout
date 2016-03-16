package me.pennekamp.trout

import org.lwjgl.Version
import org.lwjgl.glfw.{GLFWKeyCallback, GLFWErrorCallback}
import org.lwjgl.opengl.GL

import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil._

class Main {

  // We need to strongly reference callback instances.
  var errorCallback: GLFWErrorCallback = null
  var keyCallback: GLFWKeyCallback = null

  // The window handle
  var window: Long = 0

  def run(): Unit = {
    println("Hello LWJGL " + Version.getVersion + "!")

    try {
      init()
      loop()

      // Destroy window and window callbacks
      glfwDestroyWindow(window)
      keyCallback.free()
    } finally {
      // Terminate GLFW and free the GLFWErrorCallback
      glfwTerminate()
      errorCallback.free()
    }
  }

  private def init(): Unit = {
    // Setup an error callback.
    errorCallback = GLFWErrorCallback.createPrint(System.err)
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

    val height = 480
    val width = (height * (16.0f / 9.0f)).toInt

    // Create the window
    window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL)
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window")
    }

    keyCallback = new GLFWKeyCallback {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
          glfwSetWindowShouldClose(window, GLFW_TRUE) // We will detect this in our rendering loop
        }
      }
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, keyCallback)

    // Get the resolution of the primary monitor
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center our window
    glfwSetWindowPos(
      window,
      (vidmode.width() - width) / 2,
      (vidmode.height() - height) / 2
    )

    // Make the OpenGL context current
    glfwMakeContextCurrent(window)

    // Enable v-sync
    glfwSwapInterval(1)

    // Make the window visible
    glfwShowWindow(window)
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

    //println(s"Maximum OpenGL version: ${capabilities.}")

    // Clear with a nice blue.
    glClearColor(0.2f, 0.4f, 1.0f, 0.0f)

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (glfwWindowShouldClose(window) == GLFW_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT) // clear the framebuffer

      glfwSwapBuffers(window) // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents()
    }
  }
}

object Main {

  def main(args: Array[String]) {
    new Main().run()
  }

}