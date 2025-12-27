-- Schema MariaDB pour HRMS
-- Cible : MariaDB 10.4+ (JSON support)

-- Remarques :
-- - MariaDB ne dispose pas de type UUID natif comme Postgres; on utilise CHAR(36) avec UUID()
-- - Pas d'ENUMs dynamiques créés via DO block, on déclare directement les types ENUM

CREATE DATABASE IF NOT EXISTS hrms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hrms_db;

-- Departement
CREATE TABLE IF NOT EXISTS departement (
  id CHAR(36) PRIMARY KEY NOT NULL,
  nom VARCHAR(200) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Employe
CREATE TABLE IF NOT EXISTS employe (
  id CHAR(36) PRIMARY KEY NOT NULL,
  matricule VARCHAR(40) UNIQUE NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  nom VARCHAR(100) NOT NULL,
  date_embauche DATE NOT NULL,
  id_manager CHAR(36) NULL,
  id_departement CHAR(36) NULL,
  actif BOOLEAN NOT NULL DEFAULT TRUE,
  contrat JSON NULL,
  date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_manager) REFERENCES employe(id) ON DELETE SET NULL,
  FOREIGN KEY (id_departement) REFERENCES departement(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_employe_matricule ON employe(matricule(40));
CREATE INDEX IF NOT EXISTS idx_employe_manager ON employe(id_manager(36));

-- Presence
CREATE TABLE IF NOT EXISTS presence (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  date_jour DATE NOT NULL,
  heure_arrivee TIME NULL,
  heure_depart TIME NULL,
  status ENUM('ON_TIME','LATE','ABSENT') NOT NULL DEFAULT 'ON_TIME',
  id_justificatif CHAR(36) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_presence_employe_date ON presence(id_employe(36), date_jour);

-- Justificatif
CREATE TABLE IF NOT EXISTS justificatif (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_presence CHAR(36) NOT NULL,
  type VARCHAR(100) NULL,
  url_document TEXT NULL,
  commentaire TEXT NULL,
  date_depot TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_presence) REFERENCES presence(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SoldeConge
CREATE TABLE IF NOT EXISTS solde_conge (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  type_conge ENUM('PAID','SICK','UNPAID') NOT NULL,
  jours_restants DECIMAL(6,2) NOT NULL DEFAULT 0,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_solde_employe_type ON solde_conge(id_employe(36), type_conge);

-- DemandeConge
CREATE TABLE IF NOT EXISTS demande_conge (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  debut DATE NOT NULL,
  fin DATE NOT NULL,
  type_conge ENUM('PAID','SICK','UNPAID') NOT NULL,
  statut ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  id_manager_avis CHAR(36) NULL,
  id_rh_avis CHAR(36) NULL,
  commentaire TEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_demande_employe ON demande_conge(id_employe(36), statut(20));

-- BulletinPaie
CREATE TABLE IF NOT EXISTS bulletin_paie (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  mois_annee VARCHAR(7) NOT NULL,
  salaire_brut DECIMAL(12,2) NOT NULL DEFAULT 0,
  primes DECIMAL(12,2) NOT NULL DEFAULT 0,
  retenues DECIMAL(12,2) NOT NULL DEFAULT 0,
  cotisations DECIMAL(12,2) NOT NULL DEFAULT 0,
  salaire_net DECIMAL(12,2) NOT NULL DEFAULT 0,
  details JSON NULL,
  date_generation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX IF NOT EXISTS ux_bulletin_employe_mois ON bulletin_paie(id_employe(36), mois_annee(7));

-- Prime
CREATE TABLE IF NOT EXISTS prime (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  mois_annee VARCHAR(7) NOT NULL,
  montant DECIMAL(12,2) NOT NULL,
  raison TEXT NULL,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_prime_employe_mois ON prime(id_employe(36), mois_annee(7));

-- Retenue
CREATE TABLE IF NOT EXISTS retenue (
  id CHAR(36) PRIMARY KEY NOT NULL,
  id_employe CHAR(36) NOT NULL,
  mois_annee VARCHAR(7) NOT NULL,
  montant DECIMAL(12,2) NOT NULL,
  raison TEXT NULL,
  FOREIGN KEY (id_employe) REFERENCES employe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_retenue_employe_mois ON retenue(id_employe(36), mois_annee(7));

-- Table pour séquence matricule (MariaDB n'a pas de sequence native avant 10.3)
CREATE TABLE IF NOT EXISTS matricule_seq (
  seq_name VARCHAR(100) PRIMARY KEY,
  seq_val BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Initialiser la séquence
INSERT IGNORE INTO matricule_seq (seq_name, seq_val) VALUES ('matricule_seq', 0);

-- Fonction pour générer matricule et trigger (procedure)
DELIMITER $$
CREATE PROCEDURE gen_matricule(OUT out_matricule VARCHAR(40))
BEGIN
  UPDATE matricule_seq SET seq_val = seq_val + 1 WHERE seq_name = 'matricule_seq';
  SELECT seq_val INTO @val FROM matricule_seq WHERE seq_name = 'matricule_seq';
  SET out_matricule = CONCAT('EMP-', DATE_FORMAT(NOW(), '%Y'), '-', LPAD(@val, 3, '0'));
END$$
DELIMITER ;

-- Trigger BEFORE INSERT pour remplir id et matricule
DELIMITER $$
CREATE TRIGGER trg_before_insert_employe
BEFORE INSERT ON employe
FOR EACH ROW
BEGIN
  IF NEW.id IS NULL THEN
    SET NEW.id = UUID();
  END IF;
  IF NEW.matricule IS NULL OR NEW.matricule = '' THEN
    CALL gen_matricule(@m);
    SET NEW.matricule = @m;
  END IF;
END$$
DELIMITER ;

-- Triggers pour générer UUID par défaut sur autres tables
DELIMITER $$
CREATE TRIGGER trg_before_insert_default_uuid_presence
BEFORE INSERT ON presence
FOR EACH ROW
BEGIN
  IF NEW.id IS NULL THEN
    SET NEW.id = UUID();
  END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_before_insert_default_uuid_justif
BEFORE INSERT ON justificatif
FOR EACH ROW
BEGIN
  IF NEW.id IS NULL THEN
    SET NEW.id = UUID();
  END IF;
END$$
DELIMITER ;

-- Triggers similaires peuvent être ajoutés pour les autres tables (prime, retenue, bulletin_paie, etc.)

-- Exemple : trigger pour remplir id de bulletin_paie
DELIMITER $$
CREATE TRIGGER trg_before_insert_bulletin
BEFORE INSERT ON bulletin_paie
FOR EACH ROW
BEGIN
  IF NEW.id IS NULL THEN
    SET NEW.id = UUID();
  END IF;
END$$
DELIMITER ;

-- Fin du script
