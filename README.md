# Minesweeper_with_Help_and_Automatic_Modes
Here is a Minesweeper game I created using Java.
I implemented the game using the awt and swing libraries for the graphics, and using the game's rules from https://minesweeper.online/
I also added four game modes I created myself to make the game more diverse.

## How to play

Minesweeper is a logic puzzle game, in which we have a rectangle board with cells, each one can contain a mine and the goal is to find out which cells have a mine, and which ones don't. For that, we have the following game rules:
When we click, a tile, if it doesn't contain a mine, a number is displayed that correspond to the number of neighbor tiles (diagonal tiles included) that have a mine. There are at most 8 such neighbor tiles, so the number that will be displayed will be between 0 and 8.

[ AJOUTER IMAGE AVEC COMMENTAIRE]

The goal of the game is to use all the informations we get by clicking the tiles to fully uncover the board, without clicking a mine, which would make us lose.

The player can also perform a right click on a tile, if he thinks that there is a mine on this tile. This sets a flag on the tile, and marks it as a "safe" tile. 

[ AJOUTER IMAGE AVEC COMMENTAIRE]

Two commands have been created to speed-up the game:

When a player clicks a tile that has no neighbor mine, then the tile is just displayed as "empty", and all the neighboring tiles are displayed as well. If one of these tiles also has no mine neighbors, then it is also marked as empty, and all of their neighbors are also displayed. This process is repeated until we are out of such tiles.

[ AJOUTER IMAGE AVEC COMMENTAIRE]

Finally, a very useful move is the "chord" command, which consists in clicking on a tile that we already uncovered, and that we think we discovered all of its neighbors. The player can only do this if the number of its neighbor tiles that have a flag equals the tile's value, and then all of the un-flagged tiles will be uncovered. If we do this but the flags are uncorrect, we will get a mine, and thus the player will lose.

[ AJOUTER IMAGE AVEC COMMENTAIRE]

Minesweeper classicaly has 3 difficulties:
- Begginer, with a board of dimensions 9 x 9 and 10 mines
- Intermediate, with a board of dimensions 16 x 16 and 40 mines
- Expert, with a board of dimensions 30 x 16 and 99 mines
In my game, I also added a fourth difficulty, which I called demon, with a board of 75 x 35 and 600 mines. The number of mines is so important that it is almost impossible for a player to complete it without losing, so if you want to play it, I strongly recommend to play it using unblocker mode. Such large boards are, however, very interesting, to check computer's limits in automatic modes (see below)

Finally, the player can define a "custom" board, in which he can play a game with board's dimensions and number of mines he chooses.

One of the main interests in Minesweeper is to finish the game as fast as possible, so I added leaderboards for each difficulty to my game.

## The game modes

To add more originality to my game, I added 4 game modes, that will be presented in this section.
Of course, for a game to be saved in the leaderboard, we must play it without game modes, the only exception being the demon difficulty with "unblocker mode"

### Unblocker mode

When playing minesweeper, most of the time, we find tiles to click (Either left-click or right-click) so we can continue the game. However, it can also happen that we don't have such moves and, instead, we have to guess which tile is safe, which can be pretty frustrating, especially when we almost finished the board. In this mode, as the player completes the board, the program checks if there are solutions that can be found, and if there are not, it helps the player by displaying one mine, so he can keep going. Between all the mines that are left, the one that is displayed is the one that allows the player to deduce the most tiles, so he can keep playing as long as possible.

[ AJOUTER IMAGE AVEC COMMENTAIRE]

### Help mode

In this mode, when the player is blocked, he can ask the program for help, i.e. to display a mine to help him. In the same way as before, the mine displayed is the one that will help him deduce the biggest number of tiles.
This game mode can be very useful for beginners learning the game, as well as for experimented players who want to play with high difficulty to train (be it in expert, demon difficulties, but also with custom games with a very big number of tiles or mines).

[ AJOUTER IMAGE AVEC COMMENTAIRE]

### Automatic modes:

In these two mods, it's not the player who plays, but the program.

#### Step by step mode

This mode permits to understand what the program does, and possibly to learn some strategies. The player can't click on the tiles, but instead has three buttons to click:
- "Next step": The bot will analyse the current game, and deduce which tiles he clicks to continue the game
- "Finish game": The bot will repeat the previous step until the game is finished.
- "Display probabilities": The bot will compute the probability for each mine to be a tile, and display it on the board. Very useful for understanding how the bot plays.

[ AJOUTER IMAGE AVEC FLECHES ET COMMENTAIRES]

#### Let it play mode

In this mode, the user defines a board's dimension and a number of mines (be it with "classical" difficulties, or with custom mode), and a number of games that will be played by the bot. The bot then plays all these games, and returns the number of games that have been won.

[ AJOUTER IMAGES AVEC COMMENTAIRES]
