const etat = { chargement: false };

async function appelApi() {
    const reponse = await fetch('/api/fleet/overview', { headers: { 'Accept': 'application/json' } });
    if (!reponse.ok) {
        throw new Error('HTTP ' + reponse.status);
    }
    return reponse.json();
}

function remplirKpis(donnees) {
    const statistiques = donnees.statistiques || {};
    const voitures = donnees.voitures || [];
    const clients = donnees.clients || [];
    const registre = donnees.registre || [];

    const cartes = [
        { label: 'Clients enregistres', value: clients.length, unite: 'SERVICE-CLIENT' },
        { label: 'Vehicules du parc', value: voitures.length, unite: 'SERVICE-VOITURE' },
        { label: 'Kilometrage moyen', value: statistiques.kilometrageMoyen || 0, unite: 'km' },
        { label: 'Services decouverts dans Consul', value: registre.length, unite: 'CONSUL' }
    ];

    document.getElementById('kpis').innerHTML = cartes.map(carte => `
        <div class="kpi">
            <div class="label">${carte.label}</div>
            <div class="value">${carte.value} <small>${carte.unite}</small></div>
        </div>`).join('');
}

function remplirRegistre(registre) {
    const cible = document.getElementById('registry');
    if (!registre.length) {
        cible.innerHTML = '<span class="pill error">Aucune instance enregistree</span>';
        return;
    }
    const lignes = registre
        .map(instance => `<span class="pill"><span class="dot"></span>${instance.service} &middot; ${instance.uri}</span>`)
        .join('');
    cible.innerHTML = lignes;
}

function remplirClients(clients) {
    const corps = document.querySelector('#clients tbody');
    corps.innerHTML = clients.map(client => `
        <tr>
            <td>${client.id}</td>
            <td>${client.nom ?? ''}</td>
            <td>${client.age ?? ''}</td>
            <td>${client.email ?? '-'}</td>
            <td>${client.dateAdhesion ?? '-'}</td>
        </tr>`).join('') || '<tr><td colspan="5">Aucun client</td></tr>';
}

function remplirVoitures(voitures) {
    const corps = document.querySelector('#voitures tbody');
    corps.innerHTML = voitures.map(voiture => `
        <tr>
            <td>${voiture.id}</td>
            <td>${voiture.marque ?? ''}</td>
            <td>${voiture.modele ?? ''}</td>
            <td>${voiture.matricule ?? ''}</td>
            <td>${voiture.kilometrage ?? ''}</td>
            <td>${voiture.idClient ?? ''}</td>
        </tr>`).join('') || '<tr><td colspan="6">Aucun vehicule</td></tr>';
}

function remplirBarres(idCible, repartition) {
    const cible = document.getElementById(idCible);
    const entrees = Object.entries(repartition || {});
    if (!entrees.length) {
        cible.innerHTML = '<span class="hint">Aucune donnee</span>';
        return;
    }
    const maximum = Math.max(...entrees.map(([, valeur]) => valeur));
    cible.innerHTML = entrees.map(([cle, valeur]) => `
        <div class="bar-row">
            <span>${cle}</span>
            <div class="bar-track"><div class="bar-fill" style="width:${Math.round((valeur / maximum) * 100)}%"></div></div>
            <span class="bar-value">${valeur}</span>
        </div>`).join('');
}

async function rafraichir() {
    if (etat.chargement) {
        return;
    }
    etat.chargement = true;
    try {
        const donnees = await appelApi();
        document.getElementById('correlation').textContent = 'cid ' + (donnees.correlationId || '-');
        remplirKpis(donnees);
        remplirRegistre(donnees.registre || []);
        remplirClients(donnees.clients || []);
        remplirVoitures(donnees.voitures || []);
        remplirBarres('marques', (donnees.statistiques || {}).repartitionParMarque);
        remplirBarres('carburants', (donnees.statistiques || {}).repartitionParCarburant);
    } catch (erreur) {
        document.getElementById('kpis').innerHTML =
            `<div class="kpi"><div class="label">Erreur</div><div class="value error">${erreur.message}</div></div>`;
    } finally {
        etat.chargement = false;
    }
}

document.getElementById('refresh').addEventListener('click', rafraichir);
rafraichir();
