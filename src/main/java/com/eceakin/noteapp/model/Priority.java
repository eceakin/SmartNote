package com.eceakin.noteapp.model;

public enum Priority {
    LOW("Düşük",  "#22c55e"),      // Green
    MEDIUM("Orta", "#f59e0b"),      // Orange
    HIGH("Yüksek",  "#ef4444"),      // Red
    URGENT("Acil", "#dc2626");      // Dark Red

    private final String displayName;
    private final String color;

    Priority(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    

    public String getColor() {
        return color;
    }

    // BU METODU KONTROL ET VE DÜZELT!
    public static Priority fromString(String priority) {
        if (priority == null) {
            return MEDIUM;
        }
        // Gelen stringi İngiliz locale'ini kullanarak büyük harfe çevir
        // Böylece 'i' harfi 'I'ya dönüşecek, 'İ'ya değil.
        String normalizedPriority = priority.toUpperCase(java.util.Locale.ENGLISH);

        try {
            return Priority.valueOf(normalizedPriority);
        } catch (IllegalArgumentException e) {
            return MEDIUM; // Varsayılan olarak orta öncelik dön
        }
    }
    // Alternatif olarak, sadece enum'un kendi değerlerini kullanmak yeterli olabilir.
    // Eğer enum isimlerinde bir sorun yoksa, valueOf() çalışmalıydı.
    // Bu yüzden, ilk olarak enum sabitlerindeki yazım hatalarını kontrol etmelisin.
}