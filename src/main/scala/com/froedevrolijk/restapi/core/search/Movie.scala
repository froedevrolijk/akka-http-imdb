package com.froedevrolijk.restapi.core.search

case class Search(text: Option[String]) {
  require(text.fold(true)(_.nonEmpty), "The 'text' parameter can't be empty.")
  require(text.nonEmpty, "This endpoint requires the use of the 'text' parameter.")
}

case class Ratings(tconst: String, averageRating: Double, numVotes: Int)

case class TitleBasics(tconst: String, titleType: String, primaryTitle: String, originalTitle: String, isAdult: String,
                       startYear: String, endYear: String, runtimeMinutes: String, genres: String)

case class NameBasics(nconst: String, primaryName: String, birthYear: String,
                      deathYear: String, primaryProfession: String, knownForTitles: String)

case class Principals(tconst: String, ordering: Int, nconst: String,
                      category: String, job: String, characters: String)


case class TitleInfo(primaryTitle: String, runtimeMinutes: String, startYear: String,
                     endYear: String, genres: String, titleType: String, isAdult: String)
case class CastCrew(category: String, job: String, characters: String)

case class GenreInfo(tconst: String, primaryTitle: String, genres: String)
case class RankInfo(averageRating: Double, numVotes: Int)

case class DegreesOfKevinBacon(outputString: Int)
