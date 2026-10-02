# localhost-genetyka 🧬

Innowacyjny, zaawansowany i niespotykany dotąd system **Inżynierii Genetycznej i Mutacji DNA** w Minecraft!

Pozwala graczom na pobieranie próbek DNA z zabitych stworzeń i graczy przy pomocy **Strzykawki DNA (Ekstraktora)**, syntezę genów w interaktywnym **Laboratorium Genetycznym (`/dna`)**, odblokowywanie slotów chromosomów oraz zyskiwanie potężnych zdolności aktywnych i pasywnych z możliwością ich ulepszania (Poziomy 1-3).

---

## 🧬 Dostępne Mutacje Genetyczne (8 Typów)

### 1. 🌌 Mutacja Wardena (`warden`)
* **Pasywka (Sonar Sculk)**: Kucnięcie (`Shift`) wysyła falę ultradźwiękową w promieniu 18-32 kratek, podświetlając wszystkich ukrytych wrogów i moby efektem Glowing na 4 sekundy!
* **Aktywka (Sonic Boom)**: `Shift + Lewy klik` pustą ręką wystrzeliwuje niszczycielski promień dźwiękowy Wardena, który rani wrogów na odległość i odrzuca ich z potężną siłą!

### 2. 👁️ Mutacja Endermana (`enderman`)
* **Pasywka (Zagięcie Przestrzeni)**: 15% - 35% szans na automatyczne uniknięcie wrogiej strzały lub pocisku poprzez natychmiastową mini-teleportację!
* **Perły Enderu**: Całkowity brak obrażeń od upadku po rzuceniu perły (No Pearl Damage).

### 3. 💥 Mutacja Creepera (`creeper`)
* **Pasywka (Wybuch Zemsty)**: Gdy Twoje zdrowie spadnie poniżej 2 serc (lub otrzymasz cios śmiertelny), wokół Ciebie wybucha fala uderzeniowa odpychająca agresorów i nakładająca na nich oślepienie!

### 4. 🕷️ Mutacja Pająka (`pajak`)
* **Pasywka (Wspinaczka Ścienna)**: Kucając przy pionowej ścianie możesz wspinać się po blokach niczym pająk!
* **Pajęcza Toksyna**: 20% szans na zatrucie wroga jadem przy każdym zadanym ciosie.
* **Odporność na Pajęczyny**: Gracz porusza się w pajęczynach z pełną prędkością.

### 5. 🛡️ Mutacja Żelaznego Golema (`golem`)
* **Tytanowa Masa**: Do 75% redukcji odrzutu (Knockback Resistance)!
* **Dodatkowa Witalność**: +2 stałe serca (4 HP).
* **Trzęsienie Ziemi**: Upadek z wysokości wywołuje falę uderzeniową odpychającą wrogów.

### 6. 🔥 Mutacja Blaze'a (`blaze`)
* **Płomienna Odporność**: Całkowita niewrażliwość na ogień i lawę.
* **Ogniste Ciernie**: Każdy przeciwnik atakujący Cię w zwarciu zostaje natychmiast podpalony!

### 7. 💀 Mutacja Withera (`wither`)
* **Mroczny Uwiąd**: 25% szans na nałożenie efektu Wither II na przeciwnika.
* **Odporność**: Całkowita niewrażliwość na obrażenia od Withera.

### 8. 🦇 Mutacja Phantoma (`phantom`)
* **Spadochron Nocy**: Kucając w trakcie spadania w powietrzu aktywujesz wolne opadanie (Slow Falling), co pozwala na bezpieczne szybowanie z dowolnej wysokości!

---

## 💉 Narzędzia i Pozyskiwanie DNA

1. **Strzykawka DNA (Ekstraktor)**:
   * Specjalny przyrząd laboratoryjny wytwarzany w Crafting Table:
     * `Kwarc` (góra) + `Szklana Butelka` (środek) + `Sztabka Żelaza` (dół).
   * Zabijając moba ze strzykawką w ręku, masz 25% szansy na pozyskanie **Próbki DNA** danego stworzenia!
2. **Punkty Ewolucji (DNA Points)**:
   * Zdobywane za zabijanie mobów (+2 pkt) oraz graczy (+25 pkt).
   * Służą do odblokowywania slotów chromosomów (Slot 2 i 3) oraz podnoszenia poziomu mutacji (Poz. 1 -> 2 -> 3).

---

## 💻 Komendy i Uprawnienia

| Komenda | Uprawnienie | Opis |
|---|---|---|
| `/dna` / `/genetyka` | `genetyka.use` | Otwiera menu Laboratorium Genetycznego |
| `/dna info` | `genetyka.use` | Sprawdza profil genetyczny, punkty i aktywne geny |
| `/dna lista` | `genetyka.use` | Wyświetla spis wszystkich 8 mutacji |
| `/dna daj <gracz> <igla\|probka> [typ] [ilość]` | `genetyka.admin` | Nadaje przedmioty laboratoryjne |
| `/dna punkty <dodaj\|ustaw> <gracz> <ilość>` | `genetyka.admin` | Zarządza Punktami Ewolucji gracza |
| `/dna reload` | `genetyka.admin` | Przeładowuje plik konfiguracyjny |
