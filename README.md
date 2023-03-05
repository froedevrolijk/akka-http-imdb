# Akka HTTP IMDb
### About this project
The data used for this project is comprised of IMDb data, which is available from the IMDb website:
https://www.imdb.com/interfaces/

The project is built using Akka, and uses an in-memory database.

### Requirements
**Requirement #1**:  
IMDb copycat: Present the user with endpoint for allowing them to search by movie’s primary title or original title.
 The outcome should be related information to that title, including cast and crew.

**Requirement #2**:  
Top rated movies: Given a query by the user, you must provide what are the top rated movies for a genre
(If the user searches horror, then it should show a list of top rated horror movies).

**Requirement #3**:  
Six degrees of Kevin Bacon: Given a query by the user, you must provide what’s the degree of separation
between the person (e.g. actor or actress) the user has entered and Kevin Bacon.

### Run Application
- Run the tests: `sbt test`  
- Run the application: run Main.scala

Once the service is started, we can check if the API is up and running by visiting:
`http://localhost:9000/healthcheck` 
This endpoint should return the message *OK*

### The API endpoints:  
The application has three endpoints for searching IMDb data:  
*- Title*  
*- Genre*  
*- Person*  

#### Title
**Endpoint:** `http://localhost:9000/v1/search/title?text=<MOVIE_TITLE>`  

**Example:** `http://localhost:9000/v1/search/title?text=Corbett`  

This endpoint is used to search for movie titles and returns cast and crew information about that title.
The following fields will be returned as a JSON document:   
`primaryTitle`   
`runtimeMinutes`  
`startYear`  
`endYear`  
`genres`  
`titleType`  
`isAdult`  
`category`  
`job`  
`characters`  

#### Genre
**Endpoint** `http://localhost:9000/v1/search/genre?text=<GENRE>`  

**Example:** `http://localhost:9000/v1/search/genre?text=Comedy`    
  
This endpoint is used to search for a genre and returns the 10 top rated movies for this genre.  
The following fields will be returned as a JSON document:  
`tconst`  
`primaryTitle`  
`genres`  
`averageRating`  
`numVotes`  

#### Person
**Endpoint:** `http://localhost:9000/v1/search/person?text=<PERSON_NAME>` 
 
**Example:** `http://localhost:9000/v1/search/person?text=Marlon%20Brando` 
 
This endpoint is used to search for a person's name and returns the degrees of separation
between that person and Kevin Bacon.