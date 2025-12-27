-- Schema PostgreSQL pour HRMS
-- Utilise UUID pour les PK, enums pour les status

-- Extensions recommandées
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enums
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'presence_status') THEN
        CREATE TYPE presence_status AS ENUM ('ON_TIME','LATE','ABSENT');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'conge_type') THEN
        CREATE TYPE conge_type AS ENUM ('PAID','SICK','UNPAID');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'demande_statut') THEN
        CREATE TYPE demande_statut AS ENUM ('PENDING','APPROVED','REJECTED');
    END IF;
END$$;

-- Departement
CREATE TABLE IF NOT EXISTS departement (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL
);

-- Employe
CREATE TABLE IF NOT EXISTS employe (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    matricule VARCHAR(40) UNIQUE NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    date_embauche DATE NOT NULL,
    id_manager UUID NULL,
    id_departement UUID NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    contrat JSONB NULL,
    date_creation TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_employe_manager FOREIGN KEY (id_manager) REFERENCES employe (id) ON DELETE SET NULL,
    CONSTRAINT fk_employe_departement FOREIGN KEY (id_departement) REFERENCES departement (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_employe_matricule ON employe(matricule);
CREATE INDEX IF NOT EXISTS idx_employe_manager ON employe(id_manager);

-- Presence
CREATE TABLE IF NOT EXISTS presence (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    date_jour DATE NOT NULL,
    heure_arrivee TIME NULL,
    heure_depart TIME NULL,
    status presence_status NOT NULL DEFAULT 'ON_TIME',
    id_justificatif UUID NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_presence_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_presence_employe_date ON presence(id_employe, date_jour);

-- Justificatif
CREATE TABLE IF NOT EXISTS justificatif (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_presence UUID NOT NULL,
    type VARCHAR(100) NULL,
    url_document TEXT NULL,
    commentaire TEXT NULL,
    date_depot TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_justif_presence FOREIGN KEY (id_presence) REFERENCES presence (id) ON DELETE CASCADE
);

-- SoldeConge
CREATE TABLE IF NOT EXISTS solde_conge (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    type_conge conge_type NOT NULL,
    jours_restants NUMERIC(6,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_solde_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_solde_employe_type ON solde_conge(id_employe, type_conge);

-- DemandeConge
CREATE TABLE IF NOT EXISTS demande_conge (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    debut DATE NOT NULL,
    fin DATE NOT NULL,
    type_conge conge_type NOT NULL,
    statut demande_statut NOT NULL DEFAULT 'PENDING',
    id_manager_avis UUID NULL,
    id_rh_avis UUID NULL,
    commentaire TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_demande_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_demande_employe ON demande_conge(id_employe, statut);

-- BulletinPaie
CREATE TABLE IF NOT EXISTS bulletin_paie (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    mois_annee VARCHAR(7) NOT NULL, -- format YYYY-MM
    salaire_brut NUMERIC(12,2) NOT NULL DEFAULT 0,
    primes NUMERIC(12,2) NOT NULL DEFAULT 0,
    retenues NUMERIC(12,2) NOT NULL DEFAULT 0,
    cotisations NUMERIC(12,2) NOT NULL DEFAULT 0,
    salaire_net NUMERIC(12,2) NOT NULL DEFAULT 0,
    details JSONB NULL,
    date_generation TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_bulletin_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_bulletin_employe_mois ON bulletin_paie(id_employe, mois_annee);

-- Prime
CREATE TABLE IF NOT EXISTS prime (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    mois_annee VARCHAR(7) NOT NULL,
    montant NUMERIC(12,2) NOT NULL,
    raison TEXT NULL,
    CONSTRAINT fk_prime_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_prime_employe_mois ON prime(id_employe, mois_annee);

-- Retenue
CREATE TABLE IF NOT EXISTS retenue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_employe UUID NOT NULL,
    mois_annee VARCHAR(7) NOT NULL,
    montant NUMERIC(12,2) NOT NULL,
    raison TEXT NULL,
    CONSTRAINT fk_retenue_employe FOREIGN KEY (id_employe) REFERENCES employe (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_retenue_employe_mois ON retenue(id_employe, mois_annee);

-- Quelques fonctions utilitaires (exemple): génération de matricule
CREATE OR REPLACE FUNCTION generate_matricule_yseq() RETURNS text AS $$
DECLARE
  y text := to_char(now(),'YYYY');
  seq int;
BEGIN
  PERFORM 1 FROM pg_class WHERE relname='matricule_seq';
  -- create sequence if not exists
  BEGIN
    EXECUTE 'CREATE SEQUENCE IF NOT EXISTS matricule_seq START 1';
  EXCEPTION WHEN OTHERS THEN
    NULL;
  END;
  seq := nextval('matricule_seq');
  RETURN format('EMP-%s-%03s', y, seq::text);
END;
$$ LANGUAGE plpgsql;

-- Trigger pour remplir matricule à l'insertion
CREATE OR REPLACE FUNCTION before_insert_employe() RETURNS trigger AS $$
BEGIN
  IF NEW.matricule IS NULL OR NEW.matricule = '' THEN
    NEW.matricule := generate_matricule_yseq();
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_before_insert_employe ON employe;
CREATE TRIGGER trg_before_insert_employe
BEFORE INSERT ON employe
FOR EACH ROW EXECUTE FUNCTION before_insert_employe();

-- Fin du script
