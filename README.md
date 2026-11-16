# Kelime2000 V3 — 2000 kelime

GitHub Actions derleme sırasında NGSL sıklık sıralaması ve CEFR-J Türkçe karşılıklarıyla **tam 2000 benzersiz kelime** üretir ve APK içine çevrimdışı gömer. Kaynak dosyalar `tools/generate_words.py` tarafından indirilir; derleme için internet gerekir, APK çalışırken gerekmez. Eğer 2000 eşleşme bulunamazsa derleme hata verir; eksik listeyle başarı göstermez.

10 bölüm x 200 kelime; A/B harfleri yok, büyük iki seçenek, PASS, 3 saniye süre, yanlış/PASS/süre aşımında 4 saniye cevap gösterimi, sağa kaydırarak kategori bazında Tekrar.

**Veri kaynakları:** New General Service List 1.2 (CC BY-SA 4.0) ve CEFR-J Wordlist 1.5; Türkçe anlamlar recepyagmur/EnglishPractice derlemesinden. Kaynak ve lisanslar için https://github.com/recepyagmur/EnglishPractice/blob/main/app/content/vocab/README.md adresine bakın. Bu liste NGSL sırasındaki Türkçe karşılığı bulunabilen ilk 2000 kelimedir.

**Not:** Yanlış şık seçimi bazı sözcüklerde yeterince yakın olmayabilir; otomatik benzerlik ve belirli elle tanımlı çiftler kullanılır.
