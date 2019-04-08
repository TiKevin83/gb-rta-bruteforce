gb-rta-bruteforce
==========

#### Overview

This is a bruteforce bot to find God Nidorans for Pokemon Red RTA with some flexibility added for TASing. It could be adapted to find other encounters in other places too.

It runs a modified [GSR's libgambatte](https://github.com/gifvex/gambatte-speedrun) as a core, and spits out giant log files to be combed over manually.

#### Installation for Windows

Build a `libgambatte.dll` (Instructions can be found [here](https://github.com/gifvex/gambatte-speedrun))

Clone the repository, create a new folder called `libgambatte` and put the previously built dll in there.

After that fire up your favorite Java IDE, add the JNA library to the Classpath and start using it.
