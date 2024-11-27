import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class laporanPenjualan {

    // Method to create sales table if it doesn't exist
    public static void buatTabelPenjualan() {
        String sql = "CREATE TABLE IF NOT EXISTS penjualan ("
                   + "  id_penjualan INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "  id_produk INTEGER,"
                   + "  jumlah INTEGER,"
                   + "  total_harga INTEGER,"
                   + "  biaya_kirim INTEGER,"
                   + "  pajak INTEGER,"
                   + "  total_pembayaran INTEGER,"
                   + "  metode_pembayaran VARCHAR(50),"
                   + "  tanggal_pembelian TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                   + "  FOREIGN KEY (id_produk) REFERENCES produk(id_produk)"
                   + ")";
        try (Statement stmt = util.conDB().createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel penjualan berhasil dibuat...");
        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel penjualan: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Method to record sales transaction
    public static void catatPenjualan(Integer idProduk, Integer jumlah, String metodePembayaran) {
        // Calculate total price
        long totalHarga = 0L;
        String sql = "SELECT harga FROM produk WHERE id_produk = ?";
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalHarga = rs.getLong("harga") * jumlah;
            }
        } catch (Exception e) {
            System.out.println("Gagal menghitung harga produk: " + e.getMessage());
        }

        // Calculate shipping cost and tax (example)
        int biayaKirim = 5000;
        int pajak = (int) (totalHarga * 0.10);

        // Save the transaction to the database
        String insertPenjualanSQL = "INSERT INTO penjualan (id_produk, jumlah, total_harga, biaya_kirim, pajak, total_pembayaran, metode_pembayaran) "
                                 + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = util.conDB().prepareStatement(insertPenjualanSQL)) {
            ps.setInt(1, idProduk);
            ps.setInt(2, jumlah);
            ps.setLong(3, totalHarga);
            ps.setInt(4, biayaKirim);
            ps.setInt(5, pajak);
            ps.setLong(6, totalHarga + biayaKirim + pajak);
            ps.setString(7, metodePembayaran);
            ps.executeUpdate();
            System.out.println("Penjualan berhasil dicatat.");
        } catch (SQLException e) {
            System.out.println("Gagal mencatat penjualan: " + e.getMessage());
        }
    }

    // Method to display sales report in table format
    public static void tampilkanLaporanPenjualan() {
        String sql = "SELECT pr.id_penjualan, p.nama, pr.jumlah, pr.total_harga, pr.total_pembayaran, pr.tanggal_pembelian "
                   + "FROM penjualan pr "
                   + "JOIN produk p ON pr.id_produk = p.id_produk";
        
        ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        
        try (Statement stmt = util.conDB().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // Get data and add it to the list
            while (rs.next()) {
                LinkedHashMap<String, String> row = new LinkedHashMap<>();
                row.put("ID Penjualan", String.valueOf(rs.getInt("id_penjualan")));
                row.put("Nama Produk", rs.getString("nama"));
                row.put("Jumlah", String.valueOf(rs.getInt("jumlah")));
                row.put("Total Harga", util.formatAngka(rs.getInt("total_harga")));
                row.put("Total Pembayaran", util.formatAngka(rs.getInt("total_pembayaran")));
                row.put("Tanggal Pembelian", rs.getString("tanggal_pembelian"));
                list.add(row);
            }

            // Display the data in a table format
            util.tampilkanData(list);

            // Option to view transaction details or total monthly sales
            System.out.print("\nPilih menu:\n1. Lihat rincian penjualan\n2. Totalkan hasil penjualan\nMasukkan pilihan (1 atau 2): ");
            int pilihan = Integer.parseInt(util.input());

            if (pilihan == 1) {
                // Option 1: View transaction details
                do {
                    System.out.print("Masukkan ID Penjualan untuk melihat rincian (atau 0 untuk kembali): ");
                    int idPenjualan = Integer.parseInt(util.input());
                    if (idPenjualan != 0) {
                        tampilkanRincianPenjualan(idPenjualan);
                    }
                    // Ask if the user wants to see more details
                    System.out.print("\nApakah Anda ingin melihat rincian penjualan lagi? (y/n): ");
                } while (util.input().equalsIgnoreCase("y"));
            } else if (pilihan == 2) {
                // Option 2: Total monthly sales
                do {
                    System.out.print("Masukkan bulan (1-12): ");
                    int bulan = Integer.parseInt(util.input());
                    System.out.print("Masukkan tahun (YYYY): ");
                    int tahun = Integer.parseInt(util.input());
                    hitungTotalPenjualanBulanan(bulan, tahun);

                    // Ask if the user wants to calculate total sales again
                    System.out.print("\nApakah Anda ingin mentotalkan penjualan lagi? (y/n): ");
                } while (util.input().equalsIgnoreCase("y"));
            }

            // Back to main menu
            penjual.menuPenjual();

        } catch (SQLException e) {
            System.out.println("Gagal menampilkan laporan penjualan: " + e.getMessage());
        }
    }

    // Method to show transaction details based on sales ID
    public static void tampilkanRincianPenjualan(int idPenjualan) {
        String sql = "SELECT p.nama, pr.jumlah, pr.total_harga, pr.biaya_kirim, pr.pajak, pr.total_pembayaran, pr.metode_pembayaran, pr.tanggal_pembelian "
                   + "FROM penjualan pr "
                   + "JOIN produk p ON pr.id_produk = p.id_produk "
                   + "WHERE pr.id_penjualan = ?";
        
        ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                LinkedHashMap<String, String> row = new LinkedHashMap<>();
                row.put("Nama Produk", rs.getString("nama"));
                row.put("Jumlah", String.valueOf(rs.getInt("jumlah")));
                row.put("Total Harga", util.formatAngka(rs.getInt("total_harga")));
                row.put("Biaya Kirim", util.formatAngka(rs.getInt("biaya_kirim")));
                row.put("Pajak", util.formatAngka(rs.getInt("pajak")));
                row.put("Total Pembayaran", util.formatAngka(rs.getInt("total_pembayaran")));
                row.put("Metode Pembayaran", rs.getString("metode_pembayaran"));
                row.put("Tanggal Pembelian", rs.getString("tanggal_pembelian"));
                list.add(row);

                // Display transaction details in table format
                util.tampilkanData(list);
            } else {
                System.out.println("ID Penjualan tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan rincian penjualan: " + e.getMessage());
        }
    }

    // Method to calculate total sales for a specific month
    public static void hitungTotalPenjualanBulanan(int bulan, int tahun) {
        String sql = "SELECT SUM(total_pembayaran) AS total_penjualan "
                   + "FROM penjualan "
                   + "WHERE strftime('%m', tanggal_pembelian) = ? AND strftime('%Y', tanggal_pembelian) = ?";
        
        // Array of month names
        String[] bulanNama = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setString(1, String.format("%02d", bulan));  // Format month to two digits
            ps.setString(2, String.valueOf(tahun));  // Year in 4 digits
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int totalPenjualan = rs.getInt("total_penjualan");
                // Display the month name
                System.out.println("Total penjualan untuk bulan " + bulanNama[bulan - 1] + " tahun " + tahun + ": " + util.formatAngka(totalPenjualan));
            } else {
                System.out.println("Tidak ada penjualan untuk bulan " + bulanNama[bulan - 1] + " tahun " + tahun);
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghitung total penjualan bulanan: " + e.getMessage());
        }
    }
}
