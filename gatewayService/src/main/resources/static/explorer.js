const endpoints = [
    { m: 'GET', u: '/clients', g: 'Gateway -> lb://SERVICE-CLIENT (liste des clients)' },
    { m: 'GET', u: '/client/1', g: 'Gateway -> lb://SERVICE-CLIENT (detail client)' },
    { m: 'GET', u: '/voitures', g: 'Gateway -> lb://SERVICE-VOITURE (parc enrichi via OpenFeign)' },
    { m: 'GET', u: '/voitures/1', g: 'Gateway -> lb://SERVICE-VOITURE (vehicule + client joint)' },
    { m: 'GET', u: '/voitures/statistiques', g: 'Agregats du parc (marque / carburant / km moyen)' },
    { m: 'GET', u: '/SERVICE-CLIENT/clients', g: 'Routage dynamique par nom de service (DiscoveryLocator)' },
    { m: 'GET', u: '/SERVICE-VOITURE/voitures', g: 'Routage dynamique par nom de service (DiscoveryLocator)' },
    { m: 'GET', u: '/data/clients?size=3', g: 'Spring Data REST (HAL) expose par SERVICE-CLIENT' },
    { m: 'GET', u: '/api/fleet/overview', g: 'BFF : agregation console FleetOps' },
    { m: 'GET', u: '/actuator/gateway/routes', g: 'Routes resolues par la Gateway' },
    { m: 'GET', u: '/actuator/health', g: 'Sante de la Gateway' }
];

let selection = null;

function rendreListe() {
    const cible = document.getElementById('liste');
    cible.innerHTML = '';
    endpoints.forEach((endpoint, index) => {
        const bouton = document.createElement('button');
        bouton.type = 'button';
        bouton.className = 'endpoint' + (selection === index ? ' active' : '');
        bouton.innerHTML = '<span class="m">' + endpoint.m + '</span><span class="u">' +
            endpoint.u + '</span><span class="g">' + endpoint.g + '</span>';
        bouton.addEventListener('click', () => charger(index));
        cible.appendChild(bouton);
    });
}

async function charger(index) {
    selection = index;
    rendreListe();
    const endpoint = endpoints[index];
    document.getElementById('titre').textContent = endpoint.u;
    const sortie = document.getElementById('sortie');
    sortie.textContent = '// Chargement...';

    try {
        const reponse = await fetch(endpoint.u, { headers: { 'Accept': 'application/json' } });
        const correlationId = reponse.headers.get('X-Correlation-Id');
        document.getElementById('correlation').textContent = 'cid ' + (correlationId || '-');
        const texte = await reponse.text();
        let affichage = texte;
        try {
            affichage = JSON.stringify(JSON.parse(texte), null, 2);
        } catch (ignorer) {
            // reponse non JSON : on affiche le texte brut
        }
        document.getElementById('meta').innerHTML =
            '<span class="badge ' + (reponse.ok ? 'status-ok' : 'status-ko') + '">HTTP ' + reponse.status + '</span>' +
            '<span class="badge">' + endpoint.g + '</span>';
        sortie.textContent = affichage;
    } catch (erreur) {
        document.getElementById('meta').innerHTML = '<span class="badge status-ko">ERREUR RESEAU</span>';
        sortie.textContent = String(erreur);
    }
}

rendreListe();
const depuisUrl = decodeURIComponent((location.hash || '').replace('#', ''));
const indexInitial = depuisUrl
    ? endpoints.findIndex(endpoint => endpoint.u === depuisUrl)
    : 0;
charger(indexInitial >= 0 ? indexInitial : 0);
