package com.ace.xiatom.chat;

/**
 * Created by xiatom on 2019/4/24.
 */

public class chat_content {
    private Boolean fromOther;
    private String content;

    public chat_content(Boolean fromOther,String content){
        this.fromOther = fromOther;
        this.content = content;
    }

    public Boolean getFromOther() {
        return fromOther;
    }

    public String getContent() {
        return content;
    }
}
