package me.pennekamp.trout

object Input {
  type Key = Int
  type KeyHandler = PartialFunction[Input.Key, Unit]
}

trait InputListener {
  val keyDown: Input.KeyHandler
  val keyUp: Input.KeyHandler
}
