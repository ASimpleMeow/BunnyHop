# Bunny Hop Game
**Name**: Oleksandr Kononov
**Student Number**: 20071032
**Course**: Entertainment Systems 2017
**Subject**: Console Game Development

# New Features!
  - AI agents:
     - Idle state:
        - Change their movement direction every set amount of seconds.
        - Change their direction if they hit an edge their rock.
    - following state:
        - If the player gets within range, they will chase the player for a set amount of seconds.
        - If while chasing the player they reach an edge of a rock, they will jump over it to follow the player.
    - Additional:
        - If they touch the player, the player loses a life.
        - The player will be invincible for a set amount of time after the hit
          so there is time to run away or kill the agent.
        - The player can kill the agent by jumping on their head. (**Note**:_The player must going down to damage the agent_)
        - The player will receive score for killing the player.

- Feather power-up:
    - The feather powerup lasts only 4 seconds (was 9 seconds).
    - The feather powerup allows additional jumps in mid-air (was flight).
    - Feather powerup is meant to spawn just before large gaps

- Lives:
    - Player can have a maximum of 4 lives.
    - Every life a player picks up gives the player health (up to max of 4).
    - Lives are meant to spawn just before the agents.

- Procedural Level Generation:
    - Player spawns at one side of the screen (left) with extra height.
    - Goal spawns at the other side of the screen (right).
    - Enemies are meant to spawn on flat ground (where slope is zero) with a distance of larger than 3 meter.
      (**BUG**: _Due to initial offset issues that I couldn't fix due to time constraints, the agents spawn at a slightly higher position, altering their intended position by a small amount_).
    - Coins spawn 1 meter above the ground and continue over gaps
    - Gaps are random and feather are provided if the gap is too great or the slope is too great.
    - Lives are spawned near the agents to support the player.

# My Changes To The Initial Code
- Fixed the paused and resume bug where the player falls through the ground when unpaused by claming the deltaTime.
- In the level class, made the GOAL enum spawn a rock and on top of that rock spawn the goal, this ensures that the goal will always be on top of the rock. This is due to the offset bug that was present in the initial code.
- Since I wanted extra lives to be displayed, I changed the rendering of lives in the WorldRenderer to loop through the WorldController lives, not the Constants lives.
- Changed the feather powerup so that you don't have to mash the spacebar quickly is jump again in mid-air.
- Changed the controlls from arrows to WASD keys (**A**: _LEFT_, **D**: _RIGHT_).
