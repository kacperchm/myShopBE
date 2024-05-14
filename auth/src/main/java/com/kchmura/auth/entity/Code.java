package com.kchmura.auth.entity;

public enum Code {
    SUCCESS("Operation end success");

    public final String label;

    Code(String label) {
        this.label = label;
    }
}
