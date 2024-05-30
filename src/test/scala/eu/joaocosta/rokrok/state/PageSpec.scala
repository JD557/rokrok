package eu.joaocosta.rokrok.state

class PageSpec extends munit.FunSuite:
  test("Page.parseQuery parses queries with the correct defaults"):
    assertEquals(
      Page.parseQuery("example.org"),
      Page.parseQuery("gopher://example.org:70/1/")
    )

    assertEquals(
      Page.parseQuery("example.org:71"),
      Page.parseQuery("gopher://example.org:71/1/")
    )

    assertEquals(
      Page.parseQuery("example.org:71/2/"),
      Page.parseQuery("gopher://example.org:71/2/")
    )

    assertEquals(
      Page.parseQuery("example.org:71/2/query"),
      Page.parseQuery("gopher://example.org:71/2/query")
    )
