package com.chat.demochat.util;

import com.chat.demochat.cons.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.TreeSet;

@Slf4j
public class Utils
{

    public static final String getSessionId(List<String> accounts)
    {
        TreeSet<String> treeSet = list2TreeSet(accounts);
        String sessionId = Constant.SESSION_ID_PREFIX + treeSet.toString().replaceAll("\\[", "").replaceAll("]", "").replaceAll(", ", "-");
        log.info("sessionId:{}", sessionId);
        return sessionId;
    }

    public static final TreeSet<String> list2TreeSet(List<String> accounts)
    {
        TreeSet<String> treeSet = new TreeSet<>();
        for (String account : accounts)
        {
            treeSet.add(account);
        }
        return treeSet;
    }


    public static class Stu
    {
        private int num;

        public Stu(int num)
        {
            this.num = num;
        }

        public void setNum(int num)
        {
            this.num = num;
        }

        public int getNum()
        {
            return num;
        }

        public String toString()
        {
            return num + "";
        }
    }
}
