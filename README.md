# 🏹 HuntersSMP

A powerful Minecraft plugin that adds a **Hunter vs Hunted** game system to your server.

---

## 🎮 What is HuntersSMP?

HuntersSMP introduces a thrilling PvP experience:

* 🎯 One player becomes the **Hunter**
* 🏃 Another player becomes the **Hunted**
* ⏱ The Hunter must kill the Hunted before time runs out

### 🏆 Win Conditions

* If the Hunter kills the Hunted → **Hunter wins**
* If time runs out → **Hunted wins**

---

## 🔑 Features

* 🔥 Automatic and manual hunts
* 🛒 Hunt Key system with GUI shop
* 📊 Player stats (wins, losses, keys)
* ⚙️ Fully configurable
* 💬 Custom messages

---

## 📜 Commands

### 👤 Player Commands

* `/hunts shop` → Open Hunt Shop

### 🛠 Admin Commands

* `/hunts start` → Start a hunt
* `/hunts stop` → Stop the hunt
* `/hunts reload` → Reload config
* `/hunts help` → Show help menu
* `/hunts key add <player> <amount>`
* `/hunts key remove <player> <amount>`

---

## 🔐 Permissions

* `hunters.admin` → Access all admin commands
* `hunters.shop` → Access shop (default: everyone)

---

## ⚙️ Configuration

### `config.yml`

```yaml
hunt-duration: 600
auto-hunt: true
auto-hunt-interval: 1800
```

---

## 💬 Messages

```yaml
messages:
  no-permission: "&cYou don’t have permission!"
  hunt-started: "&aA new hunt has begun!"
  hunt-stopped: "&cThe hunt has ended!"
  player-not-found: "&cThat player is not online!"
  keys-added: "&aYou gave %player% %amount% Hunt Keys."
  keys-removed: "&cYou removed %amount% Hunt Keys from %player%."
```

---

## 💾 Data Storage

```yaml
players:
  uuid1:
    wins: 3
    losses: 2
    keys: 5
  uuid2:
    wins: 1
    losses: 4
    keys: 2
```

---

## 🚀 Installation

1. Download the plugin `.jar`
2. Put it in your `/plugins` folder
3. Restart your server (PaperMC recommended)

---

## ⭐ Support the Project

If you like this plugin:

* ⭐ Star the repo
* 🐛 Report bugs
* 💡 Suggest features

---

## 📌 Author

Made by **Hamza** ❤️
