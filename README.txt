Author: Raimond Bogdiun

This is a Java application project "laivu_musis" made during the 4week Java beginner course at Vilnius Coding School.

It solves the communication with a provided server (http protocol) that acts as a repository (~game status in JSON)
and a set of game rules for a battleship game.

Maven is used for dependency injection. pom.xml added

ConseleUI.java let's user connect, set up ships locally, and join a game on the server, retrieves status updates when needed.
BattleshipBotApp.java let's the user launch a BOT who will play the game using an algorithm, goal is to obviously make it smart.

- checks before making a turn could be added, narrowing down randomly generated coordinates could also be done
  by checking the least used areas, or creating patterns etc.


Both apps have the option to run an additional bot just before joining a game on the server

-this one was mostly for testing purposes because the server may not have a player waiting for a game or a bot.

-also just for trying out concurrency myself,


Not a proper documentation file.
