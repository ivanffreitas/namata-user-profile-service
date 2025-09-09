package com.namata.userprofile.util;

import java.text.DecimalFormat;

/**
 * Utilitário para formatação de estatísticas para apresentação no app
 */
public class StatisticsFormatter {
    
    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("0.00");
    
    /**
     * Formata distância em km com 2 casas decimais
     * @param distanceKm Distância em quilômetros
     * @return String formatada "X.XX km"
     */
    public static String formatDistance(Double distanceKm) {
        if (distanceKm == null || distanceKm == 0.0) {
            return "0.00 km";
        }
        return DISTANCE_FORMAT.format(distanceKm) + " km";
    }
    
    /**
     * Formata tempo em minutos para formato horas:minutos
     * @param totalMinutes Tempo total em minutos
     * @return String formatada "H:MM" ou "MM:SS" dependendo do contexto
     */
    public static String formatTime(Integer totalMinutes) {
        if (totalMinutes == null || totalMinutes == 0) {
            return "0:00";
        }
        
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d", hours, minutes);
        } else {
            return String.format("%d:%02d", 0, minutes);
        }
    }
    
    /**
     * Formata elevação como número inteiro em metros
     * @param elevationM Elevação em metros
     * @return String formatada "XXX m"
     */
    public static String formatElevation(Double elevationM) {
        if (elevationM == null || elevationM == 0.0) {
            return "0 m";
        }
        return Math.round(elevationM) + " m";
    }
    
    /**
     * Formata elevação como número inteiro em metros (sobrecarga para Integer)
     * @param elevationM Elevação em metros
     * @return String formatada "XXX m"
     */
    public static String formatElevation(Integer elevationM) {
        if (elevationM == null || elevationM == 0) {
            return "0 m";
        }
        return elevationM + " m";
    }
    
    /**
     * Formata ritmo em minutos por quilômetro para formato mm:ss
     * @param paceMinutesPerKm Ritmo em minutos por quilômetro
     * @return String formatada "MM:SS /km"
     */
    public static String formatPace(Double paceMinutesPerKm) {
        if (paceMinutesPerKm == null || paceMinutesPerKm == 0.0) {
            return "0:00 /km";
        }
        
        int minutes = (int) Math.floor(paceMinutesPerKm);
        int seconds = (int) Math.round((paceMinutesPerKm - minutes) * 60);
        
        // Ajustar se os segundos chegarem a 60
        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds = seconds % 60;
        }
        
        return String.format("%d:%02d /km", minutes, seconds);
    }
    
    /**
     * Calcula e formata o ritmo médio baseado na distância e tempo total
     * @param distanceKm Distância em quilômetros
     * @param totalMinutes Tempo total em minutos
     * @return String formatada "MM:SS /km"
     */
    public static String calculateAndFormatAveragePace(Double distanceKm, Integer totalMinutes) {
        if (distanceKm == null || totalMinutes == null || distanceKm == 0.0 || totalMinutes == 0) {
            return "0:00 /km";
        }
        
        double paceMinutesPerKm = totalMinutes / distanceKm;
        return formatPace(paceMinutesPerKm);
    }
}