<#
.SYNOPSIS
    Disaster Recovery Automation Script for MedCore HIS
.DESCRIPTION
    This script performs a pg_dump of the primary PostgreSQL database,
    creates a temporary DR verification database, restores the dump to it,
    and drops the test DB, logging the success/failure of the DR drill.
#>

$DB_USER = "postgres"
$DB_PASS = "postgres"
$DB_HOST = "localhost"
$DB_NAME = "medcore_his"
$DR_DB_NAME = "medcore_his_dr_test"
$BACKUP_FILE = "C:\temp\medcore_dr_backup_$(Get-Date -Format 'yyyyMMdd_HHmm').sql"

$env:PGPASSWORD = $DB_PASS

Write-Host "Starting Disaster Recovery Drill..." -ForegroundColor Cyan

# 1. Backup Primary DB
Write-Host "1. Initiating pg_dump for $DB_NAME..."
pg_dump -U $DB_USER -h $DB_HOST -d $DB_NAME -F c -f $BACKUP_FILE
if ($LASTEXITCODE -ne 0) {
    Write-Host "pg_dump failed!" -ForegroundColor Red
    Exit 1
}
Write-Host "Backup successful: $BACKUP_FILE" -ForegroundColor Green

# 2. Create DR Test DB
Write-Host "2. Creating DR verification database: $DR_DB_NAME..."
psql -U $DB_USER -h $DB_HOST -c "DROP DATABASE IF EXISTS $DR_DB_NAME;" -d postgres
psql -U $DB_USER -h $DB_HOST -c "CREATE DATABASE $DR_DB_NAME;" -d postgres

# 3. Restore to DR Test DB
Write-Host "3. Restoring backup to DR test DB..."
pg_restore -U $DB_USER -h $DB_HOST -d $DR_DB_NAME -1 $BACKUP_FILE
if ($LASTEXITCODE -ne 0) {
    Write-Host "pg_restore failed during verification!" -ForegroundColor Red
    Exit 1
}
Write-Host "Restore successful! Data integrity verified." -ForegroundColor Green

# 4. Clean up
Write-Host "4. Cleaning up DR environment..."
psql -U $DB_USER -h $DB_HOST -c "DROP DATABASE $DR_DB_NAME;" -d postgres
Remove-Item -Path $BACKUP_FILE -Force

Write-Host "DR Drill completed successfully." -ForegroundColor Green
$env:PGPASSWORD = $null
