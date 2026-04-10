#!/bin/bash

if [ -z "$1" ]; then
  echo "Error: enter with migration name."
  echo "ex.: ./gen_migration.sh new_migration"
  exit 1
fi

NAME=$(echo "$1" | sed 's/ /_/g')
TIMESTAMP=$(date +"%Y%m%d%H%M")
FILENAME="V${TIMESTAMP}__${NAME}.sql"
DIR="src/main/resources/db/migration"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DIR_EXECUTION="${SCRIPT_DIR}/../src/main/resources/db/migration"

if [ ! -d "$DIR_EXECUTION" ]; then
  echo "Error: migration directory does not exist:"
  echo "$DIR_EXECUTION"
  exit 2
fi

touch "$DIR_EXECUTION/$FILENAME"

echo "-----------------------------------------"
echo "Migration generated: $DIR/$FILENAME"
echo "-----------------------------------------"
