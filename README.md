# SignGuiAPI

A lightweight, high-performance, and optimized API for creating Sign GUIs on Minecraft servers. This API has been
extensively tested and is **100% functional** across all versions (from 1.8.8 up to 1.21+) as long as **ViaVersion** is
present and the prerequisites are met.

## ⚠️ Prerequisites & Compatibility

* **Java 21**: This project requires Java 21 or higher.
* **ProtocolLib 4.8.0+**: Essential for stable packet handling.
* **Legacy Support**: This version specifically supports **Legacy Signs**.
* ⚠️ **Note**: Only the **Front** side of the sign is supported. There is no back-side (Back-only) editing support in
  this legacy-focused implementation.

* ⚠️ **Plugin YAML**: **You MUST add** `api-version: '1.13'` (or higher) to your `plugin.yml` to ensure compatibility
  with modern Material systems.
* **Cross-Version**: Fully tested and working on all versions provided you use **ViaVersion** for protocol translation.

---

## Features

* ✅ **Modern Multi-Threaded Support**: Built to be safe on Folia, Paper, and Spigot.
* ✅ **Unicode & Color Fix**: Automatically handles JSON escaping to prevent broken symbols like `\u0026`.
* ✅ **Global Color Control**: Toggle `SignGUI.STRIP_COLOR` globally for your project.
* ✅ **Built-in Templates**: Rapidly implement Renames, Confirmations, and Amount inputs.
* ✅ **Clean Cleanup**: Automatic `BLOCK_CHANGE` (Air) packets to remove virtual signs immediately after use.

---

## Usage

### 1. Initialization

You must initialize the API in your plugin's `onEnable()`:

```java

@Override
public void onEnable() {
    SignGUI.init(this);

    // Global toggle for stripping color codes from player input
    SignGUI.STRIP_COLOR = true;
}

```

### 2. Basic Manual GUI

Using the fluent builder to create a custom sign:

```java
SignGUI.builder()
    .

setLines("","^^^^^^^^^^^^^","Enter Name","Above")
    .

type(Material.SIGN_POST) // Recommended for maximum compatibility
    .

onInput((player, lines) ->{
String input = lines[0];
        player.

sendMessage("You entered: "+input);
    })
            .

build()
    .open(player);

```

### 3. Rapid Templates

The `SignTemplates` class provides pre-configured logic for common tasks:

#### **Rename Template**

```java
SignTemplates.requestRename(player, "OldName",(p, newName) ->{
        p.

sendMessage("Name updated to: "+newName);
});

```

#### **Confirmation Template**

```java
SignTemplates.requestConfirmation(player, "Deleting Data",() ->{
        player.

sendMessage("Action confirmed!");
});

```

#### **Amount/Number Input**

```java
SignTemplates.requestAmount(player, "Diamonds",(p, amount) ->{
        p.

sendMessage("Selected: "+amount);
});

```

---

## Technical Note

This API intercepts `PacketType.Play.Client.UPDATE_SIGN` and uses `WrappedChatComponent` to ensure that player input is
correctly sanitized and returned as clean strings, even on older 1.8.8 protocols.