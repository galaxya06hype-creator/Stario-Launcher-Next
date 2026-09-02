# iOS 26 Liquid Glass — Implementasi di Stario Launcher

> Riset WWDC 2025 (9 Juni 2025) + iOS 26.0–26.4 + library `com.qmdeve.liquidglass:core:1.0.5`

## 1. Apa itu Liquid Glass di iOS 26?

**Liquid Glass** adalah bahasa desain terbesar Apple sejak iOS 7, diperkenalkan di WWDC25 untuk **iOS 26 / iPadOS 26 / macOS Tahoe 26 / watchOS 26 / tvOS 26 / visionOS 26**.

**Sifat material:**
- **Translucent + blur + refraction + dispersion** — memantulkan & membiaskan background di baliknya (live rendering saat device digerakkan, highlight bergerak)
- **Depth berlapis** — panel, button, tab bar, widget, app icon semua terasa seperti kaca nyata dengan soft blur, shiny edges, layered transparency.
- **Varian:** `Glass.regular` (default), `Glass.clear` (lebih transparan), `Glass.identity` (solid). Di Android via `blurRadius`, `refractionHeight`, `dispersion`, `tone`.
- **Adaptive:** Lock Screen Glass/Solid toggle (iOS 26.2), Tinted mode, Reduce Bright Effects (iOS 26.4), Reduce Motion (jelly animation dimatikan untuk aksesibilitas).

**Referensi:** conor.fyi comprehensive reference (Nov 2025, update May 2026), Apple Video WWDC25 #219 “Meet Liquid Glass”, FoneArena & DailyStar WWDC coverage.

---

## 2. Elemen-Elemen iOS 26 (yang kita tiru ke Stario)

### A. Material & Surfaces
| iOS 26 | Stario implementasi |
|--------|---------------------|
| `glassEffect()` + `GlassEffectContainer` + `glassEffectUnion` | `com.qmdeve.liquidglass.LiquidGlassView` via `LiquidGlassHelper.wrap()` |
| `Glass.regular` / `.clear` / `.identity` | `GlassVariant.REGULAR` (blur 24, corner 24), `CLEAR` (12,18), `PROMINENT` (30,36) di `LiquidGlassHelper.java:18` |
| `Refraction height` 10, `Dispersion` 0.4 | `IOS26_REFRACTION_HEIGHT`, `IOS26_DISPERSION` |

### B. Tab Bar & Sheets (paling ikonik iOS 26)
- **Floating, minimizing tab bar** — `tabBarMinimizeBehavior(.onScrollDown/.automatic)` + `tabViewBottomAccessory`. Di Stario: `drawer.xml` dock searchbar jadi floating pill 36dp, elevation 8dp, `background_sheet.xml` radius 32dp dengan top highlight.
- **Sheet morph** — sheets sekarang bisa morph antar detent dengan `glassEffectTransition`. Stario `SheetBehavior` (bottom/left/right/top) ideal untuk `glassEffectTransition` nanti.
- **Search bar glass** — `searchbar_background.xml` sebelumnya solid `colorSurfaceContainer`, sekarang gradient 2-layer + dispersion tint bottom (imitasi refraction).
- **Card & briefing** — `ios26_bg_liquid_glass.xml` (24dp) untuk article card.

### C. App Icons — Icon Composer (iOS 26 baru)
- Apple **Icon Composer** app memungkinkan layered effects + dynamic lighting, 3 style: Default, Dark, Clear (floating). 
- **Themed icon:** sudah kita fix di commit sebelumnya `ic_launcher_monochrome.xml` (monochrome layer untuk Android 13 themed).
- **Clear style:** analog di Stario: icon pack `clear` → pakai `ic_launcher_foreground` dengan background transparan + glass highlight 12% white.
- Library tidak perlu — cukup vector drawable adaptive-icon.

### D. Animasi
| iOS 26 API | Efek | Implementasi Android |
|------------|------|-------------------|
| `matchedTransitionSource` + `.navigationTransition(.zoom)` | Zoom morph antar screen (klik app → buka) | `Transition` + `MaterialContainerTransform`, atau view.animate scale 0.96→1 + Overshoot |
| `Draw On / Draw Off` (SF Symbols 7) | Garis digambar masuk/keluar | `AnimatedVectorDrawable` path trim `trimPathStart/End` |
| `Variable Draw` | Ubah layer sesuai progress (mis download) | `AnimatedVectorDrawable` `level` atau `ValueAnimator` alpha |
| `Magic Replace` | Ganti icon terkait tanpa kedip (mis play→pause) | `AnimatedVectorDrawable` + `TransitionManager` |
| Jelly / Spring | Pantulan elastis saat drag | `BounceInterpolator` / `OvershootInterpolator(2f)` + `SquigglySlider` sudah ada |
| Parallax Lock Screen | Clock gerak sesuai wallpaper & device tilt | `SensorManager` + `View.translationX/Y` (belum di-Stario) |
| Progressive blur | Blur tepi | `FadingEdgeLayout` sudah ada, tinggal tingkatkan `size_top/bottom` |

### E. SF Symbols 7 — Icon Vektor (6,900+)
Detail dari WWDC25 + SF Symbols 7:
- **6900+ symbols**, 9 weights (ultralight..black), 3 scales (S/M/L), otomatis align dengan San Francisco font.
- **Rendering modes:** monochrome, hierarchical, palette, multicolor.
- **Animasi baru:** Draw On/Off, Variable Draw, Magic Replace, automatic **linear gradient** (dari 1 warna).
- **Lokalisasi:** 20+ script (Latin, Cyrillic, Arabic, Hebrew, CJK, Thai, Devanagari, dll).
- **Alternatif Android:** Tidak bisa pakai SF Symbols langsung (lisensi Apple). Pakai **Material Symbols** (Google) yang serupa: variable font, 4 styles (outlined, rounded, sharp, variable), weights 100-700, optical size 20-48, animatable via `AnimatedVectorDrawable`. Kita sudah buat 3 contoh:
  - `ios26_ic_search.xml` → `magnifyingglass` (hierarchical + 12% highlight)
  - `ios26_ic_home.xml` → `house.fill` (floating + tint?)
  - `ios26_ic_appgrid.xml` → `square.grid.2x2.fill` (drawer)

Aplikasi Android cukup impor Google Material Symbols variable font atau buat `AnimatedVectorDrawable` manual seperti contoh di atas.

### F. Typography & Warna
- **Font:** San Francisco (iOS) → Stario pakai `dm_sans_*` + `stario_clockface_variable`. iOS 26 tetap Dynamic Type. Saran: tambah `fontVariationSettings` untuk variable font.
- **Warna:** System color styles + materials (ultraThin, thin, regular, thick). Stario `Theme.java` 12 tema (Dynamic, Monochrome, Red..Pink) sudah mirip — bisa tambah `IOS26_TINTED` variant.
- **Widget:** Home & Lock Screen widget shimmer — Stario `ClockWidget`, `Glance` (Weather, Calendar, Media) perlu dibungkus glass.

---

## 3. Library `com.qmdeve.liquidglass:core:1.0.5` — Cocok atau Tidak?

### Profil Library
- **Repo:** https://github.com/QmDeve/AndroidLiquidGlassView (289 stars, 157 commits, MIT)
- **Artefak:** `com.qmdeve.liquidglass:core:1.0.5` Maven Central (23KB aar, 1 Jun 2026) — sebelumnya 1.0.4 (26 Apr 2026)
- **Fitur klaim:** *Real refraction + dispersion* (bukan fake blur),高度可定制: corner radius, refraction height/offset, dispersion, blur radius, tone, elastic/highlight.
- **Syarat:** Min SDK 24 (dok terbaru 21), Java 11, Compile SDK 36 — **tapi efek full hanya di Android 13+ (API 33)** karena butuh `RenderEffect` blur hardware. Di bawah API 33 jadi transparent background saja.
- **Cara pakai:** XML `<com.qmdeve.liquidglass.LiquidGlassView>` atau code, docs di https://liquidglass.qmdeve.com/

### Kecocokan dengan Stario
| Aspek | Stario | QmDeve | Cocok? |
|-------|--------|--------|--------|
| Min SDK | 29 (Android 10) | 24 | ✅ Ya, Stario di atas min |
| Compile SDK | 36 | 36 | ✅ pas |
| Java | 11 | 11 | ✅ |
| UI toolkit | **View XML** (Recycler, ViewPager, SheetBehavior) | **View** | ✅ **SANGAT COCOK** |
| Compose | hanya material3 sebagian | tidak perlu Compose | ✅ |
| Efek | butuh refraction real | render real | ✅ |

**Kesimpulan: COCOK 95% — library ini pilihan tepat untuk Stario.**

**Kekurangan kecil:**
- Di Android 10-12 (SDK 29-32) user tidak dapat efek full, cuma fallback translucent. Tidak masalah karena Stario masih support lama — kita sudah buat `LiquidGlassHelper fallback` (FrameLayout semi-transparent 30 white).
- Dokumentasi sebagian Mandarin, tapi API sederhana.

### Alternatif Jika QmDeve Tidak Cocok

**1. Kyant0/AndroidLiquidGlass (alias Backdrop)**
- Repo: https://github.com/Kyant0/AndroidLiquidGlass (3,4k stars, 285 commits, Apache 2.0) — **lebih populer**
- Artefak: `io.github.kyant0:backdrop` Maven Central
- Keunggulan: Compose Multiplatform (Android+iOS+Desktop), lebih customizable, docs GitBook rapi, contoh komponen `LiquidButton`, `LiquidToggle`, `LiquidSlider`, `LiquidBottomTabs` sudah jadi.
- **Kekurangan untuk Stario:** Harus pakai **Jetpack Compose**. Stario masih 90% View XML — migrasi butuh rewrite besar. Jika mau full Compose rewrite, ini lebih bagus. Tapi untuk mod habis-habisan cepat, QmDeve menang.
- **Rekomendasi:** Pakai Kyant0 jika kamu mau bikin **Stario v3 Compose rewrite** ke depan; sekarang tetap QmDeve.

**2. Lain-lain (kurang matang):**
- `shuding/liquid-glass` (SVG shader, web), `iyinchao/liquid-glass-studio` (WebGL), `huozhi/vaso` (React) — bukan Android native.

**Saran final:** **Pakai QmDeve 1.0.5 untuk sekarang**, tapi catat Kyant0 sebagai upgrade path kalau nanti migrasi ke Compose.

---

## 4. Apa yang Sudah Diimplement di Repo Ini

### Gradle
`app/build.gradle:186` tambah:
```gradle
implementation 'com.qmdeve.liquidglass:core:1.0.5'
implementation 'androidx.vectordrawable:vectordrawable:1.2.0'
```

### Helper
`app/src/main/java/com/stario/launcher/ui/liquidglass/LiquidGlassHelper.java` (baru, 80 lines)
- Enum `GlassVariant` REGULAR/CLEAR/PROMINENT/SHEET
- `wrap(context, view, variant)` pakai refleksi `LiquidGlassView` untuk API 33+, fallback FrameLayout translucent untuk <33
- `styleGlassButton()` dengan spring Press 0.96 + bounce release (imitasi jelly)
- Konstanta iOS 26: corner 36/24/18/32, blur 24/12, refraction 10, dispersion 0.4

### Layout & Drawable
- `res/search/drawable/searchbar_background.xml`: solid jadi `#4DFFFFFF` + highlight gradient 33% white + dispersion bottom gradient (imitasi refraction)
- `res/drawer/layout/drawer.xml`: `FadingEdgeLayout` margin 8dp + `ios26_bg_liquid_glass`, search pill dibungkus `FrameLayout ios26_search_wrapper` elevasi 8dp + `ios26_bg_liquid_glass_prominent`
- `res/sheet/drawable/background_sheet.xml`: stroke 1.2dp 33 white, solid 4D white, highlight top 40 white gradient (glassRegular)
- Baru: `drawable/ios26_bg_liquid_glass.xml` (24dp, 33 white, stroke 1A), `ios26_bg_liquid_glass_prominent.xml` (36dp, 55 white), `drawable/ios26_ic_search/home/appgrid.xml` (SF Symbols analog hierarchical, plus highlight 12-18% white)

### Cara Aktifkan Runtime (perlu tambah di Activity)
Di `Launcher.java` atau `DrawerPage.java`:
```java
View search = findViewById(R.id.search);
View glass = LiquidGlassHelper.wrap(this, search, LiquidGlassHelper.GlassVariant.REGULAR);
((ViewGroup)search.getParent()).addView(glass);
LiquidGlassHelper.styleGlassButton(glass, false);
```

Untuk full: bungkus `pager`, `FadingEdgeLayout`, `BriefingAdapter` card, `Glance` widget dengan helper yang sama.

### Vector Icon iOS 26
Sudah buat 3, tinggal duplikasi pattern untuk semua icon Stario:
- Settings: `gearshape` → buat `ios26_ic_settings.xml`
- Weather: `cloud.sun.fill` → `ios26_ic_weather.xml`
- Calendar: `calendar` → `ios26_ic_calendar.xml`
Ikuti template `ios26_ic_search.xml`: stroke 1.6, pathData SF, tambah highlight 12% white arc.

---

## 5. Langkah Lanjut (Jika mau 100% iOS 26)

1. **Ganti semua background** `colorSurfaceContainer` → `LiquidGlassView` (drawer, briefing, settings, page_manager)
2. **Tab bar:** buat `LiquidBottomTabs` custom pakai QmDeve + `ViewPager` minimize on scroll (`OnScrollListener` → animate height)
3. **Animasi:** Convert semua `item_animation_scale.xml` jadi spring (Overshoot/Bounce), tambah `AnimatedVectorDrawable` untuk icon Draw On
4. **Typography:** impor `SF Pro` variable font atau pakai `dm_sans` variable, set `fontVariationSettings="wght 400"` + Dynamic Type via `TypedValue`
5. **Blur fallback:** untuk API 29-32, pakai `RenderScript` manual blur atau library `BlurView` (di `fallback()`), jangan cuma transparent
6. **Compose path:** jika mau pakai Kyant0, buat module `app-compose` dan migrasi `Glance` dulu, baru sheet.

Build & test:
```bash
./gradlew assembleDebug
# APK di app/build/outputs/apk/debug/app-debug.apk -> install, cek efek di Android 13+ ada refraction, di Android 10-12 tetap translucent.
```

Push sama seperti sebelumnya: `git add -A && git commit && git push`.
