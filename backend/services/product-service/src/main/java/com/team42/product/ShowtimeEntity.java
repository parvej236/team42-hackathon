package com.team42.product;

import jakarta.persistence.*;

@Entity
@Table(name = "showtimes")
public class ShowtimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private String theatre;     // Sony Square, Mirpur, Dhaka

    @Column(nullable = false)
    private String hall;        // Hall 1, Hall 2

    @Column(nullable = false)
    private String showDate;    // 8th, August 2026

    @Column(nullable = false)
    private String showTime;    // 8:00 PM

    private String dayOfWeek;   // Saturday

    private int totalRows = 13;
    private int seatsPerRow = 14;
    private int priceRegular = 400;
    private int pricePremium = 550;

    public ShowtimeEntity() {}

    public ShowtimeEntity(Long movieId, String theatre, String hall, String showDate,
                          String showTime, String dayOfWeek) {
        this.movieId = movieId;
        this.theatre = theatre;
        this.hall = hall;
        this.showDate = showDate;
        this.showTime = showTime;
        this.dayOfWeek = dayOfWeek;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public String getTheatre() { return theatre; }
    public void setTheatre(String theatre) { this.theatre = theatre; }
    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }
    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }
    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
    public int getPriceRegular() { return priceRegular; }
    public void setPriceRegular(int priceRegular) { this.priceRegular = priceRegular; }
    public int getPricePremium() { return pricePremium; }
    public void setPricePremium(int pricePremium) { this.pricePremium = pricePremium; }
}
