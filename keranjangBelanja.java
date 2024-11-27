import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;

public class keranjangBelanja {    
    public static HashMap<Integer, Integer> keranjang = new HashMap<>();   // Menyimpan id produk dan jumlahnya
    
    // Method untuk menambah produk ke keranjang belanja
    public static void keranjang() {
        util.cetakJudul("Keranjang Belanja");

        // Tampilkan tabel produk
        ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
        String sql = "SELECT * FROM produk";
        try (ResultSet rs = util.conDB().createStatement().executeQuery(sql)) {
            while (rs.next()) {
                LinkedHashMap<String, String> data = new LinkedHashMap<>();
                data.put("No.", rs.getString("id_produk"));
                data.put("Nama Produk", rs.getString("nama"));
                data.put("Harga", util.formatAngka(rs.getInt("harga")));
                data.put("Stok", rs.getString("stok"));
                list.add(data);
            }
        } catch (Exception e) {
            System.out.println("Gagal menampilkan daftar produk: " + e.getMessage());
        }

        // Pilih produk
        Integer idProduk = produk.pilih();
        if (idProduk == 0) {
            pembeli.menuPembeli();
        }

        // Input jumlah produk
        System.out.print("Masukkan jumlah produk yang ingin dibeli: ");
        Integer jumlah = Integer.parseInt(util.input());

        // Validasi jumlah
        if (jumlah <= 0) {
            System.out.println("Jumlah harus lebih besar dari 0!");
            keranjang();
            return;
        }

        // Cek stok produk
        String sqlStok = "SELECT stok FROM produk WHERE id_produk = ?";
        try (PreparedStatement ps = util.conDB().prepareStatement(sqlStok)) {
            ps.setInt(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int stok = rs.getInt("stok");
                if (stok < jumlah) {
                    System.out.println("Stok tidak cukup. Tersisa hanya " + stok + " produk.");
                    keranjang();
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memeriksa stok produk: " + e.getMessage());
        }

        // Tambahkan produk ke keranjang
        keranjang.put(idProduk, jumlah);
        System.out.println("Produk berhasil ditambahkan ke keranjang!");

        pembeli.menuPembeli();
    }

    // Method untuk menampilkan isi keranjang belanja
    public static void tampilKeranjang() {
        if (keranjang.isEmpty()) {
            System.out.println("Keranjang belanja Anda kosong!");
            return;
        }

        ArrayList<LinkedHashMap<String, String>> listKeranjang = new ArrayList<>();
        for (Integer idProduk : keranjang.keySet()) {
            LinkedHashMap<String, String> data = new LinkedHashMap<>();
            String sql = "SELECT * FROM produk WHERE id_produk = ?";
            try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
                ps.setInt(1, idProduk);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    data.put("ID Produk", rs.getString("id_produk"));
                    data.put("Nama Produk", rs.getString("nama"));
                    data.put("Harga", util.formatAngka(rs.getInt("harga")));
                    data.put("Jumlah", keranjang.get(idProduk).toString());
                    data.put("Subtotal", util.formatAngka(rs.getInt("harga") * keranjang.get(idProduk)));
                    listKeranjang.add(data);
                }
            } catch (Exception e) {
                System.out.println("Gagal memuat data produk di keranjang: " + e.getMessage());
            }
        }

        util.tampilkanData(listKeranjang);
    }

}