# GUI (`src/`)

JavaFX app entry point: `GUIApp`.

FXML layouts are under `fxml/`. Controllers and menu logic are the `.java` files in this folder.

## Requirements

- JDK **23+** (JavaFX 25 jars need a newer compiler than Java 21)
- OpenJFX SDK unzipped at the **project root** as `javafx-sdk-25/` (or `javafx-sdk-27/`)
  - Download: https://openjfx.io/

## Compile and run (from this directory)

Linux / macOS — use `:` in paths. Windows — use `;` if you add a classpath later.

```bash
javac --module-path ../javafx-sdk-25/lib --add-modules javafx.controls,javafx.fxml *.java
java --enable-native-access=javafx.graphics --module-path ../javafx-sdk-25/lib --add-modules javafx.controls,javafx.fxml GUIApp
```

Replace `javafx-sdk-25` with `javafx-sdk-27` if that is what you installed.

If the SDK sits **next to** the project (sibling of `Team-8-Project-2`) instead of inside it, use `../../javafx-sdk-25/lib` for `--module-path`.

## Troubleshooting

| Error | Fix |
|-------|-----|
| `module not found: javafx.controls` | Wrong `--module-path` (folder missing or one `..` short) |
| `class file has wrong version 67.0, should be 65.0` | `javac` is too old — point `JAVA_HOME` at JDK 23+ |
| `Could not find or load main class GUIApp` | Compile failed, or you are not in `src/` when running |

## Cashier notes

- Menu items are hardcoded in `MenuCatalog` (not loaded from the DB yet).
- Cart is in-memory for the session only.
