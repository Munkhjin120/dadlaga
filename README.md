# CinemaBookingApp

Кино театр болон тоглолтын тасалбар захиалах desktop программ.
**JavaFX 21 + Maven + MySQL + FXML (SceneBuilder-тэй нийцтэй)**

## Онцлогууд

- Хэрэглэгч бүртгүүлэх / нэвтрэх (нууц үг SHA-256 + salt-аар шифрлэгдэнэ)
- Хэрэглэгч, Админ гэсэн 2 эрхийн түвшин
- Одоо/удахгүй болох кино, тоглолтын жагсаалт харах
- Суудлын схем дэлгэц (сул=ногоон, захиалагдсан=улаан, сонгосон=цэнхэр)
- Тасалбар захиалах, цуцлах, захиалгын түүх харах
- Админ: кино/тоглолт, танхим (+суудал автомат үүсгэлт), захиалга хайх, хэрэглэгч жагсаалт удирдах
- Программ анх ажиллахад MySQL хүснэгтүүд автоматаар үүснэ
- Windows дээр суулгах `.exe` installer бэлдэх скрипттэй

## Төслийн бүтэц

```
CinemaBookingApp/
├── pom.xml
├── build-installer.bat          # Windows .exe installer бэлдэх
├── database/schema.sql          # Лавлагаа схем (программ автоматаар үүсгэдэг тул заавал ажиллуулах шаардлагагүй)
└── src/main/
    ├── java/mn/cinema/
    │   ├── Main.java
    │   ├── db/            (DBConnection, DBInitializer)
    │   ├── model/          (User, Event, Hall, Seat, Booking)
    │   ├── dao/            (UserDAO, EventDAO, HallDAO, SeatDAO, BookingDAO)
    │   ├── util/           (PasswordUtil, SessionManager)
    │   └── controller/     (Login, Register, Main, EventList, SeatSelection, BookingHistory, Admin)
    └── resources/
        ├── db.properties
        └── mn/cinema/
            ├── view/*.fxml   (SceneBuilder-ээр нээж болно)
            ├── css/style.css
            └── images/icon.png
```

## Шаардлагатай зүйлс

- JDK 17 эсвэл дээш
- Maven 3.8+
- MySQL Server 8.x (локал эсвэл сервер дээр ажиллаж байгаа)
- (Заавал биш) SceneBuilder — FXML файлуудыг визуал засах бол

## 1. Өгөгдлийн сан тохируулах

`src/main/resources/db.properties` файлыг өөрийн MySQL тохиргоондоо тааруулна:

```properties
db.url=jdbc:mysql://localhost:3306/cinema_booking?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.password=root
```

MySQL сервер ажиллаж байхад л хангалттай — `cinema_booking` сан болон 5 хүснэгт
(`users`, `halls`, `seats`, `events`, `bookings`) программ **анх ажиллахад автоматаар үүснэ**.

Анхны админ хэрэглэгч автоматаар үүснэ:
- **Хэрэглэгчийн нэр:** `admin`
- **Нууц үг:** `admin123`

## 2. Ажиллуулах (хөгжүүлэлтийн горим)

```bash
mvn clean javafx:run
```

## 3. SceneBuilder-ээр FXML засах

SceneBuilder-ийг татаад (https://gluonhq.com/products/scene-builder/) дурын
`src/main/resources/mn/cinema/view/*.fxml` файлыг шууд нээж, drag-and-drop байдлаар
дэлгэцийн байршлыг өөрчилж болно. Controller класс бүрийг `fx:controller` attribute-аар
холбосон тул SceneBuilder автоматаар танина.

## 4. Windows дээр суулгах .exe Installer бэлдэх

1. JDK 17+ болон Maven-ийг Windows компьютертоо суулгана (PATH дээр байх ёстой).
2. JavaFX SDK (модулиудтай хувилбар)-ийг татаж авна:
   https://gluonhq.com/products/javafx/ → **jmods биш, "SDK"** хувилбарыг сонгоно.
   Жишээ нь `C:\javafx-sdk-21.0.2` болгож задална.
3. `icon.png`-ийг `.ico` болгож хөрвүүлээд (жишээ нь https://convertio.co/png-ico/ гэх
   мэт онлайн хөрвүүлэгчээр, эсвэл ImageMagick-аар) `src\main\resources\mn\cinema\images\icon.ico`
   болгож хадгална.
4. Төслийн үндсэн хавтаснаас (`CinemaBookingApp/`) дараах командыг ажиллуулна:

   ```
   build-installer.bat "C:\javafx-sdk-21.0.2\lib"
   ```

5. Амжилттай бол `installer\CinemaBooking-1.0.0.exe` файл үүснэ — үүнийг ажиллуулбал
   Windows дээр Start Menu shortcut-той бүрэн суулгагдана. Программын нэр, icon
   өөрийн тохируулсан байдлаар харагдана.

> **Тайлбар:** jpackage нь зөвхөн локал (build хийж байгаа) OS-т зориулсан
> installer үүсгэдэг тул `.exe` авахын тулд Windows компьютер дээр build хийх
> шаардлагатай. Linux/macOS дээрээс шууд `.exe` үүсгэх боломжгүй.

## Ашиглалт

### Энгийн хэрэглэгч
1. "Бүртгүүлэх" дараад шинэ хэрэглэгч үүсгэнэ (эсвэл `admin/admin123`-аар нэвтэрч болно).
2. Нэвтэрсний дараа кино/тоглолтын жагсаалтаас сонгоно.
3. "Суудал сонгох" дараад ногоон (сул) суудлуудаас нэгийг дарна → "Тасалбар захиалах".
4. "Миний захиалгууд"-аас түүхээ харж, шаардлагатай бол цуцалж болно.

### Админ (`admin` / `admin123`)
Дээд цэснээс **"Админ удирдлага"**-г сонгоод:
- **Кино/Тоглолт** — шинэ кино/тоглолт нэмэх, засах, устгах
- **Танхим** — шинэ танхим нэмэх (мөр × баганаар суудал автоматаар үүснэ), засах, устгах
- **Захиалгууд** — бүх захиалгыг харах, хэрэглэгч/нэрээр хайх
- **Хэрэглэгчид** — бүртгэлтэй хэрэглэгчдийн жагсаалт харах

## Аюулгүй байдал

- Нууц үг хэзээ ч тодоор (plain text) хадгалагдахгүй — SHA-256 + санамсаргүй
  16 байт `salt`-аар хослуулан шифрлэж, зөвхөн hash утгыг л хадгална.
- SQL инъекцээс сэргийлж бүх query нь `PreparedStatement` ашигладаг.
