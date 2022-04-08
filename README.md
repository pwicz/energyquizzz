# EnergyQuizzz

## Description of project

EnergyQuizzz is a multiplayer quiz game intented to raise awareness about the energy consumed on different activities. You will be challenged to compare and guess different energy consumption values for commmon activities, such as taking a shower, charging a car... 
Improve your knowledge and gain more points to be displayed in the leaderboard!

## Group members

| Profile Picture | Name | Email |
|---|---|---|
| ![](https://secure.gravatar.com/avatar/e5cfaf517ba077aeca5521ce6169e0c7?s=50&d=identicon) | Alexandra Velicu | A.Velicu@student.tudelft.nl |
| ![](https://secure.gravatar.com/avatar/bbda4979c6998138da346cc07a7011e2?s=50&d=identicon) | Javier Perez | J.PerezLopez@student.tudelft.nl |
| ![](https://secure.gravatar.com/avatar/4d0c23cb9b4a3a99edf62fea5de8dd48?s=50&d=identicon) | Mike Erkemeij | M.J.J.S.Erkemeij@student.tudelft.nl |
| ![](https://i.imgur.com/HT9ZzNn.jpeg) | Piotr Politowicz | P.A.Politowicz@student.tudelft.nl |
| ![](https://secure.gravatar.com/avatar/c632d904328514baa7c2f19b2e52bf8b?s=50&d=identicon) | Justin Wisker | J.P.T.Wisker@student.tudelft.nl |
| ![](https://i.postimg.cc/MGghTBJS/Hnet-com-image-1.png) | Ioana Ilincescu | I.Ilincescu@student.tudelft.nl |

## How to run it

- Java & Java FX are required to run the project

1. Start the server
   - When running the server for the first time, enter the Command Line Argument `load`, which will import activities from an actvitiy bank in a specified location. Activities will then be saved in the database and, in the future, this `load` argument is not required anymore unless the server owner wants to import new activities into the game.
   - Run `server/src/main/java/server/Main.java`
  
2. Start the client
   - In Edit Configurations -> Add VM Options -> `--module-path="/Users/seb/Downloads/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.fxml`. The path should point to the javaFX SDK, which can be downloaded [here](https://gluonhq.com/products/javafx/)
   - Run `client/src/main/java/client/Main.java`
   - You are now ready to play

- You do not need to own a server to play, since you can connect to a server hosted on the internet through its IP address

## How to contribute to it

- Players can contribute by importing new activities into the activity bank. Simply insert them into your local activity bank, and when running the server, enter "load" as a command line argument. This will import your new activities into the database and other players will be able to see the changes