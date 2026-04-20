#!/usr/bin/env python3
"""
executor.py -- Execute ADB shell commands via Shizuku (requires Shizuku app installed)
Usage: python3 executor.py "pm list packages"
       python3 executor.py "settings put global limit_ad_tracking 1"
"""
import subprocess, sys

def adb(cmd):
    r = subprocess.run(f"adb shell {cmd}", shell=True, capture_output=True, text=True)
    return r.stdout.strip()

def check_shizuku():
    pkg = adb("pm list packages | grep shizuku")
    return "shizuku" in pkg.lower()

if not check_shizuku():
    print("❌ Shizuku not installed. Install from: https://shizuku.rikka.app")
    sys.exit(1)

if len(sys.argv) < 2:
    print("Usage: python3 executor.py <command>")
    print("\nExamples:")
    print("  python3 executor.py 'pm list packages'")
    print("  python3 executor.py 'settings put global limit_ad_tracking 1'")
    sys.exit(1)

cmd = sys.argv[1]
result = adb(cmd)
print(result)
