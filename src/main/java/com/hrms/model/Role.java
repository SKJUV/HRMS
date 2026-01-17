package com.hrms.model;

/**
 * Énumération des rôles utilisateurs dans le système HRMS
 */
public enum Role {
    ADMIN("Administrateur"),
    MANAGER("Manager"),
    EMPLOYEE("Employé");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
