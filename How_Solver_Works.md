# Algorithms used by the solver

The Solver is used when playing in automatic mode, i.e. in Step by step mode or in Let it play mode. Its role is, at any moment of the game, to find out which tiles to click to continue the game, by guaranteeing the highest win-rate.

It distinguishes between three different possibilities, depending of which it acts differently

## Beginning of the game

Unfortunately, at the beginning of the game, since we have almost no information on the mines, we have to click tiles and hope we don't get a mine. We consider that the game "really" begins when we fall into a tile that has 0 (i.e. that has no mined neighbors), because then, many other tiles will be displayed and we will get much more informations.

The best startegy is to click tiles by beginning with the corner tiles, because these have only 3 neighbors instead of 8 like the central tiles, and hence have much more chances of having a 0. Then, we click tiles on the border, which have only 5 neighbors.

At this point, the solver searches for the best tile on the border to click. The best tile to click is the one that is the furthest from the tiles already clicked, i.e. the tile that requires the most moves (N-S-E-W or diagonal) to go to an uncovered tile.

The best tile to click is the one that has the biggest distance to the tiles that have already been uncovered, i.e. the shortest number of steps we have to do to get to an uncovered tile, counting N-S-E-W step, or diagonal steps. Then, we get to central tiles, but this case is very rare.

Unfortunately, a proportion of the games are lost during this step, which inscreases with the difficulty:
* Beginner: Around 5%
* Intermediate: Around 9%
* Expert: Around 17%
* Demon: Around 22%

## During the game

### Computing groups

### Computing probabilities
