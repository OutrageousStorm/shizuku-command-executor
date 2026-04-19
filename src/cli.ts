#!/usr/bin/env node
// CLI tool for Shizuku command execution
import { executeShizukuCommand, getDeviceInfo } from './index';

async function main() {
  const args = process.argv.slice(2);

  if (!args.length) {
    console.log('shizuku-executor — Run ADB commands via Shizuku');
    console.log('Usage: shizuku-executor <command>');
    console.log('  shizuku-executor "pm list packages"');
    console.log('  shizuku-executor --device-info');
    process.exit(0);
  }

  if (args[0] === '--device-info') {
    const info = await getDeviceInfo();
    console.log(`Device: ${info.model}`);
    console.log(`Android: ${info.androidVersion}`);
    console.log(`Serial: ${info.serial}`);
    return;
  }

  const command = args.join(' ');
  const result = await executeShizukuCommand(command);

  if (result.success) {
    console.log(result.output);
  } else {
    console.error(`Error: ${result.error}`);
    process.exit(1);
  }
}

main();
