#!/bin/bash
mvn clean package
jpackage \
  --type dmg \
  --name "CinemaBooking" \
  --app-version "1.0.0" \
  --input target \
  --dest installer \
  --main-jar CinemaBookingApp-1.0.0.jar \
  --main-class mn.cinema.Main
