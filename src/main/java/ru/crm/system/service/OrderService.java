package ru.crm.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.crm.system.database.entity.Admin;
import ru.crm.system.database.entity.Comment;
import ru.crm.system.database.entity.enums.ActionType;
import ru.crm.system.database.entity.enums.OrderStatus;
import ru.crm.system.database.repository.AdminRepository;
import ru.crm.system.database.repository.OrderRepository;
import ru.crm.system.dto.loginfo.LogInfoCreateDto;
import ru.crm.system.dto.order.OrderCreateEditDto;
import ru.crm.system.dto.order.OrderReadDto;
import ru.crm.system.listener.entity.AccessType;
import ru.crm.system.listener.entity.EntityEvent;
import ru.crm.system.mapper.OrderCreateEditMapper;
import ru.crm.system.mapper.OrderReadMapper;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCreateEditMapper orderCreatedEditMapper;
    private final ApplicationEventPublisher publisher;
    private final OrderReadMapper orderReadMapper;
    private final AdminRepository adminRepository;

    @Transactional
    public OrderReadDto create(OrderCreateEditDto createDto) {
        return Optional.of(createDto)
                .map(orderCreatedEditMapper::map)
                .map(orderRepository::save)
                .map(order -> {
                    var logInfo = createLogInfo(order.getId());
                    logInfo.setDescription(String.format("Создана новая заявка (№%s)", order.getId()));
                    logInfo.setAction(ActionType.CREATED);
                    publisher.publishEvent(new EntityEvent<>(order, AccessType.CREATE, logInfo));
                    return orderReadMapper.map(order);
                })
                .orElseThrow();
    }

    public List<OrderReadDto> findAll() {
        return orderRepository.findAll().stream()
                .map(orderReadMapper::map)
                .toList();
    }

    public Optional<OrderReadDto> findById(Integer id) {
        return orderRepository.findById(id)
                .map(orderReadMapper::map);
    }

    @Transactional
    public Optional<OrderReadDto> update(Integer orderId,
                                         Integer adminId,
                                         OrderCreateEditDto editDto) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.getAdmin() == null) {
                        adminRepository.findById(adminId)
                                .ifPresent(order::setAdmin);
                        editDto.setAdminId(adminId);
                    }
                    return orderCreatedEditMapper.map(editDto, order);
                })
                .map(orderRepository::saveAndFlush)
                .map(orderReadMapper::map);
    }

    @Transactional
    public Optional<OrderReadDto> addComment(Integer orderId,
                                             Integer adminId,
                                             String comment) {
        return adminRepository.findById(adminId)
                .map(admin -> orderRepository.findById(orderId).
                        map(order -> {
                            if (order.getStatus().equals(OrderStatus.UNPROCESSED)) {
                                order.setAdmin(admin);
                            }
                            order.addComment(createComment(comment));
                            var logInfo = createCommentLogInfo(orderId, comment, admin);
                            publisher.publishEvent(new EntityEvent<>(order, AccessType.UPDATE, logInfo));
                            return orderReadMapper.map(order);
                        })).flatMap(order -> order);
    }

    @Transactional
    public Optional<OrderReadDto> changeStatus(Integer orderId,
                                               Integer adminId,
                                               OrderStatus status) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.getAdmin() == null) {
                        adminRepository.findById(adminId)
                                .ifPresent(order::setAdmin);
                    }
                    order.setStatus(status);
                    var logInfo = createChangeStatusLogInfo(adminId, orderId, status);
                    publisher.publishEvent(new EntityEvent<>(order, AccessType.CHANGE_STATUS, logInfo));
                    return orderRepository.saveAndFlush(order);
                })
                .map(orderReadMapper::map);
    }

    /**
     * Метод для создания базового LogInfo для всех методов
     */
    private LogInfoCreateDto createLogInfo(Integer orderId) {
        return LogInfoCreateDto.builder()
                .createdAt(now().truncatedTo(SECONDS))
                .orderId(orderId)
                .build();
    }

    /**
     * Метод для создания Comment
     */
    private Comment createComment(String comment) {
        return Comment.of(comment, now().truncatedTo(SECONDS));
    }

    /**
     * Метод для создания LogInfo при изменении статуса заказа
     */
    private LogInfoCreateDto createChangeStatusLogInfo(Integer adminId, Integer orderId, OrderStatus status) {
        var logInfo = createLogInfo(orderId);
        var actionType = getActionType(status);
        logInfo.setAction(actionType);
        logInfo.setAdminId(adminId);
        logInfo.setDescription(String.format("Статус заявки №%s изменён на: %s ", orderId, status.name()));
        return logInfo;
    }

    private ActionType getActionType(OrderStatus status) {
        return switch (status) {
            case APPOINTMENT_SCHEDULED, APPOINTMENT_COMPLETED -> ActionType.CHANGE_ORDER_STATUS;
            case SUCCESSFULLY_COMPLETED -> ActionType.SUCCESSFULLY_COMPLETED;
            case POOR_LEAD, REFUSED_TO_PURCHASE -> ActionType.COMPLETED_UNSUCCESSFULLY;
            case RESERVED -> ActionType.ARCHIVED;
            case UNPROCESSED -> ActionType.CREATED;
        };
    }

    /**
     * Метод для создания LogInfo при добавлении комментария к заказу
     */
    private LogInfoCreateDto createCommentLogInfo(Integer orderId, String comment, Admin admin) {
        var logInfo = createLogInfo(orderId);
        logInfo.setAdminId(admin.getId());
        logInfo.setAction(ActionType.COMMENT);
        logInfo.setDescription(String.format("Админ %s %s добавил комментарий к заявке №%d: %s",
                admin.getUserInfo().getFirstName(),
                admin.getUserInfo().getLastName(),
                orderId,
                comment));
        return logInfo;
    }
}