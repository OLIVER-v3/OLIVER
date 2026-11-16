#!/data/data/com.termux/files/usr/bin/bash
# OLIVER activation via Termux (Wireless debugging, no PC needed).
# Usage:
#   bash oliver-activate.sh pair <PAIR_PORT> <PAIR_CODE>   (first time only)
#   bash oliver-activate.sh start <CONNECT_PORT>           (every time)
set -e
PKG=com.jerry.oliver

command -v adb >/dev/null || { pkg update -y && pkg install -y android-tools; }

case "$1" in
  pair)
    adb pair "localhost:$2" "$3"
    ;;
  start)
    adb connect "localhost:$2"
    APK=$(adb -s "localhost:$2" shell pm path $PKG | head -1 | cut -d: -f2 | tr -d '\r')
    [ -n "$APK" ] || { echo "OLIVER is not installed"; exit 1; }
    adb -s "localhost:$2" shell "pkill -f com.jerry.oliver.server.Main" || true
    adb -s "localhost:$2" shell "CLASSPATH=$APK nohup app_process / com.jerry.oliver.server.Main >/data/local/tmp/oliver.log 2>&1 &"
    echo "OLIVER server started"
    ;;
  *)
    echo "usage: $0 pair <port> <code> | start <port>"; exit 1
    ;;
esac
