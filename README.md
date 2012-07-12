DiveCraft
=======
Introduction
-----------------
This plugin allows you to use a helmet for extended underwater exploration.
Once your air bar is empty while wearing a helmet, the helmet consumes fuel to
refill the air bar and in absence of fuel, you start drowning. If there is fuel
but not enough for a full refill, a partial refill is done. You can extend your
base lung capacity by wearing a chestplate. Depending on the kind of
chestplate, a different amount of capacity is added.

Fuel can be any item, you just have to have it on you. Depending on the helmet,
a different fuel consumption can be configured (helmets = anything you can put
on your head and that you configure to be a valid diving helmet).

This is intended for servers where an extended submarine experience is wanted
but where unlimited diving capabilities and client mods are undesirable.

Features
-------------
* Being able to do extended underwater exploration
* Configurable fuel item
* Configurable dive-enabled helmets
* Configurable fuel consumption
* Configurable dive-enabled chestplates for additional air capacity
* Configurable base lung capacity
* No client mod necessary
* Permission nodes for enabling/disabling diving equipment

Default behaviour
-------------------------
Uses sugar cane for fuel. Pumpkins are the most fuel-efficient (but the view
stinks) and the worst helmet is a Leather Cap. These are configured in the
[configuration file](https://github.com/wthys/DiveCraft/wiki/Configuration).
The default permission setting is to allow usage of DiveCraft features. The
base lung capacity is 10.5 seconds and each helmet adds a number of seconds to
this.  Leather chestplate adds 1.5 second, diamond and chain chestplates add 6
seconds.

Download
---------------
[DiveCraft v4](https://github.com/downloads/wthys/DiveCraft/DiveCraft.v4.jar) (Bukkit 1.2.5-R4 compatible)

Possible future work
---------
* Multiple fuel types with different energetic values
* Flippers for faster water travel
