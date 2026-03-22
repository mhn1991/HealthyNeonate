# HealthyNeonate

## Project Overview

HealthyNeonate is a single-module Android application that provides offline-accessible neonatal clinical reference content through two main content areas:

- `NeoFax`: neonatal drug reference content
- `DPAL`: pregnancy and lactation / drug safety reference content

A third entry point, `Icter`, exists in navigation but is currently only a placeholder screen.

This repository is not a trading or backtesting system. It is a legacy Java/Android reference application whose primary workflow is: acquire SQLite content databases, present searchable/indexed lists, render record details, and allow users to save favourites.

## Architecture

The application is organised around Android activities rather than a layered domain architecture.

### Main Components

- **`MainActivity`**
  - Landing screen
  - Routes the user to `NeoFax`, `DPAL`, or `Icter`

- **`NeoFax`**
  - Owns the NeoFax module workflow
  - Checks for a local SQLite file (`db1.sqlite`)
  - Downloads the database if missing
  - Fetches a remote checksum and prompts for updates if the local copy is outdated
  - Loads drug titles from the `info` table
  - Builds search/autocomplete and an indexed list view
  - Opens `ShowDrugs` for detail display
  - Supports favourites and feedback submission

- **`DPAL`**
  - Owns the DPAL module workflow
  - Checks for a local SQLite file (`db2.sqlite`)
  - Downloads the database if missing
  - Fetches a remote checksum and prompts for updates
  - Loads titles from the `book` table
  - Parses each record into a `Drug` model for list presentation
  - Builds search/autocomplete and an indexed list view
  - Opens `ShowDrugs` for detail display
  - Supports favourites and feedback submission

- **`ShowDrugs`**
  - Detail screen for a selected item
  - Receives the selected item name and source module via intent extras
  - Opens the correct SQLite database
  - Queries matching rows and renders record fields dynamically into the screen
  - Manages favourite state through `SharedPreferences`

- **`ShowFavourite`**
  - Rebuilds a favourites list by:
    1. reading all titles from the relevant SQLite database
    2. checking `SharedPreferences` for starred items
    3. rendering matching items as clickable entries

- **`Drug`**
  - Simple data holder used by the DPAL list view

- **`DrugAdapter`**
  - Custom adapter for DPAL list rows
  - Formats title, subtitle, and recommendation text
  - Implements `SectionIndexer` for alphabetical side navigation

- **`Icter`**
  - Present in navigation and resources
  - Currently implemented as a placeholder activity only

### Architectural Characteristics

- Data access, networking, parsing, and UI rendering are largely handled directly inside activities.
- SQLite is accessed through raw queries; there is no repository/DAO abstraction.
- Database provisioning and update checks are implemented with `AsyncTask`.
- Favourites are persisted with `SharedPreferences`.
- The app is functionally modular by screen, but not strongly separated by layers.

## System Flow

### 1. Application Entry

- The app launches into `MainActivity`.
- The user chooses one of the available modules.

### 2. Module Initialization

For both `NeoFax` and `DPAL`:

- The activity checks whether its expected local SQLite database exists in app-internal storage.
- If the file is missing:
  - network availability is checked
  - the database is downloaded from a hard-coded remote URL
- If the file exists:
  - the database is loaded immediately
  - a checksum request is made in the background to determine whether an update is available

### 3. Content Loading

- `NeoFax` reads titles from the `info` table in `db1.sqlite`.
- `DPAL` reads titles from the `book` table in `db2.sqlite`.
- `DPAL` also parses each row into a `Drug` object for richer list rendering.

### 4. Search and Navigation

- Each module populates an `AutoCompleteTextView` for quick search.
- Each module builds a scrollable list with alphabetical side indexing.
- Selecting an item opens `ShowDrugs`.

### 5. Detail Rendering

`ShowDrugs`:

- determines the source module from the intent extra
- opens the corresponding SQLite database
- queries the record matching the selected title
- iterates through columns and renders non-empty fields dynamically as `TextView` blocks

### 6. Favourites

- Users can toggle a star icon on the detail screen.
- The selection is stored in `SharedPreferences`.
- `ShowFavourite` reconstructs the favourites view by scanning database titles and filtering for starred entries.

### 7. Feedback

- `NeoFax` and `DPAL` expose a feedback dialog.
- Feedback is posted to a remote PHP endpoint.

## Project Structure

```text
.
├── app/
│   ├── src/main/java/ir/pudica/test/
│   │   ├── MainActivity.java       # Home screen / module launcher
│   │   ├── NeoFax.java            # NeoFax module controller
│   │   ├── DPAL.java              # DPAL module controller
│   │   ├── ShowDrugs.java         # Detail renderer
│   │   ├── ShowFavourite.java     # Favourite listing
│   │   ├── Drug.java              # DPAL data model
│   │   ├── DrugAdapter.java       # DPAL list adapter / section indexer
│   │   └── Icter.java             # Placeholder screen
│   ├── src/main/res/              # Layouts, menus, drawables, strings
│   └── build.gradle               # Android app module configuration
├── gradle/                        # Gradle wrapper
├── build.gradle                   # Top-level Gradle configuration
├── settings.gradle                # Single-module project setup
└── neofax.sqlite                  # SQLite database artifact present in repo root
```

## Technologies Used

- **Java**
- **Android SDK**
- **Android support libraries**
  - `appcompat-v7`
  - `support-v4`
  - `design`
  - legacy support `constraint-layout`
- **SQLite**
  - direct access through `SQLiteDatabase` and raw SQL
- **SharedPreferences**
  - favourite state persistence
- **AsyncTask**
  - background download, checksum, and feedback operations
- **Gradle / Android Gradle Plugin**
  - currently builds on Gradle `7.5` and AGP `7.4.2`

## Design Decisions

- **Single-module app**
  - Appropriate for a relatively small, screen-driven application.

- **Activity-centric implementation**
  - Keeps behaviour close to the UI, but couples view logic, networking, and data access tightly.

- **SQLite as the content store**
  - Sensible for offline reference data and fast lookup on-device.

- **Remote database download plus checksum comparison**
  - Avoids bundling all content updates into app releases.
  - Introduces operational dependence on external endpoints.

- **Dynamic field rendering in `ShowDrugs`**
  - Allows record content to vary by database row structure without building fixed forms.

- **`SharedPreferences` for favourites**
  - Lightweight and sufficient for per-item star state.

## Current Status

### Implemented

- Home screen and module navigation
- NeoFax content loading and browsing
- DPAL content loading and browsing
- Search/autocomplete in both main content modules
- Alphabetical side index navigation
- Detail rendering from SQLite records
- Favourites persistence and display
- Feedback submission UI and remote post
- Database download and update-check flow
- Build upgraded to a working AGP `7.4.2` / Gradle `7.5` toolchain

### Incomplete or Weak Areas

- `Icter` is not implemented beyond a placeholder activity.
- The app still uses legacy Android support libraries rather than AndroidX.
- Networking uses hard-coded URLs and legacy APIs.
- There is no abstraction layer for data access or networking.
- Error handling is basic and largely UI-driven.
- Test coverage is effectively absent; only template unit/instrumentation tests exist.
- Runtime behaviour depends on external endpoints remaining available.

## Future Improvements

- Migrate from support libraries to **AndroidX**
- Raise `compileSdkVersion` / `targetSdkVersion` and modernise the platform baseline
- Extract data access and network logic out of activities into dedicated services/repositories
- Replace `AsyncTask` with a modern alternative such as coroutines, executors, or WorkManager
- Replace raw `URLConnection` usage with a maintained HTTP client
- Introduce a proper local data layer, ideally with schema management
- Improve resilience around database download/update failures
- Add meaningful automated tests around:
  - database parsing
  - favourites behaviour
  - module navigation
  - detail rendering
- Complete or remove the `Icter` module to align navigation with delivered functionality