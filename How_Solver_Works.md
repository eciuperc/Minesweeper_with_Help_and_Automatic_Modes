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

Once a 0-tile has been found, we can finally stop clicking tiles blindly, and really begin the game. The solver will now perform precise computations to choose the best move, so he can guarantee the best chance of continuing the game

In this phase, the solver can do two things:
* Look for tiles we can be sure are safe or mined
* Compute the probability that each tile has to be a mine, and choose the one that has the lowest one. This is done only if the first task doesn't return a single tile

### Computing groups

First, the solver tries to find tiles that he can be 100% sure of. Let's understand how he proceeds by looking at an example:

<figure class="image">
  <p align="center">
    <img src="Other_Images/100pSure_Before.png" width=30% height=30%>
  </p>
  <figcaption> <p align="center">Example</p> </figcaption>
</figure>

On this example, there are obvious tiles: 
* There are 3 tiles which numbers equal the number of unknown neighbors, and therefore all the neighbors of these tiles are mines
* If we add these mines, there are 4 more tiles which numbers equal the number of flagged neighbors, and therefore all their remaining neighbors are safe

<figure class="image">
  <p align="center">
    <img src="Other_Images/100pSure_First_Step.png" width=30% height=30%>
  </p>
  <figcaption> <p align="center">First safe and mined tiles deduced</p> </figcaption>
</figure>

But there are also other tiles that we can find looking on this example, which a bit less obvious than the previous ones.

On both of the below images, there is only one mine in the blue rectangle, and two in the yellow rectangle. Therefore, there has to be one mine in the right tile (resp. left tile) of the yellow rectangle, and the left tile (resp. right tile) of the blue rectangle of the first image (resp. second image)

<figure class="image">
  <p align="center" float="left">
    <img src="ReadMe_Images/100pSure_Second_Step.png" width=30% height=30%>
    <img src="ReadMe_Images/100pSure_Second_Step_Bis.png" width=30% height=30%> 
  </p>
  <figcaption> <p align="center">Other safe and mined tiles deduced</p> </figcaption>
</figure>

Finally, after applying the same reasoning for the remaining tiles, we get the following disposition:

<figure class="image">
  <p align="center">
    <img src="Other_Images/100pSure_After.png" width=30% height=30%>
  </p>
  <figcaption> <p align="center">Safe and mined tiles deduced</p> </figcaption>
</figure>

### Computing probabilities
