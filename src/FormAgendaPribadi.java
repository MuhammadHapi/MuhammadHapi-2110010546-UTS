
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarListener;
import com.github.lgooddatepicker.zinternaltools.CalendarSelectionEvent;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import com.github.lgooddatepicker.zinternaltools.YearMonthChangeEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import model.DailyAgenda;
import model.DailyAgendaDAO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author Muhammad hapi
 */
public class FormAgendaPribadi extends javax.swing.JFrame {

    private DailyAgendaDAO dailyAgendaDAO;

    /**
     * Creates new form FormAgendaPribadi
     */
    public FormAgendaPribadi() {
        try {
            // Membuat instance dari DailyAgendaDAO untuk menghubungkan ke database SQLite
            dailyAgendaDAO = new DailyAgendaDAO("agenda.sqlite");
        } catch (SQLException ex) {
            // Menampilkan pesan error dengan JOptionPane jika terjadi kesalahan saat menghubungkan ke database
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam menghubungkan ke database: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            // Keluar dari aplikasi jika koneksi gagal
            System.exit(0);
        }

        // Inisialisasi komponen-komponen GUI
        initComponents();
        // Menambahkan context menu pada komponen JList
        initContextMenu();
        // Menambahkan listener untuk perubahan tanggal
        initListeners();

        // Menyiapkan pengaturan untuk kalender (highlighting)
        setUpCalendarSettings();
    }

    // Menyiapkan pengaturan kalender seperti highlighting tanggal dengan agenda
    private void setUpCalendarSettings() {
        // Membuat pengaturan untuk DatePicker
        DatePickerSettings calendarSettings = new DatePickerSettings();
        // Menetapkan highlight policy agar tanggal dengan agenda di-highlight
        calendarSettings.setHighlightPolicy((LocalDate ld) -> {
            try {
                // Cek apakah ada agenda untuk tanggal ini
                List<DailyAgenda> agendasForSelectedDate = dailyAgendaDAO.getAgendaByDate(ld);

                // Jika ada agenda, beri highlight
                if (!agendasForSelectedDate.isEmpty()) {
                    // Mengatur warna berdasarkan jumlah agenda
                    int numAgendas = agendasForSelectedDate.size();
                    Color highlightColor;

                    highlightColor = switch (numAgendas) {
                        case 1 ->
                            new Color(230, 230, 250); // Lavender muda untuk 1 agenda
                        case 2 ->
                            new Color(200, 200, 230); // Lavender sedikit lebih gelap untuk 2 agenda
                        case 3 ->
                            new Color(170, 170, 210); // Lavender lebih gelap untuk 3 agenda
                        default ->
                            new Color(140, 140, 180); // Lavender yang lebih gelap untuk 4 atau lebih agenda
                    };
                    return new HighlightInformation(highlightColor, null, "Ada agenda!");
                }
            } catch (SQLException ex) {
                // Menampilkan error jika terjadi kesalahan dalam mengambil data agenda
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengambil data agenda: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            // Jika tidak ada agenda, tidak perlu highlight
            return null;
        });
        // Menerapkan pengaturan pada calendar panel
        calendarPanel1.setSettings(calendarSettings);
    }

    // Menginisialisasi listener untuk mendeteksi perubahan tanggal pada kalender
    private void initListeners() {
        // Memperbarui daftar agenda setiap kali tanggal dipilih atau berubah
        updateAgendaList();
        calendarPanel1.addCalendarListener(new CalendarListener() {
            @Override
            public void selectedDateChanged(CalendarSelectionEvent cse) {
                // Ketika tanggal dipilih berubah, perbarui daftar agenda sesuai tanggal tersebut
                updateAgendaList();
            }

            @Override
            public void yearMonthChanged(YearMonthChangeEvent ymce) {
                // Tidak ada aksi khusus saat bulan atau tahun berubah
            }
        });
    }

    // Method untuk menambahkan context menu pada JList
    private void initContextMenu() {
        // Membuat popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        // Menu item untuk Edit
        JMenuItem editMenuItem = new JMenuItem("Edit");
        editMenuItem.addActionListener((ActionEvent e) -> {
            // Ambil objek agenda yang dipilih
            DailyAgenda selectedAgenda = listAgendaHarian.getSelectedValue();
            if (selectedAgenda != null) {
                // Menampilkan dialog FormEditAgenda untuk edit agenda
                FormEditAgenda dialog = new FormEditAgenda(this, selectedAgenda, dailyAgendaDAO, true);
                dialog.setVisible(true);
                updateAgendaList();  // Perbarui daftar agenda setelah diedit
            }
        });

        // Menu item untuk Delete
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            // Ambil objek agenda yang dipilih
            DailyAgenda selectedAgenda = listAgendaHarian.getSelectedValue();
            if (selectedAgenda != null) {
                int agendaId = selectedAgenda.getId();  // Ambil ID agenda

                // Menampilkan dialog konfirmasi
                int confirm = JOptionPane.showConfirmDialog(FormAgendaPribadi.this,
                        "Apakah Anda yakin ingin menghapus agenda dengan ID: " + agendaId + "?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Lakukan operasi delete pada database menggunakan agendaId
                    try {
                        // Misalnya menggunakan metode `deleteAgendaById` di DAO untuk menghapus agenda dari database
                        dailyAgendaDAO.deleteAgenda(selectedAgenda);
                        JOptionPane.showMessageDialog(FormAgendaPribadi.this, "Agenda berhasil dihapus.");
                        updateAgendaList();  // Perbarui daftar agenda setelah dihapus
                    } catch (SQLException ex) {
                        // Menampilkan pesan error jika terjadi masalah saat menghapus data
                        JOptionPane.showMessageDialog(FormAgendaPribadi.this, "Terjadi kesalahan saat menghapus agenda: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                // Jika tidak ada agenda yang dipilih
                JOptionPane.showMessageDialog(FormAgendaPribadi.this, "Silakan pilih agenda terlebih dahulu.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Menambahkan menu items ke popup menu
        popupMenu.add(editMenuItem);    // Menambahkan menu "Edit"
        popupMenu.add(deleteMenuItem);  // Menambahkan menu "Delete"

        // Menambahkan MouseListener untuk mendeteksi klik kanan pada JList
        listAgendaHarian.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showContextMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showContextMenu(e);
            }

            private void showContextMenu(MouseEvent e) {
                // Cek apakah klik kanan dan ada item di JList
                if (e.isPopupTrigger() && listAgendaHarian.getModel().getSize() > 0) {
                    int selectedIndex = listAgendaHarian.locationToIndex(e.getPoint());

                    // Hanya tampilkan context menu jika ada item yang valid
                    if (selectedIndex != -1) {
                        listAgendaHarian.setSelectedIndex(selectedIndex);  // Pilih item yang diklik
                        popupMenu.show(listAgendaHarian, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    // Method untuk memperbarui JList dengan agenda sesuai tanggal yang dipilih
    private void updateAgendaList() {
        // Mengambil tanggal yang dipilih langsung dari calendar panel
        LocalDate selectedDate = calendarPanel1.getSelectedDate();

        DefaultListModel<DailyAgenda> listModel = (DefaultListModel<DailyAgenda>) listAgendaHarian.getModel();
        listModel.clear(); // Bersihkan item sebelumnya

        // Jika tidak ada tanggal yang dipilih, clear JList dan keluar dari method
        if (selectedDate == null) {
            // Perbarui judul panel untuk mencerminkan bahwa tidak ada tanggal yang dipilih
            panelAgendaHarian.setBorder(javax.swing.BorderFactory.createTitledBorder("List Agenda (Tidak ada tanggal yang dipilih)"));
            return;
        }

        try {
            // Mengambil daftar agenda yang sesuai dengan tanggal yang dipilih dari database
            List<DailyAgenda> agendasForSelectedDate = dailyAgendaDAO.getAgendaByDate(selectedDate);

            // Mengubah daftar agenda menjadi model list yang dapat ditampilkan di JList
            for (DailyAgenda agenda : agendasForSelectedDate) {
                listModel.addElement(agenda);
            }

            // Perbarui judul panel dengan tanggal yang dipilih
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String formattedDate = selectedDate.format(formatter);
            panelAgendaHarian.setBorder(javax.swing.BorderFactory.createTitledBorder("List Agenda (" + formattedDate + ")"));

        } catch (SQLException ex) {
            // Menampilkan pesan error dengan JOptionPane jika terjadi kesalahan saat mengambil data agenda
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengambil data agenda: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        calendarPanel1 = new com.github.lgooddatepicker.components.CalendarPanel();
        jPanel2 = new javax.swing.JPanel();
        panelAgendaHarian = new javax.swing.JScrollPane();
        listAgendaHarian = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 24, 24, 24));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calendar Agenda Pribadi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        jPanel1.setLayout(new java.awt.BorderLayout());

        calendarPanel1.setSelectedDate(LocalDate.now());
        calendarPanel1.setSelectedDateWithoutShowing(null);
        jPanel1.add(calendarPanel1, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        panelAgendaHarian.setBorder(javax.swing.BorderFactory.createTitledBorder("List Agenda"));

        listAgendaHarian.setModel(new DefaultListModel<>());
        listAgendaHarian.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listAgendaHarian.setAutoscrolls(false);
        panelAgendaHarian.setViewportView(listAgendaHarian);

        jPanel2.add(panelAgendaHarian, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 12, 0));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 5));

        jButton1.setText("Buat Agenda");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jPanel2.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel4.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Menampilkan dialog FormEditAgenda untuk menambah agenda baru
        DailyAgenda dailyAgenda = new DailyAgenda(calendarPanel1.getSelectedDate());
        FormEditAgenda dialog = new FormEditAgenda(this, dailyAgenda, dailyAgendaDAO, false);
        dialog.setVisible(true);
        updateAgendaList();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormAgendaPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormAgendaPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormAgendaPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormAgendaPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormAgendaPribadi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.CalendarPanel calendarPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JList<DailyAgenda> listAgendaHarian;
    private javax.swing.JScrollPane panelAgendaHarian;
    // End of variables declaration//GEN-END:variables
}
