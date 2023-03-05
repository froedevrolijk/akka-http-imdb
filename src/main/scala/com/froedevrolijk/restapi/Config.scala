package com.froedevrolijk.restapi

import pureconfig.loadConfig

case class Config(http: HttpConfig)

object Config {
  def load(): Config =
    loadConfig[Config] match {
      case Right(config) => config
      case Left(error)   => throw new RuntimeException("Cannot read config file, errors:\n" + error.toList.mkString("\n"))
    }
}

case class HttpConfig(host: String, port: Int)