import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;

public class pembeli {

    private static String jasaKirim = "";
    private static int totalHarga = 0;
    
    // Method Menu Pembeli
    public static void menuPembeli() {
        util.cetakJudul("Menu Pembeli");
        System.out.println("1. Tampilkan Daftar Produk");
        System.out.println("2. Masukkan ke Keranjang");
        System.out.println("3. Checkout");
        System.out.println();
        System.out.println("0. Kembali Ke Menu Utama");
        System.out.println();
        System.out.print("Silakan pilih menu (0-3): ");
    
        switch (util.input()) {
            case "1": tampilProdukPembeli(true); break; // Menampilkan daftar produk
            case "2": keranjangBelanja.keranjang(); break; // Menambah produk ke keranjang belanja
            case "3": checkoutProduk.checkout(); break;  // Checkout untuk pembayaran
            case "0": tokoParfum.menuUtama(); break; 
            default:
                System.out.println("Nomor menu tidak valid! Silakan pilih menu dengan angka 0 sampai 3.");
                menuPembeli(); break;
        }
    }
    
    public static void tampilProdukPembeli(Boolean navigasi) {
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
            menuPembeli();
        }
    }
}


