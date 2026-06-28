package com.schoolfinder.app.ui;

import java.text.NumberFormat;
import java.util.Locale;

/** Shared formatting helpers. */
public final class Format {

    private Format() {}

    /** "50 000 XAF" style formatting (space thousands separator, fits XAF). */
    public static String money(Double amount, String currency) {
        if (amount == null) return "—"; // em dash
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        return nf.format(amount) + " " + currency;
    }

    public static String categoryLabel(String raw) {
        if (raw == null) return "";
        switch (raw) {
            case "PRIMARY": return "Primary";
            case "SECONDARY": return "Secondary";
            case "HIGH_SCHOOL": return "High School";
            case "VOCATIONAL": return "Vocational";
            case "UNIVERSITY": return "University";
            default:
                String lower = raw.toLowerCase(Locale.ROOT);
                return lower.isEmpty() ? lower : Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }
    }
}
