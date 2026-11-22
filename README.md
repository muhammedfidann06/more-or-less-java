# 🔢 More or Less – Less is More

[![ci](https://github.com/muhammedfidann06/more-or-less-java/actions/workflows/ci.yml/badge.svg)](https://github.com/muhammedfidann06/more-or-less-java/actions/workflows/ci.yml)

A number puzzle game in **Java 21 + Swing**, written with clean object-oriented design and unit tests (JUnit 5, Maven).

## Rules
The field is an *n × n* grid of buttons with random digits 1–9. Above it you see the **target value**, below it the **current sum**.

1. **First move:** pick any button. Its digit becomes *Current*.
2. **Second move:** you may pick any button whose row **or** column number is divisible by *Current*.
3. **After that:** only intersections are allowed – row divisible by *Previous* and column divisible by *Current* (or the other way round).
4. Each chosen digit is added to the sum and the button becomes inactive. *Previous = Current, Current = new digit.*

When no moves are left, the game shows how far you are from the target. **Goal:** finish as close to the target as possible.

## Features
| Feature | |
|---|---|
| New random field each game (default 10 × 10) | ✅ |
| Save a field to a file and load it again | ✅ |
| Game menu: restart, field size, number of moves, target value | ✅ |
| Difficulty (easy / medium / hard) changes moves and target | ✅ |
| Shows the deviation from the target at the end | ✅ |
| Detects when there is no possible move left | ✅ |
| Hover preview: shows the new sum before you click | ✅ |
| Move suggestion (look-ahead heuristic) | ✅ |
| Undo the last move (menu or Ctrl+Z) | ✅ |

## Design
```
moreorless/
  Position      record – a cell (1-based)
  Board         digits + used state, input validation
  Game          rules: allowed moves, select, stuck/over, deviation, suggestion
  GameSettings  record – size, moves, target (+ factory from Difficulty)
  Difficulty    enum
  BoardStorage  save/load the field as a text file
  ui/GameFrame  main window + menus
  ui/BoardPanel button grid
```
The game logic has no Swing dependency, so it is fully unit-tested. Randomness is injected (`RandomGenerator`), which makes the tests deterministic.

## Run
```bash
mvn verify                              # build + tests
java -jar target/more-or-less-1.0.0.jar # start the game
```

## Kurzbeschreibung (Deutsch)
Ein Zahlenspiel mit Java und Swing. Man wählt Knöpfe mit Ziffern und versucht, mit der Summe genau den Zielwert zu treffen.
Die Spiellogik ist von der Oberfläche getrennt und mit JUnit getestet. Felder kann man speichern und laden.

## How this was built
This project was created with the help of an AI coding assistant (Claude). I'm learning to program and use it to understand
how the code works – the tests and the CI pipeline show that it runs correctly.
*Dieses Projekt ist mit Hilfe eines KI-Assistenten entstanden; ich nutze es, um Programmieren zu lernen.*
