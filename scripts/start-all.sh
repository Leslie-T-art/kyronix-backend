#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN_DIR="$ROOT_DIR/scripts/.run"
LOG_DIR="$RUN_DIR/logs"
PID_FILE="$RUN_DIR/services.pid"
MAVEN_REPO="$ROOT_DIR/.m2"

SERVICES=(
  "audit-service"
  "auth-service"
  "olts-service"
  "notifications-service"
  "document-service"
  "dashboard-service"
  "kri-service"
  "risk-register-service"
  "process-flows-service"
  "self-assessment-service"
  "api-gateway"
)

PORTS=(
  "8083"
  "8081"
  "8082"
  "8084"
  "8085"
  "8086"
  "8087"
  "8088"
  "8089"
  "8090"
  "8080"
)

mkdir -p "$LOG_DIR" "$MAVEN_REPO"

if [[ -f "$PID_FILE" ]]; then
  echo "Existing PID file found at $PID_FILE"
  echo "Run scripts/stop-all.sh first if those services are still running."
  exit 1
fi

echo "Starting infrastructure containers..."
docker compose -f "$ROOT_DIR/infrastructure/docker-compose.yml" up -d

echo "Building shared modules into local Maven repo..."
mvn -q -Dmaven.repo.local="$MAVEN_REPO" -DskipTests install \
  -pl shared/common-api,shared/common-security,shared/common-events,shared/common-authorization,shared/common-observability,shared/common-test \
  -am \
  -f "$ROOT_DIR/pom.xml"

touch "$PID_FILE"

cleanup_partial_start() {
  if [[ -f "$PID_FILE" ]]; then
    while IFS='|' read -r service_name service_pid service_port; do
      [[ -n "${service_pid:-}" ]] || continue
      if kill -0 "$service_pid" >/dev/null 2>&1; then
        kill "$service_pid" >/dev/null 2>&1 || true
      fi
    done < "$PID_FILE"
    rm -f "$PID_FILE"
  fi
}

trap cleanup_partial_start ERR

start_service() {
  local service_name="$1"
  local service_port="$2"
  local service_dir="$ROOT_DIR/services/$service_name"
  local log_file="$LOG_DIR/$service_name.log"

  echo "Starting $service_name on port $service_port..."

  (
    cd "$service_dir"
    nohup mvn -Dmaven.repo.local="$MAVEN_REPO" spring-boot:run >"$log_file" 2>&1 &
    echo $! > "$RUN_DIR/$service_name.pid"
  )

  local service_pid
  service_pid="$(cat "$RUN_DIR/$service_name.pid")"
  printf '%s|%s|%s\n' "$service_name" "$service_pid" "$service_port" >> "$PID_FILE"

  sleep 2
  if ! kill -0 "$service_pid" >/dev/null 2>&1; then
    echo "$service_name exited immediately. Check $log_file"
    return 1
  fi
}

for i in "${!SERVICES[@]}"; do
  start_service "${SERVICES[$i]}" "${PORTS[$i]}"
done

trap - ERR

cat <<EOF
All services have been launched.

Logs: $LOG_DIR
PID file: $PID_FILE

Suggested health checks:
  curl -s http://localhost:8080/actuator/health
  curl -s http://localhost:8081/actuator/health
  curl -s http://localhost:8082/actuator/health
  curl -s http://localhost:8083/actuator/health
EOF
