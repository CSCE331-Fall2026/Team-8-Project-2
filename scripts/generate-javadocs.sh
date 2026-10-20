#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
javafx_home="${JAVAFX_HOME:-}"
output_directory="${1:-docs/javadocs}"

if [[ -z "$javafx_home" ]]; then
  for candidate in "$project_root/javafx-sdk-25" "$project_root/javafx-sdk-27"; do
    if [[ -d "$candidate/lib" ]]; then
      javafx_home="$candidate"
      break
    fi
  done
fi

if [[ -z "$javafx_home" || ! -d "$javafx_home/lib" ]]; then
  echo "JavaFX SDK not found. Install it in the project root or set JAVAFX_HOME to its directory." >&2
  exit 1
fi

mapfile -t source_files < <(find "$project_root/src" -type f -name '*.java' ! -name 'dbSetup.java' -print)
if [[ "${#source_files[@]}" -eq 0 ]]; then
  echo "No Java source files were found under src." >&2
  exit 1
fi

output_path="$project_root/$output_directory"
rm -rf "$output_path"
mkdir -p "$output_path"
javadoc -private -Xdoclint:all,-missing -d "$output_path" \
  --module-path "$javafx_home/lib" \
  --add-modules javafx.controls,javafx.fxml \
  "${source_files[@]}"

echo "Javadocs written to $output_path"
