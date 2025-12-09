# Script to view login-related logs from Android device
# Make sure your device is connected via USB with USB Debugging enabled

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "School Management App - Login Log Viewer" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if device is connected
Write-Host "Checking for connected devices..." -ForegroundColor Yellow
adb devices

Write-Host ""
Write-Host "Clearing old logs..." -ForegroundColor Yellow
adb logcat -c

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Watching for login-related logs..." -ForegroundColor Green
Write-Host "Now open the app and try to login" -ForegroundColor Green
Write-Host "Press Ctrl+C to stop" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Watch logs with color coding
adb logcat | Select-String "AuthRepository|LoginActivity|FirebaseAuth|FirebaseFirestore" | ForEach-Object {
    $line = $_.Line
    if ($line -match "ERROR|failed|Failed|Exception") {
        Write-Host $line -ForegroundColor Red
    } elseif ($line -match "SUCCESS|successful|Successful|complete") {
        Write-Host $line -ForegroundColor Green
    } elseif ($line -match "WARN|Warning") {
        Write-Host $line -ForegroundColor Yellow
    } else {
        Write-Host $line -ForegroundColor White
    }
}
