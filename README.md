# Debuggery

[![Latest Release](https://img.shields.io/github/v/release/kennytv/Debuggery)](https://github.com/kennytv/Debuggery/releases)
[![Build Status](https://github.com/kennytv/Debuggery/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/kennytv/Debuggery/actions)

## Download
See the [**releases page**](https://github.com/kennytv/Debuggery/releases).

## Building
Debuggery requires **Java 11** to build. I'd recommend using [Adoptium builds].

Once cloned, use the gradle wrapper included in this repository to build a runnable jar.

`./gradlew build`

You will find a compiled version in the `./build/libs/` directory.

[Adoptium builds]: https://adoptium.net/

## What is it?
Debuggery is a small plugin designed to expose API values at runtime.

![tab-complete](https://i.imgur.com/H6IgP2H.png)
![webby](https://i.imgur.com/UKVYKN8.png)
![console](https://i.imgur.com/ETbwcCe.png)
![explode](https://i.imgur.com/LWzkAy2.png)
![return-info](https://i.imgur.com/IHLW3B7.png)
![exception](https://i.imgur.com/dVx9M3U.png)

## Why?
I got sick of maintaining a complicated API testing suite. This plugin allows me to check individual API behavior
without having to write a test plugin for every minor issue that gets reported. That's not to say this can entirely
replace proper testing of all API interactions in all circumstances... yet.

Furthermore, this is a massively handy utility to have while testing vanilla behaviors and other unrelated systems.

**I do not recommend you install this plugin on a production server**

Yes, it has permissions checks. No, I'm not aware of any way to get around them.

This is still a lot of power to dump on your server. Do so with caution and at your own risk.

## How do I use it?
To get a command listing, type /debuggery and hit tab.
The plugin is very reliant on tab auto completion, so try it on any command.

## How does it handle more complicated objects?
There is basic support for formatting object types without proper toString() methods and/or those that have poor
toString() methods. Every one of them has to be added manually so right now I'm most concerned with supporting those
that the API returns. If you find any missing, feel free to open an issue or contribute them.

## Can I set values using Debuggery?
Yes. Currently input handling is rather basic.

Arguments are delimited by spaces as per Bukkit, therefore when you specify more complex inputs it is not particularly
friendly. For example, to specify a Location, you must enter `worldName,x,y,z` as in `/dplayer teleport(.) world,1,100,1`.
I will eventually get around to investigating some sort of standardized patterns for this, perhaps even moving away
from Bukkit's space delimiter system.

Furthermore, every conversion from `string -> object` is added manually, so some are currently missing. Feel free to
open an issue or contribute them.
