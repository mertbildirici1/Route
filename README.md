# Project 6: Route

This is the directions document for Project 6 Route in CompSci 201 at Duke University, Fall 2022. [This document details the workflow](hhttps://coursework.cs.duke.edu/cs-201-fall-22/resources-201/-/blob/main/projectWorkflow.md) for downloading the starter code for the project, updating your code on coursework using Git, and ultimately submitting to Gradescope for autograding.

## Outline 

- [Project Introduction](#project-introduction)
- [Part 1: Impelementing `GraphProcessor`](#part-1-implementing-graphprocessor)
- [Part 2: Creating `GraphDemo`](#part-2-creating-graphdemo)
- [Submitting, Reflect, Grading](#submitting-reflect-grading)

## Project Introduction

In this project you are asked to implement a routing service that represents the United States highway network as a graph and calculates routes and distances on this network. At a high level, in part 1 you will implement `GraphProcessor` which stores a graph representation and provides public methods to answer connectivity, distance, and pathfinding queries. This part of the project will be autograded as per usual. In part 2 you will implement a `main` method in `GraphProcessor` that produces a minimal viable product (also known as MVP) demonstrating the functionality of `GraphProcessor` and visualizing the results. For this part, you will record a brief demo of you or someone else *using* your program to find and visualize a route.

## Part 1: Impelementing `GraphProcessor`

The starter code for `GraphProcessor.java` includes five public methods you must implement:
1. `initialize`
2. `nearestPoint`
3. `routeDistance`
4. `connected`
5. `route`

## Part 2: Creating `GraphDemo`

The starter code for `GraphDemo.java` only includes an empty `main` method. Feel free to modify `GraphDemo` however you see fit - it will not be autograded. In the end, running your `GraphDemo` `main` method should produce a demonstration of the functionality of your project, including (at a minimum) the calculation of a shortest path between at least two cities in the United States, and the visualization of that route. You will produce a recording demonstrating your use of `GraphDemo` in at most 5 minutes. In your recording, you should explain how `GraphDemo` works.

See this video (TODO) for an example of a `GraphDemo` at work.

## Submitting, Reflect, Grading

Push your code to Git. Do this often. To submit:

1. Submit your code on gradescope to the autograder.
2. Submit a link to your demo in the separate Demo assignment. Be sure that your link is embedded/clickable.
3. Complete the brief [reflect form]() TODO.


