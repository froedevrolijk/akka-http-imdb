package com.froedevrolijk.restapi.utils

import com.froedevrolijk.restapi.core.search.{NameBasics, Principals, Ratings, TitleBasics}

import scala.io.Source

class ReadTSV {

  def parseRatings(fileName: String): Stream[Ratings] = {
    val ratingInfo: Stream[Ratings] = Source.fromFile(fileName).getLines().drop(1).toStream.map { line =>
      line.trim.split("\\t") match {
        case Array(tconst, averageRating, numVotes) => Ratings(tconst, averageRating.toDouble, numVotes.toInt)
      }
    }
    ratingInfo
  }

  def parseTitleBasics(fileName: String): Stream[TitleBasics] = {
    val titleBasicsInfo: Stream[TitleBasics] = Source.fromFile(fileName).getLines().drop(1).toStream.map { line =>
      line.trim.split("\\t") match {
        case Array(tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres) =>
          TitleBasics(tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres)
      }
    }
    titleBasicsInfo
  }

  def parseNameBasics(fileName: String): Stream[NameBasics] = {
    val nameBasicsInfo: Stream[NameBasics] = Source.fromFile(fileName).getLines().drop(1).toStream.map { line =>
      line.trim.split("\\t") match {
        case Array(nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles) =>
          NameBasics(nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles)
      }
    }
    nameBasicsInfo
  }

  def parsePrincipals(fileName: String): Stream[Principals] = {
    val principalsInfo: Stream[Principals] = Source.fromFile(fileName).getLines().drop(1).toStream.map { line =>
      line.trim.split("\\t") match {
        case Array(tconst, ordering, nconst, category, job, characters) =>
          Principals(tconst, ordering.toInt, nconst, category, job, characters)
      }
    }
    principalsInfo
  }
}