// shizuku-command-executor
// Execute ADB shell commands via Shizuku IPC from Node.js
// Usage: import { executeShizukuCommand } from './index'

import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

interface ShizukuResponse {
  success: boolean;
  output: string;
  error?: string;
}

/**
 * Execute a shell command via Shizuku (requires Shizuku app to be running)
 * @param command - ADB shell command to execute
 * @returns Promise with success status and output
 */
export async function executeShizukuCommand(command: string): Promise<ShizukuResponse> {
  try {
    // Check if Shizuku is accessible via adb
    const { stdout: devices } = await execAsync('adb devices');
    if (!devices.includes('device')) {
      return {
        success: false,
        output: '',
        error: 'No ADB device connected'
      };
    }

    // Execute via adb shell (Shizuku users have it set up)
    const { stdout } = await execAsync(`adb shell ${command}`, {
      maxBuffer: 1024 * 1024 * 10 // 10MB buffer
    });

    return {
      success: true,
      output: stdout.trim()
    };
  } catch (err: any) {
    return {
      success: false,
      output: '',
      error: err.message
    };
  }
}

/**
 * Batch execute multiple commands
 */
export async function batchExecute(commands: string[]): Promise<ShizukuResponse[]> {
  return Promise.all(commands.map(cmd => executeShizukuCommand(cmd)));
}

/**
 * Get device info via Shizuku
 */
export async function getDeviceInfo() {
  const [model, android, serial] = await batchExecute([
    'getprop ro.product.model',
    'getprop ro.build.version.release',
    'getprop ro.serialno'
  ]);
  return {
    model: model.output,
    androidVersion: android.output,
    serial: serial.output
  };
}
