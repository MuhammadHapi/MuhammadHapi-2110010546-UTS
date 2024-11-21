package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class DailyAgenda {

    private Integer id;         // ID agenda
    private LocalDate date;     // Tanggal agenda
    private String description; // Deskripsi kegiatan
    private LocalTime startTime; // Waktu mulai
    private LocalTime endTime;   // Waktu selesai

    // Constructor dengan tanggal saja
    public DailyAgenda(LocalDate date) {
        this(date, null, null, null);
    }

    // Constructor tanpa id
    public DailyAgenda(LocalDate date, String description, LocalTime startTime, LocalTime endTime) {
        this.id = -1;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor
    public DailyAgenda(int id, LocalDate date, String description, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if (endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time tidak boleh lebih dari end time, nyan!");
        }
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time tidak boleh kurang dari start time, nyan!");
        }
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        // Membuat string format waktu terlebih dahulu
        String startTimeFormatted = "%02d:%02d".formatted(startTime.getHour(), startTime.getMinute());
        String endTimeFormatted = "%02d:%02d".formatted(endTime.getHour(), endTime.getMinute());

        // Menggunakan panah (→) antara waktu mulai dan selesai, dan titik dua ganda (::) untuk deskripsi
        return "%s → %s :: %s".formatted(startTimeFormatted, endTimeFormatted, description);
    }

}
