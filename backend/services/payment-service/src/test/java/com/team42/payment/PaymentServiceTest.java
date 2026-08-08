package com.team42.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDuplicateCallbackIdempotency() {
        String eventId = "evt_1001";
        String paymentId = "pay_5001";
        String bookingRef = "bk_test_001";

        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        paymentService.handleCallback(eventId, paymentId, bookingRef, "SUCCEEDED", 450, "raw", null);

        verify(processedEventRepository, times(1)).existsByEventId(eventId);
        verify(processedEventRepository, never()).save(any());
        verify(paymentRepository, never()).findByBookingRef(any());
    }

    @Test
    void testNewCallbackProcessedSuccessfully() {
        String eventId = "evt_1002";
        String paymentId = "pay_5002";
        String bookingRef = "bk_test_002";

        PaymentRecordEntity record = new PaymentRecordEntity();
        record.setBookingRef(bookingRef);
        record.setStatus("PENDING");

        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);
        when(paymentRepository.findByBookingRef(bookingRef)).thenReturn(Optional.of(record));

        paymentService.handleCallback(eventId, paymentId, bookingRef, "SUCCEEDED", 450, "raw", null);

        verify(processedEventRepository, times(1)).save(any(ProcessedEventEntity.class));
        verify(paymentRepository, times(1)).save(record);
    }
}
