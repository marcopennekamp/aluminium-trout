package me.pennekamp.trout

package object input {
  type Key = Int
  case class MousePress(pos: ScreenPosition, button: Int)
  
  type Handler[A] = PartialFunction[A, Unit]
}
