#!/bin/sh
# Attend qu'un hote:port reponde (utilise en entree de conteneur si besoin)
# usage: ./healthcheck.sh mysql 3306 60
set -e
HOST="$1"; PORT="$2"; TIMEOUT="${3:-60}"
i=0
while [ "$i" -lt "$TIMEOUT" ]; do
  if nc -z "$HOST" "$PORT" 2>/dev/null; then
    echo "[OK] $HOST:$PORT joignable apres ${i}s"
    exit 0
  fi
  i=$((i+1))
  sleep 1
done
echo "[KO] $HOST:$PORT injoignable apres ${TIMEOUT}s" >&2
exit 1
