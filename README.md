# Kelime 2000 — Android kaynak projesi

Android Studio ile `Kelime2000` klasörünü açın. Gradle senkronizasyonundan sonra **Build > Build APK(s)** seçin. APK `app/build/outputs/apk/debug/app-debug.apk` yolunda oluşur.

## Özellikler
- Her soruda iki Türkçe cevap, 3 saniyelik sayaç
- Doğru cevapta anında sonraki soru
- Yanlış/PASS/süre aşımında doğru cevabı 4 saniye gösterme
- İngilizce kelime, yaklaşık Türkçe okunuş ve Android TTS ile telaffuz
- Kategori içe aktarma; yerel depolama
- Soru formatı panoya kopyalama
- Sonuç ekranı

## İçe aktarma formatı
```
KATEGORI: Günlük Hayat
borrow|borov|Ödünç almak
lend|lend|Ödünç vermek
```

## Mevcut sınırlamalar
- İlk pakette **2000 değil, 175 civarında örnek kelime** vardır. 2000 kelimelik doğrulanmış frekans listesi ve Türkçe karşılıkları henüz eklenmedi.
- Yakın anlamlı çeldiriciler bazı yaygın kelime çiftleri için tanımlıdır. Diğerlerinde aynı kategoriden rastgele çeldirici kullanılır. Bu nedenle bütün şıkların birbirine yakın olması henüz garanti edilmez.
- Yanlış kelimeleri kalıcı tekrar havuzuna ekleme henüz uygulanmadı.
- Bu çalışma ortamında Android SDK/Gradle bulunmadığından APK derlenemedi; kaynak proje oluşturuldu.
