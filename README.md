What is HuntersSMP?
HuntersSMP is a Minecraft plugin that adds a Hunter vs. Hunted system:

A Hunter is chosen to chase another player (the Hunted).

If the Hunter kills the Hunted before time runs out → Hunter wins.

If the timer runs out → Hunted wins.

Players earn Hunt Keys and can spend them in a shop GUI.
Player Command
/hunts shop → Open the Hunt Shop to spend Hunt Keys.
Admin Commands
/hunts start → Start a hunt.

/hunts stop → Stop the current hunt.

/hunts reload → Reload the plugin settings.

/hunts help → Show admin help menu.

/hunts key add <player> <amount> → Give Hunt Keys to a player.

/hunts key remove <player> <amount> → Take Hunt Keys from a player.
Permissions
hunters.admin → Use all admin commands.

hunters.shop → Open the shop (default for everyone).
Config Options (config.yml)
hunt-duration: 600 # Hunt duration in seconds (default: 10 minutes)
auto-hunt: true # Enable automatic hunts
auto-hunt-interval: 1800 # Time between automatic hunts in seconds

Messages (inside config.yml)
messages:
no-permission: "&cYou don’t have permission!"
hunt-started: "&aA new hunt has begun!"
hunt-stopped: "&cThe hunt has ended!"
player-not-found: "&cThat player is not online!"
keys-added: "&aYou gave %player% %amount% Hunt Keys."
keys-removed: "&cYou removed %amount% Hunt Keys from %player%."

Data Storage (data.yml)
players:
uuid1:
wins: 3
losses: 2
keys: 5
uuid2:
wins: 1
losses: 4
keys: 2
