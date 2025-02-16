# armor-api

A simple, version-independent API for the Bukkit/Spigot (and forked) Minecraft server software.

This API was developed due to a lack of armor-related events in the Bukkit/Spigot API.

Note: I am currently working on a replacement for this API that provides more control of different aspects of armor-related content.

## Features

The primary benefit from this API is the handling of armor-related behaviour and the events it provides:
* ArmorEquipEvent - this event is called whenever a player equips a piece of armor.
* ArmorRemoveEvent - this event is called whenever a player removes a piece of armor.

The API handles all version-specific behaviour, such as the newer interactable swap from Minecraft versions 1.19.4 and above.

## Installation

As a server owner, all you need to do is include this plugin in your *plugins* folder.

## Usage

For developers, you can use this API by linking it to your project.

As the file is currently not hosted online, you will have to use a local repository or path.

### Maven (Example)

```xml
<dependency>
    <groupId>net.hmsvr.bukkit</groupId>
    <artifactId>armor-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/ArmorAPI 1.0.0-SNAPSHOT.jar</systemPath>
</dependency>
```

### Gradle (Example)

```groovy
dependencies {
    implementation files('libs/ArmorAPI 1.0.0-SNAPSHOT.jar')
}
```

Hereafter, you will be able to reference the API.

Remember to add the API as a dependency in your *plugin.yml* file using the **depend** field.

```yaml
depend: [ArmorAPI]
```

### Blocking certain items from being equipped

Using this API, blocking certain items from being equipped is as simple as cancelling the ArmorEquipEvent:

```java
public class ExamplePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onEquip(ArmorEquipEvent event) {
        if (event.getItem().getType() != Material.DIAMOND_HELMET) return;
        event.setCancelled(true); // Disallow diamond helmets from being equipped
    }
}
```

Besides the equipped item, the event also contains information about the armor slot and the cause (e.g., a dispenser or a shift-click in the inventory).
