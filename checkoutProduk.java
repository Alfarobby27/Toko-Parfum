import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class checkoutProduk {

    private static Map<Integer, Integer> pesananBaru = new HashMap<>(); // Menyimpan ID produk dan jumlahnya untuk pesanan baru
    private static int biayaKirim = 0;

    // Method untuk checkout (proses pembayaran)
    public static void checkout() {
        util.cetakJudul("Checkout");

        // Pilih apakah menggunakan keranjang atau pesanan baru
        System.out.println("Pilih opsi checkout:");
        System.out.println("1. Checkout dari Keranjang Belanja");
        System.out.println("2. Buat Pesanan Baru");
        System.out.print("Pilih opsi: ");
        String pilihan = util.input();

        if ("1".equals(pilihan)) {
            checkoutKeranjang(); // Proses checkout menggunakan keranjang
        } else if ("2".equals(pilihan)) {
            pesananBaru(); // Proses checkout menggunakan pesanan baru
        } else {
            System.out.println("Pilihan tidak valid, kembali ke menu pembeli.");
            pembeli.menuPembeli();
            return;
        }
    }

    private static void checkoutKeranjang() {
        // Tampilkan isi keranjang
        keranjangBelanja.tampilKeranjang();

        if (keranjangBelanja.keranjang.isEmpty()) {
            System.out.println("Keranjang belanja Anda kosong!");
            System.out.print("Tekan 0 untuk kembali ke menu pembeli: ");
            String input = util.input();
            if ("0".equals(input)) {
                pembeli.menuPembeli(); // Kembali ke menu pembeli
            }
            return;
        }

        // Pilih produk dari keranjang yang ingin di-checkout
        System.out.print("Masukkan ID produk yang ingin Anda checkout dari keranjang: ");
        Integer idProduk = Integer.parseInt(util.input());

        if (!keranjangBelanja.keranjang.containsKey(idProduk)) {
            System.out.println("Produk dengan ID " + idProduk + " tidak ditemukan di keranjang.");
            System.out.print("Tekan 0 untuk kembali ke menu pembeli: ");
            String input = util.input();
            if ("0".equals(input)) {
                pembeli.menuPembeli(); // Kembali ke menu pembeli
            } else {
                checkoutKeranjang(); // Coba lagi jika input tidak valid
            }
            return;
        }

        Integer jumlahProduk = keranjangBelanja.keranjang.get(idProduk);
        System.out.println("Jumlah produk yang ingin dibeli: " + jumlahProduk);

        // Pilih jasa kirim setelah memilih produk
        pilihJasaKirim();

        // Pilih metode pembayaran dan simpan pilihan dalam variabel
        String metodePembayaran = pilihMetodePembayaran();

        // Tampilkan rincian pembayaran
        tampilkanRincianPembayaran();
        
        // Verifikasi pembayaran
        if (verifikasiPembayaran()) {
            System.out.println("Pembayaran berhasil! Pesanan sedang diproses.");

            // Proses pengiriman berdasarkan produk yang dipilih
            prosesPengiriman(idProduk, jumlahProduk);

            // Catat penjualan ke laporan penjualan
            laporanPenjualan.catatPenjualan(idProduk, jumlahProduk, metodePembayaran);

            // Hapus produk yang sudah diproses dari keranjang
            keranjangBelanja.keranjang.remove(idProduk);
        } else {
            System.out.println("Pembayaran gagal. Silakan coba lagi.");
        }

        pembeli.menuPembeli();
    }

    private static void pesananBaru() {
        System.out.println("Proses checkout pesanan baru...");

        // Pilih produk dan jumlah produk yang ingin dibeli
        Integer idProduk = pilihProdukDanJumlah(); // Mengembalikan idProduk yang dipilih
        Integer jumlah = pesananBaru.get(idProduk); // Mengambil jumlah dari pesananBaru map

        if (idProduk == null || jumlah == null) {
            System.out.println("Produk tidak valid, silakan pilih produk kembali.");
            return;
        }

        // Pilih jasa kirim
        pilihJasaKirim();

        // Pilih metode pembayaran dan simpan pilihan dalam variabel
        String metodePembayaran = pilihMetodePembayaran();
        
        // Tampilkan rincian pembayaran
        tampilkanRincianPembayaran();

        // Verifikasi pembayaran
        if (verifikasiPembayaran()) {
            System.out.println("Pembayaran berhasil! Pesanan baru sedang diproses.");
            
            // Proses pengiriman berdasarkan produk yang dipilih
            prosesPengiriman(idProduk, jumlah);

            // Catat penjualan ke laporan penjualan
            laporanPenjualan.catatPenjualan(idProduk, jumlah, metodePembayaran);
        } else {
            System.out.println("Pembayaran gagal. Silakan coba lagi.");
        }

        pembeli.menuPembeli();
    }

    // Method untuk memilih produk dan jumlah produk
    private static Integer pilihProdukDanJumlah() {
        System.out.println("Pilih produk yang ingin dibeli:");

        // Tampilkan daftar produk
        produk.tampil(false); // Tampilkan produk tanpa navigasi

        System.out.print("Masukkan ID produk yang ingin dibeli: ");
        Integer idProduk = Integer.parseInt(util.input());

        System.out.print("Masukkan jumlah produk: ");
        Integer jumlah = Integer.parseInt(util.input());

        // Validasi stok produk
        if (jumlah <= 0) {
            System.out.println("Jumlah produk harus lebih dari 0.");
            return null;
        }

        // Cek apakah produk tersedia di stok
        String sql = "SELECT stok FROM produk WHERE id_produk = ?";
        try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int stok = rs.getInt("stok");
                if (jumlah > stok) {
                    System.out.println("Stok tidak cukup. Stok saat ini: " + stok);
                    return null; // Return null jika stok tidak mencukupi
                } else {
                    pesananBaru.put(idProduk, jumlah); // Menyimpan ID produk dan jumlahnya
                    System.out.println("Produk berhasil ditambahkan ke pesanan.");
                    return idProduk; // Mengembalikan idProduk
                }
            } else {
                System.out.println("Produk dengan ID " + idProduk + " tidak ditemukan.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Gagal memeriksa stok produk: " + e.getMessage());
            return null;
        }
    }

    // Method untuk memilih jasa kirim
    private static void pilihJasaKirim() {
        util.pembatas(30);
        // Hitung biaya kirim
        System.out.println("Pilih jasa kirim:");
        System.out.println("1. Instant (Rp 20,000)");
        System.out.println("2. Same Day (Rp 15,000)");
        System.out.println("3. Next Day (Rp 10,000)");
        System.out.println("4. Reguler (Rp 5,000)");
        System.out.println("5. Hemat (Rp 2,000)");
        System.out.print("Pilih jasa kirim: ");
        String jasaKirim = util.input();
        switch (jasaKirim) {
            case "1":
                biayaKirim = 20000;
                break;
            case "2":
                biayaKirim = 15000;
                break;
            case "3":
                biayaKirim = 10000;
                break;
            case "4":
                biayaKirim = 5000;
                break;
            case "5":
                biayaKirim = 2000;
                break;
            default:
                System.out.println("Pilihan tidak valid, menggunakan biaya kirim standar (Reguler).");
                biayaKirim = 5000;
                break;
        }
    }

    // Method untuk menampilkan rincian pembayaran
    private static void tampilkanRincianPembayaran() {
        int totalHarga = 0;
        int pajak = 0;

        // Hitung total harga produk
        for (Map.Entry<Integer, Integer> entry : pesananBaru.entrySet()) {
            Integer idProduk = entry.getKey();
            Integer jumlah = entry.getValue();

            // Ambil harga produk
            String sql = "SELECT harga FROM produk WHERE id_produk = ?";
            try (PreparedStatement ps = util.conDB().prepareStatement(sql)) {
                ps.setInt(1, idProduk);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int harga = rs.getInt("harga");
                    totalHarga += harga * jumlah;
                }
            } catch (Exception e) {
                System.out.println("Gagal menghitung harga produk: " + e.getMessage());
            }
        }

        // Hitung pajak (misalnya 10%)
        pajak = (int) (totalHarga * 0.10);

        // Tampilkan rincian pembayaran
        System.out.println();
        System.out.println("===== Rincian Pembayaran =====");
        System.out.println("Total Harga Produk: Rp " + util.formatAngka(totalHarga));
        System.out.println("Biaya Kirim: Rp " + util.formatAngka(biayaKirim));
        System.out.println("Pajak (10%): Rp " + util.formatAngka(pajak));
        System.out.println("Total Pembayaran: Rp " + util.formatAngka(totalHarga + biayaKirim + pajak));
        System.out.println("=============================");
    }

    // Method untuk menghasilkan nomor Virtual Account unik
    private static String generateVirtualAccount() {
        // Generate nomor Virtual Account yang unik menggunakan UUID
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);  // Ambil 10 karakter pertama
        return "VA-" + uuid;  // Format: VA-xxxxxxxxxx
    }
    
    // Method untuk memilih metode pembayaran
    private static String pilihMetodePembayaran() {
        util.pembatas(30);
        System.out.println("Pilih Metode Pembayaran:");
        System.out.println("1. ShopeePay");
        System.out.println("2. SPayLater");
        System.out.println("3. COD (Bayar di Tempat)");
        System.out.println("4. Transfer Bank (Dicek Otomatis)/Virtual Account");
        System.out.println("5. Kartu Kredit/Debit");
        System.out.println("6. Cicilan Kartu Kredit");
        System.out.println("7. BRI Direct Debit");
        System.out.println("8. OneKlik");
        System.out.println("9. Mitra Shopee");
        System.out.println("10. Agen BRILink");
        System.out.println("11. BNI Agen46");
        System.out.println("12. Alfamart");
        System.out.println("13. Indomaret");
        System.out.println("14. Virtual Account (Kode Pembayaran)");

        System.out.print("Pilih metode pembayaran: ");
        String pilihan = util.input();

        String metodePembayaran = "";

        switch (pilihan) {
            case "1":
                metodePembayaran = "ShopeePay";
                break;
            case "2":
                metodePembayaran = "SPayLater";
                break;
            case "3":
                metodePembayaran = "COD";
                break;
            case "4":
                metodePembayaran = "Virtual Account";  // Kasus untuk Virtual Account atau Transfer Bank
                break;
            case "5":
                metodePembayaran = "Kartu Kredit/Debit";
                break;
            case "6":
                metodePembayaran = "Cicilan Kartu Kredit";
                break;
            case "7":
                metodePembayaran = "BRI Direct Debit";
                break;
            case "8":
                metodePembayaran = "OneKlik";
                break;
            case "9":
                metodePembayaran = "Mitra Shopee";
                break;
            case "10":
                metodePembayaran = "Agen BRILink";
                break;
            case "11":
                metodePembayaran = "BNI Agen46";
                break;
            case "12":
                metodePembayaran = "Alfamart";
                break;
            case "13":
                metodePembayaran = "Indomaret";
                break;
            case "14":
                metodePembayaran = "Virtual Account (Kode Pembayaran)";
                break;
            default:
                System.out.println("Pilihan metode pembayaran tidak valid.");
                break;
        }

        // Jika metode pembayaran adalah Virtual Account atau Transfer Bank, kita akan generate nomor VA
        if ("Virtual Account".equals(metodePembayaran) || "Virtual Account (Kode Pembayaran)".equals(metodePembayaran)) {
            String nomorVA = generateVirtualAccount();  // Menghasilkan nomor VA
            System.out.println("Nomor Virtual Account Anda: " + nomorVA);
            System.out.println("Harap lakukan transfer ke nomor VA di atas untuk melanjutkan pembayaran.");
        }

        return metodePembayaran;
    }

    // Method untuk memverifikasi pembayaran
    private static boolean verifikasiPembayaran() {
        System.out.println("Verifikasi pembayaran...");

        // Jika metode pembayaran adalah Virtual Account, kita akan memverifikasi nomor VA
        if (biayaKirim > 0) {  // Pastikan jika ada biaya kirim yang terlibat
            System.out.print("Masukkan status pembayaran untuk Virtual Account (y/n): ");
            String statusPembayaran = util.input();
            return "y".equalsIgnoreCase(statusPembayaran);
        }

        // Misalnya, untuk metode lain (seperti COD atau transfer manual), kita hanya memeriksa status pembayaran
        System.out.print("Masukkan status pembayaran (y/n): ");
        String statusPembayaran = util.input();
        return "y".equalsIgnoreCase(statusPembayaran);
    }

    // Method untuk memproses pengiriman setelah pembayaran berhasil
    private static void prosesPengiriman(Integer idProduk, Integer jumlah) {
        // Update stok produk yang dibeli
        String sqlUpdateStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";
        try (PreparedStatement psUpdate = util.conDB().prepareStatement(sqlUpdateStok)) {
            psUpdate.setInt(1, jumlah);
            psUpdate.setInt(2, idProduk);
            psUpdate.executeUpdate();
        } catch (Exception e) {
            System.out.println("Gagal memperbarui stok: " + e.getMessage());
        }

        System.out.println("Produk berhasil dikirim ke alamat pembeli.");
    }
}