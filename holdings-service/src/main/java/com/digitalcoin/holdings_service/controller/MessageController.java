package com.digitalcoin.holdings_service.controller;

import com.digitalcoin.holdings_service.entity.Message;
import com.digitalcoin.holdings_service.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息Controller
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 1. 获取所有消息（按时间倒序）- 保留此接口以保持兼容性
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
    
    // 2. 分页获取消息
    @GetMapping("/page")
    public ResponseEntity<Page<Message>> getMessagesByPage(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return ResponseEntity.ok(messageService.findPage(pageable));
    }

    // 2. 按币种查询消息
    @GetMapping("/coin/{coinType}")
    public ResponseEntity<List<Message>> getMessagesByCoinType(@PathVariable String coinType) {
        return ResponseEntity.ok(messageService.getMessagesByCoinType(coinType));
    }

    // 3. 标记消息为已读
    @PutMapping("/read/{messageId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok("消息已标记为已读");
    }

    /**
     * 根据ID查询消息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据用户ID分页查询消息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Message>> getMessagesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageService.findByUserId(userId, pageable);
        return ResponseEntity.ok(messagePage);
    }

    /**
     * 查询用户未读消息
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Message>> getUnreadMessagesByUserId(@PathVariable Long userId) {
        List<Message> unreadMessages = messageService.findUnreadByUserId(userId);
        return ResponseEntity.ok(unreadMessages);
    }

    /**
     * 查询用户未读消息数量
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCountByUserId(@PathVariable Long userId) {
        long unreadCount = messageService.countUnreadByUserId(userId);
        return ResponseEntity.ok(unreadCount);
    }

    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message saved = messageService.save(message);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Message>> sendMessagesBatch(@RequestBody List<Message> messages) {
        List<Message> saved = messageService.saveAll(messages);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteMessagesBatch(@RequestBody List<Long> ids) {
        messageService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 标记消息为已读（保持兼容性）
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsReadLegacy(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量标记消息为已读
     */
    @PutMapping("/batch/read")
    public ResponseEntity<Void> markAllAsRead(@RequestBody List<Long> ids) {
        messageService.markAllAsRead(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 将用户所有消息标记为已读
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllByUserIdAsRead(@PathVariable Long userId) {
        messageService.markAllByUserIdAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除用户历史消息
     */
    @DeleteMapping("/user/{userId}/history")
    public ResponseEntity<Void> deleteHistoryByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "100") int keepCount) {
        messageService.deleteHistoryByUserId(userId, keepCount);
        return ResponseEntity.noContent().build();
    }

    /**
     * 发送系统消息给指定用户
     */
    @PostMapping("/system")
    public ResponseEntity<Message> sendSystemMessage(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content) {
        Message message = messageService.sendSystemMessage(userId, title, content);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    /**
     * 发送广播消息给所有用户
     */
    @PostMapping("/broadcast")
    public ResponseEntity<List<Message>> sendBroadcastMessage(
            @RequestParam String title,
            @RequestParam String content) {
        List<Message> messages = messageService.sendBroadcastMessage(title, content);
        return new ResponseEntity<>(messages, HttpStatus.CREATED);
    }
}