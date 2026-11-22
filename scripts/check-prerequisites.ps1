<#
    Etape "Prerequis" du TP 25 : verifie outils et ports avant docker compose up.
    Usage : .\scripts\check-prerequisites.ps1
#>
$script:echecs = 0
function Check($libelle, $ok, $detail) {
    if ($ok) { Write-Host ("  [OK]   " + $libelle) -ForegroundColor Green }
    else { Write-Host ("  [KO]   " + $libelle + "  " + $detail) -ForegroundColor Red; $script:echecs++ }
}

Write-Host ""
Write-Host "1. Outils" -ForegroundColor Cyan
$docker = Get-Command docker -ErrorAction SilentlyContinue
Check "docker present" ($null -ne $docker) "-> https://www.docker.com/products/docker-desktop"
if ($docker) {
    $version = (& docker --version) 2>$null
    Check "docker fonctionne ($version)" ($LASTEXITCODE -eq 0) "-> demarrez Docker Desktop"
    $daemon = (& docker info --format "{{.ServerVersion}}") 2>$null
    Check "demon Docker joignable" ($LASTEXITCODE -eq 0 -and $daemon) "-> Docker Desktop doit tourner"
}
$compose = (& docker compose version) 2>$null
Check "docker compose disponible" ($LASTEXITCODE -eq 0) "-> plugin compose manquant"

Write-Host ""
Write-Host "2. Ports (ceux de l enonce)" -ForegroundColor Cyan
$ports = @(
    @{ port = 8500; nom = "Consul" },
    @{ port = 3306; nom = "MySQL" },
    @{ port = 8081; nom = "phpMyAdmin" },
    @{ port = 8888; nom = "Gateway" },
    @{ port = 8088; nom = "Client" },
    @{ port = 8089; nom = "Voiture" }
)
foreach ($entree in $ports) {
    $occupe = Get-NetTCPConnection -LocalPort $entree.port -State Listen -ErrorAction SilentlyContinue
    Check ("port " + $entree.port + " (" + $entree.nom + ") libre") (-not $occupe) "-> utilisez --env-file .env.local-ports"
}

Write-Host ""
if ($script:echecs -eq 0) { Write-Host "Prerequis OK : docker compose up -d --build peut etre lance." -ForegroundColor Green }
else { Write-Host "$($script:echecs) controle(s) en echec." -ForegroundColor Yellow }