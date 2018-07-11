package it.cwmp.client.view.authentication

import it.cwmp.client.utils.{LayoutRes, StringRes}
import it.cwmp.client.view.{FXChecks, FXController, FXView}
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.stage.Stage

trait SignInFXStrategy {
  def onCreate(username: String, password: String): Unit
}

object SignInFXController {
  def apply(strategy: SignInFXStrategy): SignInFXController = {
    require(strategy != null)
    new SignInFXController(strategy)
  }
}

class SignInFXController(strategy: SignInFXStrategy) extends FXController with FXView with FXChecks {

  protected val layout: String = LayoutRes.signInLayout
  protected val title: String = StringRes.appName
  protected val stage: Stage = new Stage
  protected val controller: FXController = this

  @FXML
  private var tfUsername: TextField = _
  @FXML
  private var pfPassword: PasswordField = _

  @FXML
  private def onClickSignIn(): Unit = {
    Platform.runLater(() => {
      for(
        username <- getTextFieldValue(pfPassword, "È necessario inserire lo username");
        password <- getTextFieldValue(pfPassword, "È necessario inserire la password")
      ) yield strategy.onCreate(username, password)
    })
  }

  @FXML
  private def onClickSignUp(): Unit = {

  }

  override def resetFields(): Unit = {
    tfUsername setText ""
    pfPassword setText ""
  }

}
