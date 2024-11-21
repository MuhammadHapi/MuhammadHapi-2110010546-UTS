
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import model.DailyAgenda;
import model.DailyAgendaDAO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
/**
 *
 * @author Muhammad hapi
 */
public class FormEditAgenda extends javax.swing.JDialog {

    private DailyAgenda dailyAgenda;  // Objek untuk menyimpan data agenda yang diedit/tambah
    private final DailyAgendaDAO dailyAgendaDAO;  // Objek DAO untuk akses ke database
    private final boolean isEdit;  // Menentukan apakah form ini untuk Edit atau Create

    // Konstruktor untuk inisialisasi form, baik dalam mode edit maupun create
    public FormEditAgenda(java.awt.Frame parent, DailyAgenda agenda, DailyAgendaDAO dailyAgendaDAO, boolean isEdit) {
        super(parent, true);  // Membuat dialog dengan parent window dan modal
        initComponents();  // Inisialisasi komponen UI
        setLocationRelativeTo(parent); // Atus lokasi ke tengah parent
        setResizable(false); // Jangan perbolehkan perubahan ukuran form

        this.dailyAgenda = agenda;  // Menyimpan agenda yang dipilih (untuk mode edit)
        this.dailyAgendaDAO = dailyAgendaDAO;  // Menyimpan DAO untuk akses database
        this.isEdit = isEdit;  // Menyimpan status apakah form ini untuk Edit atau Create

        initForm(); // Inisialisasi komponen form
        setUpTimePickerSettings(); // Mengatur pengaturan TimePicker
    }

    // Method untuk mengatur pengaturan TimePicker
    private void setUpTimePickerSettings() {
        // Pengaturan untuk TimePicker 'Mulai' (tmMulai) agar waktu mulai harus sebelum waktu selesai
        tmMulai.getSettings().setVetoPolicy((LocalTime lt) -> {
            // Mendapatkan waktu selesai dari TimePicker 'Selesai' (tmSelesai)
            LocalTime endTime = tmSelesai.getTime();

            // Mengembalikan true jika waktu selesai lebih besar dari waktu mulai atau waktu selesai belum dipilih
            return endTime == null || endTime.isAfter(lt);
        });

        // Pengaturan untuk TimePicker 'Selesai' (tmSelesai) agar waktu selesai harus setelah waktu mulai
        tmSelesai.getSettings().setVetoPolicy((LocalTime lt) -> {
            // Mendapatkan waktu mulai dari TimePicker 'Mulai' (tmMulai)
            LocalTime startTime = tmMulai.getTime();

            // Mengembalikan true jika waktu mulai lebih kecil dari waktu selesai atau waktu mulai belum dipilih
            return startTime == null || startTime.isBefore(lt);
        });
    }

    // Inisialisasi form tergantung dari mode
    private void initForm() {
        // Menyesuaikan title form sesuai dengan mode
        setTitle(isEdit ? "Edit Agenda" : "Tambah Agenda");

        // Gabungkan inisialisasi form untuk mode Create dan Edit
        if (dailyAgenda != null) {
            // Jika mode Edit, inisialisasi form dengan data dari agenda yang ada
            dtTanggal.setDate(dailyAgenda.getDate());  // Set tanggal
            tmMulai.setTime(dailyAgenda.getStartTime());  // Set jam mulai
            tmSelesai.setTime(dailyAgenda.getEndTime());  // Set jam selesai
            txtDeskripsi.setText(dailyAgenda.getDescription());  // Set deskripsi
        } else {
            // Jika mode Create, kosongkan form
            dtTanggal.clear();
            tmMulai.clear();
            tmSelesai.clear();
            txtDeskripsi.setText("");
        }
    }

    // Validasi input yang diisi oleh pengguna, memastikan semuanya lengkap
    private boolean isValidInput() {
        // Cek apakah semua field telah diisi dengan benar
        return dtTanggal.getDate() != null && tmMulai.getTime() != null && tmSelesai.getTime() != null
                && !txtDeskripsi.getText().trim().isEmpty();
    }

    // Fungsi untuk menyimpan agenda (baik itu edit atau tambah baru)
    private void saveAgenda() {
        // Mengambil nilai terbaru dari form
        LocalDate newDate = dtTanggal.getDate();
        LocalTime newStartTime = tmMulai.getTime();
        LocalTime newEndTime = tmSelesai.getTime();
        String newDescription = txtDeskripsi.getText();

        // Mengupdate objek dailyAgenda dengan data yang diambil dari form
        dailyAgenda.setDate(newDate);  // Update tanggal
        dailyAgenda.setDescription(newDescription);  // Update deskripsi
        dailyAgenda.setStartTime(newStartTime);  // Update jam mulai
        dailyAgenda.setEndTime(newEndTime);  // Update jam selesai

        try {
            // Simpan agenda (baik mode edit atau create)
            if (isEdit) {
                dailyAgendaDAO.updateAgenda(dailyAgenda);
                JOptionPane.showMessageDialog(FormEditAgenda.this, "Agenda berhasil diperbarui.");
            } else {
                dailyAgendaDAO.createAgenda(dailyAgenda);
                JOptionPane.showMessageDialog(FormEditAgenda.this, "Agenda berhasil ditambahkan.");
            }
            dispose();  // Tutup form setelah berhasil disimpan
        } catch (IllegalArgumentException e) {
            // Tangani kesalahan validasi waktu
            JOptionPane.showMessageDialog(FormEditAgenda.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            // Menampilkan pesan error jika terjadi kesalahan saat menyimpan ke database
            JOptionPane.showMessageDialog(FormEditAgenda.this, "Gagal menyimpan agenda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        tmSelesai = new com.github.lgooddatepicker.components.TimePicker();
        tmMulai = new com.github.lgooddatepicker.components.TimePicker();
        dtTanggal = new com.github.lgooddatepicker.components.DatePicker();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDeskripsi = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        java.awt.GridBagLayout jPanel1Layout1 = new java.awt.GridBagLayout();
        jPanel1Layout1.columnWidths = new int[] {0, 10, 0, 10, 0, 10, 0};
        jPanel1Layout1.rowHeights = new int[] {0, 10, 0, 10, 0, 10, 0, 10, 0};
        jPanel1.setLayout(jPanel1Layout1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(tmSelesai, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(tmMulai, gridBagConstraints);

        dtTanggal.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(dtTanggal, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Deskripsi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Jam Mulai");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Tanggal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Jam Selesai");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(txtDeskripsi, gridBagConstraints);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan);

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel1.add(jPanel2, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();  // Menutup dialog tanpa menyimpan perubahan
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // Cek apakah input sudah valid sebelum disimpan
        if (isValidInput()) {
            // Jika valid, simpan data agenda
            saveAgenda();
        } else {
            // Jika ada input yang belum diisi, tampilkan pesan peringatan
            JOptionPane.showMessageDialog(FormEditAgenda.this, "Semua field harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private com.github.lgooddatepicker.components.DatePicker dtTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.github.lgooddatepicker.components.TimePicker tmMulai;
    private com.github.lgooddatepicker.components.TimePicker tmSelesai;
    private javax.swing.JTextField txtDeskripsi;
    // End of variables declaration//GEN-END:variables
}
