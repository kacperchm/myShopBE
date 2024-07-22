package com.kchmura.auth.entity;

public enum Code {
    SUCCESS("Operacja zakończona sukcesen"),
    PERMIT("Przyznano dostep"),
    A1("Nie udało się zalogować"),
    A2("Podane dane są nieprawidłowe"),
    A3("Wskazany token jest pusty lub nie ważny"),
    A4("Użytkownik o podanej nazwie już istnieje"),
    A5("Użytkownik o podanym mailu już istnieje");


    public final String label;

    Code(String label) {
        this.label = label;
    }
}
