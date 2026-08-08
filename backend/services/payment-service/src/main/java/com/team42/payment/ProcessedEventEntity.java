package com.team42.payment;

import jakarta.persistence.*;

@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {
    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "booking_ref")
    private String bookingRef;

    private String status;

    public ProcessedEventEntity() {}

    public ProcessedEventEntity(String eventId, String paymentId, String bookingRef, String status) {
        this.eventId = eventId;
        this.paymentId = paymentId;
        this.bookingRef = bookingRef;
        this.status = status;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
