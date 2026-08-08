package com.team42.product;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String category;  // 2D, 3D, IMAX
    private String status;    // Now Showing, Coming Soon
    private String releaseDate;
    private String image;

    @Column(length = 2000)
    private String synopsis;

    private String actor;
    private String genre;
    private String language;
    private String duration;

    public MovieEntity() {}

    public MovieEntity(String title, String category, String status, String releaseDate,
                       String image, String synopsis, String actor, String genre,
                       String language, String duration) {
        this.title = title;
        this.category = category;
        this.status = status;
        this.releaseDate = releaseDate;
        this.image = image;
        this.synopsis = synopsis;
        this.actor = actor;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
