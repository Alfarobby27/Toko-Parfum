import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class produk {
    // Menu Penjual
    public static void menuProduk() {
        util.cetakJudul("Manajemen Produk");
        System.out.println("1. Tampilkan Daftar Produk");
        System.out.println("2. Tambah Produk");
        System.out.println("3. Ubah Produk");
        System.out.println("4. Hapus Produk");
        System.out.println();
        System.out.println("0. Kembali Ke Menu Utama");
        System.out.println();
        System.out.print("Silakan pilih menu (0-4): ");

        switch(util.input()) {
            case "1": tampil(true); break;
            case "2": tambah(); break;
            case "3": ubah(); break;
            case "4": hapus(); break;
            case "0": penjual.menuPenjual(); break;
            default: 
                System.out.println("Nomor menu tidak valid! Silakan pilih menu dengan angka 0 sampai 4."); 
                menuProduk(); break;
        }
    }


    // METHOD - METHOD FITUR MANAJEMEN PRODUK

    // Method untuk menampilkan daftar produk
    public static void tampil(Boolean navigasi) {
        if (navigasi) {
            util.pembatas(30);
            System.out.println("Daftar Produk");
        }
        ArrayList<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
        String sql = "SELECT * FROM produk";
        try (ResultSet rs = util.conDB().createStatement().executeQuery(sql)) {
            while (rs.next()) {
                LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
                data.put("No.", rs.getString("id_produk"));
                data.put("Nama Produk", rs.getString("nama"));
                data.put("Harga", util.formatAngka(rs.getInt("harga")));
                data.put("Stok", rs.getString("stok"));
                list.add(data);
            }
            
            if (list.isEmpty()) {
                System.out.println("Data Produk Kosong. Silakan isi data produk terlebih dahulu.");
            } else {
                util.tampilkanData(list);
            }
            
            if (navigasi) {
                util.tahan("Silahkan Tekan Enter ⏎ Untuk Kembali Ke Menu Produk...");
            }
            
        } catch (Exception e) {
            System.out.println("Gagal menampilkan daftar produk: " + e.getMessage());
        }
        if (navigasi) {
            menuProduk();
        }
    }

    // Method untuk menambah produk
    public static void tambah() {
        util.pembatas(30);
        System.out.println("Tambah Data Produk");
        String nama = inputNama();
        Integer harga = inputHarga();
        Integer stok = inputStok();
        
        if (harga <= 0 || stok < 0) {
            System.out.println("Harga dan stok harus lebih besar dari 0!");
            menuProduk(); // Mengulang proses jika ada input yang salah
            return;
        }

        String sql = "INSERT INTO produk (nama, harga, stok) VALUES (?, ?, ?)";
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setInt(2, harga);
            ps.setInt(3, stok);
            ps.executeUpdate();
            System.out.println("Data produk berhasil ditambahkan!");
        } catch (Exception e) {
            System.out.println("Gagal menambah data produk: " + e.getMessage());
        }
        menuProduk();
    }


    // Method untuk mengubah produk
    public static void ubah() {
        util.pembatas(30);
        System.out.println("Ubah Data Produk");
        Integer idProduk = pilih();
        if (idProduk == 0) {
            menuProduk();
        }
        String nama = inputNama();
        Integer harga = inputHarga();
        Integer stok = inputStok();
        String sql = "UPDATE produk SET nama = ?, harga = ?, stok = ? WHERE id_produk = ?";
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setInt(2, harga);
            ps.setInt(3, stok);
            ps.setInt(4, idProduk);
            ps.executeUpdate();
            System.out.println("Data produk berhasil diubah!");
        } catch (Exception e) {
            System.out.println("Gagal mengubah data produk: " + e.getMessage());
        }
        menuProduk();
    }

    public static void hapus() {
        util.pembatas(30);
        System.out.println("Hapus Data Produk");
        Integer idProduk = pilih(); // Pilih ID produk yang akan dihapus
        
        if (idProduk == 0) {
            menuProduk();
        }
    
        // Menghapus produk
        String sqlDelete = "DELETE FROM produk WHERE id_produk = ?";
        try (PreparedStatement psDelete = util.conDB().prepareStatement(sqlDelete)) {
            psDelete.setInt(1, idProduk);
            psDelete.executeUpdate();
            System.out.println("Data produk berhasil dihapus!");
        } catch (Exception e) {
            System.out.println("Gagal menghapus data produk: " + e.getMessage());
            menuProduk();
            return;
        }
        
        // Menyusun ulang ID produk setelah penghapusan
        updateProdukID();
        menuProduk();
    }
    
    // Method untuk menyusun ulang ID produk setelah penghapusan
    private static void updateProdukID() {
        String sql = "SELECT id_produk FROM produk ORDER BY id_produk ASC";
        try (ResultSet rs = util.conDB().createStatement().executeQuery(sql)) {
            int newId = 1;  // Mulai ID dari 1 lagi
            while (rs.next()) {
                int currentId = rs.getInt("id_produk");
                if (currentId != newId) {
                    // Update ID produk yang lebih besar dari newId untuk menjadi newId
                    String sqlUpdate = "UPDATE produk SET id_produk = ? WHERE id_produk = ?";
                    try (PreparedStatement psUpdate = util.conDB().prepareStatement(sqlUpdate)) {
                        psUpdate.setInt(1, newId);
                        psUpdate.setInt(2, currentId);
                        psUpdate.executeUpdate();
                    }
                }
                newId++;
            }
        } catch (Exception e) {
            System.out.println("Gagal memperbarui ID produk: " + e.getMessage());
        }
    }
    
    // Method untuk memilih produk berdasarkan ID
    public static Integer pilih() {
        tampil(false);
        System.out.print("Pilih produk berdasarkan ID: ");
        String input = util.input();
        if (input.equals("0")) {
            return 0;
        }

        Integer idProduk = getIdByNomor(input);
        if (idProduk > 0) {
            return idProduk;
        }
        System.out.println("ID produk tidak ditemukan! Silakan masukkan ID produk yang valid atau 0 untuk membatalkan.");
        return pilih();
    }

    private static String inputNama() {
        System.out.print("Masukkan Nama Produk: ");
        return util.input();
    }

    private static Integer inputHarga() {
        System.out.print("Masukkan Harga Produk: ");
        return Integer.parseInt(util.input());
    }

    private static Integer inputStok() {
        System.out.print("Masukkan Stok Produk: ");
        return Integer.parseInt(util.input());
    }

    private static Integer getIdByNomor(String nomor) {
        Integer idProduk = 0;
        String sql = "SELECT id_produk FROM produk WHERE id_produk = ? LIMIT 1";
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setString(1, nomor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idProduk = rs.getInt("id_produk");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return idProduk;
    }
}
