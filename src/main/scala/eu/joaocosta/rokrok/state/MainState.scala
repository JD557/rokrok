package eu.joaocosta.rokrok.state

/** Main application state
  */
final case class MainState(
    page: Page = Page(),
    settings: Settings = Settings()
)
