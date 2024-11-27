import java.sql.*;
import java.util.*;

public class penjual {
    // Migration: Method untuk membuat tabel produk
    public static void buatTabel() {
        String sql = ""
            + "CREATE TABLE IF NOT EXISTS produk ("
            + "  id_produk INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "  nama VARCHAR(255),"
            + "  harga INTEGER,"
            + "  stok INTEGER"
            + ")";
        try (Statement stmt = util.conDB().createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel produk berhasil dibuat...");
        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel produk: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Seeder: Membuat data awal produk
    public static void buatDataAwal() {
        String sql = ""
            + "INSERT OR IGNORE INTO produk (nama, harga, stok) VALUES "
            + "('Chanel NO.5', 150000, 10), "
            + "('Dior Sauvage', 120000, 15), "
            + "('Gucci Guilty', 140000, 20), "
            + "('Bleu de Chanel', 180000, 10), "
            + "('Hugo Boss Bottled', 130000, 25)";
        try (Statement stmt = util.conDB().createStatement()) {
            stmt.execute(sql);
            System.out.println("Data awal produk berhasil dibuat...");
        } catch (SQLException e) {
            System.out.println("Gagal membuat data awal produk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method Menu Penjual
    public static void menuPenjual() {
        util.cetakJudul("Menu Penjual");
        System.out.println("1. Manajemen Produk");
        System.out.println("2. Laporan Penjualan");
        System.out.println();
        System.out.println("0. Keluar");
        System.out.println();
        System.out.print("Silakan pilih menu (0-2) : ");

        switch (util.input()) {
            case "1": produk.menuProduk(); break;
            case "2": LaporanPenjualan.tampilkanLaporanPenjualan(); break;
            case "0": tokoParfum.menuUtama(); break;
            default:
                System.out.println("Nomor menu tidak valid! silakan pilih menu dengan angka 0 sampai 2");
                menuPenjual();
                break;
        }
    }
}
