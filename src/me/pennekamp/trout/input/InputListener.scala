package me.pennekamp.trout.input

trait InputListener {
  val keyDown: Handler[Key]
  val keyUp: Handler[Key]
  val mouseDown: Handler[MousePress]
  val mouseUp: Handler[MousePress]
}

/**
  * Invokes the first handler in a sequence of handlers that is defined at the input point.
  * If no handler in the sequence is defined at the input point, this handler will not be
  * defined at said point, and calling apply will lead to a MatchError.
  */
object HandlerChain {
  def apply[A](seq: Traversable[Handler[A]]): Handler[A] = {
    if (seq.isEmpty) PartialFunction.empty
    else seq.tail.foldLeft(seq.head) { case (pf, handler) => pf.orElse(handler) }
  }
}

case class InputListenerChain(self: Traversable[InputListener]) extends InputListener {
  override val keyDown = HandlerChain(self.map(_.keyDown))
  override val keyUp = HandlerChain(self.map(_.keyUp))
  override val mouseDown: Handler[MousePress] = HandlerChain(self.map(_.mouseDown))
  override val mouseUp: Handler[MousePress] = HandlerChain(self.map(_.mouseUp))
}
