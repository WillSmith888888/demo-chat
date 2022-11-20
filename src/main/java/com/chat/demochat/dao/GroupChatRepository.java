package com.chat.demochat.dao;

import com.chat.demochat.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepository extends JpaRepository<GroupChat, String>
{

}
