package it.cwmp.client.model.game

/**
  * A trait that describes a Tentacle
  */
trait Tentacle {
  def startCell: Cell

  def arriveCell: Cell

  def launchTime: Long
}

/**
  * Companion object
  */
object Tentacle {

  def apply(startCell: Cell, arriveCell: Cell, launchTime: Long): Tentacle = TentacleDefault(startCell, arriveCell, launchTime)

  /**
    * Default implementation of Tentacle
    *
    * @param startCell  the cell where tentacle starts
    * @param arriveCell the cell where tentacle is directed
    * @param launchTime the time at which the tentacle was launched
    */
  private case class TentacleDefault(startCell: Cell, arriveCell: Cell, launchTime: Long) extends Tentacle

  /**
    * Tentacle length calculator
    *
    * @param tentacle the tentacle of wich to calculate the distance
    */
  implicit class TentacleLengthCalculator(tentacle: Tentacle) {

    def length(actualTime: Long)(tentacleSpeedStrategy: TentacleSpeedStrategy): Int =
      tentacleSpeedStrategy.timeToDistance(actualTime - tentacle.launchTime)
  }

}