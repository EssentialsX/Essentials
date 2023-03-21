# EssentialsX Discord Link

EssentialsX Discord Link is an addon for EssentialsX Discord which provides numerous features related to
group/role synchronization.

EssentialsX Discord Link offers features such as:
* Vault Group -> Discord Role Synchronization
* Discord Role -> Vault Group Synchronization
* Prevent unlinked players from joining
* Prevent unlinked players from moving/chatting
* & more...

---

## Table of Contents
> * [Setting Up Role Sync](#setting-up-role-sync)
> * [Linking an Account](#linking-an-account)
> * [Developer API](#developer-api)

---

## Setting Up Role Sync

In EssentialsX Discord Link, you can define a synchronizations for both Vault groups -> Discord roles and for
Discord roles -> Vault groups.

The following tutorial (as an example) will show how to give players with the Discord role `Patreon` the `donator`
Vault group and how to give players with the `vip` Vault group the `VIP` Discord role.

0. First, head to your server's role page in order to get their IDs.

1. For both the `Patreon` and `VIP` role, right click them and click on "Copy ID".
> ![Copy Role ID](https://i.imgur.com/YS9P2ej.gif)
> Right Click on Role(s) -> `Copy ID` -> Paste into Notepad for later step

2. Now that you have the IDs you need from Discord, you can begin configuring the plugin. First place the
EssentialsX Discord Link jar (you can download it [here](https://essentialsx.net/downloads.html) if you do not
already have it) in your plugins folder and then start your server.
> ![Start Server](https://i.imgur.com/64IwqoO.gif)
> Drag EssentialsXDiscordLink jar into plugins folder -> Start Server

3. Once the server started, open the config for EssentialsX Discord Link at
`plugins/EssentialsDiscordLink/config.yml`. Once opened, put `group-name: role-id` in the `groups` section
to create a Vault group -> Discord role synchronization (`vip: 882835722640433242` for this example); Then put
`role-id: group-name` in the `roles` section to create a Discord role -> Vault group synchronization 
(`882835662280224818: donator` for this example). When done, save the file.
> ![Paste Synchronizations](https://i.imgur.com/JYZHzW0.gif)
> Paste Vault->Discord syncs in the group section & Discord->Vault syncs in the roles section

5. Finally, once the file is saved, run `ess reload` from your console and then linked accounts should now have
their groups/roles linked between Minecraft/Discord! Now that you completed the basics of group/role syncing,
go back up to the [Table of Contents](#table-of-contents) to see what else you can do!

---

## Linking an Account

0. This assumes the server has started and you have joined the server.

1. Once on the server, run `/link` in Minecraft and take note of the code if gives you.
> ![Run /link](https://i.imgur.com/1EdqdOa.gif)
> Run `/link` in Minecraft

2. Next, all you have to do is run the `/link` command in discord with the code provided.
> ![Run /link in Discord](https://i.imgur.com/yXkvMDX.gif)
> Run `/link` with the code in Discord

3. That's it! Now that you've learned how to link an account, go back up to the
[Table of Contents](#table-of-contents) to see what else you can do!

---

## Developer API

EssentialsX Discord Link has a simple API to provide very simple methods to check if players are linked,
link players, unlink players, and to get linked player data.

Outside the specific examples below, you can also view javadocs for EssentialsX Discord Link
[here](https://jd-v2.essentialsx.net/EssentialsDiscordLink).

### Get a linked player's Discord tag

The following example shows how to get a linked player's Discord tag (in `Name#0000` format) or null if the player
isn't linked.

```java
public String getDiscordTag(final Player player) {
    // Gets the API service for EssentialsX Discord Link
    final DiscordLinkService linkApi = Bukkit.getServicesManager().load(DiscordLinkService.class);
    
    final String discordId = linkApi.getDiscordId(player.getUniqueId());
    if (discordId == null) {
        return null;
    }
    
    // Gets the API service for EssentialsX Discord which we will use to get the actual user
    final DiscordService discordApi = Bukkit.getServicesManager().load(DiscordService.class);
    
    final InteractionMember member = discordApi.getMemberById(discordId).join();
    return member == null ? null : member.getTag();
}
```