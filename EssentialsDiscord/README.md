# EssentialsXDiscord

EssentialsXDiscord is an EssentialX module that brings a simple, lightweight, easy-to-use, and bloat-free
Discord to Minecraft bridge.

EssentialsXDiscord offers *essential* features you'd want from a Discord bridge such as;
* MC Chat -> Discord Channel
* Discord Channel -> MC Chat
* Basic MC -> Discord Event Monitoring (Join/Leave/Death/Mute)
* MC Console -> Discord Relay
* Discord Slash Commands 
  * /execute - Execute console commands from discord
  * /msg - Message minecraft players from discord
  * /list - Same as /list from EssentialsX
* & more...

---

## Table of Contents
> * [Initial Setup](#initial-setup)
> * [Bottom Text]()

---

## Initial Setup

0. Before starting your server, there are a few steps you have to take. First, you must create a new
discord bot at [discord.com/developers/applications](https://discord.com/developers/applications/).

1. Once on that page, click on "New Application" button on the top right, give your bot a name, and
then click "Create".
> ![Creating Application](https://i.imgur.com/8ffp4R1.gif)
> `New Application` -> Give Application a Name -> `Create`

2. Once you create the application, you'll be directed to its overview. From this screen, you'll
need to copy your "Client ID" and save it for a later step. To copy your client id, click the
upper-left most blue "Copy" button.
> ![Copy Client ID](https://i.imgur.com/W3OMTu5.gif)
> `Copy` -> Paste into Notepad for later step

3. Optionally, you can set an icon for your application as it will be the icon for the bot too.
> ![Avatar](https://i.imgur.com/NuFS9kT.png)

4. The next step is actually creating a bot user for your application. From the overview screen,
this is done by going to the "Bot" tab on the left, then clicking the "Add Bot" on the right,
and finally then clicking "Yes, do it!".
> ![Create Bot](https://i.imgur.com/S14iAFS.gif)
> `Bot` -> `Add Bot` -> `Yes, do it!`

5. Once on this screen, you'll need to uncheck the "Public Bot" setting and then click "Save Changes",
so other people can't add your bot to servers that are not your own.
> ![Disable Public Bot](https://i.imgur.com/HHqWvQ1.gif)
> Uncheck `Public Bot` -> `Save Changes`

6. Finally, you'll need to copy your bot's token and save it for a later step. To cop your bot's token,
click the blue "Copy" button right of your bot's icon.
> ![Copy Token](https://i.imgur.com/OqpaSQH.gif)
> `Copy` -> Paste into Notepad for later step

7. You can now leave this website!

8. Next up is adding your bot to your discord server. Copy the link bellow while replacing the
`<CLIENT_ID>` part with the client id you saved from earlier.
> `https://discord.com/api/oauth2/authorize?client_id=<CLIENT_ID>&permissions=0&scope=bot%20applications.commands`

> For example, if my client id was `800593184077250570`, my url would look like;
> `https://discord.com/api/oauth2/authorize?client_id=800593184077250570&permissions=0&scope=bot%20applications.commands`

9. When you go to that link, select the server you want to add the bot to from the dropdown and then
click "Authorize".

10. Once the bot is on your server, you'll need to copy some more ids to save for later. To start
copying ids, you'll need to enable Developer Mode on Discord. To enable developer mode, goto your
Discord settings, click the "Appearance" tab, scroll to the bottom, and check the "Developer Mode"
toggle on.

11. Once you enable Developer Mode, it's time to copy some ids. First, right click on the server
you added your bot to and press "Copy ID" and save it for later (This is called your guild id).

12. Now, put the EssentialsXDiscord jar file in your server's plugins folder, start your server and then
stop it. This will generate EssentialsXDiscord's default config of which you can start to set up now.

13. Open EssentialXDiscord's config located at `plugins/EssentialsDiscord/config.yml`

14. Replace the `INSERT-TOKEN-HERE` in the config with the token you copied earlier from step 6.

15. Replace the `000000000000000000` next to `guild: ` with the guild id you copied from step 11.

