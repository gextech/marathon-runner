Marathon Runner
===============


    -   \O                       ^__^
   -     /\                      (oo)\_______
  -   __/\ `                     (__)\       )\/\
     `    \                          ||----w |
^^^^^^^^`^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The marathon runner is a very simple (and opinionated) nashorn runner that aims to be node friendly.

Our main goal is to have a battle tested production environment, while
enjoying the benefits of using a better language and development environment.

Just to be clear this is NOT a complete node replacement, instead we want to be able to use npm (or bower)
dependencies that don't depend a lot on the node runtime.

Now let's list our specific goals:

* To be able to use CommonJS
* To mimic NodeJS dependency resolution
* To make reactive extensions interoperable with java
* Just enough node to run coffee

