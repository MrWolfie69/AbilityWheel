# ✦ AbilityWheel — PaperMC Plugin for Aternos

## 🚀 Aternos pe Install kaise karein (Step by Step)

### Step 1: JAR Build karo (GitHub se — FREE)

1. **GitHub account banao**: https://github.com/signup (free hai)
2. **Naya repository banao**: `New Repository` → naam: `AbilityWheel` → Public → Create
3. **Ye ZIP extract karo** aur saari files upload karo GitHub repo mein
4. **Actions tab** pe jao → Build automatically start ho jaayega
5. Build complete hone ke baad → **Actions → Latest run → Artifacts → AbilityWheel-Plugin** → Download karo ✅

### Step 2: Aternos Setup

1. Aternos pe login karo: https://aternos.org
2. **New Server** banao → **PaperMC** select karo → Version: **1.21.5**
3. **Plugins** tab pe jao → **Upload** button → `AbilityWheel-1.0.0.jar` upload karo
4. Server **Start** karo
5. Console mein dikhe: `Ability Wheel enabled!` → ✅ Done!

### Step 3: OP bano (Admin ke liye)
Console mein type karo:
```
op TumharaUsername
```

---

## 🎮 Commands

```
/ability              → Apni current ability dekho
/ability wheel        → Wheel kholo (first time free)
/ability respin       → 5 Netherite Blocks deke ability change karo
/ability info         → Sabhi abilities ki list

/ability admin set <player> <ability>    → Admin: kisi ki ability set karo
/ability admin remove <player>           → Admin: kisi ki ability hatao
/ability admin respin <player>           → Admin: free wheel open karo
/ability admin list                      → Admin: sabhi players ki abilities dekho
```

## ⚡ Ability IDs
```
strength, regeneration, defence, speed, jump_boost,
haste, night_vision, fire_resistance, water_breathing,
luck, absorption, saturation
```

## 📁 Data Save Location
```
plugins/AbilityWheel/players/<uuid>.yml
```
