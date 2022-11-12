# Project 6: Route

This is the directions document for Project 6 Route in CompSci 201 at Duke University, Fall 2022. [This document details the workflow](hhttps://coursework.cs.duke.edu/cs-201-fall-22/resources-201/-/blob/main/projectWorkflow.md) for downloading the starter code for the project, updating your code on coursework using Git, and ultimately submitting to Gradescope for autograding.

For this project, **you are allowed to work with a partner** (that is, in a group of two). If you are working with a partner, read the details in the expandable section below on how to collaborate effectively using Git. 

<details>
<summary>Details on Git with a Partner for P4</summary>

You may find it helpful to begin by reading the Working Together section of the [Git tutorial](https://gitlab.oit.duke.edu/academic-technology/cct/-/tree/master/git) from the Duke Colab.

One person should fork the starter code and then add their partner as a collaborator on the project. Choose Settings>Members>Invite Members. Then use the autocomplete feature to invite your partner to the project as a *maintainer*. Both of you can now clone and push to this project. See the [gitlab documentation here](https://docs.gitlab.com/ee/user/project/members/).

Now you should be ready to clone the code to your local machines.

1. Both students should clone the same repository and import it into VS Code just like previous projects.  
2. After both students have cloned and imported, one person should make a change (you could just write a comment in the code, for example). Commit and push this change. 
3. The other partner will then issue a git pull request. Simply use the command-line (in the same project directory where you cloned the starter code for the project) and type:
```bash
git pull
```
4. If the other partner now opens the project in VS Code again, they should see the modified code with the edit created by the first partner. 
5. You can continue this workflow: Whenever one person finishes work on the project, they commit and push. Whenever anyone starts work on the project, they begin by downloading the current version from the shared online repository using a git pull command.

This process works as long as only one person is editing at a time, and **you always pull before editing** and remember to **commit/push when finished**. If you forget to pull before editing your local code, you might end up working from an old version of the code different than what is in the shared online gitlab repository. If that happens, you may experience an error when you attempt to push your code back to the shared online repository. 

There are many ways to resolve these conflicts, essentially you just need to pick which of the different versions of the code you want to go with. See the [working together Git tutorial](https://gitlab.oit.duke.edu/academic-technology/cct/-/blob/master/git/working_together.md) [branching and merging Git tutorial](https://gitlab.oit.duke.edu/academic-technology/cct/-/blob/master/git/branching_merging.md) from the Duke Colab for more information. You can also refer to our [Git troubleshooting document](https://coursework.cs.duke.edu/201-public-documentation/resources-201/-/blob/main/troubleshooting.md#git-faq). 

If you run into a merge conflict, one thing that might be confusing is that the editor that opens where you can resolve them may, by default, be [VIM](https://www.vim.org), which can be very unintuitive if you have not used it before. You can either look up the basics there, or if you prefer you can set a different text editor as the default that git uses for editing commit messages, merge conflicts, etc. For example, to make Visual Studio Code the default editor:
1. Open the command palette on visual studio code (`shift` + `command` + `p` on Mac, or `shift` + `ctrl` + `p` on Windows).
2. Write `Shell Command: Install 'Code' command in path` - it should autocomplete to this, press enter. It may ask for your permission.
3. In a terminal, enter the command `git config --global core.editor "code --wait"`. Now VS Code should be the default editor for git.
4. You can confirm the change by trying `git config --global -e` in a terminal. This should open a VS Code window showing your `git config` file (you don't need to edit, this is just to confirm it worked).

The `--wait` command means that whenever `git` opens something in VS Code for you to edit, it will wait until you close that window/tab before proceeding.

Additional resources: if you have any concerns about using Git with a partner, please consult the [Git troubleshooting guide](https://coursework.cs.duke.edu/cs201projects/resources-201/-/blob/main/gitTroubleshooting.md).

</details> 

## Outline 

- [Project Introduction](#project-introduction)
- [Part 1: Impelementing `GraphProcessor`](#part-1-implementing-graphprocessor)
- [Part 2: Creating `GraphDemo`](#part-2-creating-graphdemo)
- [Submitting, Reflect, Grading](#submitting-reflect-grading)

## Project Introduction

In this project you are asked to implement a routing service that represents the United States highway network as a graph and calculates routes and distances on this network. At a high level, in part 1 you will implement `GraphProcessor` which stores a graph representation and provides public methods to answer connectivity, distance, and pathfinding queries. This part of the project will be autograded as per usual. In part 2 you will implement a `main` method in `GraphProcessor` that produces a minimal viable product (also known as MVP) demonstrating the functionality of `GraphProcessor` and visualizing the results. For this part, you will record a brief demo of you or someone else *using* your program to find and visualize a route.

The rest of this section introduces you to the starter code, especially the `Point` and `Visualize` classes, as well as the graph data we will be using.

### The `Point` Class

You are provided in the starter code with `Point.java` that represents an immutable (meaning it cannot be changed after creation) point on the Earth's surface. Each such point consists of a [latitude](https://en.wikipedia.org/wiki/Latitude), or north-south angle relative to the equator, and a [longitude](https://en.wikipedia.org/wiki/Longitude), or east-west angle relative to the prime meridian. We use the convention whereby latitudes and longitudes are both measured in degrees between -180 and 180, where positive latitudes are for north of the equator and negative latitudes are for south of the equator. Similarly, positive longitudes are for east of the prime meridian, and negative longitudes are for west of the equator. 

Vertices/nodes in the graph we will use to represent the United States highway system will be `Point` objects. You should not need to do edit anything in the `Point` class. However, you may wish to use the various methods that are supplied for you. These methods are described in more detail in the expandable section below.

<details><summary>Expand for details on Point methods</summary>

- `getLat` and `getLon` are getter methods for returning the values of the private latitude and longitude instance variables. 
- The `distance` method calculates the "straight-line" distance from one point to another. Note that latitudes and longitudes are *angles* and not x-y coordinates, so this calculation requires trigonometric projection onto a sphere. This can get a little complicated, see [great circle distances](https://en.wikipedia.org/wiki/Great-circle_distance) if you're curious, but you do **not** need to understand or change this math. Please use the `distance` method provided and do not alter or implement a different one, for the sake of consistency with the autograder.
- The `equals` method checks if two points have the same `latitude` and `longitude`.
- The `hashCode` method has been implemented to be consistent with `equals`, and so that you can use `Point` objects in `HashSet`s or as keys in `HashMap`s.
- The `toString` allows you to directly print Point objects.
- The `compareTo` method compares `Point` objects by latitude, then breaks ties by longitude. Note that `Point implements Comparable<Point>`.

</details>

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

1. Submit your code on gradescope to the autograder. If you worked with a partner, you and your partner will make a **single submission together on gradescope**. Refer to [this document](https://docs.google.com/document/d/e/2PACX-1vREK5ajnfEAk3FKjkoKR1wFtVAAEN3hGYwNipZbcbBCnWodkY2UI1lp856fz0ZFbxQ3yLPkotZ0U1U1/pub) for submitting to Gradescope with a partner. 
2. Submit a link to your demo in the separate Demo assignment. Be sure that your link is embedded/clickable. Again, if you worked with a partner, you and your partner will make a single combined submission.
3. Complete the brief [reflect form]() TODO.


