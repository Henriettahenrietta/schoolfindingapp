// Starts BOTH local servers (API + web) with one command: `npm start`.
// No dependencies — just spawns the two Node servers and forwards their output.
import { spawn } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');

function run(file, label) {
  const proc = spawn(process.execPath, [path.join('tools', file)], { cwd: root, stdio: 'inherit' });
  proc.on('exit', (code) => {
    console.log(`[${label}] stopped (code ${code}). Shutting down.`);
    process.exit(code || 0);
  });
  return proc;
}

console.log('Starting UniMatch Cameroon …');
run('mock-server.mjs', 'api');
run('web-server.mjs', 'web');
console.log('\n  API : http://localhost:8080');
console.log('  Web : http://localhost:3000   ← open this in your browser');
console.log('\nPress Ctrl+C to stop.\n');
