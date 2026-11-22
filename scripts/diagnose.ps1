<#
    Diagnostic guide des problemes classiques du TP 25.
    Usage : .\scripts\diagnose.ps1
#>
$racine = Split-Path $PSScriptRoot -Parent
Push-Location $racine
try {
    Write-Host ""
    Write-Host "=== docker compose ps ===" -ForegroundColor Cyan
    docker compose ps

    Write-Host ""
    Write-Host "=== Ports en conflit ===" -ForegroundColor Cyan
    foreach ($port in 8500,3306,8081,8888,8088,8089) {
        $c = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
        if ($c) { Write-Host ("  port " + $port + " : occupe (pid " + ($c | Select-Object -First 1).OwningProcess + ")") -ForegroundColor Yellow }
        else { Write-Host ("  port " + $port + " : libre") }
    }

    Write-Host ""
    Write-Host "=== Santé des conteneurs ===" -ForegroundColor Cyan
    foreach ($nom in @("mysql-container1", "consul-container", "gateway-service", "client-service", "voiture-service")) {
        $etat = (& docker inspect --format "{{.State.Health.Status}}" $nom) 2>$null
        if (-not $etat) { $etat = "pas de healthcheck" }
        Write-Host ("  " + $nom + " : " + $etat)
    }

    Write-Host ""
    Write-Host "=== Erreurs recentes (gateway-service) ===" -ForegroundColor Cyan
    docker compose logs --tail 40 gateway-service 2>$null | Select-String -Pattern "ERROR|Exception|Connection refused|UnknownHost" | Select-Object -Last 10

    Write-Host ""
    Write-Host "=== Rappels ===" -ForegroundColor Cyan
    Write-Host "  - En Docker, une base se joint par le NOM du service (mysql:3306), jamais localhost."
    Write-Host "  - Un service ne peut s enregistrer dans Consul que si le healthcheck /actuator/health repond."
    Write-Host "  - Si un port est pris : docker compose --env-file .env up -d (voir .env.local-ports)"
}
finally { Pop-Location }