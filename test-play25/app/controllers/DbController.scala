package controllers

import javax.inject.Singleton

import play.api.mvc.{Action, Controller}

@Singleton
class DbController extends Controller {


  def set() = Action {
    Ok("OK")
  }

  def get() = Action {
    Ok("OK")
  }
}
