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
upper-left most blue "Copy" button. Make sure to save it for a later step.
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

6. Finally, you'll need to copy your bot's token and save it for a later step. To copy your bot's token,
click the blue "Copy" button right of your bot's icon. Make sure to save it for a later step.
> ![Copy Token](https://i.imgur.com/OqpaSQH.gif)
> `Copy` -> Paste into Notepad for later step
   
9. Next up is adding your bot to your discord server. First, goto [essentialsx.net/discord.html](https://essentialsx.net/discord.html)
and paste your Client ID you copied from step 2 into the text box on that page. Once you do that, click
the "Authorize" button next to the text box. This will redirect you to Discord's authorization website 
to chose which server to add the bot to.
> ![OAuth Link Gen](https://i.imgur.com/u6MFJgQ.gif)
> Paste Client ID -> `Authorize`

10. Once on the Discord authorization website, select the server from the "Select a server" dropdown 
that you want to add the bot to. Then click the "Authorize" button. You may be prompted to confirm
you are not a bot, proceed with that like you would any other captcha.
> ![Authorize](https://i.imgur.com/KXkESqC.gif)
> Select Server -> `Authorize`

11. For the next few steps, you're going to need to do some stuff in discord, so start up your
Discord desktop/web client. 

12. Once in your Discord client, you'll need to enable developer mode. Do this by going into the 
Settings, then go to the "Appearance" tab and check on the "Developer Mode" at the bottom of the
page. Once you've checked "Developer Mode" on, click the `X` at the top right to exit Settings.
> ![Developer Mode](https://i.imgur.com/CrW31Up.gif)
> `User Settings` -> `Appearance` -> Check `Developer Mode` -> Exit Settings

13. Next is copying a few IDs. First up, you'll want to copy the server (aka guild) id. Do this by
finding the server you added the bot to, right click its icon, and click "Copy ID". Once you copied
it, make sure to save it for a later step.
> ![Guild ID](https://i.imgur.com/0mg2yT3.gif)
> Right click server -> `Copy ID` -> Paste into Notepad for later step

14. The other ID you need to copy is the ID of the channel you wish to be your primary channel.
In other words, this will be the channel that, by default, receives messages for player chat/join/leave/death
messages as well as mute/kicks. To see how to further configure message types, see [TODO Configuring Messages](#).
> ![Primary Channel ID](https://i.imgur.com/uMODfiQ.gif)
> Right click your 'primary' channel -> `Copy ID` -> Paste into Notepad for later step

15. You've successfully copied all the necessary IDs needed for a basic setup. Next up is generating the
default config for EssentialsXDiscord, so you can start setting it up! Do this by putting the
EssentialsXDiscord jar (you can download it [here](https://essentialsx.net/downloads.html) if you do not
already have one) in your plugins folder, starting your server, and then stopping it as soon as it finishes
starting up.
> ![Start/Stop Server](https://i.imgur.com/JQX6hqM.gif)
> Drag EssentialsXDiscord jar into plugins folder -> Start Server -> Stop Server

16. Now you can start to configure the plugin with all the stuff you copied from earlier. Open the config
for EssentialsXDiscord located at `plugins/EssentialsDiscord/config.yml`. When you open the config, the
first thing to configure is your bot's token. Replace `INSERT-TOKEN-HERE` in the config with the token you
copied earlier from step 6.
> ![Paste Token](https://i.imgur.com/EnD31Wg.gif)
> Re-Copy Token from Step 6 -> Paste as token value 

17. Next, is the guild id, replace the zeros for the guild value in the config with the guild id you copied
from step 13.
> ![Paste Guild](https://i.imgur.com/YxkHykd.gif)

18. Finally, you'll need to paste the primary channel id you copied from step 14 and paste it as the
primary value in the channels section and once you've done that save the config file!
> ![Paste Primary](https://i.imgur.com/4xaHMfO.gif)

19. Congratulations, you've completed the initial setup guide! When you start up your server, you should
notice that chat and other messages start showing up in the channel you requested they be. Now that you
completed the initial, go back up to the [Table Of Contents](#table-of-contents) to see what other cool things you can do!

