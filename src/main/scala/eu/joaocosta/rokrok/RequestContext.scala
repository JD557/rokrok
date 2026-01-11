package eu.joaocosta.rokrok

import eu.joaocosta.rokrok.client.*

final case class Request(client: Client, host: String, port: Int, path: String):
  def protocol: String = client.protocol
  def baseUrl: String  = s"$protocol$host:$port"

object Request:
  def parse(query: String, defaultClient: Client = GopherClient): Request =
    val (client: Client, baseQuery: String) =
      Seq(GopherClient, HttpClient, SpartanClient)
        .find(client => query.startsWith(client.protocol))
        .map(client => (client, query.drop(client.protocol.size)))
        .getOrElse((defaultClient, query))

    val (host: String, port: Int, path: String) = baseQuery match
      case s"$host:$port/$path" => (host, port.toIntOption.getOrElse(client.defaultPort), "/" + path)
      case s"$host/$path"       => (host, client.defaultPort, "/" + path)
      case s"$host:$port"       => (host, port.toIntOption.getOrElse(client.defaultPort), "/")
      case host                 => (host, client.defaultPort, "/")

    Request(client, host, port, path)
