## 8. Spécifications du Système de Sécurité (RBAC)

Rôles principaux

- ROLE_EMPLOYE : accès à ses propres données (pointages, congés, bulletins)
- ROLE_MANAGER : accès aux données des subordonnés (lecture et actions de validation)
- ROLE_RH : accès complet aux endpoints de gestion (contrats, paie)

Principes d'accès

- Isolation par filtre : les endpoints doivent appliquer un filtre `WHERE id = principal.id` ou `WHERE id_manager = principal.id` selon le rôle.
- Vérification à chaque entrée de service (méthode `authorize(principal, action, resource)`).

Exemples de règles

- GET /api/employe/{id}
  - si principal.hasRole(RH) -> autorisé
  - sinon si principal.id == id -> autorisé
  - sinon si principal.hasRole(MANAGER) et employe.id_manager == principal.id -> autorisé
  - sinon -> 403

Sécurité des données sensibles

- Les bulletins de paie sont chiffrés au repos (optionnel)
- Les logs doivent masquer les informations sensibles (numéros de sécurité sociale, salaire complet) sauf pour RH.

Audit

- Actions critiques (validation congé, modification contrat, génération paie) loggées avec actor, timestamp, action, resourceId.
