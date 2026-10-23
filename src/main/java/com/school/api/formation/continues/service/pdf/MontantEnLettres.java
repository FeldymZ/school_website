package com.school.api.formation.continues.service.pdf;

import java.util.StringJoiner;

/** 503700 → "cinq cent trois mille sept cents" */
public final class MontantEnLettres {

    private static final String[] UNITES = {
            "zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
            "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize"
    };
    private static final String[] DIZAINES = {
            "", "dix", "vingt", "trente", "quarante", "cinquante", "soixante"
    };

    private MontantEnLettres() {}

    public static String convertir(long n) {
        if (n == 0) return "zéro";
        if (n < 0) return "moins " + convertir(-n);

        long milliards = n / 1_000_000_000L;
        long millions = (n / 1_000_000L) % 1000;
        long milliers = (n / 1000L) % 1000;
        int reste = (int) (n % 1000);

        StringJoiner j = new StringJoiner(" ");
        if (milliards > 0) j.add(moinsDeMille((int) milliards, true) + (milliards > 1 ? " milliards" : " milliard"));
        if (millions > 0)  j.add(moinsDeMille((int) millions, true) + (millions > 1 ? " millions" : " million"));
        if (milliers > 0)  j.add(milliers == 1 ? "mille" : moinsDeMille((int) milliers, false) + " mille");
        if (reste > 0)     j.add(moinsDeMille(reste, true));
        return j.toString();
    }

    /** pluriel = false devant « mille » (deux cent mille, quatre-vingt mille) */
    private static String moinsDeMille(int n, boolean pluriel) {
        int c = n / 100;
        int r = n % 100;
        StringJoiner j = new StringJoiner(" ");

        if (c > 0) {
            String cent = (c > 1 ? UNITES[c] + " " : "") + "cent";
            if (c > 1 && r == 0 && pluriel) cent += "s";
            j.add(cent);
        }
        if (r > 0) j.add(moinsDeCent(r, pluriel));
        return j.toString();
    }

    private static String moinsDeCent(int n, boolean pluriel) {
        if (n <= 16) return UNITES[n];
        if (n < 20) return "dix-" + UNITES[n - 10];

        int d = n / 10;
        int u = n % 10;

        if (d == 7) return n == 71 ? "soixante et onze" : "soixante-" + moinsDeCent(n - 60, false);
        if (d == 8) return u == 0 ? (pluriel ? "quatre-vingts" : "quatre-vingt") : "quatre-vingt-" + UNITES[u];
        if (d == 9) return "quatre-vingt-" + moinsDeCent(n - 80, false);

        if (u == 0) return DIZAINES[d];
        if (u == 1) return DIZAINES[d] + " et un";
        return DIZAINES[d] + "-" + UNITES[u];
    }
}