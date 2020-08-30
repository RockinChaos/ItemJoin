![](https://i.imgur.com/zXs2ka8.png)

<p align="center">
 See <a href="https://github.com/RockinChaos/ItemJoin/wiki">ItemJoin's Wiki</a> for the full detailed documentation on the plugin.<br>
</p>

## ItemJoin - Get Custom Items on Join!
-----

### Description
-----
```
ItemJoin is a feature-packed custom items plugin allowing you and your players to get custom items upon performing 
certain triggers such as on join, world-switch, respawn, region-enter, region-leave and more. ItemJoin is very 
versatile allowing you to only enable features as you need them as most features are not shown by default you will 
need to add them to the items.yml when you need them. These features can be found in the wiki and the config.yml 
has all additional features disabled by default. The goal of this plugin is to provide a seamless transition from 
any previous custom items plugin and to give you the power to add features as you want them so that you do not 
have any unnecessary features cluttering your configurations. The number of features that ItemJoin support is 
limitless however some features include item commands, join commands, custom enchantments, custom map images, 
player heads, and a fully in-game GUI creator.
```
-----
### Installation
```
1) Once the ItemJoin.jar is downloaded, the jar must be placed into your server's plugins folder.
2) Double-check the server's plugins folder for any duplicate ItemJoin.jar files, such as ItemJoin(1).jar.
3) Delete any found duplicate instances or there will be conflicting errors in the console window.
4) Once this is completed, the server must be restarted to register and enable the plugin. The plugin
   will then attempt to find your server version and generate a configuration based on the found version.
5) From this point, it is up to you to configure the plugin or join the discord for additional help.
```

### Developer Notes
-----
This plugin has taken up many countless hours and has had continued support for several years, please consider [donating](https://www.paypal.me/RockinChaos) as it is the best way to support the plugin.

Required Libraries when compiling (there are no required dependencies, only softDepends):
```
* Bukkit/Spigot (Latest Official)
* Vault (Latest Official)
* AuthMe (Latest Official)
* BetterNick (Latest Official)
* HeadDatabase (Latest Official)
* PlaceholderAPI (Latest Official)
* SkinRestorerX (Latest Official)
* TokenEnchant (Latest Official)
* WorldEdit (Latest Official)
* WorldGuard (Latest Official)
```

### Import with Maven
-----
If you are using ItemJoin's API, you first have to import it into your project.

To import ItemJoin, simply add the following code to your pom.xml
Replace {VERSION} with the version with the current release or snapshot version.
This should look like `5.0.6-RELEASE` or `5.0.7-SNAPSHOT` as an example.
```
    <repositories>
    <!--CraftationGaming Repository-->
        <repository>
            <id>CraftationGaming</id>
            <url>https://raw.githubusercontent.com/RockinChaos/repository/maven-public/</url>
        </repository>
    </repositories>
    <dependencies>
    <!--ItemJoin API-->
        <dependency>
            <groupId>me.RockinChaos.itemjoin</groupId>
            <artifactId>ItemJoin</artifactId>
            <version>{VERSION}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

![](https://i.imgur.com/vFllc29.png)![](https://i.imgur.com/vFllc29.png)[<img src="https://i.imgur.com/WR5dVKN.png">](https://discord.gg/D5FnJ7C)[<img src="https://i.imgur.com/2YBE4mr.png">](http://ci.craftationgaming.com/)
