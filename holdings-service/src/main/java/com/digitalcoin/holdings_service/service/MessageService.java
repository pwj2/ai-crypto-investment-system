package com.digitalcoin.holdings_service.service;

import com.digitalcoin.holdings_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 根据ID查询消息
     */
    Optional<Message> findById(Long id);

    /**
     * 根据用户ID分页查询消息
     */
    Page<Message> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID查询未读消息
     */
    List<Message> findUnreadByUserId(Long userId);

    /**
     * 查询用户未读消息数量
     */
    long countUnreadByUserId(Long userId);

    /**
     * 保存消息
     */
    Message save(Message message);
    
    /**
     * 保存消息（定时任务使用）
     */
    default void saveMessage(Message message) {
        save(message);
    }

    /**
     * 批量保存消息
     */
    List<Message> saveAll(List<Message> messages);

    /**
     * 根据ID删除消息
     */
    void deleteById(Long id);

    /**
     * 批量删除消息
     */
    void deleteByIds(List<Long> ids);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long id);

    /**
     * 批量标记消息为已读
     */
    void markAllAsRead(List<Long> ids);

    /**
     * 将用户所有消息标记为已读
     */
    void markAllByUserIdAsRead(Long userId);

    /**
     * 根据用户ID删除历史消息（保留最近N条）
     */
    void deleteHistoryByUserId(Long userId, int keepCount);

    /**
     * 发送系统消息给指定用户
     */
    Message sendSystemMessage(Long userId, String title, String content);

    /**
     * 发送广播消息给所有用户
     */
    List<Message> sendBroadcastMessage(String title, String content);
    
    /**
     * 发送指定币种的广播消息给所有用户
     */
    void sendBroadcastMessage(String title, String content, String coinType);
    
    /**
     * 获取所有消息（按时间倒序）
     */
    List<Message> getAllMessages();
    
    /**
     * 分页查询所有消息
     */
    Page<Message> findPage(Pageable pageable);
    
    /**
     * 按币种查询消息
     */
    List<Message> getMessagesByCoinType(String coinType);
}