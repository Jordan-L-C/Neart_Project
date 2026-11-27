# Accessibility in Neart

This document outlines the accessibility features currently in the Neart app, with code examples showing how each one is implemented.

---

## 1. Labelled Navigation Icons

Each icon in the navigation bar has a `contentDescription`, so screen readers such as TalkBack can announce what it does. Each item also has a visible text label, so the icons are never the only clue.

**`MainActivity.kt`**
```kotlin
item(
    icon = { Icon(painterResource(R.drawable.ic_history), contentDescription = "History") },
    label = { Text("History") },
    selected = currentDestination == AppDestinations.HISTORY,
    onClick = { currentDestination = AppDestinations.HISTORY }
)
```

---

## 2. Labelled Form Fields

Every input on the Log screen has a label. TalkBack reads the label aloud when the field is focused, so users know what each field is for.

**`Log.kt`**
```kotlin
OutlinedTextField(
    value = weight,
    label = { Text("Weight (kg)") },
    ...
)
```

The reusable `DropdownField` (used for Exercise, Sets and Reps) passes its label through in the same way:
```kotlin
OutlinedTextField(
    value = selected,
    readOnly = true,
    label = { Text(label) },
    ...
)
```

---

## 3. Errors Not Shown by Colour Alone

When the weight is invalid, the field turns red **and** a written message appears underneath. This means users who are colour blind, or using a screen reader, still know what went wrong.

**`Log.kt`**
```kotlin
isError = showError,                                                   // turns the field red
supportingText = { if (showError) Text("Enter a weight above 0") },    // written explanation
```

---

## 4. Readable Colour Contrast (Material Theme Builder)

The app's colours were generated with Material Theme Builder. Every colour comes in a pair: a background colour and an "on" colour for text and icons drawn on top of it. These pairs are designed to have readable contrast.

**`ui/theme/Theme.kt`**
```kotlin
private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,   // text/icons on primary are always readable
    ...
)
```

The app's own code uses these pairs together, for example the PB badge in **`History.kt`**:
```kotlin
Surface(color = MaterialTheme.colorScheme.primary) {
    Text("PB", color = MaterialTheme.colorScheme.onPrimary)
}
```

---

## 5. Text That Scales With the User's Font Size

All text uses Material typography styles, which are measured in `sp` (scale-independent pixels). If the user increases the font size in their phone settings, the text in Neart grows with it.

**Example from `History.kt`**
```kotlin
Text(lift.exercise, style = MaterialTheme.typography.titleMedium)
```

The Log screen form also scrolls, so larger text doesn't get cut off on small screens.

**`Log.kt`**
```kotlin
Column(
    modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())   // lets the form scroll on small screens
        ...
```

---

## 6. Minimal Typing

Most of the form uses dropdowns and chips instead of typing, which reduces effort and avoids spelling mistakes. The only typed field (weight) opens a number keyboard.

**`Log.kt`**
```kotlin
// Muscle group picked with chips instead of typing
FilterChip(
    selected = muscleGroup == group,
    onClick = { muscleGroup = group },
    label = { Text(group) }
)

// Weight field shows a number keyboard
keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
```

---

## 7. Clear Empty States

When there is no data yet, the screens explain what to do instead of showing a blank screen.

**`History.kt`**
```kotlin
if (lifts.isEmpty()) {
    Text("No lifts yet. Tap + to log your first lift.")
}
```

**`Stats.kt`**
```kotlin
if (personalBests.isEmpty()) {
    Text("Log a lift to see your personal bests.")
}
```
