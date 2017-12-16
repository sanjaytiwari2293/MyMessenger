package com.example.sanja.mymessenger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sanja on 7/17/2017.
 */

public class GroupDetails implements Serializable {

    String groupTitle, groupPic, groupId, createdBy, creatorId;
    ArrayList<User> membersList = new ArrayList<>();
    /*ArrayList<ChatMsges> messages = new ArrayList<>();*/

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupPic() {
        return groupPic;
    }

    public void setGroupPic(String groupPic) {
        this.groupPic = groupPic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public ArrayList<User> getMembersList() {
        return membersList;
    }

    public void setMembersList(ArrayList<User> membersList) {
        this.membersList = membersList;
    }

    @Override
    public String toString() {
        return "GroupDetails{" +
                "groupTitle='" + groupTitle + '\'' +
                ", groupPic='" + groupPic + '\'' +
                ", groupId='" + groupId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", membersList=" + membersList +
                '}';
    }
}
