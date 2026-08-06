#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_DIR="$ROOT_DIR/scripts/.run"
PID_FILE="$RUN_DIR/services.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "No PID file found at $PID_FILE"
  echo "Stopping infrastructure containers anyway..."
  docker compose -f "$ROOT_DIR/infrastructure/docker-compose.yml" down
  exit 0
fi

echo "Stopping Spring services..."
while IFS='|' read -r service_name service_pid service_port; do
  [[ -n "${service_pid:-}" ]] || continue
  if kill -0 "$service_pid" >/dev/null 2>&1; then
    echo "Stopping $service_name ($service_pid)..."
    kill "$service_pid" >/dev/null 2>&1 || true
  else
    echo "$service_name ($service_pid) is not running."
  fi
  rm -f "$RUN_DIR/$service_name.pid"
done < "$PID_FILE"

rm -f "$PID_FILE"

echo "Stopping infrastructure containers..."
docker compose -f "$ROOT_DIR/infrastructure/docker-compose.yml" down

echo "All tracked services stopped."
