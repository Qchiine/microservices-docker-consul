# TP 25 — Conteneurisation des microservices avec Docker + Consul
### Dockerfile multi-stage · Docker Compose · Consul Discovery

---

## 1. Consul : les 3 microservices enregistres dans le reseau Docker

<p align="center">
  <img src="docs/screenshots/01-consul-ui-services.png" alt="Consul UI - services" width="100%">
</p>

<p align="center">
  <img src="docs/screenshots/02-consul-instance-service-client.png" alt="Consul UI - instance" width="100%">
</p>

---

## 2. MySQL et phpMyAdmin

<p align="center">
  <img src="docs/screenshots/03-phpmyadmin-login.png" alt="phpMyAdmin - connexion (host: mysql)" width="100%">
</p>

<p align="center">
  <img src="docs/screenshots/04-phpmyadmin-databases.png" alt="phpMyAdmin - bases" width="100%">
</p>

<p align="center">
  <img src="docs/screenshots/05-phpmyadmin-table-client.png" alt="phpMyAdmin - table client" width="100%">
</p>

---

## 3. Routage de la Gateway via Consul

<p align="center">
  <img src="docs/screenshots/06-api-voitures-via-consul.png" alt="Gateway -> SERVICE-VOITURE" width="100%">
</p>

<p align="center">
  <img src="docs/screenshots/07-gateway-routes.png" alt="Routes lb:// resolues par Consul" width="100%">
</p>

---

## 4. Console et diagnostic (fonctionnalite additionnelle)

<p align="center">
  <img src="docs/screenshots/08-console-fleetops-docker.png" alt="Console FleetOps" width="100%">
</p>

<p align="center">
  <img src="docs/screenshots/09-diagnostic-docker.png" alt="Diagnostic de la pile Docker" width="100%">
</p>

