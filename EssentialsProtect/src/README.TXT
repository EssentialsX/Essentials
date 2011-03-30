EssentialsProtect:

REQUIRED : Essentials.jar. Also sqlite.jar, mysql.jar in the bukkit lib folder.

Config Settings in plugins/Essentials/config.yml

protect:
  datatype: 'sqlite' type of db, options sqlite or mysql
  username: 'root'  mysql username
  pasword: 'root' mysql password
  mysqlDb: 'jdbc:mysql://localhost:3306/minecraft'  mysql database location
  protectSigns: true
  protectRails: true
  protectBlockBelow: true
  preventBlockOnRail: false prevents block placement on protected rails

On startup creates a sqlite database under /plugins/essentials called EssentialsProtect.db

Permissions:

"essentials.protect" - allows protection
"essentials.protect.admin" can delete protected blocks / query protection by right clicking a protected item.

Usage:

Place a sign or rail and they (plus optionally the block below) will be protected. Only the owners and those with admin permissions can destroy.




