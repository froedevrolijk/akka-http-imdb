package com.froedevrolijk.restapi.core.search

import scala.concurrent.{ExecutionContext, Future}
import com.froedevrolijk.restapi.utils.ReadTSV

import scala.annotation.tailrec

trait Database {

  def getTitleIDs(searchTitleBasics: Search): Future[Vector[String]]

  def getTitleInfo(titles: Vector[String]): Future[Vector[(TitleInfo, CastCrew)]]

  def getGenreIDs(searchTitleBasics: Search): Future[Vector[String]]

  def getGenreInfo(tconsts: Vector[String]): Future[Vector[(GenreInfo, RankInfo)]]

  def getPersonID(searchTitleBasics: Search): Future[Vector[String]]

  def getTitlesForPerson(nconst: Set[String]): Map[Set[String], Set[String]]

  def getPersonsForTitle(tconst: Set[String]): Map[Set[String], Set[String]]

  def findTitlesAndPersonsForPersonID(f: String): Future[Map[Set[String], Set[String]]]

  def iterateTitlesAndPersons(f: Map[Set[String], Set[String]]): Map[Set[String], Set[String]]

  def checkIfKevinBaconInSet(f: Map[Set[String], Set[String]], acc: Int = 1): Int

}

class InMemoryDatabase(implicit ec: ExecutionContext) extends Database {

  val TSVReader = new ReadTSV()

  private val ratings: Vector[Ratings] = TSVReader.parseRatings("src/main/resources/assets/selection.title.ratings.tsv").toVector
  private val titleBasics: Vector[TitleBasics] = TSVReader.parseTitleBasics("src/main/resources/assets/selection.title.basics.tsv").toVector
  private val nameBasics: Vector[NameBasics] = TSVReader.parseNameBasics("src/main/resources/assets/selection.name.basics.tsv").toVector
  private val principals: Vector[Principals] = TSVReader.parsePrincipals("src/main/resources/assets/selection.title.principals.tsv").toVector
  private val nameBasicsSelection: Vector[NameBasics] = TSVReader.parseNameBasics("src/main/resources/assets/kb.name.basics.tsv").toVector
  private val principalsSelection: Vector[Principals] = TSVReader.parsePrincipals("src/main/resources/assets/kb.title.principals.tsv").toVector

  /** Gets the IDs for a title search query.
    *
    * @param searchTitleBasics the search query string for a title.
    * @return the unique ID (`tconst`) string for the search query.
    */
  override def getTitleIDs(searchTitleBasics: Search): Future[Vector[String]] = Future.successful {
    val titleBasicsInfo = {
      searchTitleBasics.text.fold(titleBasics)(text => titleBasics.filter { titles =>
        titles.primaryTitle.toLowerCase.contains(text.toLowerCase) ||
        titles.originalTitle.toLowerCase.contains(text.toLowerCase)
      })
    }
    titleBasicsInfo.map(_.tconst)
  }

  /** Extracts information regarding a title.
    *
    * @param tconsts vector of IDs for a title search query.
    * @return related information to that title, including cast and crew.
    */
  override def getTitleInfo(tconsts: Vector[String]): Future[Vector[(TitleInfo, CastCrew)]] = Future.successful {
    val titleBasicsList = titleBasics.filter(tconsts contains _.tconst) map { x =>
      TitleInfo(x.primaryTitle, x.runtimeMinutes, x.startYear, x.endYear, x.genres, x.titleType, x.isAdult) }
    val principalsList = principals.filter(tconsts contains _.tconst) map { x =>
      CastCrew(x.category, x.job, x.characters) }
    titleBasicsList zip principalsList
  }

  /** Gets the IDs for a genre search query.
    *
    * @param searchGenreBasics the search query string for a genre.
    * @return the unique ID (`tconst`) string for the search query.
    */
  override def getGenreIDs(searchGenreBasics: Search): Future[Vector[String]] = Future.successful {
    val genresList = searchGenreBasics.text.fold(titleBasics)(text => titleBasics.filter { titles =>
      titles.genres.toLowerCase.contains(text.toLowerCase)
    })
    genresList.map(_.tconst)
  }

  /** Extracts information about the top 10 rating movies for a certain genre.
    *
    * @param tconsts vector of IDs for a genre search query.
    * @return the top 10 rating movies for a genre.
    */
  override def getGenreInfo(tconsts: Vector[String]): Future[Vector[(GenreInfo, RankInfo)]] = Future.successful {
    val orderedRatingsList = ratings.filter(tconsts contains _.tconst).sortWith(_.averageRating > _.averageRating).map(_.tconst).take(10)
    val genreInfo = titleBasics.filter(orderedRatingsList contains _.tconst) map { x => GenreInfo(x.tconst, x.primaryTitle, x.genres) }
    val rankInfo = ratings.filter(orderedRatingsList contains _.tconst) map { x => RankInfo(x.averageRating, x.numVotes) }
    genreInfo zip rankInfo.sortBy(_.averageRating)(Ordering[Double].reverse)
  }

  /** Gets the ID for a person search query.
    *
    * @param searchPersonBasics the search query string for a person.
    * @return the unique ID (`nconst`) string for the search query.
    */
  override def getPersonID(searchPersonBasics: Search): Future[Vector[String]] = Future.successful {
    val person = searchPersonBasics.text.fold(nameBasics)(text => nameBasics.filter { names =>
      names.primaryName.toLowerCase.contentEquals(text.toLowerCase)
    })
    person.map(_.nconst)
  }

  /** Gets titles for a set of persons.
    *
    * @param nconst a set of persons.
    * @return a `Map[Set[String], Set[String]]` which maps a set of persons to the titles that they occurred in.
    */
  override def getTitlesForPerson(nconst: Set[String]): Map[Set[String], Set[String]] = {
    val titlesOfActor = nameBasicsSelection.filter(nconst contains _.nconst).map(_.knownForTitles).toSet
    val titlesOfActor2 = titlesOfActor.flatMap(x => x.mkString.split(",").toSet)
    List(nconst -> titlesOfActor2).toMap
  }

  /** Gets actors for a set of titles.
    *
    * @param tconst a set of titles.
    * @return a `Map[Set[String], Set[String]]` which maps a set of titles to a set of persons starring in these titles.
    */
  override def getPersonsForTitle(tconst: Set[String]): Map[Set[String], Set[String]] = {
    val actorsInTitle = principalsSelection.filter(tconst contains _.tconst).map(_.nconst).toSet
    List(tconst -> actorsInTitle).toMap
  }

  /** Finds the titles and actors for a certain person.
    *
    * @param personID a unique ID (`nconst`) string identifying a person.
    * @return a `Map[Set[String], Set[String]]` which maps a set of titles to a set of persons starring in these titles,
    *         applicable for the `nconst` string that was provided.
    */
  override def findTitlesAndPersonsForPersonID(personID: String): Future[Map[Set[String], Set[String]]] = Future.successful {
    val titlesForPersonID = Map(List(personID) -> List("")).flatMap(x => getTitlesForPerson(x._1.toSet))
    titlesForPersonID.flatMap(x => getPersonsForTitle(x._2))
  }

  /** Combines the `getTitlesForPerson` and `getPersonsForTitle` functionality in order to find titles and persons iteratively.
    *
    * @param actorsToTitlesMap a mapping containing values from a set of persons to a set of titles.
    * @return a `Map[Set[String], Set[String]]` which maps a set of titles to a set of persons starring in these titles.
    */
  override def iterateTitlesAndPersons(actorsToTitlesMap: Map[Set[String], Set[String]]): Map[Set[String], Set[String]] = {
    val titlesToActorsMap = actorsToTitlesMap.flatMap(x => getTitlesForPerson(x._2))
    titlesToActorsMap.flatMap(x => getPersonsForTitle(x._2))
  }

  /** Calculates the degrees of separation between a person and Kevin Bacon.
    *
    * @param f a map for the person sought, containing the titles he or she occurred in.
    * @param acc an accumulator to count the number of iterations (i.e. the degrees of separation).
    * @return an integer value representing the degrees of separation between a person and Kevin Bacon.
    */
  @tailrec
  final override def checkIfKevinBaconInSet(f: Map[Set[String], Set[String]], acc: Int = 1): Int = {
    if (f.valuesIterator.exists(_.contains("nm0000102"))) acc
    else checkIfKevinBaconInSet(iterateTitlesAndPersons(f), acc + 1)
  }

}