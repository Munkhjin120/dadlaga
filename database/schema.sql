-- Cinema Booking App - MySQL схем
-- Программ анх удаа ажиллахад эдгээр хүснэгтүүд DBInitializer.java-с автоматаар үүснэ.
-- Гэхдээ хүсвэл гараар mysql клиентээр ажиллуулж болно.

CREATE DATABASE IF NOT EXISTS cinema_booking CHARACTER SET utf8mb4;
USE cinema_booking;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS halls (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    rows_count INT NOT NULL,
    cols_count INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_id INT NOT NULL,
    row_label VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    CONSTRAINT fk_seat_hall FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_type VARCHAR(20) NOT NULL DEFAULT 'MOVIE',
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    hall_id INT NOT NULL,
    CONSTRAINT fk_event_hall FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    seat_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_seat FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Анхны admin: username=admin, password=admin123 (SHA-256 + salt-аар шифрлэгдэнэ)
