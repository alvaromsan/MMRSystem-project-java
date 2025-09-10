# MMR/CR System - JAVA

## Description
This project is a **Matchmaking Rating (MMR) and Current Rating (CR) simulator** for two teams in a game. The program calculates possible changes in MMR and CR for each team depending on the match outcome, considering:

- Team MMR and CR differences
- Win/loss scenarios
- Catch-up mechanics
- Grace system to prevent extreme rating swings

The program shows the updated ratings for both teams for **both possible outcomes** of a match.

## Background
This program is modelling the MMR (Matchmaking Rating) and CR (Combat Rating) system used in World of Warcraft’s Arena PvP. Find the explanation below:

- In WoW arenas, MMR is a hidden skill-based value used to match players or teams with opponents of comparable skill level. CR, by contrast, is a visible rating that adjusts more slowly, helping your displayed rank converge toward your underlying MMR.
- Players experience larger swings in MMR, while CR changes more conservatively, acting as a stabilizing factor in matchmaking progression.
- MMR fluctuations are greater, especially when facing opponents with significantly different MMR from your own.
- CR aims to catch up, but much more slowly—this dynamic lies at the heart of the implemented program's "catch-up mechanic" and "grace system".

---

## Functionality
1. Ask the user for input (before the match begins):
    - Team A: MMR and CR
    - Team B: MMR and CR
2. Calculate MMR and CR changes based on:
    - Difference between teams
    - Maximum allowed changes
    - Catch-up mechanics
    - Grace system rules
3. Display results for both scenarios:
    - If Team A wins
    - If Team B wins

---

## Files
- `MMRSystem.java`: Main program containing all logic.

---

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mmr-cr-system.git

2. Navigate to the project folder:
   ```bash
    cd /src

3. Compile the program:
    ```bash
    javac MMRSystem.java
   
4. Run the program:
    ```bash
    java MMRSystem
   
5. Enter team ratings when prompted to see the updated results.

---

## MMR & CR Calculation Mechanics

### 1. MMR Calculation
- Uses predefined breakpoints to calculate percentage changes depending on the **MMR difference** between teams.
- Maximum MMR change per match is **50 points**.
- Positive or negative differences adjust the **winning team’s gain** or the **losing team’s loss**.

---

### 2. CR Calculation
- Maximum CR change per match is **30 points**.
- CR changes are influenced by:
    - Base rating difference between teams
    - **Catch-up mechanic** (helps teams “behind” gain extra points or lose less)
    - **Grace system** (prevents extreme swings if MMR–CR difference is too high or too low)

---

### 3. Catch-Up Mechanic
- Applies **extra points** to underperforming teams to balance progression.
- Points are interpolated based on predefined **breakpoints**.

---

### 4. Grace System
- Ensures no team **loses CR** if **MMR–CR difference > +200**.
- Ensures no team **gains CR** if **MMR–CR difference < -200**.
- Prevents **rating runaway** or harsh penalties for large mismatches.  
