## MicroLimbo

This is a lightweight Minecraft limbo server, written in Java with Netty.
The main goal of this project is maximum simplicity with a minimum number of sent and processed packets.
The limbo is empty; there is no ability to set a schematic building since this is not necessary.
You can send useful information via chat or boss bar.

The server is fully clear. It is only able to keep a lot of players while the main server is down.

General features:

* High performance. The server doesn't save or cache any useless (for limbo) data.
* Doesn't spawn threads per player. Use a fixed thread pool.
* Support for **BungeeCord** and **Velocity** info forwarding.
* Support for [BungeeGuard](https://www.spigotmc.org/resources/79601/) handshake format.
* Runnable as a Velocity plugin.
* Multiple versions support.
* Fully configurable.
* Plugin API.

### Contributing

Feel free to create a pull request if you find some bug or optimization opportunity, or if you want
to add some functionality that is suitable for a limbo server and won't significantly load the server.

### Credits

* [NanoLimbo](https://github.com/Nan1t/NanoLimbo) by [Nan1t](https://github.com/Nan1t) - the original project.