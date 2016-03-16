package me.pennekamp.trout

object Input {
  type Key = Int
}

trait InputListener {
  val keyDown: PartialFunction[Input.Key, Unit]
  val keyUp: PartialFunction[Input.Key, Unit]
}
