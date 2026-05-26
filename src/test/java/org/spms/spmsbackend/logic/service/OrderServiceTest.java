package org.spms.spmsbackend.logic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.ShelfRowEntity;
import org.spms.spmsbackend.entity.UserEntity;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.mapper.PackageMapper;
import org.spms.spmsbackend.repository.PackageRepository;
import org.spms.spmsbackend.repository.ShelfRowRepository;
import org.spms.spmsbackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private NotificationService notificationService;
    @Mock private PackageRepository packageRepository;
    @Mock private PackageMapper packageMapper;
    @Mock private UserRepository userRepository;
    @Mock private ShelfRowRepository shelfRowRepository;

    @InjectMocks
    private OrderService orderService;

    private PackageEntity sampleEntity;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleEntity = new PackageEntity();
        sampleEntity.setId(1);
        sampleEntity.setTrackingNumber("TRK-001");
        sampleEntity.setStatus("STORED");

        sampleOrder = new Order();
        sampleOrder.setId(1);
    }

    // ── getAllOrders ────────────────────────────────────────────────────────

    @Test
    void getAllOrders_returnsMappedList() {
        when(packageRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(sampleOrder, result.get(0));
    }

    // ── getOrderById ────────────────────────────────────────────────────────

    @Test
    void getOrderById_found_returnsMappedOrder() {
        when(packageRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Order result = orderService.getOrderById(1);

        assertEquals(sampleOrder, result);
    }

    @Test
    void getOrderById_notFound_throwsNotFoundException() {
        when(packageRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(99));
    }

    // ── getOrderByTrackingNumber ─────────────────────────────────────────────

    @Test
    void getOrderByTrackingNumber_notFound_throwsNotFoundException() {
        when(packageRepository.findByTrackingNumber("MISSING")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.getOrderByTrackingNumber("MISSING"));
    }

    // ── storePackage ─────────────────────────────────────────────────────────

    @Test
    void storePackage_setsStatusStoredAndPersists() {
        UserEntity employee = new UserEntity();
        employee.setId(5);

        ShelfRowEntity row = new ShelfRowEntity();
        row.setId(3);

        when(packageRepository.findByTrackingNumber("TRK-001"))
                .thenReturn(Optional.empty()); // new package
        when(userRepository.findById(5)).thenReturn(Optional.of(employee));
        when(shelfRowRepository.findById(3)).thenReturn(Optional.of(row));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(sampleEntity);
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Order result = orderService.storePackage("TRK-001", 5, 3);

        assertNotNull(result);
        verify(packageRepository, atLeastOnce()).save(any(PackageEntity.class));
    }

    @Test
    void storePackage_employeeNotFound_throwsNotFoundException() {
        when(packageRepository.findByTrackingNumber("TRK-001"))
                .thenReturn(Optional.empty());
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> orderService.storePackage("TRK-001", 99, 3));
    }

    @Test
    void storePackage_notificationFailure_doesNotBlockStorage() {
        UserEntity employee = new UserEntity();
        employee.setId(5);

        ShelfRowEntity row = new ShelfRowEntity();
        row.setId(3);

        when(packageRepository.findByTrackingNumber("TRK-001"))
                .thenReturn(Optional.empty());
        when(userRepository.findById(5)).thenReturn(Optional.of(employee));
        when(shelfRowRepository.findById(3)).thenReturn(Optional.of(row));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(sampleEntity);
        when(notificationService.notifyPackageArrived(any(), any()))
                .thenThrow(new RuntimeException("mail server down"));
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        // Should not throw — notification errors are swallowed
        assertDoesNotThrow(() -> orderService.storePackage("TRK-001", 5, 3));
    }

    // ── markAsPickedUp ───────────────────────────────────────────────────────

    @Test
    void markAsPickedUp_setsPickedUpAndPersists() {
        when(packageRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(sampleEntity);
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Order result = orderService.markAsPickedUp(1);

        assertNotNull(result);
        assertTrue(sampleEntity.getPickedUp());
        assertEquals("PICKED_UP", sampleEntity.getStatus());
    }

    @Test
    void markAsPickedUp_notificationFailure_doesNotBlockPickup() {
        when(packageRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(sampleEntity);
        doThrow(new RuntimeException("smtp error"))
                .when(notificationService).notifyPickupConfirmed(any());
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        assertDoesNotThrow(() -> orderService.markAsPickedUp(1));
    }

    // ── updateStatus ─────────────────────────────────────────────────────────

    @Test
    void updateStatus_updatesEntityStatus() {
        OrderStatus pickedUp = new OrderStatus(2, "PICKED_UP", "Package collected");
        when(packageRepository.findById(1)).thenReturn(Optional.of(sampleEntity));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(sampleEntity);
        when(packageMapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        orderService.updateStatus(1, pickedUp);

        assertEquals("PICKED_UP", sampleEntity.getStatus());
    }
}
