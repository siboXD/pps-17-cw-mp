package it.cwmp.room

import it.cwmp.model.User
import it.cwmp.testing.VertxTest
import org.scalatest.{BeforeAndAfterEach, Matchers}

import scala.concurrent.Future
import scala.util.Random

/**
  * Test class for local RoomsDAO
  *
  * @author Enrico Siboni
  */
class RoomsLocalDAOTest extends VertxTest with Matchers with BeforeAndAfterEach {

  private var daoFuture: Future[RoomsDAO] = _

  private val user: User = User(s"Enrico${Random.nextInt(Int.MaxValue)}")
  private val playersNumber = 4

  private def roomName = s"Stanza${Random.nextInt(Int.MaxValue)}"

  override protected def beforeEach(): Unit = {
    val localDAO = RoomsDAO(vertx)
    daoFuture = localDAO.initialize().map(_ => localDAO)
  }

  describe("Private Room") {

    describe("Creation") {
      it("should succeed returning roomId, if parameters are correct") {
        daoFuture.flatMap(_.createRoom(roomName, playersNumber))
          .flatMap(roomID => roomID.nonEmpty shouldBe true)
      }
      describe("should fail") {
        it("if roomName empty") {
          recoverToSucceededIf[IllegalArgumentException] {
            daoFuture.flatMap(_.createRoom("", playersNumber))
          }
        }
        it("if playersNumber less than 2") {
          recoverToSucceededIf[IllegalArgumentException] {
            daoFuture.flatMap(_.createRoom(roomName, 0))
          }
        }
      }
    }

    describe("Entering") {
      it("should succeed if roomId is provided") {
        daoFuture.flatMap(conn =>
          conn.createRoom(roomName, playersNumber).flatMap(roomID =>
            conn.enterRoom(roomID)(user).map(_ => succeed)))
      }

      it("user should be inside after entering") {
        daoFuture.flatMap(conn =>
          conn.createRoom(roomName, playersNumber).flatMap(roomID =>
            conn.enterRoom(roomID)(user).flatMap(_ =>
              conn.roomInfo(roomID))
              .flatMap(_.get.participants should contain(user))))
      }

      describe("should fail") {
        it("if roomId is empty") {
          recoverToSucceededIf[IllegalArgumentException] {
            daoFuture.flatMap(_.enterRoom("")(user))
          }
        }

        it("if user already inside a room") {
          recoverToSucceededIf[IllegalArgumentException] {
            daoFuture.flatMap(conn =>
              conn.createRoom(roomName, playersNumber).flatMap(roomID =>
                conn.enterRoom(roomID)(user).flatMap(_ =>
                  conn.enterRoom(roomID)(user))))
          }
        }
      }
    }

    describe("Retrieving Info") {
      it("should succeed if roomId is correct") {
        daoFuture.flatMap(conn =>
          conn.createRoom(roomName, playersNumber).flatMap(roomID =>
            conn.roomInfo(roomID))
            .flatMap(_.get.participants shouldBe empty))
      }

      it("should return empty option if room not present") {
        daoFuture.flatMap(_.roomInfo("1111111"))
          .flatMap(_ shouldBe empty)
      }

      describe("should fail") {
        it("if room id is empty") {
          recoverToSucceededIf[IllegalArgumentException] {
            daoFuture.flatMap(_.roomInfo(""))
          }
        }

      }
    }
  }

  describe("Public Room") {
    it("listing should be nonEmpty") {
      daoFuture.flatMap(_.listPublicRooms())
        .flatMap(_ should not be empty)
    }

    it("should show only public rooms") {
      daoFuture.flatMap(dao =>
        dao.createRoom(roomName, playersNumber).flatMap(_ =>
          dao.listPublicRooms())
          .map(_.forall(_.identifier.contains(RoomsDAO.publicPrefix)) shouldBe true)
      )
    }
  }

  describe("The Helper shouldn't work") {
    describe("if not initialized") {
      val fakeRoomID = "12342134"
      implicit val fakeUser: User = User("Enrico")
      it("createRoom") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).createRoom(roomName, playersNumber))
      }
      it("enterRoom") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).enterRoom(fakeRoomID))
      }
      it("roomInfo") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).roomInfo(fakeRoomID))
      }
      it("exitRoom") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).exitRoom(fakeRoomID))
      }
      it("listPublicRooms") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).listPublicRooms())
      }
      it("enterPublicRoom") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).enterPublicRoom(playersNumber))
      }
      it("publicRoomInfo") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).publicRoomInfo(playersNumber))
      }
      it("exitPublicRoom") {
        recoverToSucceededIf[IllegalStateException](RoomsDAO(vertx).exitPublicRoom(playersNumber))
      }
    }
  }
}
