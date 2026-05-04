package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.domain.notifications.Notification;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.domain.orders.OrderSize;
import org.spms.spmsbackend.domain.orders.OrderStatus;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.spms.spmsbackend.mapper.PackageMapper;
import org.spms.spmsbackend.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final NotificationService notificationService;
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;

    public OrderService(
            NotificationService notificationService,
            PackageRepository packageRepository,
            PackageMapper packageMapper
    ) {
        this.notificationService = notificationService;
        this.packageRepository = packageRepository;
        this.packageMapper = packageMapper;
    }

    @Override
    public List<Order> getAllOrders() {
        return packageRepository.findAll()
                .stream()
                .map(packageMapper::toDomain)
                .toList();
    }

    @Override
    public Order getOrderById(int id) {
        return packageRepository.findById(id)
                .map(packageMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Order getOrderByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .map(packageMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Order> getOrdersForEmployee(int employeeId) {
        return packageRepository.findByEmployee_Id(employeeId)
                .stream()
                .map(packageMapper::toDomain)
                .toList();
    }

    @Override
    public Order createOrder(int employeeId, String trackingNumber, OrderSize size) {
        throw new UnsupportedOperationException("Not implemented in current sprint");
    }

    @Override
    public Order markAsPickedUp(int orderId) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);

        entity.setPickedUp(true);
        entity.setPickedUpAt(LocalDateTime.now());

        PackageEntity saved = packageRepository.save(entity);
        return packageMapper.toDomain(saved);
    }

    @Override
    public Order updateStatus(int orderId, OrderStatus newStatus) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);

        entity.setStatus(newStatus.getName());
        PackageEntity saved = packageRepository.save(entity);
        return packageMapper.toDomain(saved);
    }

    @Override
    public void deleteOrder(int id) {
        packageRepository.deleteById(id);
    }

    @Override
    public Notification createArrivalNotification(int orderId, List<Integer> targetUserIds) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        Notification result = notificationService.notifyPackageArrived(entity, targetUserIds);
        // Persist any classification update the service made.
        packageRepository.save(entity);
        return result;
    }

    @Override
    public Notification createPickupReminderNotification(int orderId) {
        PackageEntity entity = packageRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        return notificationService.notifyPickupReminder(entity);
    }
}