package model;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DailyAgendaDAO {

    private final Connection connection;

    // Constructor untuk koneksi ke database
    public DailyAgendaDAO(String databasePath) throws SQLException {
        String url = "jdbc:sqlite:" + databasePath;
        connection = DriverManager.getConnection(url);
        createTableIfNotExists();
    }

    // Method untuk membuat tabel jika belum ada
    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS daily_agenda (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                description TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL
            )
        """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    // Create (menambah agenda baru)
    public void createAgenda(DailyAgenda agenda) throws SQLException {
        String sql = "INSERT INTO daily_agenda (date, description, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, agenda.getDate().toString());
            preparedStatement.setString(2, agenda.getDescription());
            preparedStatement.setString(3, agenda.getStartTime().toString());
            preparedStatement.setString(4, agenda.getEndTime().toString());
            preparedStatement.executeUpdate();

            // Mengambil ID yang dihasilkan setelah insert
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    agenda.setId(generatedKeys.getInt(1));  // Set ID yang dihasilkan ke objek agenda
                }
            }
        }
    }

    // Update (mengubah agenda yang sudah ada)
    public void updateAgenda(DailyAgenda agenda) throws SQLException {
        String sql = "UPDATE daily_agenda SET date = ?, description = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, agenda.getDate().toString());
            preparedStatement.setString(2, agenda.getDescription());
            preparedStatement.setString(3, agenda.getStartTime().toString());
            preparedStatement.setString(4, agenda.getEndTime().toString());
            preparedStatement.setInt(5, agenda.getId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete (menghapus agenda berdasarkan object DailyAgenda)
    public void deleteAgenda(DailyAgenda agenda) throws SQLException {
        String sql = "DELETE FROM daily_agenda WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, agenda.getId());  // Menggunakan ID dari objek agenda
            preparedStatement.executeUpdate();
        }
    }

    // Method untuk mendapatkan agenda pada LocalDate tertentu
    public List<DailyAgenda> getAgendaByDate(LocalDate date) throws SQLException {
        List<DailyAgenda> agendas = new ArrayList<>();
        String query = "SELECT * FROM daily_agenda WHERE date = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, date.toString()); // Set tanggal sebagai string (yyyy-mm-dd)

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Ambil data dari result set dan buat objek DailyAgenda
                DailyAgenda agenda = new DailyAgenda(
                        resultSet.getInt("id"),
                        LocalDate.parse(resultSet.getString("date")), // Parse string menjadi LocalDate
                        resultSet.getString("description"),
                        LocalTime.parse(resultSet.getString("start_time")), // Parse string menjadi LocalTime
                        LocalTime.parse(resultSet.getString("end_time")) // Parse string menjadi LocalTime
                );
                agendas.add(agenda);
            }
        }

        return agendas;
    }

    // Menutup koneksi ke database
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
