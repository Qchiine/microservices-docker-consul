<#
    Verifie la pile du TP 25 : conteneurs healthy, services dans Consul,
    bases MySQL, puis les endpoints HTTP.
    Usage : .\scripts\verify-stack.ps1 [-EnvFile .env]
#>
param(
    [string]$EnvFile = ".env",
    [int]$GatewayPort = 8888,
    [int]$ClientPort = 8088,
    [int]$VoiturePort = 8089,
    [int]$ConsulPort = 8500
)

$racine = Split-Path $PSScriptRoot -Parent
Push-Location $racine
try {
    $script:echecs = 0
    function Check($libelle, $ok, $detail) {
        if ($ok) { Write-Host ("  [OK]   " + $libelle) -ForegroundColor Green }
        else { Write-Host ("  [KO]   " + $libelle + "  " + $detail) -ForegroundColor Red; $script:echecs++ }
    }

    Write-Host ""
    Write-Host "1. Conteneurs" -ForegroundColor Cyan
    $ps = (& docker compose --env-file $EnvFile ps --format "{{.Name}}|{{.Status}}") 2>$null
    foreach ($nom in @("mysql-container1", "consul-container", "gateway-service", "client-service", "voiture-service", "phpmyadmin-container")) {
        $ligne = $ps | Where-Object { $_ -like "$nom|*" }
        Check "$nom demarre" ($null -ne $ligne) "-> docker compose ps"
    }

    Write-Host ""
    Write-Host "2. Decouverte Consul" -ForegroundColor Cyan
    try {
        $services = (Invoke-RestMethod "http://localhost:$ConsulPort/v1/catalog/services" -TimeoutSec 10)
        $noms = @($services.PSObject.Properties.Name)
        Check "SERVICE-CLIENT enregistre"  ($noms -contains "SERVICE-CLIENT")
        Check "SERVICE-VOITURE enregistre" ($noms -contains "SERVICE-VOITURE")
        Check "SERVICE-GATEWAY enregistre" ($noms -contains "SERVICE-GATEWAY")
    } catch { Check "Consul joignable" $false "-> http://localhost:$ConsulPort" }

    Write-Host ""
    Write-Host "3. Bases MySQL" -ForegroundColor Cyan
    $bases = (& docker exec mysql-container1 mysql -uroot -proot -N -e "SHOW DATABASES;" 2>$null)
    Check "Micro_ClientDB existe"  ($bases -contains "Micro_ClientDB")
    Check "Micro_VoitureDB existe" ($bases -contains "Micro_VoitureDB")

    Write-Host ""
    Write-Host "4. Endpoints HTTP" -ForegroundColor Cyan
    foreach ($cible in @(
        @{ url = "http://localhost:$GatewayPort/actuator/health"; nom = "Gateway health" },
        @{ url = "http://localhost:$GatewayPort/clients"; nom = "Gateway -> SERVICE-CLIENT" },
        @{ url = "http://localhost:$GatewayPort/voitures"; nom = "Gateway -> SERVICE-VOITURE" },
        @{ url = "http://localhost:$ClientPort/clients"; nom = "SERVICE-CLIENT direct" },
        @{ url = "http://localhost:$VoiturePort/voitures"; nom = "SERVICE-VOITURE direct" }
    )) {
        try {
            $r = Invoke-WebRequest $cible.url -UseBasicParsing -TimeoutSec 20
            Check $cible.nom ($r.StatusCode -eq 200)
        } catch { Check $cible.nom $false "-> " + $cible.url }
    }

    Write-Host ""
    if ($script:echecs -eq 0) { Write-Host "Pile validee : Docker + Consul fonctionnent ensemble." -ForegroundColor Green }
    else { Write-Host "$($script:echecs) controle(s) en echec." -ForegroundColor Red; exit 1 }
}
finally { Pop-Location }