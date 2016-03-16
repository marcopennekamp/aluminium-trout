package me.pennekamp.trout

trait ApplicationListener {

  /**
    * Called when the application has been set up. This
    * is called before the render method is called.
    */
  def init(): Unit

  /**
    * This method is used to draw the next frame.
    */
  def render(): Unit

  /**
    * Called when the application is about to be closed.
    */
  def dispose(): Unit

}
