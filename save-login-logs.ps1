# Script to save all logs to a file for analysis
# Make sure your device is connected via USB with USB Debugging enabled

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$logFile = "login_logs_$timestamp.txt"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Capturing Login Logs to File" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if device is connected
Write-Host "Checking for connected devices..." -ForegroundColor Yellow
adb devices

Write-Host ""
Write-Host "Clearing old logs..." -ForegroundColor Yellow
adb logcat -c

Write-Host ""
Write-Host "Ready to capture logs!" -ForegroundColor Green
Write-Host "Now open the app and try to login" -ForegroundColor Green
Write-Host "Logs will be saved to: $logFile" -ForegroundColor Yellow
Write-Host "Press Ctrl+C after you've tried logging in" -ForegroundColor Green
Write-Host ""

# Capture logs to file
adb logcat | Tee-Object -FilePath $logFile | Select-String "AuthRepository|LoginActivity|FirebaseAuth|FirebaseFirestore" | ForEach-Object {
    Write-Host $_.Line
}

Write-Host ""
Write-Host "Logs saved to: $logFile" -ForegroundColor Green
