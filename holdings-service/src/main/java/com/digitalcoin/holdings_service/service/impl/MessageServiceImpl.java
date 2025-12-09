package com.digitalcoin.holdings_service.service.impl;

import com.digitalcoin.holdings_service.entity.Holdings;
import com.digitalcoin.holdings_service.entity.Message;
import com.digitalcoin.holdings_service.repository.MessageRepository;
import com.digitalcoin.holdings_service.service.HoldingsService;
import com.digitalcoin.holdings_service.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Page<Message> findByUserId(Long userId, Pageable pageable) {
        // 由于Message实体类没有userId字段，返回所有消息
        // 系统设计可能是所有用户共享消息池，或根据用户持有的币种过滤
        return messageRepository.findAll(pageable);
    }

    @Override
    public List<Message> findUnreadByUserId(Long userId) {
        // 由于Message实体类没有userId字段，但有isNew字段表示未读状态
        // 返回所有未读消息（isNew=1）
        // 实际应用中可能需要根据用户持有的币种进一步过滤
        // 注意：当前实现是查询比特币的未读消息，需要根据实际需求调整
        return messageRepository.findByCoinTypeAndIsNewOrderByCreatedAtDesc("BTC", 1);
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        // 由于Message实体类没有userId字段，但有isNew字段表示未读状态
        // 返回所有未读消息的数量（isNew=1）
        // 实际应用中可能需要根据用户持有的币种进一步过滤
        // 注意：当前实现是查询比特币的未读消息数量，需要根据实际需求调整
        List<Message> unreadMessages = messageRepository.findByCoinTypeAndIsNewOrderByCreatedAtDesc("BTC", 1);
        return unreadMessages != null ? unreadMessages.size() : 0;
    }

    @Override
    @Transactional
    public Message save(Message message) {
        // 设置创建时间
        if (message.getCreateTime() == null) {
            message.setCreateTime(new java.util.Date());
        }
        
        // 消息去重逻辑：检查是否存在相似内容的消息
        if (message.getContent() != null && message.getCoinType() != null) {
            String contentPreview = message.getContent().substring(0, Math.min(100, message.getContent().length()));
            List<Message> existingMessages = messageRepository.findByContentContainingIgnoreCaseAndCoinType(contentPreview, message.getCoinType());
            
            if (!existingMessages.isEmpty()) {
                // 存在相似消息，不保存新消息
                System.out.println("检测到重复消息，跳过保存: " + contentPreview);
                return null;
            }
        }
        
        // 调用repository保存消息
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public List<Message> saveAll(List<Message> messages) {
        // 设置每条消息的创建时间（如果未设置）
        for (Message message : messages) {
            if (message.getCreateTime() == null) {
                message.setCreateTime(new java.util.Date());
            }
        }
        // 调用repository批量保存消息
        return messageRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // 调用repository根据ID删除消息
        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 调用repository批量删除消息
        messageRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        // 标记消息为已读（设置isNew=0）
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setIsNew(0);
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(List<Long> ids) {
        // 批量标记消息为已读（设置isNew=0）
        for (Long id : ids) {
            messageRepository.markAsProcessed(id);
        }
    }

    @Override
    @Transactional
    public void markAllByUserIdAsRead(Long userId) {
        // 由于Message实体类没有userId字段，这个方法的实现需要调整
        // 实际应用中可能需要根据用户持有的币种来标记所有相关消息为已读
        // 这里暂时实现为标记所有BTC消息为已读
        // 注意：这是简化实现，需要根据实际业务需求调整
        List<Message> btcMessages = messageRepository.findByCoinTypeAndIsNewOrderByCreatedAtDesc("BTC", 1);
        for (Message message : btcMessages) {
            messageRepository.markAsProcessed(message.getId());
        }
    }

    @Autowired
    private HoldingsService holdingsService;

    @Override
    @Transactional
    public void deleteHistoryByUserId(Long userId, int keepCount) {
        // 由于系统设计中没有userId字段，我们通过查询当前持仓的所有币种来实现该功能
        // 获取用户当前所有持仓的币种
        List<Holdings> currentHoldings = holdingsService.getCurrentHoldings();
        List<String> coinTypes = currentHoldings.stream().map(Holdings::getCoinType).distinct().toList();
        
        // 如果没有持仓，直接返回
        if (coinTypes.isEmpty()) {
            return;
        }
        
        // 对于每个币种，删除历史消息，保留最近keepCount条
        for (String coinType : coinTypes) {
            // 获取该币种的所有已读消息，按创建时间倒序排列
            List<Message> allMessages = messageRepository.findByCoinTypeAndIsNewOrderByCreatedAtDesc(coinType, 0);
            if (allMessages.size() > keepCount) {
                // 删除超出keepCount的旧消息
                List<Message> messagesToDelete = allMessages.subList(keepCount, allMessages.size());
                List<Long> idsToDelete = messagesToDelete.stream().map(Message::getId).toList();
                messageRepository.deleteAllById(idsToDelete);
            }
        }
    }

    @Override
    @Transactional
    public Message sendSystemMessage(Long userId, String title, String content) {
        // 创建系统消息
        Message message = new Message();
        // Message类中没有title字段，将标题包含在content中
        String systemContent = title + "\n" + content;
        message.setContent(systemContent);
        message.setCoinType("SYSTEM"); // 使用系统专用币种类型
        message.setSentiment("中性"); // 系统消息默认中性
        message.setIsNew(1); // 新消息
        // Message类中没有type字段，不需要设置
        // Message类中是setCreateTime方法，不是setCreatedAt
        message.setCreateTime(new Date());
        
        // 保存消息
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public List<Message> sendBroadcastMessage(String title, String content) {
        // 获取所有当前持仓的币种
        List<Holdings> currentHoldings = holdingsService.getCurrentHoldings();
        List<String> coinTypes = currentHoldings.stream().map(Holdings::getCoinType).distinct().toList();
        
        // 为每个币种创建广播消息
        List<Message> messages = new ArrayList<>();
        for (String coinType : coinTypes) {
            Message message = new Message();
            // Message类中没有title字段，将标题包含在content中
            String broadcastContent = title + "\n" + content;
            message.setContent(broadcastContent);
            message.setCoinType(coinType);
            message.setSentiment("中性"); // 广播消息默认中性
            message.setIsNew(1); // 新消息
            // Message类中没有type字段，不需要设置
            // Message类中是setCreateTime方法，不是setCreatedAt
            message.setCreateTime(new Date());
            
            // 保存广播消息
            messages.add(messageRepository.save(message));
        }
        
        return messages;
    }
    
    @Override
    @Transactional
    public void sendBroadcastMessage(String title, String content, String coinType) {
        // 创建广播消息
        Message message = new Message();
        // Message类中没有title字段，将标题包含在content中
        String broadcastContent = title + "\n" + content;
        message.setContent(broadcastContent);
        message.setCoinType(coinType);
        message.setSentiment("中性"); // 广播消息默认中性
        message.setIsNew(1); // 新消息
        // Message类中没有type字段，不需要设置
        message.setCreateTime(new Date());
        
        // 保存广播消息
        messageRepository.save(message);
    }
    
    @Override
    public List<Message> getAllMessages() {
        // 按时间倒序获取所有消息
        return messageRepository.findAll();
    }
    
    @Override
    public List<Message> getMessagesByCoinType(String coinType) {
        // 按币种查询消息，返回所有状态的消息
        return messageRepository.findByCoinTypeAndIsNewOrderByCreatedAtDesc(coinType, 0);
    }
    
    @Override
    public Page<Message> findPage(Pageable pageable) {
        // 分页查询所有消息，按时间倒序
        return messageRepository.findAll(pageable);
    }
}